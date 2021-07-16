package org.cuongnv.segmentview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class SegmentContainerFix @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr), SegmentContainer {
    private lateinit var _segmentIndicator: SegmentIndicator

    private var _selectedIndex = -1
    private var _segmentChangedListener: SegmentLayout.OnSegmentChanged? = null

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        if (params is LayoutParams) {
            params.weight = 1f
        }

        child.setTag(R.id.tag_segment_index, childCount)
        child.setOnClickListener {
            val newSelectedIndex = it.getTag(R.id.tag_segment_index) as Int
            if (newSelectedIndex != _selectedIndex) {
                setSelectedIndexInternal(newSelectedIndex, true)
            }
        }
        super.addView(child, index, params)
    }

    override fun reset() {
        removeAllViews()
    }

    override fun setOnSegmentChangedListener(l: SegmentLayout.OnSegmentChanged?) {
        _segmentChangedListener = l
    }

    override fun setIndicator(segmentIndicator: SegmentIndicator) {
        _segmentIndicator = segmentIndicator
    }

    override fun dispatchSetChildSelected(index: Int, isSelected: Boolean, notify: Boolean) {
        if (index in 0 until childCount) {
            getChildAt(index).isSelected = isSelected
            if (notify) {
                if (isSelected) {
                    _segmentChangedListener?.onSegmentSelected(index)
                } else {
                    _segmentChangedListener?.onSegmentUnselected(index)
                }
            }
        }
    }

    override fun onSelectedChange(animate: Boolean) {
        if (_selectedIndex in 0 until childCount) {
            val child = getChildAt(_selectedIndex)

            _segmentIndicator.onSegmentScroll(
                child.left.toFloat(),
                child.width,
                child.top.toFloat(),
                child.height,
                animate
            )
        }
    }

    private fun setSelectedIndexInternal(newSelectedIndex: Int, animate: Boolean) {
        dispatchSetChildSelected(_selectedIndex, false)

        _selectedIndex = newSelectedIndex
        dispatchSetChildSelected(_selectedIndex, true)

        post { onSelectedChange(animate) }
    }

    override fun setSelectedIndex(index: Int, animate: Boolean) {
        if (index != _selectedIndex && index in 0 until childCount) {
            setSelectedIndexInternal(index, animate)
        }
    }

    override fun getSelectedIndex() = _selectedIndex

    override fun getItemCount() = childCount
}