package com.company.zicure.campusconnect.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.fragment.AddFriendFragment;

import com.company.zicure.campusconnect.common.BaseActivity;
import com.company.zicure.campusconnect.utility.VariableConnect;

public class ScanQRCodeActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        if (savedInstanceState == null) {
            showLoadingDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, AddFriendFragment.newInstance("", ""), VariableConnect.addFriendFragmentKey);
                    transaction.commit();
                    dismissDialog();
                }
            }, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
