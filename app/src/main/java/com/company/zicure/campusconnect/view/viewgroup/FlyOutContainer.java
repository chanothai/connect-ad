package com.company.zicure.campusconnect.view.viewgroup;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.company.zicure.campusconnect.R;

public class FlyOutContainer extends LinearLayout {

	// References to groups contained in this view.
	private View menu = null;
	private View content = null;

	//View
	private FrameLayout layoutGhost = null;
	private CoordinatorLayout coordinatorLayout = null;


	// Layout Constants
	protected static final int menuMargin = 150;
	private int marginIn = 20;
	private double limitSpeedTouch = 100;

	private static int currentWidth = 0;

	public enum MenuState {
		CLOSED, OPEN
	}


	// Position information attributes
	protected int currentContentOffset = 0;
	public static MenuState menuCurrentState = null;

	private int animationDuration = 300;


	public FlyOutContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlyOutContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlyOutContainer(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		menuCurrentState = MenuState.CLOSED;
		this.menu = this.getChildAt(0);
		this.content = this.getChildAt(1);

		//bind view
		layoutGhost = content.findViewById(R.id.layout_ghost);
		coordinatorLayout = content.findViewById(R.id.rootLayout);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
							int bottom) {
		if (changed)
			this.calculateChildDimensions();
		this.menu.layout(left, top, right - menuMargin, bottom);
		this.content.layout(left + this.currentContentOffset, top, right + this.currentContentOffset, bottom);
	}

	private void openning(){
		menuCurrentState = MenuState.OPEN; //Content is opening
		layoutGhost.setVisibility(View.VISIBLE);
		this.menu.setVisibility(View.VISIBLE);
	}

	private void closing(){
		menuCurrentState = MenuState.CLOSED;
		layoutGhost.setVisibility(View.GONE);
	}

	public void setAlphaMenu(int value){
		if (menuCurrentState == MenuState.OPEN){

		}

		int newValue = getMenuWidth() - value;
		double d = (double) newValue;
		double result = d / 1000;
		if (result <= 0.1){
			result = 0.1;
		}
	}

	public void setAnimation(int target){
		AnimatorSet animTogether = new AnimatorSet();
		ObjectAnimator animSlideMenu = null;
		if (menuCurrentState == MenuState.OPEN){
			if (target <= getMenuWidth() && target > (getMenuWidth() / 2)){
				if (currentWidth > 0){
					animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, currentWidth, target);
				}else{
					animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, 0, target);
				}
			}else{
				animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, currentWidth, target);
			}

		}
		else if (menuCurrentState == MenuState.CLOSED){
			if (target <= (getMenuWidth() / 2) && target > 0){
				animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, target, 0);
			}else{
				if (currentWidth > 0){
					animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, currentWidth ,target);
				}else{
					animSlideMenu = ObjectAnimator.ofFloat(content, View.TRANSLATION_X, getMenuWidth() ,target);
				}
			}
			setMarginLayout(0);
		}

		animTogether.play(animSlideMenu);
		animTogether.setDuration(animationDuration);
		animTogether.setInterpolator(new DecelerateInterpolator());
		animTogether.start();
	}

	private void setMarginLayout(int left){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coordinatorLayout.getLayoutParams();
		params.leftMargin = left;
		coordinatorLayout.setLayoutParams(params);
	}

	public void toggleMenu(int width, double speedTouch) {
		switch (menuCurrentState) {
			case CLOSED:
				if (width <= (getMenuWidth()/ 2) && width > 0){
					currentWidth = width;
					if (speedTouch > limitSpeedTouch){
						openning();
						setAnimation(getMenuWidth() - marginIn);
					}else{
						setAnimation(0);
						layoutGhost.setVisibility(View.GONE);
					}
				}else{
					openning();
					currentWidth = width;
					adjustContentPosition();
				}

				break;
			case OPEN:
				if (width >= (getMenuWidth() / 2) && width <= getMenuWidth()){
					currentWidth = width;
					if (speedTouch < -100) {
						closing();
						adjustContentPosition();
					}else{
						if (width == getMenuWidth() - marginIn){
							closing();
							adjustContentPosition();
						}else{
							setAnimation(getMenuWidth() - marginIn);
						}
					}
				}else{
					closing();
					currentWidth = width;
					adjustContentPosition();
				}
				break;
			default:
				return;
		}
	}

	private int getMenuWidth() {
		return this.menu.getLayoutParams().width;
	}

	private void calculateChildDimensions() {
		this.content.getLayoutParams().height = this.getHeight();
		this.content.getLayoutParams().width = this.getWidth();

		this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
		this.menu.getLayoutParams().height = this.getHeight();
	}

	private void adjustContentPosition() {
		int scrollerOffset;
		if (currentWidth > 0){
			if (menuCurrentState ==  MenuState.CLOSED){
				scrollerOffset = 0;
			}else{
				int result = (currentWidth + ((getMenuWidth() - marginIn) - currentWidth));
				scrollerOffset = result;
			}
		}else{
			if (menuCurrentState == MenuState.OPEN){
				scrollerOffset = getMenuWidth() - marginIn;
			}else{
				scrollerOffset = 0;
			}
		}

		setAnimation(scrollerOffset);
	}


}
