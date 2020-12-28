package com.jb.goscanner.function.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.common.StringUtils;
import com.jb.goscanner.R;
import com.jb.goscanner.base.fragment.BaseFragmentActivity;
import com.jb.goscanner.base.fragment.BaseFragmentManager;
import com.jb.goscanner.function.bean.CardInfo;
import com.jb.goscanner.function.bean.ContactInfo;
import com.jb.goscanner.function.bean.DetailItem;
import com.jb.goscanner.function.fragment.MainFragment;
import com.jb.goscanner.function.fragment.MainFragmentManager;
import com.jb.goscanner.function.sqlite.ContactDBUtils;
import com.jb.goscanner.function.sqlite.ContactDetailDBUtils;
import com.jb.goscanner.network.CardInterface;
import com.jb.goscanner.util.ContactHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseFragmentActivity {

    public static final String LOG_TAG = "MainActivity";
    private MainFragmentManager mFragmentManager;
    private TakePictureBroadcastReceiver mBroadReceiver;
    private RxPermissions mRxPermissions;
    private ProgressDialog mProgressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();



    }

    @Override
    protected BaseFragmentManager createBaseFragmentManager() {
        mFragmentManager = new MainFragmentManager(this);
        return mFragmentManager;
    }


    private void initView() {
        startFragment(MainFragment.class, null);

        mRxPermissions = new RxPermissions(this);
        //开启权限
        mRxPermissions.request(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        //Toast.makeText(MainActivity.this, "请手动赋予权限，否则无法正常使用", Toast.LENGTH_LONG).show();
                        // At least one permission is denied
                        // All requested permissions are granted
                        ContactDBUtils contactInstance = ContactDBUtils.getInstance(this);
                        ContactDetailDBUtils detailInstance = ContactDetailDBUtils.getInstance(this);
                        ContactHelper contactHelper = ContactHelper.getInstance();
                        List<ContactInfo> contacts = contactHelper.getContacts(this);
                        Log.i("onCreate" , contacts.size()+"");
                        List<ContactInfo> contactInfos = contactInstance.queryExistContact();
                        Log.i("contactInfos" , contactInfos.size()+"");
                        if(contactInfos.size() < contacts.size()){
                            for (ContactInfo contactInfo : contacts){
                                contactInstance.insertContact(contactInfo);
                                for(DetailItem detailItem:contactInfo.getPhone()){
                                    detailInstance.insertDetail(detailItem);
                                }

                            }
                        }

                    }
                });
    }

    private void setListener() {
        mBroadReceiver = new TakePictureBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("take_picture");
        registerReceiver(mBroadReceiver, intentFilter);
    }

    public class TakePictureBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            String qrcode_result = intent.getStringExtra("qrcode_result");
            Log.i("qrcode_result",qrcode_result);
            if(qrcode_result!=null && !qrcode_result.isEmpty()) {
                //DetailItem detailItem = DetailItem.parseToDetailItem(qrcode_result);
                if(qrcode_result.contains("{")){
                    ContactInfo detailItem = ContactInfo.parseToContactInfo(qrcode_result);
                    Log.i("qrcode",detailItem.toString());
                    //是否添加
                    showNormalDialog(detailItem);
                }

            }


            if(path!=null && !path.isEmpty()){
                new CardInterface().getCardInfo(path, new CardInterface.IUploadListener() {
                    @Override
                    public void uploadStart() {
                        Intent takePictureIntent = new Intent();
                        takePictureIntent.setAction("getInfoFailure");
                        sendBroadcast(takePictureIntent);
                    }

                    @Override
                    public void uploadFailure(String error) {
                        Intent takePictureIntent = new Intent();
                        takePictureIntent.setAction("getInfoFailure");
                        sendBroadcast(takePictureIntent);
                        Toast.makeText(MainActivity.this, "识别名片失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void uploadSuccess(CardInfo.ResultBean bean) {
                        Intent takePictureIntent = new Intent();
                        takePictureIntent.setAction("getInfoFailure");
                        sendBroadcast(takePictureIntent);
                        ContactInfo info = new ContactInfo();
                        if (bean.getName() != null && bean.getName().size() > 0 && !TextUtils.isEmpty(bean.getName().get(0))) {
                            info.setName(bean.getName().get(0));
                        }
                        if (bean.getTel() != null && bean.getTel().size() > 0 && !TextUtils.isEmpty(bean.getTel().get(0))) {
                            info.setName(bean.getTel().get(0));
                        }
                        Intent intent = new Intent(MainActivity.this, RecordDetailActivity.class);
                        intent.putExtra(RecordDetailActivity.EXTRA_CONTACT_INFO, info);
                        startActivity(intent);
                    }
                });
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String qrcode_result = data.getStringExtra("qrcode_result");
        Toast.makeText(MainActivity.this,qrcode_result,Toast.LENGTH_LONG).show();
        if (resultCode == 0) {

//            switch (requestCode) {
//                case 0:
//
//                break;
//            }
        }
    }


    private void showNormalDialog(ContactInfo detailItem){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_created_save);
        normalDialog.setTitle("添加用户");
        normalDialog.setMessage("添加用户："+detailItem.getName());
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                        ArrayList<DetailItem> email = detailItem.getEmail();

                        ArrayList<DetailItem> other = detailItem.getOther();

                        ArrayList<DetailItem> phone = detailItem.getPhone();

                        ArrayList<DetailItem> wechat = detailItem.getWechat();
                            ContactDBUtils.getInstance(MainActivity.this).insertContact(detailItem);
                            ContactDetailDBUtils.getInstance(MainActivity.this).deleteDetailByContactId(detailItem.getId());
                            if(email != null && email.size() > 0){
                                for (int i = 1; i < email.size(); i++) {
                                    if(email.get(i).getId().length()>0){
                                        ContactDetailDBUtils.getInstance(MainActivity.this).insertDetail(email.get(i));
                                    }

                                }
                            }
                        if(other != null && other.size() > 0){
                            for (int i = 1; i < other.size(); i++) {
                                if(other.get(i).getId().length()>0)
                                ContactDetailDBUtils.getInstance(MainActivity.this).insertDetail(other.get(i));
                            }
                        }
                        if(phone != null && phone.size() > 0){
                            for (int i = 1; i < phone.size(); i++) {
                                if(phone.get(i).getId().length()>0)
                            ContactDetailDBUtils.getInstance(MainActivity.this).insertDetail(phone.get(i));
                        }
                        }
                        if(wechat != null && wechat.size() > 0){
                            for (int i = 1; i < wechat.size(); i++) {
                                if(wechat.get(i).getId().length()>0)
                                ContactDetailDBUtils.getInstance(MainActivity.this).insertDetail(wechat.get(i));
                            }
                        }



                        Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_LONG).show();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Toast.makeText(MainActivity.this,"取消添加",Toast.LENGTH_LONG).show();
                    }
                });
        // 显示
        normalDialog.show();
    }


   
}

