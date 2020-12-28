package com.jb.goscanner.function.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.goscanner.R;
import com.jb.goscanner.base.fragment.BaseFragment;
import com.jb.goscanner.function.bean.ContactBean;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 *
 * 联系人详情卡片
 * Created by panruijie on 2017/9/10.
 * Email : zquprj@gmail.com
 */

public class CardFragment extends BaseFragment {

    public static final String EXTRA_CONTACT_BEAN = "extra_contact_bean";
    private ContactBean mBean;
    private View mRootView;
    private ImageView mPersonImg;
    private TextView mName;
    private TextView mRemarks;
    private ImageView mQrCode;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mWechat;
    private TextView mAddress;
    private Button mCopyButton;

    ClipboardManager myClipboard;





    public static CardFragment newInstance(ContactBean bean) {
        CardFragment instance = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CONTACT_BEAN, bean);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myClipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        mBean = getArguments() != null ? (ContactBean) getArguments().getSerializable(EXTRA_CONTACT_BEAN) : null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_card, container, false);
        mPersonImg = (ImageView) mRootView.findViewById(R.id.card_person_img);
        mName = (TextView) mRootView.findViewById(R.id.card_name);
        mRemarks = (TextView) mRootView.findViewById(R.id.card_remarks);
        mQrCode = (ImageView) mRootView.findViewById(R.id.card_qrcode);
        mPhone = (TextView) mRootView.findViewById(R.id.card_phone);
        mEmail = (TextView) mRootView.findViewById(R.id.card_email);
        mWechat = (TextView) mRootView.findViewById(R.id.card_wechat);
        mAddress = (TextView) mRootView.findViewById(R.id.card_address);
        mCopyButton = (Button) mRootView.findViewById(R.id.card_copy_button);

        initData();
        setListener();


        return mRootView;
    }

    private void initData() {
        if (!TextUtils.isEmpty(mBean.getName())) {
            mName.setText(mBean.getName());
        }
        if (!TextUtils.isEmpty(mBean.getRemark())) {
            mRemarks.setText(mBean.getRemark());
        }
        if (mBean.getPhone() != null
                && mBean.getPhone().size() > 0
                && mBean.getPhone().get(0) != null
                && mBean.getPhone().get(0).getValue() != null) {
            mPhone.setText(mBean.getPhone().get(0).getValue());
        }
        if (mBean.getWechat() != null
                && mBean.getWechat().size() > 0
                && mBean.getWechat().get(0) != null
                && mBean.getWechat().get(0).getValue() != null) {
            mWechat.setText(mBean.getWechat().get(0).getValue());
        }
        if (mBean.getEmail() != null
                && mBean.getEmail().size() > 0
                && mBean.getEmail().get(0) != null
                && mBean.getEmail().get(0).getValue() != null) {
            mEmail.setText(mBean.getEmail().get(0).getValue());
        }
    }

    private void setListener() {
        mCopyButton.setOnClickListener(v -> {
                //获取粘贴代码
            Intent intent = new Intent();
            intent.putExtra("mBean",mBean);
            ClipData copy = ClipData.newIntent("copy", intent);
            myClipboard.setPrimaryClip(copy);
            Toast.makeText(getActivity(),"复制成功",Toast.LENGTH_LONG).show();
        });

    }

}
