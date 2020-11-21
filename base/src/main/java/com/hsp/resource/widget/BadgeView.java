package com.hsp.resource.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TabWidget;

import androidx.appcompat.widget.AppCompatTextView;

import com.hsp.resource.R;


/**
 * Description: 红点(文字)提示View
 * <p>
 * Author：reese
 * <p>
 * CreateTime：2019-12-23
 */
public class BadgeView extends AppCompatTextView {
    public static final int POSITION_TOP_LEFT = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    public static final int POSITION_BOTTOM_LEFT = 3;
    public static final int POSITION_BOTTOM_RIGHT = 4;
    public static final int POSITION_CENTER = 5;
    private static final int DEFULT_OVAL_DIAMETER = 18;

    private static final int DEFAULT_MARGIN_DIP = 5;
    private static final int DEFAULT_SM_PADDING_DIP = 3;
    private static final int DEFAULT_LR_PADDING_DIP = 5;
    private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
    private static final int DEFAULT_POSITION = POSITION_TOP_RIGHT;
    private static final int DEFAULT_BADGE_COLOR = Color.parseColor("#EA5502");
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private static Animation fadeIn;
    private static Animation fadeOut;

    private Context context;
    private View target;

    private int badgePosition;
    private int badgeMarginH;
    private int badgeMarginV;
    private int badgeColor;

    private boolean isShown;

    private ShapeDrawable badgeBg;

    private int targetTabIndex;
    /**
     * 是否是角标(只显示红点不显示数字),默认显示数字
     */
    private boolean isAngle = false;

