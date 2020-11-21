package com.hsp.resource.widget.drop;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @Description: 未读数红点View（自绘红色的圆和数字）
 * 触摸之产生DOWN/MOVE/UP事件（不允许父容器处理TouchEvent），回调给浮在上层的DropCover进行拖拽过程绘制。
 * View启动过程：Constructors -> onAttachedToWindow -> onMeasure() -> onSizeChanged() -> onLayout() -> onDraw()
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/10/21 11:39
 * @UpdateRemark:
 */
public class DropFake extends View {

    private final Context mContext;

    /**
     * 未读数红点检测触摸事件产生DOWN/MOVE/UP
     */
    public interface ITouchListener {
        void onDown();

        void onMove(float curX, float curY);

        void onUp();
    }

    private int radius; // 圆形半径
    private float circleX; // 圆心x坐标
    private float circleY; // 圆心y坐标
    private String text; // 要显示的文本（数字）
    private boolean firstInit = true; // params init once
    private ITouchListener touchListener;

    public DropFake(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        DropManager.getInstance(mContext).initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (firstInit) {
            firstInit = false;
            radius = DropManager.getInstance(mContext).getCircleRadius(); // 或者view.getWidth()/2
            circleX = w / 2;
            circleY = h / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // circle
        canvas.drawCircle(circleX, circleY, radius, DropManager.getInstance(mContext).getCirclePaint());
        // text
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(text, circleX, circleY + DropManager.getInstance(mContext).getTextYOffset(),
                    DropManager.getInstance(mContext).getTextPaint());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果未初始化 DropManager，则默认任何事件都不处理
        if (!DropManager.getInstance(mContext).isEnable()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (DropManager.getInstance(mContext).isTouchable()) {
                    if (touchListener != null) {
                        DropManager.getInstance(mContext).setTouchable(false);
                        // 不允许父控件处理TouchEvent，当父控件为ListView这种本身可滑动的控件时必须要控制
                        disallowInterceptTouchEvent(true);
                        touchListener.onDown();
                    }
                    return true; // eat
                }

                return false;
            case MotionEvent.ACTION_MOVE:
                if (touchListener != null) {
                    // getRaw:获取手指当前所处的相对整个屏幕的坐标
                    touchListener.onMove(event.getRawX(), event.getRawY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touchListener != null) {
                    // 将控制权还给父控件
                    disallowInterceptTouchEvent(false);
                    touchListener.onUp();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setTouchListener(ITouchListener listener) {
        touchListener = listener;
    }

    private void disallowInterceptTouchEvent(boolean disable) {
        ViewGroup parent = (ViewGroup) getParent();
        parent.requestDisallowInterceptTouchEvent(disable);

        while (true) {
            if (parent == null) {
                return;
            }

            if (parent instanceof RecyclerView || parent instanceof ListView || parent instanceof GridView ||
                    parent instanceof ScrollView) {
                parent.requestDisallowInterceptTouchEvent(disable);
                return;
            }

            ViewParent vp = parent.getParent();
            if (vp instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                return; // DecorView
            }
        }
    }
}
