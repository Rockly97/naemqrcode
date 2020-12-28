package com.jb.goscanner.function.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitmapUtils;
import com.jb.goscanner.R;
import com.jb.goscanner.base.activity.BaseActivity;
import com.jb.goscanner.function.bean.ContactInfo;
import com.jb.goscanner.function.bean.DetailItem;
import com.jb.goscanner.function.sqlite.ContactDBUtils;
import com.jb.goscanner.function.sqlite.ContactDetailDBUtils;
import com.jb.goscanner.util.FileUtil;
import com.jb.goscanner.util.IntentUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by liuyue on 2017/9/9.
 */

public class ShowQRCodeActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_CONTACTINFO = "extra_contact_info";
    private ImageView mQRCodeImageView;
    private LinearLayout mSaveBtn;
    private LinearLayout mShareBtn;
    private TextView mNameText;
    private Bitmap bitmap;
    private ContactInfo info;
    private ContactDBUtils contactDBUtils;
    private ContactDetailDBUtils detailDBUtils;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_show_qrcode);
        contactDBUtils = ContactDBUtils.getInstance(this);
        detailDBUtils = ContactDetailDBUtils.getInstance(this);
        initView();
        info = (ContactInfo)getIntent().getSerializableExtra(EXTRA_CONTACTINFO);
        String contactString = ContactInfo.parseToString(info);
        try {
            bitmap = BitmapUtils.create2DCode(contactString);//根据内容生成二维码
            mQRCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mNameText.setText(info.getName());
    }

    private void initView() {
        mQRCodeImageView = (ImageView)findViewById(R.id.qrcode_imageview);
        mSaveBtn = (LinearLayout)findViewById(R.id.save_qrcode_btn);
        mShareBtn = (LinearLayout)findViewById(R.id.share_qrcode_btn);
        mNameText = (TextView)findViewById(R.id.name_show_text);

        ((ImageView)findViewById(R.id.activity_top_back)).setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_qrcode_btn) {
            //保存二维码
            if(bitmap != null){
                //保存文件问题
//                FileUtil.saveBitmapToSDFile(bitmap,info.getImgUrl(), Bitmap.CompressFormat.JPEG);
                boolean fileFlag = FileUtil.saveBitmapToSDFile(bitmap, this.getFilesDir().getAbsolutePath()+"/"+info.getName()+".jpg");
                if(fileFlag){
                    info.setImgUrl(this.getFilesDir().getAbsolutePath()+"/"+info.getName()+".jpg");
                    ContactDBUtils.getInstance(this).insertContact(info);
                    Toast.makeText(ShowQRCodeActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                    ActivityCompat.finishAfterTransition(ShowQRCodeActivity.this);
                }else {
                    Toast.makeText(ShowQRCodeActivity.this,"保存失败",Toast.LENGTH_LONG).show();

                }
            }
            //保存

        } else if (id == R.id.share_qrcode_btn) {
                //分享二维码
            boolean sdFile = FileUtil.saveBitmapToSDFile(bitmap, this.getFilesDir().getAbsolutePath()+"/"+ info.getName() + ".jpg");
            if(sdFile){
                Log.i("sdFile","二维码");
                Uri imageUri = FileProvider.getUriForFile(
                        ShowQRCodeActivity.this,
                        "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                        new File(this.getFilesDir().getAbsolutePath() + "/" + info.getName() + ".jpg"));
                info.setImgUrl(this.getFilesDir().getAbsolutePath()+"/"+info.getName()+".jpg");
                ContactDBUtils.getInstance(this).insertContact(info);
                //Uri uri = Uri.fromFile(new File(this.getFilesDir().getAbsolutePath() + "/" + info.getName() + ".jpg"));
                Intent intent = IntentUtils.getShareImageIntent("名片二维码", imageUri);
                startActivity(Intent.createChooser(intent,"二维码"));
            }

        } else if (id == R.id.activity_top_back) {
            ActivityCompat.finishAfterTransition(ShowQRCodeActivity.this);
        }
    }
}