    public BadgeView(Context context) {
        this(context, (AttributeSet) null, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    /**
     * Constructor -
     * <p>
     * create a new BadgeView instance attached to a target {@link View}.
     *
     * @param context context for this view.
     * @param target  the View to attach the badge to.
     */
    public BadgeView(Context context, View target) {
        this(context, null, android.R.attr.textViewStyle, target, 0);
    }

    /**
     * Constructor -显示圆点提示，无数字显示
     * <p>
     * create a new BadgeView instance attached to a target {@link View}.
     *
     * @param context context for this view.
     * @param target  the View to attach the badge to.
     */
    public BadgeView(Context context, View target, boolean isAngle) {
        this(context, null, android.R.attr.textViewStyle, target, isAngle);
    }

    /**
     * Constructor -
     * <p>
     * create a new BadgeView instance attached to a target {@link TabWidget}
     * tab at a given index.
     *
     * @param context context for this view.
     * @param target  the TabWidget to attach the badge to.
     * @param index   the position of the tab within the target.
     */
    public BadgeView(Context context, TabWidget target, int index) {
        this(context, null, android.R.attr.textViewStyle, target, index);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex) {
        super(context, attrs, defStyle);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeView);
        isAngle = array.getBoolean(R.styleable.BadgeView_isAngle, false);
        array.recycle();
        init(context, target, tabIndex);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, boolean isAngle) {
        super(context, attrs, defStyle);
        this.isAngle = isAngle;
        init(context, target, -1);
    }

    private void init(Context context, View target, int tabIndex) {

        this.context = context;
        this.target = target;
        this.targetTabIndex = tabIndex;

        // apply defaults
        badgePosition = DEFAULT_POSITION;
        badgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
        badgeMarginV = badgeMarginH;
        badgeColor = DEFAULT_BADGE_COLOR;

        // 显示红点附带文本
        if (!isAngle) {
            setTypeface(Typeface.DEFAULT_BOLD);
            setTextColor(DEFAULT_TEXT_COLOR);
            int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
            setPadding(paddingPixels, 0, paddingPixels, 0);
        } else { // 显示角标
            int pad = dipToPixels(3);
            setPadding(pad, pad, pad, pad);
            setTextSize(3f);
        }

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(200);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);

        isShown = false;

        if (this.target != null) {
            applyTo(this.target);
        } else {
            show();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isAngle) {
            setMeasuredDimension(DEFULT_OVAL_DIAMETER, DEFULT_OVAL_DIAMETER);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void applyTo(View target) {
        ViewGroup.LayoutParams lp = target.getLayoutParams();
        ViewParent parent = target.getParent();
        FrameLayout container = new FrameLayout(context);

        if (target instanceof TabWidget) {
            // set target to the relevant tab child container
            target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
            this.target = target;

            ((ViewGroup) target).addView(container,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            this.setVisibility(View.GONE);
            container.addView(this);

        } else {
            // verify that parent is indeed a ViewGroup
            ViewGroup group = (ViewGroup) parent;
            int index = group.indexOfChild(target);
            // 先把目标View从父容器中删除掉
            group.removeView(target);
            // 将新new出来的FrameLayout添加到原来目标View的位置替换到
            group.addView(container, index, lp);
            // 再目标View添加到new出来的FrameLayout中
            container.addView(target);

            this.setVisibility(View.GONE);
            // 再讲当前BadgeView添加到FrameLayout中
            container.addView(this);

            group.invalidate(); // 触发绘制
        }
    }

    /**
     * Make the badge visible in the UI.
     */
    public void show() {
        show(false, null);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param animate flag to apply the default fade-in animation.
     */
    public void show(boolean animate) {
        show(animate, fadeIn);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param anim Animation to apply to the view when made visible.
     */
    public void show(Animation anim) {
        show(true, anim);
    }

    /**
     * Make the badge non-visible in the UI.
     */
    public void hide() {
        hide(false, null);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param animate flag to apply the default fade-out animation.
     */
    public void hide(boolean animate) {
        hide(animate, fadeOut);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param anim Animation to apply to the view when made non-visible.
     */
    public void hide(Animation anim) {
        hide(true, anim);
    }

    /**
     * Toggle the badge visibility in the UI.
     */
    public void toggle() {
        toggle(false, null, null);
    }

    /**
     * Toggle the badge visibility in the UI.
     *
     * @param animate flag to apply the default fade-in/out animation.
     */
    public void toggle(boolean animate) {
        toggle(animate, fadeIn, fadeOut);
    }

    /**
     * Toggle the badge visibility in the UI.
     *
     * @param animIn  Animation to apply to the view when made visible.
     * @param animOut Animation to apply to the view when made non-visible.
     */
    public void toggle(Animation animIn, Animation animOut) {
        toggle(true, animIn, animOut);
    }

    private void show(boolean animate, Animation anim) {
        if (getBackground() == null) {
            if (badgeBg == null) {
                badgeBg = getDefaultBackground();
            }
            setBackground(badgeBg);
        }

        applyLayoutParams();
        if (animate) {
            this.startAnimation(anim);
        }
        this.setVisibility(View.VISIBLE);
        isShown = true;
    }

    private void hide(boolean animate, Animation anim) {
        this.setVisibility(View.GONE);
        if (animate) {
            this.startAnimation(anim);
        }
        isShown = false;
    }

    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        if (isShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    /**
     * Increment the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     *
     * @param offset the increment offset.
     */
    public int increment(int offset) {
        CharSequence txt = getText();
        int i;
        if (txt != null) {
            try {
                i = Integer.parseInt(txt.toString());
            } catch (NumberFormatException e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        i = i + offset;
        setText(String.valueOf(i));
        return i;
    }

    /**
     * Decrement the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     *
     * @param offset the decrement offset.
     */
    public int decrement(int offset) {
        return increment(-offset);
    }

    private ShapeDrawable getDefaultBackground() {
        ShapeDrawable drawable = null;
        // 绘制常规的椭圆实现显示数字个数
        if (!isAngle) {
            int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
            float[] outerR = new float[]{r, r, r, r, r, r, r, r};
            RoundRectShape rr = new RoundRectShape(outerR, null, null);
            drawable = new ShapeDrawable(rr);

        } else { // 只是绘制提示红点
            OvalShape ovalShape = new OvalShape();
            drawable = new ShapeDrawable(ovalShape);
            drawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        }
        drawable.getPaint().setColor(badgeColor);

        return drawable;
    }

    private void applyLayoutParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        switch (badgePosition) {
            case POSITION_TOP_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.TOP;
                lp.setMargins(badgeMarginH, badgeMarginV, 0, 0);
                break;
            case POSITION_TOP_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                lp.setMargins(0, badgeMarginV, badgeMarginH, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                lp.setMargins(badgeMarginH, 0, 0, badgeMarginV);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                lp.setMargins(0, 0, badgeMarginH, badgeMarginV);
                break;
            case POSITION_CENTER:
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0, 0, 0, 0);
                break;
            default:
                break;
        }

        setLayoutParams(lp);

    }

    /**
     * Returns the target View this badge has been attached to.
     */
    public View getTarget() {
        return target;
    }

    /**
     * Is this badge currently visible in the UI?
     */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /**
     * Returns the positioning of this badge.
     * <p>
     * one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
     */
    public int getBadgePosition() {
        return badgePosition;
    }

    /**
     * Set the positioning of this badge.
     *
     * @param layoutPosition one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
     */
    public void setBadgePosition(int layoutPosition) {
        this.badgePosition = layoutPosition;
    }

    /**
     * Returns the horizontal margin from the target View that is applied to this badge.
     */
    public int getHorizontalBadgeMargin() {
        return badgeMarginH;
    }

    /**
     * Returns the vertical margin from the target View that is applied to this badge.
     */
    public int getVerticalBadgeMargin() {
        return badgeMarginV;
    }

    /**
     * Set the horizontal/vertical margin from the target View that is applied to this badge.
     *
     * @param badgeMargin the margin in pixels.
     */
    public void setBadgeMargin(int badgeMargin) {
        this.badgeMarginH = badgeMargin;
        this.badgeMarginV = badgeMargin;
    }

    /**
     * Set the horizontal/vertical margin from the target View that is applied to this badge.
     *
     * @param horizontal margin in pixels.
     * @param vertical   margin in pixels.
     */
    public void setBadgeMargin(int horizontal, int vertical) {
        this.badgeMarginH = horizontal;
        this.badgeMarginV = vertical;
    }

    /**
     * Returns the color value of the badge background.
     */
    public int getBadgeBackgroundColor() {
        return badgeColor;
    }

    /**
     * Set the color value of the badge background.
     *
     * @param badgeColor the badge background color.
     */
    public void setBadgeBackgroundColor(int badgeColor) {
        this.badgeColor = badgeColor;
        badgeBg = getDefaultBackground();
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 设置需要显示的内容--外观多于默认圆润度
     *
     * @param text
     */
    public void setCount(CharSequence text) {
        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        if (!TextUtils.isEmpty(text)) {
            if (text.length() > 1) {
                setPadding(paddingPixels, 0, paddingPixels, 0);
            } else {
                // 计算默认TextView的上下padding在内
                setPadding(paddingPixels, 2, paddingPixels, 2);
            }
        } else {
            text = "";
        }
        super.setText(text, BufferType.NORMAL);
    }
}
