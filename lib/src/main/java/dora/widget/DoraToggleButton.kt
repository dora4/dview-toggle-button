package dora.widget

import android.animation.Animator
import android.animation.Animator.AnimatorPauseListener
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable

class DoraToggleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Checkable {

    /**
     * 阴影的半径。
     */
    private var shadowRadius = 0

    /**
     * 阴影的颜色。
     */
    private var shadowColor = 0

    /**
     * 按钮的半径。
     */
    private var buttonRadius = 0f
    private var viewRadius = 0f
    private var width = 0f
    private var height = 0f
    private var left = 0f
    private var top = 0f
    private var right = 0f
    private var bottom = 0f
    private var centerX = 0f
    private var centerY = 0f

    /**
     * 背景颜色。
     */
    private var bgColor = 0

    /**
     * 未选中的颜色。
     */
    private var uncheckColor = 0

    /**
     * 选中的颜色。
     */
    private var checkedColor = 0

    /**
     * 边框的宽度。
     */
    private var borderWidth = 0

    /**
     * 选中指示器的颜色。
     */
    private var checkLineColor = 0

    /**
     * 选中指示器的宽度。
     */
    private var checkLineWidth = 0

    /**
     * 选中指示器的长度。
     */
    private var checkLineLength = 0f

    /**
     * 选中指示器的x偏移。
     */
    private var checkLineOffsetX = 0f

    /**
     * 选中指示器的y偏移。
     */
    private var checkLineOffsetY = 0f

    /**
     * 未选中指示器的颜色。
     */
    private var uncheckCircleColor = 0

    /**
     * 未选择指示器的宽度。
     */
    private var uncheckCircleWidth = 0

    /**
     * 未选中指示器x偏移。
     */
    private var uncheckCircleOffsetX = 0f

    /**
     * 未选中指示器的半径。
     */
    private var uncheckCircleRadius = 0f

    /**
     * 按钮可以滑动到的最小的x轴位置。
     */
    private var buttonMinX = 0f

    /**
     * 按钮可以滑动到的最大的x轴位置。
     */
    private var buttonMaxX = 0f
    private var buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var touchDownTime: Long = 0
    private var viewState = ViewState()
    private var beforeState = ViewState()
    private var afterState = ViewState()
    private var animateState = STATE_NORMAL
    private var isChecked = false

    /**
     * 是否启用拖拽效果。
     */
    private var enableEffect = false

    /**
     * 是否启用引用。
     */
    private var enableShadow = false

    /**
     * 是否显示指示器圆圈和线
     */
    private var showIndicator = false
    private var isTouchingDown = false
    private var isFirstLoaded = false
    private var isCheckedChanging = false
    private lateinit var valueAnimator: ValueAnimator
    private val argbEvaluator = ArgbEvaluator()

    /**
     * 按钮的颜色，默认白色。
     */
    private var buttonColor = Color.WHITE

