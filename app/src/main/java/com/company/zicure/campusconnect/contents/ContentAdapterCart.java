package com.company.zicure.campusconnect.contents;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.BlocContentActivity;
import com.company.zicure.campusconnect.activity.ContactListActivity;
import com.company.zicure.campusconnect.activity.IDCardActivity;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.adapter.MainMenuAdapter;
import com.company.zicure.campusconnect.adapter.SlideMenuAdapter;
import com.company.zicure.campusconnect.holder.MainMenuHolder;
import com.company.zicure.campusconnect.holder.SlideMenuHolder;
import com.company.zicure.campusconnect.interfaces.ItemClickListener;
import com.company.zicure.campusconnect.network.ClientHttp;

import java.util.ArrayList;
import java.util.List;

import gallery.zicure.company.com.modellibrary.common.BaseActivity;
import gallery.zicure.company.com.modellibrary.models.bloc.ResponseBlocUser;
import gallery.zicure.company.com.modellibrary.models.drawer.SlideMenuDetail;
import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

/**
 * Created by 4GRYZ52 on 4/1/2017.
 */

public class ContentAdapterCart {
    private BaseActivity baseActivity = null;
    private Activity atv = null;

    public ContentAdapterCart(){

    }

    public SlideMenuAdapter setSlideMenuAdapter(BaseActivity activity, ArrayList<SlideMenuDetail> arrMenu){
        baseActivity = activity;
        SlideMenuAdapter slideMenuAdapter = new SlideMenuAdapter(activity, arrMenu) {
            @Override
            public void onBindViewHolder(SlideMenuHolder holder, int position) {
                holder.subTitle.setText(getTitle(position));
                holder.imgIcon.setImageResource(getImage(position));
                holder.setItemOnClickListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (getTitle(position).equalsIgnoreCase(baseActivity.getString(R.string.menu_feed_th))){
                            baseActivity.showLoadingDialog();
                            ClientHttp.getInstance(baseActivity).requestUserBloc(ModelCart.getInstance().getKeyModel().getToken());
                        }
                        else if (getTitle(position).equalsIgnoreCase(baseActivity.getString(R.string.logout_menu_th))){
                            SharedPreferences pref = baseActivity.getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();

                            baseActivity.openActivity(LoginActivity.class, true);
                        }
                        else if (getTitle(position).equalsIgnoreCase(baseActivity.getString(R.string.payment_th))) {
                            intentToPayment();
                        }
                    }
                });
            }
        };
        return slideMenuAdapter;
    }

    private void intentToPayment(){
        String authToken = null;
        String strPackage = "com.company.zicure.connectpay";
        try{
            authToken = ModelCart.getInstance().getKeyModel().getToken();
            Intent intent = baseActivity.getPackageManager().getLaunchIntentForPackage(strPackage);
            if (authToken != null){
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, authToken);
            }

            baseActivity.startActivity(intent);
            ModelCart.getInstance().getKeyModel().setAuthToken("");

        }catch (NullPointerException e){
            e.printStackTrace();
            try{
                baseActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + strPackage)));
            }catch (ActivityNotFoundException ef){
                baseActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + strPackage)));
            }
        }
    }

    public MainMenuAdapter setMainMenuAdapter(Activity activity, final List<ResponseBlocUser.ResultBlocUser.DataBloc.UserAccessControl.BlocUser> arrBloc, final int category) {
        atv = activity;
        //set adapter
         MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(activity,arrBloc) {
            //Data is bound to view
            @Override
            public void onBindViewHolder(final MainMenuHolder holder, int position) {
                holder.topicMenu.setText(getData().get(position).getBlocNameTH());
                Glide.with(atv)
                        .load(getData().get(position).getImagePath())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imgBtnMenu);

                holder.setItemOnClickListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (category == 0) {
                            if (position == 1){
                                Bundle bundle = new Bundle();
                                bundle.putString(VariableConnect.TITLE_CATEGORY, atv.getString(R.string.title_activity_id_card_th));
                                Intent intent = new Intent(atv, IDCardActivity.class);
                                intent.putExtras(bundle);

                                atv.startActivity(intent);
                                atv.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_scale_out);
                            }else if (position == 2) {
                                atv.startActivity(new Intent(atv, ContactListActivity.class));
                                atv.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_scale_out);
                            } else{
                                Bundle bundle = new Bundle();
                                bundle.putString(VariableConnect.TITLE_CATEGORY, getData().get(position).getBlocNameTH());

                                if (getData().get(position).getOrderSequence() == 7) {
                                    bundle.putBoolean("status_check_in", true);
                                    bundle.putString(VariableConnect.PATH_BLOC, "");
                                }else{
                                    bundle.putString(VariableConnect.PATH_BLOC, getData().get(position).getBlocURL());
                                }

                                Intent intent = new Intent(atv, BlocContentActivity.class);
                                intent.putExtras(bundle);
                                atv.startActivity(intent);
                                atv.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_scale_out);
                            }
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putString(VariableConnect.TITLE_CATEGORY, getData().get(position).getBlocNameTH());
                            bundle.putString(VariableConnect.PATH_BLOC, getData().get(position).getBlocURL());

                            Intent intent = new Intent(atv, BlocContentActivity.class);
                            intent.putExtras(bundle);
                            atv.startActivity(intent);
                            atv.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_scale_out);
                        }
                    }
                });
            }
        };

        return mainMenuAdapter;
    }
}