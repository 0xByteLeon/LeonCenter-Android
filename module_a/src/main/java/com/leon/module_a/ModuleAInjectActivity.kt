package com.leon.module_a

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.leon.common.api.Result
import com.leon.common.base.BaseActivity
import com.leon.module_router.RouterUrls

@Route(path = RouterUrls.MODULE_A_INJECT)
class ModuleAInjectActivity : BaseActivity() {

    @Autowired
    lateinit var name:String

    @Autowired
    @JvmField
    var age:Int = -1

    @Autowired(name = "sex")
    @JvmField
    var girl:Boolean = false

    // 支持解析自定义对象，URL中使用json传递
    @Autowired
    @JvmField
    var obj: Result<String>? = null

    // 使用 withObject 传递 List 和 Map 的实现了
    // Serializable 接口的实现类(ArrayList/HashMap)
    // 的时候，接收该对象的地方不能标注具体的实现类类型
    // 应仅标注为 List 或 Map，否则会影响序列化中类型
    // 的判断, 其他类似情况需要同样处理
    @Autowired
    @JvmField
    var list: List<Result<String>>? = null
    @Autowired
    @JvmField
    var map: Map<String, List<Result<String>>>? = null


    override val layoutId: Int
        get() = R.layout.module_a_activity_inject

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun bindModel() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.module_a_activity_inject)
    }
}
