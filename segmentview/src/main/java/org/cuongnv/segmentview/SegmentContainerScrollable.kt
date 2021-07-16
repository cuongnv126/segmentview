package org.cuongnv.segmentview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

class SegmentContainerScrollable @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : HorizontalScrollView(context, attrs, defStyleAttr), SegmentContainer {

    private lateinit var _segmentIndicator: SegmentIndicator

    private var _selectedIndex = -1
    private val _viewContainer: LinearLayout = LinearLayout(context)
    private var _segmentChangedListener: SegmentLayout.OnSegmentChanged? = null
    private val _childLocation = IntArray(2)
    private val _indicatorLocation = IntArray(2).apply { set(0, -1) }

    init {
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false

        _viewContainer.orientation = LinearLayout.HORIZONTAL
        _viewContainer.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(_viewContainer)
    }

    override fun setIndicator(segmentIndicator: SegmentIndicator) {
        _segmentIndicator = segmentIndicator
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is LinearLayout) {
            super.addView(child, index, params)
        } else {
            child.setTag(R.id.tag_segment_index, _viewContainer.childCount)
            child.setOnClickListener {
                val newSelectedIndex = it.getTag(R.id.tag_segment_index) as Int
                if (newSelectedIndex != _selectedIndex) {
                    setSelectedIndexInternal(newSelectedIndex, true)
                }
            }

            _viewContainer.addView(child, index, params)
        }
    }

    override fun reset() {
        _viewContainer.removeAllViews()
    }

    override fun setOnSegmentChangedListener(l: SegmentLayout.OnSegmentChanged?) {
        _segmentChangedListener = l
    }

    override fun dispatchSetChildSelected(index: Int, isSelected: Boolean, notify: Boolean) {
        if (index in 0 until _viewContainer.childCount) {
            _viewContainer.getChildAt(index).isSelected = isSelected

            if (notify) {
                if (isSelected) {
                    _segmentChangedListener?.onSegmentSelected(index)
                } else {
                    _segmentChangedListener?.onSegmentUnselected(index)
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (_selectedIndex >= 0) {
            onSegmentSelectedChangeInternal(_selectedIndex, false)
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (_selectedIndex in 0 until _viewContainer.childCount) {
            onSelectedChange()
        }
    }

    override fun onSelectedChange(animate: Boolean) {
        onSegmentSelectedChangeInternal(animate = animate)
    }

    private fun onSegmentSelectedChangeInternal(index: Int = _selectedIndex, animate: Boolean) {
        if (index !in 0 until _viewContainer.childCount) return

        val child = _viewContainer.getChildAt(index)
        child.getLocationInWindow(_childLocation)

        val xIndicator = getXIndicator(_segmentIndicator)
        _segmentIndicator.onSegmentScroll(
            (_childLocation[0] - xIndicator).toFloat(),
            child.width,
            child.top.toFloat(),
            child.height,
            animate
        )
    }

    private fun getXIndicator(indicator: SegmentIndicator): Int {
        indicator.getLocationInWindow(_indicatorLocation)
        return _indicatorLocation[0]
    }

    private fun setSelectedIndexInternal(newSelectedIndex: Int, animate: Boolean) {
        dispatchSetChildSelected(_selectedIndex, false)

        _selectedIndex = newSelectedIndex
        dispatchSetChildSelected(_selectedIndex, true)

        post { onSelectedChange(animate) }
    }

    override fun setSelectedIndex(index: Int, animate: Boolean) {
        if (index != _selectedIndex && index in 0 until _viewContainer.childCount) {
            setSelectedIndexInternal(index, animate)
        }
    }

    override fun getSelectedIndex() = _selectedIndex

    override fun getItemCount() = _viewContainer.childCount
}