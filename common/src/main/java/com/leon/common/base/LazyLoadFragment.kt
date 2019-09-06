package com.leon.common.base


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.leon.common.R

/**
 * 数据懒加载fragment
 */
abstract class LazyLoadFragment : BaseFragment() {

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean, invokeInResumeOrPause: Boolean) {
        //View显示时刷新数据
        if (isVisibleToUser){
            initData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        bindModel()
    }

}
