package org.cuongnv.segmentviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import org.cuongnv.segmentview.SegmentLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // testProgrammatically()
    }

    private fun testProgrammatically() {
        val options = arrayOf(R.string.text1, R.string.text2, R.string.text3)

        // Build SegmentView programmatically.
        findViewById<SegmentLayout>(R.id.segment_view).apply {
            reset()
            options.forEach { resId ->
                addView(
                    TextView(this@MainActivity).apply {
                        TextViewCompat.setTextAppearance(this, R.style.SimpleTextView)
                        setText(resId)
                    },
                    ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                        ViewGroup.MarginLayoutParams.MATCH_PARENT
                    )
                )
            }
        }
    }
}
