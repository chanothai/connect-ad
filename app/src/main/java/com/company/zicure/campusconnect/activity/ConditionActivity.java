package com.company.zicure.campusconnect.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.customView.ButtonView;
import com.company.zicure.campusconnect.fragment.AppMenuFragment;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

public class ConditionActivity extends BaseActivity implements View.OnClickListener {

    /** Make: View **/
    ButtonView btnConfirm;

    /** Make: Properties **/
    private String url = "http://connect01.pakgon.com/app/webroot/agreement/agreement.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        bindView();

        if (savedInstanceState == null){
            initCondition();
        }
    }

    private void bindView(){
        btnConfirm = (ButtonView) findViewById(R.id.btn_confirm_condition);
        btnConfirm.setOnClickListener(this);
    }

    private void initCondition(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, AppMenuFragment.newInstance(url), VariableConnect.appMenuFragmentKey);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
