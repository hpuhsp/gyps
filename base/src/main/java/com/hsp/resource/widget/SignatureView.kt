package com.hsp.resource.widget

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import com.hsp.resource.R
import java.io.*
import java.lang.Exception

/**
 * @Description: 手签面板
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/9/25 18:47
 * @UpdateRemark:
 */
class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    //画笔x坐标起点
    private var mPenX = 0f

    //画笔y坐标起点
    private var mPenY = 0f
    private val mPaint = Paint()
    private val mPath = Path()
    private var mCanvas: Canvas? = null
    private var cacheBitmap: Bitmap? = null

    //画笔宽度
    private var mPentWidth = PEN_WIDTH

    //画笔颜色
    private var mPenColor = PEN_COLOR

    //画板颜色
    private var mBackColor = BACK_COLOR
    var touched = false
        private set

    /**
     * 获取保存路径
     */
    var savePath: String? = null
        private set

    private fun init() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mPentWidth.toFloat()
        mPaint.color = mPenColor
    }

    fun setPentWidth(pentWidth: Int) {
        mPentWidth = pentWidth
    }

    fun setPenColor(@ColorInt penColor: Int) {
        mPenColor = penColor
    }

    fun setBackColor(@ColorInt backColor: Int) {
        mBackColor = backColor
    }

    /**
     * 清空签名
     */
    fun clear() {
        if (mCanvas != null) {
            touched = false
            mCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mCanvas!!.drawColor(mBackColor)
            invalidate()
        }
    }

    /**
     * 保存图片
     *
     * @param path 保存的地址
     * @param clearBlank 是否清除空白区域
     * @param blank 空白区域留空距离
     * @throws IOException
     */
    fun save(path: String?, clearBlank: Boolean, blank: Int): String {
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(context, "图片保存路径不可为空~", Toast.LENGTH_SHORT).show()
            return ""
        }
        savePath = path
        try {
            var bitmap = cacheBitmap
            if (clearBlank) {
                bitmap = clearBlank(bitmap, blank)
            }
            val bos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val buffer = bos.toByteArray()
            if (buffer != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
                val os: OutputStream = FileOutputStream(file)
                os.write(buffer)
                os.close()
                bos.close()
            }
            return savePath ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 逐行扫描，清除边界空白
     *
     * @param blank 边界留多少个像素
     */
    private fun clearBlank(bmp: Bitmap?, blank: Int): Bitmap {
        var blank = blank
        val height = bmp!!.height
        val width = bmp.width
        var top = 0
        var left = 0
        var right = 0
        var bottom = 0
        var pixs = IntArray(width)
        var isStop: Boolean
        //扫描上边距不等于背景颜色的第一个点
        for (i in 0 until height) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    top = i
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (i in height - 1 downTo 0) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    bottom = i
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        pixs = IntArray(height)
        //扫描左边距不等于背景颜色的第一个点
        for (x in 0 until width) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    left = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (x in width - 1 downTo 1) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    right = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        if (blank < 0) {
            blank = 0
        }
        //计算加上保留空白距离之后的图像大小
        left = if (left - blank > 0) left - blank else 0
        top = if (top - blank > 0) top - blank else 0
        right = if (right + blank > width - 1) width - 1 else right + blank
        bottom = if (bottom + blank > height - 1) height - 1 else bottom + blank
        return Bitmap.createBitmap(bmp, left, top, right - left, bottom - top)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(cacheBitmap!!)
        mCanvas!!.drawColor(mBackColor)
        touched = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(cacheBitmap!!, 0f, 0f, mPaint)
        canvas.drawPath(mPath, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPenX = event.x
                mPenY = event.y
                mPath.moveTo(mPenX, mPenY)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                touched = true
                val x = event.x
                val y = event.y
                val penX = mPenX
                val penY = mPenY
                val dx = Math.abs(x - penX)
                val dy = Math.abs(y - penY)
                if (dx >= 3 || dy >= 3) {
                    val cx = (x + penX) / 2
                    val cy = (y + penY) / 2
                    mPath.quadTo(penX, penY, cx, cy)
                    mPenX = x
                    mPenY = y
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mCanvas!!.drawPath(mPath, mPaint)
                mPath.reset()
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private val TAG = SignatureView::class.java.simpleName
        const val PEN_WIDTH = 10
        const val PEN_COLOR = Color.BLACK
        const val BACK_COLOR = Color.WHITE
    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SignatureView)
        mPenColor = typedArray.getColor(
            R.styleable.SignatureView_penColor,
            PEN_COLOR
        )
        mBackColor = typedArray.getColor(
            R.styleable.SignatureView_backColor,
            BACK_COLOR
        )
        mPentWidth =
            typedArray.getInt(R.styleable.SignatureView_penWidth, PEN_WIDTH)
        typedArray.recycle()
        init()
    }
}

