# Android-MVC-Sample
Android框架模式,MVC的使用

# MVC概念

​	[MVC](https://baike.baidu.com/item/MVC)全名是Model View Controller，是模型(model)－视图(view)－控制器(controller)的缩写，一种软件设计典范，用一种业务逻辑、数据、界面显示分离的方法组织代码，将业务逻辑聚集到一个部件里面，在改进和个性化定制界面及用户交互的同时，不需要重新编写业务逻辑。MVC被独特的发展起来用于映射传统的输入、处理和输出功能在一个逻辑的图形化用户界面的结构中。

# MVC编程模式

MVC 是一种使用 MVC（Model View Controller 模型-视图-控制器）设计创建 Web 应用程序的模式：

- Model（模型）表示应用程序核心（比如数据库记录列表）。
- View（视图）显示数据（数据库记录）。
- Controller（控制器）处理输入（写入数据库记录）。

MVC 模式同时提供了对 HTML、CSS 和 JavaScript 的完全控制。

**Model（模型）**是应用程序中用于处理应用程序数据逻辑的部分。
　　通常模型对象负责在数据库中存取数据。

**View（视图）**是应用程序中处理数据显示的部分。
　　通常视图是依据模型数据创建的。

**Controller（控制器）**是应用程序中处理用户交互的部分。
　　通常控制器负责从视图读取数据，控制用户输入，并向模型发送数据。

MVC 分层有助于管理复杂的应用程序，因为您可以在一个时间内专门关注一个方面。例如，您可以在不依赖业务逻辑的情况下专注于视图设计。同时也让应用程序的测试更加容易。

MVC 分层同时也简化了分组开发。不同的开发人员可同时开发视图、控制器逻辑和业务逻辑。

# MVC模型图
![mvc模型图](http://img.blog.csdn.net/20180307173108992?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvSm9rZXlfd3o=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)


# MVC for Android

在Android开发中，比较流行的开发框架模式采用的是MVC框架模式，采用MVC模式的好处是便于UI界面部分的显示和业务逻辑，数据处理分开。那么Android项目中哪些代码来充当M,V,C角色呢？

1. M层：适合做一些业务逻辑处理，比如数据库存取操作，网络操作，复杂的算法，耗时的任务等都在model层处理。
2. V层：应用层中处理数据显示的部分，XML布局可以视为V层，显示Model层的数据结果。
3. C层：在Android中，Activity处理用户交互问题，因此可以认为Activity是控制器，Activity读取V视图层的数据（当前的请求数据按钮），控制用户点击（请求输入Button），并向Model发送数据请求（发起网络请求，回调给V视图显示数据）。

接下来我们通过一个小项目来解读MVC在Android中的使用。先上一个界面图：

![demo图](http://img.blog.csdn.net/20180307173259177?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvSm9rZXlfd3o=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## Controller控制器

``` kotlin
class MainActivity : AppCompatActivity(), OnGankListener {

    private lateinit var gankModel: GankModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gankModel = GankModelImpl(this)

        btn_request_data.setOnClickListener({
            gankModel.showLoading(this)
            gankModel.getGankList()
        })
    }

    override fun onSuccess(json: String) {
        tv_content.run {
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.default_text_color))
            text = json
        }
    }

    override fun onFailed(msg: String) {
        tv_content.text = msg
        tv_content.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark))
    }

    override fun onTerminate() {
        gankModel.hideLoading()
    }

}
```

从上面代码可以看到，Activity持有了GankModel模型的对象，当用户有点击Button交互的时候，Activity作为Controller控制层收到用户点击的按钮监听，然后向Model模型发起数据请求，也就是调用GankModel对象的方法 getGankList()方法。当Model模型处理数据结束后，通过接口OnGankListener通知View视图层数据处理完毕，View视图层该更新界面UI了。然后View视图层调用onSuccess()方法更新UI。至此，整个MVC框架流程就在Activity中体现出来了。

## Model模型

```kotlin
interface GankModel {

    fun showLoading(context: Context)

    fun hideLoading()

    fun getGankList()

}
```

```kotlin
class GankModelImpl constructor(private val view: OnGankListener) : GankModel {

    private var loadingDialog: AlertDialog? = null

    override fun showLoading(context: Context) {
        if (loadingDialog == null) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("请求中...")
            loadingDialog = builder.create()
        }
        loadingDialog!!.show()
    }

    override fun hideLoading() {
        if (loadingDialog != null)
            loadingDialog!!.dismiss()
    }

    override fun getGankList() {
        val api: GankService = RetrofitModel().createApi(GankService::class.java, GankService.BASE_URL)

        api.getGankList("Android", 5, 1)
                .compose(RxSchedulers.ioMain())
                .doOnTerminate {
                    view.onTerminate()
                }
                .subscribe({
                    view.onSuccess(it.string())
                }, {
                    view.onFailed(it.message!!)
                })
    }
}
```

以上代码看出，这里设计了一个GankModel模型接口，然后实现了接口GankModelImpl类。controller控制器activity调用GankModelImpl类中的方法发起网络请求，然后通过实现OnGankListener接口来获得网络请求的结果通知View视图层更新UI 。至此，Activity就将View视图显示和Model模型数据处理隔离开了。activity担当contronller完成了model和view之间的协调作用。

# MVC使用总结

1. 利用MVC设计模式，使得这个小项目有了很好的可扩展和维护性，当需要改变UI显示的时候，无需修改Contronller（控制器）Activity的代码和Model（模型）GankModel模型中的业务逻辑代码，很好的将业务逻辑和界面显示分离。
2. 在Android项目中，业务逻辑，数据处理等担任了Model（模型）角色，XML界面显示等担任了View（视图）角色，Activity担任了Contronller（控制器）角色。contronller（控制器）是一个中间桥梁的作用，通过接口通信来协同 View（视图）和Model（模型）工作，起到了两者之间的通信作用。
3. 什么时候适合使用MVC设计模式？当然一个小的项目且无需频繁修改需求就不用MVC框架来设计了，那样反而觉得代码过度设计，代码臃肿。一般在大的项目中，且业务逻辑处理复杂，页面显示比较多，需要模块化设计的项目使用MVC就有足够的优势了。
4. 在MVC模式中我们发现，其实控制器Activity主要是起到解耦作用，将View视图和Model模型分离，虽然Activity起到交互作用，但是找Activity中有很多关于视图UI的显示代码，因此View视图和Activity控制器并不是完全分离的，也就是说一部分View视图和Contronller控制器Activity是绑定在一个类中的。
5. MVC的优点：
   - 耦合性低。所谓耦合性就是模块代码之间的关联程度。利用MVC框架使得View（视图）层和Model（模型）层可以很好的分离，这样就达到了解耦的目的，所以耦合性低，减少模块代码之间的相互影响。
   - 可扩展性好。由于耦合性低，添加需求，扩展代码就可以减少修改之前的代码，降低bug的出现率。
   - 模块职责划分明确。主要划分层M,V,C三个模块，利于代码的维护。

---
源码地址：[Github：Android-MVC-Sample](https://github.com/wz1509/Android-MVC-Sample)
