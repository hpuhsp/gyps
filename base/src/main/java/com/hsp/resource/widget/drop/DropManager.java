package com.hsp.resource.widget.drop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;

import com.hsp.resource.R;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/10/21 11:57
 * @UpdateRemark:
 */
public class DropManager {

    // constant
    private static final String TAG = "DropManager";

    private int TEXT_SIZE = 12; // 默认设置 12sp
    private int CIRCLE_RADIUS = 10; // 默认设置 10dip

    public interface IDropListener {
        void onDropBegin();

        void onDropEnd();
    }

    // single instance
    private static DropManager instance;

    public static synchronized DropManager getInstance(Context context) {
        if (instance == null) {
            instance = new DropManager();
            instance.initSize(context);
        }

        return instance;
    }

    public int getCircleRadius() {
        return CIRCLE_RADIUS;
    }

    private void initSize(Context context) {
        TEXT_SIZE = sp2px(context, 10);
        CIRCLE_RADIUS = sp2px(context, 9);
    }

    // field
    private boolean isTouchable; // 是否响应按键事件，如果一个红点已经在响应，其它红点就不响应，同一界面始终最多只有一个红点响应触摸

    private int statusBarHeight; // 状态栏(通知栏)高度

    private DropCover dropCover; // Drop全屏动画

    private Object currentId; // 当前正在执行动画的业务节点

    private TextPaint textPaint; // 文本画笔共享

    private float textYOffset; // 文本y轴居中需要的offset

    private Paint circlePaint; // 圆形画笔共享

    private IDropListener listener; // 红点拖拽动画监听器

    private boolean enable;
    private int[] explosionResIds = new int[]{ // 爆裂的动画效果
            R.drawable.nim_explosion_one,
            R.drawable.nim_explosion_two,
            R.drawable.nim_explosion_three,
            R.drawable.nim_explosion_four,
            R.drawable.nim_explosion_five
    };

    // interface
    public void init(Context context, DropCover dropCover, DropCover.IDropCompletedListener listener, int topDistance) {
        this.isTouchable = true;
        this.statusBarHeight = topDistance;
        this.dropCover = dropCover;
        this.dropCover.addDropCompletedListener(listener);
        this.listener = null;
        this.enable = true;
    }

    public void initPaint() {
        getCirclePaint();
        getTextPaint();
    }

    public void destroy() {
        this.isTouchable = false;
        this.statusBarHeight = 0;
        if (this.dropCover != null) {
            this.dropCover.removeAllDropCompletedListeners();
            this.dropCover = null;
        }
        this.currentId = null;
        this.textPaint = null;
        this.textYOffset = 0;
        this.circlePaint = null;
        this.enable = false;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isTouchable() {
        if (!enable) {
            return true;
        }
        return isTouchable;
    }

    public void setTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;

        if (listener != null) {
            if (!isTouchable) {
                listener.onDropBegin(); // touchable = false
            } else {
                listener.onDropEnd(); // touchable = true
            }
        }
    }

    public int getTop() {
        return statusBarHeight;
    }

    public void down(View fakeView, String text) {
        if (dropCover == null) {
            return;
        }

        dropCover.down(fakeView, text);
    }

    public void move(float curX, float curY) {
        if (dropCover == null) {
            return;
        }

        dropCover.move(curX, curY);
    }

    public void up() {
        if (dropCover == null) {
            return;
        }

        dropCover.up();
    }

    public void addDropCompletedListener(DropCover.IDropCompletedListener listener) {
        if (dropCover != null) {
            dropCover.addDropCompletedListener(listener);
        }
    }

    public void removeDropCompletedListener(DropCover.IDropCompletedListener listener) {
        if (dropCover != null) {
            dropCover.removeDropCompletedListener(listener);
        }
    }

    public void setCurrentId(Object currentId) {
        this.currentId = currentId;
    }

    public Object getCurrentId() {
        return currentId;
    }

    public Paint getCirclePaint() {
        if (circlePaint == null) {
            circlePaint = new Paint();
            circlePaint.setColor(Color.parseColor("#F15660")); // 红色背景
            circlePaint.setAntiAlias(true);
        }

        return circlePaint;
    }

    public TextPaint getTextPaint() {
        if (textPaint == null) {
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE); // 白色字体
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(TEXT_SIZE);
            Paint.FontMetrics textFontMetrics = textPaint.getFontMetrics();

            /*
             * drawText从baseline开始，baseline的值为0，baseline的上面为负值，baseline的下面为正值，
             * 即这里ascent为负值，descent为正值。
             * 比如ascent为-20，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2
             */
            textYOffset = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2;
        }

        return textPaint;
    }

    /**
     * sp 转 px
     *
     * @param context [Context]
     * @param spValue `spValue`
     * @return `pxValue`
     */
    private static int sp2px(@NonNull Context context, int spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public float getTextYOffset() {
        getTextPaint();
        return textYOffset;
    }

    public int[] getExplosionResIds() {
        return explosionResIds;
    }

    public void setDropListener(IDropListener listener) {
        this.listener = listener;
    }
}
