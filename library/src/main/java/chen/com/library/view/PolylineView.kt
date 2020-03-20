package chen.com.library.view

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*

/**
 * @author chenxingyu
 * data 2020/3/18
 * description 折线图
 */
class PolylineView : View {
    
    //<editor-fold desc="属性">
    private val textPaint: Paint
    private val linePaint: Paint
    //坐标系边距
    private var margin = 20
    //刻度最大值
    private var xCalibrationMax = 8
    private var yCalibrationMax = 4
    //刻度之间间隔值
    private var xCalibrationInterval = 1
    private var yCalibrationInterval = 1
    //刻度高度
    private var calibrationHeight = 5
    private val chartLine = Path()
    private val coordinateSystem = Path()
    private val textBounds = Rect()
    private val points: MutableList<PointF?> = ArrayList()
    
    private var xUnit = ""
    private var yUnit = ""
    
    //</editor-fold>
    
    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attrs, defStyleAttr) {
        linePaint = Paint()
        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dip2px(1f).toFloat()
        linePaint.isAntiAlias = true //设置抗锯齿
        linePaint.isDither = true //设置防抖动
        textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.textSize = sp2px(10f).toFloat()
        textPaint.isAntiAlias = true //设置抗锯齿
        textPaint.isDither = true //设置防抖动
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        margin = dip2px(18f)
        calibrationHeight = dip2px(5f)
        
        
    }
    
    //<editor-fold desc="classInitializer">
    
    
    //</editor-fold>
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCoordinateSystem(canvas, coordinateSystem)
        drawLineChart(canvas, chartLine)
    }
    
    /**
     * 绘制坐标系
     */
    private fun drawCoordinateSystem(canvas: Canvas, path: Path) {
        path.reset()
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        path.moveTo(margin.toFloat(), margin.toFloat())
        path.lineTo(margin.toFloat(), measuredHeight - margin.toFloat())
        path.lineTo(measuredWidth - margin.toFloat(), measuredHeight - margin.toFloat())
        //竖线上箭头
        path.moveTo(margin.toFloat(), margin.toFloat())
        path.lineTo(margin - margin / 3f, margin + dip2px(6f).toFloat())
        path.moveTo(margin.toFloat(), margin.toFloat())
        path.lineTo(margin + margin / 3f, margin + dip2px(6f).toFloat())
        //横线上箭头
        path.moveTo(measuredWidth - margin.toFloat(), measuredHeight - margin.toFloat())
        path.lineTo(measuredWidth - margin - dip2px(6f).toFloat(), measuredHeight - margin - margin / 3f)
        path.moveTo(measuredWidth - margin.toFloat(), measuredHeight - margin.toFloat())
        path.lineTo(measuredWidth - margin - dip2px(6f).toFloat(), measuredHeight - margin + margin / 3f)
        //X轴有效距离
        val xValidDistance = measuredWidth - 3 * margin
        //绘制X轴刻度
        var distance = xValidDistance / xCalibrationMax
        path.moveTo(margin.toFloat(), measuredHeight - margin.toFloat())
        var startX: Int
        for (i in 0..xCalibrationMax) {
            if (i % xCalibrationInterval == 0) {
                startX = i * distance + margin
                path.moveTo(startX.toFloat(), measuredHeight - margin.toFloat())
                path.lineTo(startX.toFloat(), measuredHeight - margin - calibrationHeight.toFloat())
                val textBounds = getTextBounds(i.toString())
                val textY = (measuredHeight - margin + textBounds.height() * 1.5).toInt()
                val textX = startX - textBounds.width() / 2 - 1
                canvas.drawText(i.toString(), textX.toFloat(), textY.toFloat(), textPaint)
            }
        }
        //Y轴有效距离
        val yValidDistance = measuredHeight - 3 * margin
        //绘制X轴刻度
        distance = yValidDistance / yCalibrationMax
        var startY: Int
        for (i in 1..yCalibrationMax) {
            if (i % yCalibrationInterval == 0) {
                startY = measuredHeight - margin - distance * i
                path.moveTo(margin.toFloat(), startY.toFloat())
                path.lineTo(margin + calibrationHeight.toFloat(), startY.toFloat())
                val textBounds = getTextBounds(i.toString())
                val textY = startY + textBounds.height() / 2
                var textX = (margin - textBounds.width() * 1.5).toInt()
                if (i == 1) {
                    textX -= dip2px(2f)
                }
                canvas.drawText(i.toString(), textX.toFloat(), textY.toFloat(), textPaint)
            }
        }
        canvas.drawPath(path, linePaint)
        
        
        //绘制单位
        if (!TextUtils.isEmpty(yUnit)) {
            val tX = margin
            val ty = margin - dip2px(3f)
            canvas.drawText(yUnit, tX.toFloat(), ty.toFloat(), textPaint)
        }
        
        if (!TextUtils.isEmpty(xUnit)) {
            val textBounds1 = getTextBounds(xUnit)
            val tX = measuredWidth - margin - (textBounds1.width())
            val ty = measuredHeight - margin - textBounds1.height()
            canvas.drawText(xUnit, tX.toFloat(), ty.toFloat(), textPaint)
        }
        
        
    }
    
    /**
     * 绘制折线图
     */
    private fun drawLineChart(canvas: Canvas, path: Path) {
        if (isEmpty(points)) return
        val size = points.size
        path.reset()
        val xValidDistance = measuredWidth - 3 * margin
        val distanceX = xValidDistance / xCalibrationMax
        val measuredHeight = measuredHeight
        val yValidDistance = measuredHeight - 3 * margin
        val distanceY = yValidDistance / yCalibrationMax
        path.moveTo(margin.toFloat(), measuredHeight - margin.toFloat())
        for (i in 0 until size) {
            val point = points[i]
            val x = point!!.x * distanceX + margin
            val y = measuredHeight - margin - distanceY * point.y
            path.lineTo(x, y)
        }
        linePaint.shader = getLinearGradient( )
        canvas.drawPath(path, linePaint)
        linePaint.shader = null
    }
    
    private fun getLinearGradient(): LinearGradient {
        return LinearGradient(0f, 0f, 0f, height.toFloat(), Color.GREEN, Color.MAGENTA, Shader.TileMode.CLAMP)
    }
    
    //<editor-fold desc="setget">
    
    
    fun setPoints(points: List<PointF?>?) {
        this.points.clear()
        this.points.addAll(points!!)
    }
    
    fun addPoints(points: List<PointF?>?) {
        this.points.addAll(points!!)
    }
    
    fun setXCalibrationMax(xCalibrationMax: Int) {
        if (xCalibrationMax <= 0) return
        this.xCalibrationMax = xCalibrationMax
    }
    
    fun setYCalibrationMax(yCalibrationMax: Int) {
        if (yCalibrationMax <= 0) return
        this.yCalibrationMax = yCalibrationMax
    }
    
    fun setXCalibrationInterval(xCalibrationInterval: Int) {
        if (xCalibrationInterval <= 0) return
        this.xCalibrationInterval = xCalibrationInterval
    }
    
    fun setYCalibrationInterval(yCalibrationInterval: Int) {
        if (yCalibrationInterval <= 0) return
        this.yCalibrationInterval = yCalibrationInterval
    }
    
    fun setXUnit(unit: String) {
        xUnit = unit
    }
    
    fun setYUnit(unit: String) {
        yUnit = unit
    }
    
    
    //</editor-fold>
    
    fun refresh() {
        invalidate()
    }
    
    
    //<editor-fold desc="Tools">
    
    private fun getTextBounds(text: String): Rect {
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        return textBounds
    }
    
    private fun dip2px(dipValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics).toInt()
    }
    
    private fun sp2px(spValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, resources.displayMetrics).toInt()
    }
    
    private fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }
    
    //</editor-fold>
    
    
    //<editor-fold desc="子类">
    class PointF {
        var x = 0f
        var y = 0f
        
        constructor(x: Float, y: Float) {
            this.x = x
            this.y = y
        }
        
        constructor() : super() {}
        
        override fun toString(): String {
            return "PointF{" +
                    "x=" + x +
                    ", y=" + y +
                    '}'
        }
    }
    
    companion object {
        @JvmStatic
        fun create(x: Float, y: Float): PointF {
            return PointF(x, y)
        }
    }
    //</editor-fold>
    
    
}