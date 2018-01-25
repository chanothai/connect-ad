package com.company.zicure.campusconnect.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.LoginActivity;
import com.company.zicure.campusconnect.activity.MainMenuActivity;
import com.company.zicure.campusconnect.customView.LabelView;
import com.company.zicure.campusconnect.modelview.Item;
import com.company.zicure.campusconnect.network.request.ProfileRequest;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.List;

import gallery.zicure.company.com.modellibrary.utilize.ModelCart;
import gallery.zicure.company.com.modellibrary.utilize.VariableConnect;

/**
 * Created by macintosh on 19/1/18.
 */

class MenuCategoryViewHolderWithoutChild extends RecyclerView.ViewHolder {
    public LabelView menuChild;
    public ImageView menuIcon;

    public MenuCategoryViewHolderWithoutChild(View itemView) {
        super(itemView);
        menuChild = itemView.findViewById(R.id.menu_child);
        menuIcon = itemView.findViewById(R.id.menu_icon);
    }
}

class MenuCategoryViewHolderWithChild extends RecyclerView.ViewHolder {
    public ImageView menuIcon, menuArrow;
    public LabelView subTitle, menuChild;
    public RelativeLayout btnExpanded;
    public ExpandableLinearLayout expandableLayout;

    public MenuCategoryViewHolderWithChild(View itemView) {
        super(itemView);
        menuIcon = itemView.findViewById(R.id.menu_icon);
        menuArrow = itemView.findViewById(R.id.menu_arrow);
        subTitle = itemView.findViewById(R.id.sub_title);
        menuChild = itemView.findViewById(R.id.menu_child);
        btnExpanded = itemView.findViewById(R.id.btn_expanded);
        expandableLayout = itemView.findViewById(R.id.expandable_layout);
    }
}
public class MenuCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private String code = null;
    private List<Item> arrItems = null;
    private Context context = null;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    public MenuCategoryAdapter(List<Item> arrItems) {
        this.arrItems = arrItems;

        for (int i = 0; i < arrItems.size(); i++) {
            expandState.append(i, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrItems.get(position).isExpandable()){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == 0) { //Without Item
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.model_menu_category_without_child, parent, false);
            return new MenuCategoryViewHolderWithoutChild(view);
        }else{
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.model_menu_category_with_child, parent, false);
            return new MenuCategoryViewHolderWithChild(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            case 0:{
                MenuCategoryViewHolderWithoutChild viewHolder = (MenuCategoryViewHolderWithoutChild) holder;
                Item item = arrItems.get(position);
                viewHolder.setIsRecyclable(false);

                viewHolder.menuChild.setText(item.getMenu().get(0));

                viewHolder.menuIcon.setImageResource(R.drawable.out_icon);
                viewHolder.menuChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Sign out", Toast.LENGTH_SHORT).show();
                        clearData();
                    }
                });
            }
            break;
            case 1:{
                final MenuCategoryViewHolderWithChild viewHolder = (MenuCategoryViewHolderWithChild) holder;
                Item item = arrItems.get(position);
                viewHolder.setIsRecyclable(false);
                viewHolder.subTitle.setText(item.getText());
                viewHolder.menuIcon.setImageResource(R.drawable.language_icon);

                viewHolder.expandableLayout.setInRecyclerView(true);
                viewHolder.expandableLayout.setExpanded(expandState.get(position));

                viewHolder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
                    @Override
                    public void onPreOpen() {
                        changeRotate(viewHolder.menuArrow, 0f, 90f).start();
                        expandState.put(position, true);
                    }

                    @Override
                    public void onPreClose() {
                        changeRotate(viewHolder.menuArrow, 90f, 0f).start();
                        expandState.put(position, true);
                    }
                });

                viewHolder.menuArrow.setRotation(expandState.get(position)?90f:0f);

                viewHolder.btnExpanded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Expandable child item
                        viewHolder.expandableLayout.toggle();
                    }
                });

                code = arrItems.get(position).getSubText().get(0).getCode();

                // issue
                viewHolder.menuChild.setText(arrItems.get(position).getSubText().get(0).getValue());

                viewHolder.menuChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, code, Toast.LENGTH_SHORT).show();
                        ((MainMenuActivity) context).showLoadingDialog();
                        ProfileRequest profileRequest = new ProfileRequest(context);
                        profileRequest.requestProfile(code);

                        ((MainMenuActivity)context).setToggle(0,0);
                    }
                });
            }

            break;
            default:{
                break;
            }
        }
    }

    private void clearData(){
        SharedPreferences sharedPref = context.getSharedPreferences(VariableConnect.keyFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        ModelCart.getInstance().clearAllData();

        ((MainMenuActivity) context).openActivity(LoginActivity.class, true);
    }

    private ObjectAnimator changeRotate(ImageView button, float from, float to){
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to);
        animator.setDuration(100);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        return arrItems.size();
    }
}
