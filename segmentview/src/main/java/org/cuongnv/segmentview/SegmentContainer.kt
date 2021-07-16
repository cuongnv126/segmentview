package org.cuongnv.segmentview

import android.view.View
import android.view.ViewGroup

// Created by cuongnv on 2019-11-28.

interface SegmentContainer {
    fun setIndicator(segmentIndicator: SegmentIndicator)
    fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?)

    fun reset()

    fun setOnSegmentChangedListener(l: SegmentLayout.OnSegmentChanged?)

    fun onSelectedChange(animate: Boolean = true)
    fun dispatchSetChildSelected(index: Int, isSelected: Boolean, notify: Boolean = true)

    fun setSelectedIndex(index: Int, animate: Boolean = true)
    fun getSelectedIndex(): Int

    fun getItemCount(): Int
}