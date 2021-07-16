package org.cuongnv.segmentview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat

// Created by cuongnv on 2019-11-27.

class SegmentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var _isFixSize = false

    private val _segmentContainer: SegmentContainer
    private val _segmentIndicator = SegmentIndicator(context)

    init {
        attrs?.let { parseAttributeSet(context, it) }

        _segmentContainer =
            if (_isFixSize) SegmentContainerFix(context) else SegmentContainerScrollable(context)
        _segmentContainer.setIndicator(_segmentIndicator)

        reset()
    }

    private fun parseAttributeSet(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentLayout)
        _isFixSize = typedArray.getBoolean(R.styleable.SegmentLayout_sgm_isFixSize, false)

        val selectedDrawableId =
            typedArray.getResourceId(R.styleable.SegmentLayout_sgm_selectedDrawable, 0)
        if (selectedDrawableId != 0) {
            val selectedDrawable = ContextCompat.getDrawable(context, selectedDrawableId)
            _segmentIndicator.setSelectedDrawable(selectedDrawable)
        }

        typedArray.recycle()
    }

    /**
     * Before add child tab by programmatically, please call it to clear sub child and re-init view
     */
    fun reset() {
        removeAllViews()

        _segmentContainer.reset()

        addView(
            _segmentIndicator,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        addView(
            _segmentContainer as View,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is SegmentContainer || child is SegmentIndicator) {
            super.addView(child, index, params)
        } else {
            var childParam = params
            if (childParam != null) {
                childParam = LinearLayout.LayoutParams(childParam as MarginLayoutParams)
            } else {
                childParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                childParam.weight = 1f
            }
            _segmentContainer.addView(child, index, childParam)
        }
    }

    fun setSelectedIndex(index: Int, animate: Boolean = true) {
        _segmentContainer.setSelectedIndex(index, animate)
    }

    fun getItemCount(): Int {
        return _segmentContainer.getItemCount()
    }

    fun setOnSegmentChangedListener(l: OnSegmentChanged?) {
        _segmentContainer.setOnSegmentChangedListener(l)
    }

    fun getSelectedIndex(): Int = _segmentContainer.getSelectedIndex()

    interface OnSegmentChanged {
        fun onSegmentSelected(index: Int)
        fun onSegmentUnselected(index: Int)
    }
}