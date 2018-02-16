package com.company.zicure.campusconnect.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.activity.MainMenuActivity;
import com.company.zicure.campusconnect.customView.LabelView;
import com.company.zicure.campusconnect.modelview.Item;
import com.company.zicure.campusconnect.service.BadgeController;
import com.company.zicure.campusconnect.utility.RestoreLogin;

import java.util.List;

import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by macintosh on 19/1/18.
 */

public class MenuCategoryAdapter extends BaseExpandableListAdapter{
    private List<Item> items = null;
    private Context context = null;

    public MenuCategoryAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return items.get(groupPosition).getArrLanguage().size();
            case 1:
                return items.get(groupPosition).getMenus().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                return items.get(groupPosition).getArrLanguage().get(childPosition);
            case 1:
                return items.get(groupPosition).getMenus().get(childPosition);
        }
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (groupPosition) {
                case 0:
                    convertView = inflater.inflate(R.layout.model_menu_category_with_child, null);
                    LabelView header = convertView.findViewById(R.id.header);
                    header.setText(items.get(groupPosition).getHeader());

                    ImageView menuIcon = convertView.findViewById(R.id.menu_icon);
                    menuIcon.setImageResource(R.drawable.language_icon);

                    ImageView menuArrow = convertView.findViewById(R.id.menu_arrow);

                    if (isExpanded) {
                        changeRotate(menuArrow, 0f, 90f).start();
                    }else{
                        changeRotate(menuArrow, 0f, 0f).start();
                    }

                    return convertView;
                case 1:
                    RelativeLayout layout = new RelativeLayout(context);
                    return layout;
            }
        }
        return null;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (groupPosition) {
                case 0:
                    convertView = inflater.inflate(R.layout.model_menu_child_view, null);
                    LabelView child = convertView.findViewById(R.id.text_child_menu);
                    child.setText(items.get(groupPosition).getArrLanguage().get(childPosition).getValue());

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (items.get(groupPosition).getArrLanguage().get(childPosition).getCode().equalsIgnoreCase("TH")) {
                                ModelCart.getInstance().getKeyModel().setLanguage(items.get(groupPosition).getArrLanguage().get(childPosition).getCode());
                                ((MainMenuActivity)context).setLanguage(items.get(groupPosition).getArrLanguage().get(childPosition).getCode());
                            }else if (items.get(groupPosition).getArrLanguage().get(childPosition).getCode().equalsIgnoreCase("EN")){
                                ModelCart.getInstance().getKeyModel().setLanguage(items.get(groupPosition).getArrLanguage().get(childPosition).getCode());
                                ((MainMenuActivity)context).setLanguage(items.get(groupPosition).getArrLanguage().get(childPosition).getCode());
                            }else{
                                ModelCart.getInstance().getKeyModel().setLanguage("EN");
                                ((MainMenuActivity)context).setLanguage("EN");
                            }
                        }
                    });

                    return convertView;
                case 1:
                    convertView = inflater.inflate(R.layout.model_menu_category_without_child, null);
                    LabelView child2 = convertView.findViewById(R.id.menu_child);
                    child2.setText(items.get(groupPosition).getMenus().get(childPosition).getLabel());

                    ImageView menuIcon = convertView.findViewById(R.id.menu_icon);
                    menuIcon.setImageResource(R.drawable.out_icon);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainMenuActivity)context).openActivity(LoginActivity.class, true);
                            RestoreLogin.getInstance(context).clearAllData();
                            BadgeController.getInstance(context).removeBadge();
                        }
                    });
                    return convertView;
            }
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private ObjectAnimator changeRotate(ImageView button, float from, float to){
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to);
        animator.setDuration(100);
        return animator;
    }
}