    /**
     * 拖拽效果的时长。
     */
    private var effectDuration = 0

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(0, 0, 0, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        initAttrs(context, attrs)
        initPaints()
        if (enableShadow) {
            buttonPaint.setShadowLayer(
                shadowRadius.toFloat(), 0f, 0f,
                shadowColor
            )
        }
        viewState = ViewState()
        beforeState = ViewState()
        afterState = ViewState()
        valueAnimator = createValueAnimator(effectDuration, { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            when (animateState) {
                STATE_PENDING_SETTLE -> {
                    run {
                        viewState.checkedLineColor = argbEvaluator.evaluate(
                            value,
                            beforeState.checkedLineColor,
                            afterState.checkedLineColor
                        ) as Int
                        viewState.radius = (beforeState.radius
                                + (afterState.radius - beforeState.radius) * value)
                        if (animateState != STATE_PENDING_DRAG) {
                            viewState.buttonX = (beforeState.buttonX
                                    + (afterState.buttonX - beforeState.buttonX) * value)
                        }
                        viewState.checkStateColor = argbEvaluator.evaluate(
                            value,
                            beforeState.checkStateColor,
                            afterState.checkStateColor
                        ) as Int
                    }
                }
                STATE_PENDING_RESET -> {
                    run {
                        viewState.checkedLineColor = argbEvaluator.evaluate(
                            value,
                            beforeState.checkedLineColor,
                            afterState.checkedLineColor
                        ) as Int
                        viewState.radius = (beforeState.radius
                                + (afterState.radius - beforeState.radius) * value)
                        if (animateState != STATE_PENDING_DRAG) {
                            viewState.buttonX = (beforeState.buttonX
                                    + (afterState.buttonX - beforeState.buttonX) * value)
                        }
                        viewState.checkStateColor = argbEvaluator.evaluate(
                            value,
                            beforeState.checkStateColor,
                            afterState.checkStateColor
                        ) as Int
                    }
                }
                STATE_PENDING_DRAG -> {
                    viewState.checkedLineColor = argbEvaluator.evaluate(
                        value,
                        beforeState.checkedLineColor,
                        afterState.checkedLineColor
                    ) as Int
                    viewState.radius = (beforeState.radius
                            + (afterState.radius - beforeState.radius) * value)
                    if (animateState != STATE_PENDING_DRAG) {
                        viewState.buttonX = (beforeState.buttonX
                                + (afterState.buttonX - beforeState.buttonX) * value)
                    }
                    viewState.checkStateColor = argbEvaluator.evaluate(
                        value,
                        beforeState.checkStateColor,
                        afterState.checkStateColor
                    ) as Int
                }
                STATE_SWITCH -> {
                    viewState.buttonX = (beforeState.buttonX
                            + (afterState.buttonX - beforeState.buttonX) * value)
                    val fraction = (viewState.buttonX - buttonMinX) / (buttonMaxX - buttonMinX)
                    viewState.checkStateColor = argbEvaluator.evaluate(
                        fraction,
                        uncheckColor,
                        checkedColor
                    ) as Int
                    viewState.radius = fraction * viewRadius
                    viewState.checkedLineColor = argbEvaluator.evaluate(
                        fraction,
                        Color.TRANSPARENT,
                        checkLineColor
                    ) as Int
                }
            }
            postInvalidate()
        }, object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                when (animateState) {
                    STATE_PENDING_DRAG -> {
                        animateState = STATE_DRAGGING
                        viewState.checkedLineColor = Color.TRANSPARENT
                        viewState.radius = viewRadius
                        postInvalidate()
                    }
                    STATE_PENDING_RESET -> {
                        animateState = STATE_NORMAL
                        postInvalidate()
                    }
                    STATE_PENDING_SETTLE -> {
                        animateState = STATE_NORMAL
                        postInvalidate()
                        notifyCheckedChanged()
                    }
                    STATE_SWITCH -> {
                        isChecked = !isChecked
                        animateState = STATE_NORMAL
                        postInvalidate()
                        notifyCheckedChanged()
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        }, null, 0f, 1f)
        this.isClickable = true
        setPadding(0, 0, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 设置wrap_content时的宽高
        super.onMeasure(
            applyWrapContentSize(widthMeasureSpec, DEFAULT_WIDTH),
            applyWrapContentSize(heightMeasureSpec, DEFAULT_HEIGHT)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val viewPadding = shadowRadius.coerceAtLeast(borderWidth).toFloat()
        height = h - viewPadding - viewPadding
        width = w - viewPadding - viewPadding
        viewRadius = height * .5f
        buttonRadius = viewRadius - borderWidth
        left = viewPadding
        top = viewPadding
        right = w - viewPadding
        bottom = h - viewPadding
        centerX = (left + right) * .5f
        centerY = (top + bottom) * .5f
        buttonMinX = left + viewRadius
        buttonMaxX = right - viewRadius
        if (isChecked()) {
            setCheckedViewState(viewState)
        } else {
            setUncheckViewState(viewState)
        }
        isFirstLoaded = true
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        bgPaint.strokeWidth = borderWidth.toFloat()
        bgPaint.style = Paint.Style.FILL
        // 绘制白色背景
        bgPaint.color = bgColor
        drawRoundRect(
            canvas,
            left, top, right, bottom,
            viewRadius, bgPaint
        )
        // 绘制关闭状态的边框
        bgPaint.style = Paint.Style.STROKE
        bgPaint.color = uncheckColor
        drawRoundRect(
            canvas,
            left, top, right, bottom,
            viewRadius, bgPaint
        )
        // 绘制小圆圈
        if (showIndicator) {
            drawUncheckIndicator(canvas)
        }
        // 绘制开启背景色
        val des = viewState.radius * .5f //[0-backgroundRadius*0.5f]
        bgPaint.style = Paint.Style.STROKE
        bgPaint.color = viewState.checkStateColor
        bgPaint.strokeWidth = borderWidth + des * 2f
        drawRoundRect(
            canvas,
            left + des, top + des, right - des, bottom - des,
            viewRadius, bgPaint
        )
        // 绘制按钮左边绿色长条遮挡
        bgPaint.style = Paint.Style.FILL
        bgPaint.strokeWidth = 1f
        drawArc(
            canvas,
            left, top,
            left + 2 * viewRadius, top + 2 * viewRadius, 90f, 180f, bgPaint
        )
        canvas.drawRect(
            left + viewRadius, top,
            viewState.buttonX, top + 2 * viewRadius,
            bgPaint
        )
        // 绘制小线条
        if (showIndicator) {
            drawCheckedIndicator(canvas)
        }
        // 绘制按钮
        drawButton(canvas, viewState.buttonX, centerY)
    }

    /**
     * 绘制选中状态指示器。
     */
    protected fun drawCheckedIndicator(
            canvas: Canvas,
            color: Int =
            viewState.checkedLineColor,
            lineWidth: Float =
            checkLineWidth.toFloat(),
            sx: Float =
            left + viewRadius - checkLineOffsetX,
            sy: Float = centerY - checkLineLength,
            ex: Float =
            left + viewRadius - checkLineOffsetY,
            ey: Float = centerY + checkLineLength,
            paint: Paint =
            bgPaint
    ) {
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = lineWidth
        canvas.drawLine(
            sx, sy, ex, ey,
            paint
        )
    }

    /**
     * 绘制关闭状态指示器。
     *
     * @param canvas
     */
    private fun drawUncheckIndicator(canvas: Canvas) {
        drawUncheckIndicator(
            canvas,
            uncheckCircleColor,
            uncheckCircleWidth.toFloat(),
            right - uncheckCircleOffsetX, centerY,
            uncheckCircleRadius,
            bgPaint
        )
    }

    /**
     * 绘制关闭状态指示器。
     *
     * @param canvas
     * @param color
     * @param lineWidth
     * @param centerX
     * @param centerY
     * @param radius
     * @param paint
     */
    protected fun drawUncheckIndicator(
        canvas: Canvas,
        color: Int,
        lineWidth: Float,
        centerX: Float, centerY: Float,
        radius: Float,
        paint: Paint
    ) {
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = lineWidth
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawButton(canvas: Canvas, x: Float, y: Float) {
        canvas.drawCircle(x, y, buttonRadius, buttonPaint)
        bgPaint.style = Paint.Style.STROKE
        bgPaint.strokeWidth = 1f
        bgPaint.color = -0x222223
        canvas.drawCircle(x, y, buttonRadius, bgPaint)
    }

    override fun setChecked(checked: Boolean) {
        if (checked == isChecked()) {
            postInvalidate()
            return
        }
        toggle(enableEffect, false)
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        toggle(true)
    }

    fun toggle(animate: Boolean) {
        toggle(animate, true)
    }

    private fun toggle(animate: Boolean, broadcast: Boolean) {
        if (!isEnabled) {
            return
        }
        if (isCheckedChanging) {
            throw RuntimeException("should NOT switch the state in method: [onCheckedChanged]!")
        }
        if (!isFirstLoaded) {
            isChecked = !isChecked
            if (broadcast) {
                notifyCheckedChanged()
            }
            return
        }
        if (valueAnimator.isRunning) {
            valueAnimator.cancel()
        }
        if (!enableEffect || !animate) {
            isChecked = !isChecked
            if (isChecked()) {
                setCheckedViewState(viewState)
            } else {
                setUncheckViewState(viewState)
            }
            postInvalidate()
            if (broadcast) {
                notifyCheckedChanged()
            }
            return
        }
        animateState = STATE_SWITCH
        beforeState.copy(viewState)
        if (isChecked()) {
            // 切换到unchecked
            setUncheckViewState(afterState)
        } else {
            setCheckedViewState(afterState)
        }
        valueAnimator.start()
    }

    private fun notifyCheckedChanged() {
        if (onCheckedChangeListener != null) {
            isCheckedChanging = true
            onCheckedChangeListener!!.onCheckedChanged(this, isChecked())
        }
        isCheckedChanging = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchingDown = true
                touchDownTime = System.currentTimeMillis()
                // 取消准备进入拖动状态
                removeCallbacks(postPendingDrag)
                // 预设100ms进入拖动状态
                postDelayed(postPendingDrag, 100)
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                if (isPendingDragState) {
                    // 在准备进入拖动状态过程中，可以拖动按钮位置
                    var fraction = x / getWidth()
                    fraction = Math.max(0f, Math.min(1f, fraction))
                    viewState.buttonX = (buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction)
                } else if (isDragState) {
                    // 拖动按钮位置，同时改变对应的背景颜色
                    var fraction = x / getWidth()
                    fraction = 0f.coerceAtLeast(1f.coerceAtMost(fraction))
                    viewState.buttonX = (buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction)
                    viewState.checkStateColor = argbEvaluator.evaluate(
                        fraction,
                        uncheckColor,
                        checkedColor
                    ) as Int
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                isTouchingDown = false
                // 取消准备进入拖动状态
                removeCallbacks(postPendingDrag)
                if (System.currentTimeMillis() - touchDownTime <= 300) {
                    // 点击时间小于300ms，认为是点击操作
                    toggle()
                } else if (isDragState) {
                    // 在拖动状态，计算按钮位置，设置是否切换状态
                    val x = event.x
                    var fraction = x / getWidth()
                    fraction = 0f.coerceAtLeast(1f.coerceAtMost(fraction))
                    val newCheck = fraction > .5f
                    if (newCheck == isChecked()) {
                        pendingCancelDragState()
                    } else {
                        isChecked = newCheck
                        pendingSettleState()
                    }
                } else if (isPendingDragState) {
                    // 在准备进入拖动状态过程中，取消之，复位
                    pendingCancelDragState()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isTouchingDown = false
                removeCallbacks(postPendingDrag)
                if (isPendingDragState
                    || isDragState
                ) {
                    // 复位
                    pendingCancelDragState()
                }
            }
        }
        return true
    }

    /**
     * 是否在动画状态。
     *
     * @return
     */
    private val isInAnimating: Boolean
        private get() = animateState != STATE_NORMAL

    /**
     * 是否在进入拖动或离开拖动状态。
     *
     * @return
     */
    private val isPendingDragState: Boolean
        private get() = (animateState == STATE_PENDING_DRAG
                || animateState == STATE_PENDING_RESET)

    /**
     * 是否在手指拖动状态。
     *
     * @return
     */
    private val isDragState: Boolean
        private get() = animateState == STATE_DRAGGING

    /**
     * 设置是否启用阴影效果。
     *
     * @param enableShadow true代表启用阴影效果
     */
    fun setEnableShadow(enableShadow: Boolean) {
        if (this.enableShadow == enableShadow) {
            return
        }
        this.enableShadow = enableShadow
        if (this.enableShadow) {
            buttonPaint.setShadowLayer(
                shadowRadius.toFloat(), 0f, 0f,
                shadowColor
            )
        } else {
            buttonPaint.setShadowLayer(
                0f, 0f, 0f,
                0
            )
        }
    }

    /**
     * 设置启用阴影效果。
     */
    fun setEnableEffect(enable: Boolean) {
        enableEffect = enable
    }

    /**
     * 开始进入拖动状态。
     */
    private fun pendingDragState() {
        if (isInAnimating) {
            return
        }
        if (!isTouchingDown) {
            return
        }
        if (valueAnimator.isRunning) {
            valueAnimator.cancel()
        }
        animateState = STATE_PENDING_DRAG
        beforeState.copy(viewState)
        afterState.copy(viewState)
        if (isChecked()) {
            afterState.checkStateColor = checkedColor
            afterState.buttonX = buttonMaxX
            afterState.checkedLineColor = checkedColor
        } else {
            afterState.checkStateColor = uncheckColor
            afterState.buttonX = buttonMinX
            afterState.radius = viewRadius
        }
        valueAnimator.start()
    }

    /**
     * 取消拖动状态。
     */
    private fun pendingCancelDragState() {
        if (isDragState || isPendingDragState) {
            if (valueAnimator.isRunning) {
                valueAnimator.cancel()
            }
            animateState = STATE_PENDING_RESET
            beforeState.copy(viewState)
            if (isChecked()) {
                setCheckedViewState(afterState)
            } else {
                setUncheckViewState(afterState)
            }
            valueAnimator.start()
        }
    }

    /**
     * 设置新的状态。
     */
    private fun pendingSettleState() {
        if (valueAnimator.isRunning) {
            valueAnimator.cancel()
        }
        animateState = STATE_PENDING_SETTLE
        beforeState.copy(viewState)
        if (isChecked()) {
            setCheckedViewState(afterState)
        } else {
            setUncheckViewState(afterState)
        }
        valueAnimator.start()
    }

    override fun setOnClickListener(l: OnClickListener?) {}

    override fun setOnLongClickListener(l: OnLongClickListener?) {}

    fun setOnCheckedChangeListener(l: OnCheckedChangeListener) {
        onCheckedChangeListener = l
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: DoraToggleButton?, isChecked: Boolean)
    }

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    private val postPendingDrag = Runnable {
        if (!isInAnimating) {
            pendingDragState()
        }
    }

    private class ViewState internal constructor() {

        /**
         * 按钮x位置。
         */
        var buttonX = 0f

        /**
         * 状态背景颜色。
         */
        var checkStateColor = 0

        /**
         * 选中线的颜色。
         */
        var checkedLineColor = 0

        /**
         * 状态背景的半径。
         */
        var radius = 0f

        fun copy(source: ViewState) {
            buttonX = source.buttonX
            checkStateColor = source.checkStateColor
            checkedLineColor = source.checkedLineColor
            radius = source.radius
        }
    }

    private fun setUncheckViewState(viewState: ViewState) {
        viewState.radius = 0f
        viewState.checkStateColor = uncheckColor
        viewState.checkedLineColor = Color.TRANSPARENT
        viewState.buttonX = buttonMinX
    }

    private fun setCheckedViewState(viewState: ViewState) {
        viewState.radius = viewRadius
        viewState.checkStateColor = checkedColor
        viewState.checkedLineColor = checkLineColor
        viewState.buttonX = buttonMaxX
    }

    private fun applyWrapContentSize(measureSpec: Int, expected: Int): Int {
        var measureSpec = measureSpec
        val mode = MeasureSpec.getMode(measureSpec)
        if (mode == MeasureSpec.UNSPECIFIED
            || mode == MeasureSpec.AT_MOST
        ) {
            measureSpec = MeasureSpec.makeMeasureSpec(expected, MeasureSpec.EXACTLY)
        }
        return measureSpec
    }

    private fun initPaints() {
        bgPaint = getPaint(bgColor)
        buttonPaint = getPaint(buttonColor)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DoraToggleButton)
        enableShadow = a.getBoolean(
            R.styleable.DoraToggleButton_dora_enableShadow,
            true
        )
        uncheckCircleColor = a.getColor(
            R.styleable.DoraToggleButton_dora_uncheckCircleColor,
            -0x555556
        )
        uncheckCircleWidth = a.getDimensionPixelSize(
            R.styleable.DoraToggleButton_dora_uncheckCircleWidth,
            dp2px(getContext(), 1.5f)
        )
        uncheckCircleOffsetX = dp2px(getContext(), 10f).toFloat()
        uncheckCircleRadius = a.getDimensionPixelSize(
            R.styleable.DoraToggleButton_dora_uncheckCircleRadius,
            dp2px(getContext(), 4f)
        ).toFloat()
        checkLineOffsetX = dp2px(getContext(), 4f).toFloat()
        checkLineOffsetY = dp2px(getContext(), 4f).toFloat()
        shadowRadius = a.getDimensionPixelSize(
            R.styleable.DoraToggleButton_dora_shadowRadius,
            dp2px(getContext(), 2.5f)
        )
        shadowColor = a.getColor(
            R.styleable.DoraToggleButton_dora_shadowColor,
            0X33000000
        )
        uncheckColor = a.getColor(
            R.styleable.DoraToggleButton_dora_uncheckColor,
            -0x222223
        )
        checkedColor = a.getColor(
            R.styleable.DoraToggleButton_dora_checkedColor,
            -0xae2c99
        )
        borderWidth = a.getDimensionPixelSize(
            R.styleable.DoraToggleButton_dora_borderWidth,
            dp2px(getContext(), 1f)
        )
        checkLineColor = a.getColor(
            R.styleable.DoraToggleButton_dora_checkLineColor,
            Color.WHITE
        )
        checkLineWidth = a.getDimensionPixelSize(
            R.styleable.DoraToggleButton_dora_checkLineWidth,
            dp2px(getContext(), 1f)
        )
        checkLineLength = dp2px(getContext(), 6f).toFloat()
        buttonColor = a.getColor(
            R.styleable.DoraToggleButton_dora_buttonColor,
            Color.WHITE
        )
        effectDuration = a.getInt(
            R.styleable.DoraToggleButton_dora_effectDuration,
            300
        )
        isChecked = a.getBoolean(
            R.styleable.DoraToggleButton_dora_checked,
            false
        )
        showIndicator = a.getBoolean(
            R.styleable.DoraToggleButton_dora_showIndicator,
            true
        )
        bgColor = a.getColor(
            R.styleable.DoraToggleButton_dora_backgroundColor,
            Color.WHITE
        )
        enableEffect = a.getBoolean(
            R.styleable.DoraToggleButton_dora_enableEffect,
            true
        )
        a.recycle()
    }

    private fun getPaint(color: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.isDither = true
        paint.color = color
        return paint
    }

    /**
     * 绘制扇形。
     */
    private fun drawArc(
        canvas: Canvas,
        left: Float, top: Float,
        right: Float, bottom: Float,
        startAngle: Float, sweepAngle: Float,
        paint: Paint
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(
                left, top, right, bottom,
                startAngle, sweepAngle, true, paint
            )
        } else {
            canvas.drawArc(
                RectF(left, top, right, bottom),
                startAngle, sweepAngle, true, paint
            )
        }
    }

    /**
     * 绘制圆角矩形。
     */
    private fun drawRoundRect(
        canvas: Canvas,
        left: Float, top: Float,
        right: Float, bottom: Float,
        backgroundRadius: Float,
        paint: Paint
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(
                left, top, right, bottom,
                backgroundRadius, backgroundRadius, paint
            )
        } else {
            canvas.drawRoundRect(
                RectF(left, top, right, bottom),
                backgroundRadius, backgroundRadius, paint
            )
        }
    }

