package com.hsp.resource

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/12 9:34
 * @UpdateRemark:   更新说明：
 */
class MyDividerItemDecoration constructor(builder: Builder) :
    FlexibleDividerDecoration(builder) {
    private val mMarginProvider: MarginProvider
    private val mIsContainHeadder: Boolean
    private val mIsContainFooter: Boolean
    override fun getDividerBound(
        position: Int,
        parent: RecyclerView,
        child: View
    ): Rect {
        val bounds = Rect(0, 0, 0, 0)
        val transitionX = ViewCompat.getTranslationX(child).toInt()
        val transitionY = ViewCompat.getTranslationY(child).toInt()
        val params = child.layoutParams as RecyclerView.LayoutParams
        bounds.left = parent.paddingLeft + mMarginProvider.dividerLeftMargin(
            position,
            parent
        ) + transitionX
        bounds.right =
            parent.width - parent.paddingRight - mMarginProvider.dividerRightMargin(
                position,
                parent
            ) + transitionX
        val dividerSize = getDividerSize(position, parent)
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.top = child.bottom + params.topMargin + transitionY
            bounds.bottom = bounds.top + dividerSize
        } else {
            bounds.top = child.bottom + params.topMargin + dividerSize / 2 + transitionY
            bounds.bottom = bounds.top
        }
        return bounds
    }

    override fun setItemOffsets(
        outRect: Rect,
        position: Int,
        parent: RecyclerView
    ) {
        val opt = if (mIsContainHeadder) 1 else 0
        if (position < opt) {
            return
        }
        if (mIsContainFooter && position == parent.adapter!!.itemCount - 2) {
            return
        }
        outRect[0, 0, 0] = getDividerSize(position, parent)
    }

    private fun getDividerSize(position: Int, parent: RecyclerView): Int {
        if (mPaintProvider != null) {
            return mPaintProvider.dividerPaint(position, parent).strokeWidth.toInt()
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent)
        } else if (mDrawableProvider != null) {
            val drawable = mDrawableProvider.drawableProvider(position, parent)
            return drawable.intrinsicHeight
        }
        throw RuntimeException("failed to get size")
    }

    /**
     * Interface for controlling divider margin
     */
    interface MarginProvider {
        /**
         * Returns left margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return left margin
         */
        fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return right margin
         */
        fun dividerRightMargin(position: Int, parent: RecyclerView?): Int
    }

    class Builder(context: Context?) :
        FlexibleDividerDecoration.Builder<Builder>(context) {
        var mIsContainHeadder = false
        var mIsContainFooter = false
        var mMarginProvider: MarginProvider =
            object : MarginProvider {
                override fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int {
                    return 0
                }

                override fun dividerRightMargin(position: Int, parent: RecyclerView?): Int {
                    return 0
                }
            }

        fun margin(leftMargin: Int, rightMargin: Int): Builder {
            return marginProvider(object : MarginProvider {
                override fun dividerLeftMargin(position: Int, parent: RecyclerView?): Int {
                    return leftMargin
                }

                override fun dividerRightMargin(position: Int, parent: RecyclerView?): Int {
                    return rightMargin
                }
            })
        }

        fun margin(horizontalMargin: Int): Builder {
            return margin(horizontalMargin, horizontalMargin)
        }

        fun setContainHead(containHead: Boolean): Builder {
            mIsContainHeadder = containHead
            return this
        }

        fun setContainFoot(containFoot: Boolean): Builder {
            mIsContainFooter = containFoot
            return this
        }

        fun marginResId(
            @DimenRes leftMarginId: Int,
            @DimenRes rightMarginId: Int
        ): Builder {
            return margin(
                mResources.getDimensionPixelSize(leftMarginId),
                mResources.getDimensionPixelSize(rightMarginId)
            )
        }

        fun marginResId(@DimenRes horizontalMarginId: Int): Builder {
            return marginResId(horizontalMarginId, horizontalMarginId)
        }

        fun marginProvider(provider: MarginProvider): Builder {
            mMarginProvider = provider
            return this
        }

        fun build(): MyDividerItemDecoration {
            checkBuilderParams()
            return MyDividerItemDecoration(this)
        }
    }

    init {
        mMarginProvider = builder.mMarginProvider
        mIsContainHeadder = builder.mIsContainHeadder
        mIsContainFooter = builder.mIsContainFooter
    }
}