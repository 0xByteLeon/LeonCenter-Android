package com.leon.common.app

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.os.Bundle
import java.util.Stack

/**
 * @time:2019/9/9 15:37
 * @author:Leon
 * @description:
 */
object AppManager {
    //App是否在前台
    var isFront = false
    private var isInit = false
    var activityStack: Stack<Activity> = Stack()
        private set
    var fragmentStack: Stack<Fragment> = Stack()
        private set

    fun init(application: Application) {
        if (!isInit) {
            application.registerActivityLifecycleCallbacks(object :
                Application.ActivityLifecycleCallbacks {

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                    activity?.let { addActivity(it) }
                }

                override fun onActivityStarted(activity: Activity?) {
                }

                override fun onActivityResumed(activity: Activity?) {
                    isFront = true
                }

                override fun onActivityPaused(activity: Activity?) {
                    isFront = false
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivityDestroyed(activity: Activity?) {
                    removeActivity(activity)
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

            })

            isInit = true
        }

    }

    /**
     * 是否有activity
     */
    val hasActivity: Boolean
        get() = activityStack.isNotEmpty()


    /**
     * 是否有Fragment
     */
    val hasFragment: Boolean
        get() = fragmentStack.isNotEmpty()


    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * 移除指定的Activity
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            activityStack.remove(activity)
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        if (activityStack.isNotEmpty())
            return activityStack.last()

        return null
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity = activityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack.forEach {
            if (it.javaClass == cls) {
                finishActivity(it)
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        activityStack.forEach {
            finishActivity(it)
        }
        activityStack.clear()
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    fun getActivity(cls: Class<*>): Activity? {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                return activity
            }
        }
        return null
    }


    /**
     * 添加Fragment到堆栈
     */
    fun addFragment(fragment: Fragment) {
        fragmentStack.add(fragment)
    }

    /**
     * 移除指定的Fragment
     */
    fun removeFragment(fragment: Fragment?) {
        if (fragment != null) {
            fragmentStack.remove(fragment)
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentFragment(): Fragment? {
        return fragmentStack.lastElement()
    }


    /**
     * 退出应用程序
     */
    fun AppExit() {
        try {
            finishAllActivity()
            // 杀死该应用进程
            //          android.os.Process.killProcess(android.os.Process.myPid());
            //            调用 System.exit(n) 实际上等效于调用：
            //            Runtime.getRuntime().exit(n)
            //            finish()是Activity的类方法，仅仅针对Activity，当调用finish()时，只是将活动推向后台，并没有立即释放内存，活动的资源并没有被清理；当调用System.exit(0)时，退出当前Activity并释放资源（内存），但是该方法不可以结束整个App如有多个Activty或者有其他组件service等不会结束。
            //            其实android的机制决定了用户无法完全退出应用，当你的application最长时间没有被用过的时候，android自身会决定将application关闭了。
            //System.exit(0);
        } catch (e: Exception) {
            activityStack.clear()
            e.printStackTrace()
        }

    }
}