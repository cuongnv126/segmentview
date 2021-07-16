package org.cuongnv.segmentview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd

// Created by cuongnv on 2019-11-28.

class SegmentIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val SLIDE_ANIMATION_DURATION = 200L
    }

    private var _drawableSelected: Drawable? = null

    private var _xOffset: Float = 0f
    private var _yOffset: Float = 0f
    private var _segmentWidth: Int = 0
    private var _segmentHeight: Int = 0

    private var _animation: ValueAnimator? = null

    fun setSelectedDrawable(drawable: Drawable?) {
        _drawableSelected = drawable
    }

    fun onSegmentScroll(
        xOffset: Float, segmentWidth: Int,
        yOffset: Float, segmentHeight: Int,
        animate: Boolean = true,
    ) {
        var changed = false

        if (_segmentWidth != segmentWidth || _segmentHeight != segmentHeight) {
            onSegmentBoundChange(segmentWidth, segmentHeight)
            changed = true
        }

        if (_yOffset != yOffset) {
            _yOffset = yOffset
            changed = true
        }

        if (_xOffset != xOffset) {
            if (isInEditMode || !animate) {
                _xOffset = xOffset
            } else {
                onSegmentMove(xOffset)
            }
            changed = true
        }

        if (changed) {
            invalidate()
        }
    }

    private fun onSegmentMove(xOffset: Float) {
        doAnimation(xOffset)
    }

    private fun doAnimation(targetOffset: Float) {
        if (_animation != null && _animation!!.isRunning) {
            _animation!!.cancel()
        }

        _animation = ValueAnimator.ofFloat(_xOffset, targetOffset)
        _animation!!.apply {
            duration = SLIDE_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                _xOffset = it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                if (_xOffset != targetOffset) {
                    _xOffset = targetOffset
                    invalidate()
                }
            }
        }
        _animation!!.start()
    }

    private fun onSegmentBoundChange(width: Int, height: Int) {
        _segmentWidth = width
        _segmentHeight = height
        _drawableSelected?.setBounds(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        canvas.translate(_xOffset, _yOffset)
        _drawableSelected?.draw(canvas)

        canvas.restore()
    }
}