    /**
     * 转换dp为px进行绘制。
     */
    private fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 值动画的监听。
     */
    private fun createValueAnimator(
        duration: Int, updateListener: AnimatorUpdateListener?,
        stateListener: Animator.AnimatorListener?,
        pauseListener: AnimatorPauseListener?,
        vararg values: Float
    ): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(*values)
        valueAnimator.duration = duration.toLong()
        valueAnimator.addUpdateListener(updateListener)
        valueAnimator.addListener(stateListener)
        valueAnimator.addPauseListener(pauseListener)
        return valueAnimator
    }

    companion object {

        /**
         * 默认的宽度。
         */
        private const val DEFAULT_WIDTH = 174

        /**
         * 默认的高度。
         */
        private const val DEFAULT_HEIGHT = 108

        /**
         * 静止。
         */
        private const val STATE_NORMAL = 0

        /**
         * 进入拖动。
         */
        private const val STATE_PENDING_DRAG = 1

        /**
         * 处于拖动。
         */
        private const val STATE_DRAGGING = 2

        /**
         * 拖动复位。
         */
        private const val STATE_PENDING_RESET = 3

        /**
         * 拖动切换。
         */
        private const val STATE_PENDING_SETTLE = 4

        /**
         * 点击切换。
         */
        private const val STATE_SWITCH = 5
    }

    init {
        init(context, attrs)
    }
}