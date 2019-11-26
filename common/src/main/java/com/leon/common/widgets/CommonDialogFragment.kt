package com.leon.common.widgets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.leon.common.R
import org.jetbrains.anko.dip
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @time:2019/10/30 14:37
 * @author:Leon
 * @description:
 */
class CommonDialogFragment : DialogFragment() {
    private var layoutId = -1
    lateinit var builder: Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lp = dialog?.window?.attributes
        dialog?.window?.setBackgroundDrawable(ColorDrawable(builder.backgroundColor))
        lp?.apply {
            width = builder.width
            height = builder.height
            gravity = builder.gravity
        }
        layoutId = builder.layoutId
        isCancelable = builder.isCancelable
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(layoutId, container)
        setImg(dialogView)
        setText(dialogView)
        bindView(dialogView)
        setOnClickListener(dialogView)
        return dialogView
    }

    private fun bindView(view: View) {
        builder.onChangeMap.forEach { entry ->
            entry.value.first.removeObservers(this)
            entry.value.first.observe(this, Observer {
                view.findOptional<View>(entry.key)
                    ?.let { findView -> entry.value.second(findView, it, this) }
            })
        }
    }

    private fun setOnClickListener(view: View) {
        builder.onclickMap.forEach { entry ->
            view.findOptional<View>(entry.key)?.onClick {
                entry.value?.onClick(
                    this@CommonDialogFragment,
                    view.findOptional(entry.key)!!
                )
            }
        }
    }

    private fun setText(view: View) {
        builder.textMap.forEach {
            view.findOptional<TextView>(it.key)?.text = it.value
        }
    }

    private fun setImg(view: View) {
        builder.imgMap.forEach {
            view.findOptional<ImageView>(it.key)?.imageResource = it.value
        }
    }

    class Builder(val context: Context) {
        internal var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        internal var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        internal var isCancelable: Boolean = false
        internal var gravity = Gravity.CENTER
        internal var layoutId = -1
        internal var backgroundColor: Int = 0x7F000000

        internal val imgMap = mutableMapOf<Int, Int>()
        internal val textMap = mutableMapOf<Int, String>()
        internal val onclickMap = mutableMapOf<Int, OnClickListener>()
        internal val onChangeMap =
            mutableMapOf<Int, Pair<LiveData<Any>, (view: View, value: Any, dialog: CommonDialogFragment) -> Unit>>()

        fun backgroundColor(color: Int) {
            backgroundColor = color
        }

        fun setLayoutId(@LayoutRes viewId: Int): Builder {
            layoutId = viewId
            return this
        }

        fun heightPx(heightVal: Int): Builder {
            height = heightVal
            return this
        }

        fun gravity(gravity: Int) {
            this.gravity = gravity
        }

        fun widthPx(widthVal: Int): Builder {
            width = widthVal
            return this
        }

        fun heightDp(heightVal: Int): Builder {
            height = context.dip(heightVal)
            return this
        }

        fun widthDp(widthVal: Int): Builder {
            width = context.dip(widthVal)
            return this
        }

        fun heightDimenRes(dimenRes: Int): Builder {
            height = context.resources.getDimensionPixelOffset(dimenRes)
            return this
        }

        fun widthDimenRes(dimenRes: Int): Builder {
            width = context.resources.getDimensionPixelOffset(dimenRes)
            return this
        }

        fun isCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        fun setText(viewId: Int, text: String): Builder {
            textMap[viewId] = text
            return this
        }

        fun setImageRes(viewId: Int, resId: Int) {
            imgMap[viewId] = resId
        }

        fun bindView(
            viewId: Int,
            liveData: LiveData<Any>,
            onChange: (view: View, value: Any, dialog: CommonDialogFragment) -> Unit
        ) {
            onChangeMap[viewId] = liveData to onChange
        }

        fun addOnClickListener(viewRes: Int, listener: OnClickListener): Builder {
            onclickMap[viewRes] = listener
            return this
        }

        fun build(): CommonDialogFragment {
            return CommonDialogFragment().apply {
                builder = this@Builder
            }
        }
    }

    interface OnClickListener {
        fun onClick(dialogFragment: CommonDialogFragment, view: View)
    }

}