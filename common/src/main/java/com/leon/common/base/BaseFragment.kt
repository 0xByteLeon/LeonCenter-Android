package com.leon.common.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.leon.common.utils.FragmentUserVisibleController

abstract class BaseFragment : Fragment(), FragmentUserVisibleController.UserVisibleCallback {

    protected var TAG = this.javaClass.simpleName

    /**
     * 此类用来解决fragment在viewpager中预加载导致的显示隐藏状态混乱的问题
     * */
    private val userVisibleController by lazy {
        FragmentUserVisibleController(this, this)
    }

    abstract fun initView()
    abstract fun initData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        bindModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userVisibleController.activityCreated()
    }

    override fun onResume() {
        super.onResume()
        userVisibleController.resume()
    }

    override fun onPause() {
        super.onPause()
        userVisibleController.pause()
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        userVisibleController.setUserVisibleHint(isVisibleToUser)
    }

    override fun setWaitingShowToUser(waitingShowToUser: Boolean) {
        userVisibleController.isWaitingShowToUser = waitingShowToUser
    }

    override fun isWaitingShowToUser(): Boolean {
        return userVisibleController.isWaitingShowToUser
    }

    override fun isVisibleToUser(): Boolean {
        return userVisibleController.isVisibleToUser
    }

    override fun callSuperSetUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }

    override fun onVisibleToUserChanged(isVisibleToUser: Boolean, invokeInResumeOrPause: Boolean) {

    }

    protected abstract fun bindModel()

}