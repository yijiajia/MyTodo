# MyTodo
### 关于项目

​	使用Kotlin+MVVM实现的todo app，功能界面参考微软的Todo软件（只实现了部分功能，功能未完善）。

#### 功能特性

* 项目模块：添加/删除项目，项目负责管理todo任务
* 任务模块：添加/删除任务，标记任务完成情况，标记任务为重要，标记为我的一天，设置提醒时间（发送前台通知），设置过期时间。
* 搜索模块：依据任务名称模糊搜索。

#### 实现页面
查看screenshot目录

#### 待实现功能

* 项目可移动，任务可重新分组。系统模块的默认数据
* 任务可分步骤
* UI及部分bug处理



#### 技术栈

* Kotlin
* ViewModel + LiveData + Room + AlarmManager + WorkerManager + EventBus
* navigation + DiaLog + 前台通知



## 功能设计与实现

### 1. 项目模块设计实现

在项目模块中，分为固定模块和自定义模块。其中固定模块分为以下几个模块：

* 我的一天：可以查看当天需要完成的任务列表；
* 重要：可以查看标记为重要的任务列表；
* 计划内：( 未实现
* 已分配：（未实现
* 任务：可以查看未完成的所有任务列表；

而自定义项目模块是提供给用户来将任务归类到项目的功能。

项目模块主要显示：icon + 项目名称 + 包含的任务列表数量 

（没啥好说的，简单的recyclerView实现即可

### 2. 任务列表页面的动态更新

点击项目进入项目后可创建任务，任务是由Recyclerview生成的，由于想要在任务添加/删除时出现列表滑动的效果，所以任务的apater实现了ListAdapter。

```kotlin
class TasksAdapter(val viewModel: TasksViewModel)
    : ListAdapter<Task, TasksAdapter.ViewHolder>(DIFF_CALLBACK) {
        //...
    }
```

#### 任务列表页的操作

另外在搜索页面上也会用到跟任务列表一样的UI，所以将任务列表的UI用fragment实现，方便复用。

##### 任务列表Fragment化

* TasksFragment.kt

````kotlin
class TasksFragment: BaseFragment() {

    override fun getResourceId() = R.layout.fragment_task_list

    lateinit var taskViewModel : TasksViewModel
    private var projectId = 0L
    private var projectName = ""
    private var projectSign : ProjectSign? = null

    private lateinit var adapter: TasksAdapter
    private lateinit var taskRecyclerView: RecyclerView

    private var previousList : List<Task>? = null
    private lateinit var baseActivity: BaseTaskActivity

    // 搜索参数
    var searchName = ""
    var isSearchPage = false

    override fun initView(rootView: View) {
        // 判断当前fragment的Activty是哪个，方便做特殊操作
        baseActivity = if (activity is TasksMainActivity) {
            activity as TasksMainActivity
        }else {
            isSearchPage = true
            activity as SearchMainActivity
        }
        taskViewModel = ViewModelProvider(baseActivity)[TasksViewModel::class.java]

        projectId = baseActivity.intent.getLongExtra(Constants.PROJECT_ID, 0L)
        projectName = baseActivity.intent.getStringExtra(Constants.PROJECT_NAME).toString()
        val serializable = baseActivity.intent.getSerializableExtra(Constants.PROJECT_SIGN)
        if (serializable != null) {
            projectSign = serializable as ProjectSign
        }

        Log.d(Constants.TASK_PAGE_TAG, "projectId = $projectId, projectName= $projectName")
        refreshList("onCreate")

        adapter = TasksAdapter(taskViewModel)
        taskRecyclerView = rootView.findViewById(R.id.task_recycle_view)
        taskRecyclerView.layoutManager = LinearLayoutManager(baseActivity)
        taskRecyclerView.adapter = adapter

        // 下拉刷新
        val swipeRefreshTask: SwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_task)
        swipeRefreshTask.setOnRefreshListener {
            refreshList("refresh")
            swipeRefreshTask.isRefreshing = false   // 取消刷新状态
        }
        
        override fun initEvent(rootView: View) {
            initClickListener()

            initObserve()
    	}
    }
````



##### 任务item操作的封装

对任务的item操作有三个点击事件，分别是标记完成、点击item进入详情页编辑、标记为重要。故构建出TaskItem用来封装item的三个操作

```kotlin
class TaskItem(private val nameText: MaterialTextView?,
               private val checkTaskBtn : ImageButton,
               private val setTaskStartBtn: ImageButton,
               val task: Task) {

    var nameTextEdit: EditText? = null
    var curTaskName : String? = null
    
    fun initItem() {
        flushItemUI()
    }

    fun initClickListener(viewModel: TasksViewModel) {
        // 标记完成按钮
        checkTaskBtn.setOnClickListener {
            val upState = if (task.state == TaskState.DONE) {
                TaskState.DOING
            } else  {
                Log.d(Constants.TASK_PAGE_TAG,"播放动画")
                TaskState.DONE
            }
            task.state = upState
            viewModel.updateTask(task)
            flushItemUI()
            Log.d(Constants.TASK_PAGE_TAG,"update task state id= ${task.id} state for $upState")
        }
        // 标记重要按钮
        setTaskStartBtn.setOnClickListener {
            val isStart = !FlagHelper.containsFlag(task.flag, Task.IS_START)
            if (isStart) {
                task.flag = FlagHelper.addFlag(task.flag, Task.IS_START)
            }else {
                task.flag = FlagHelper.removeFlag(task.flag,Task.IS_START)
            }
            viewModel.setStart(task.id, task.flag)
            updateStartUI()
            Log.d(Constants.TASK_PAGE_TAG,"update task start id= ${task.id} isStart for $isStart")
        }
    }


    fun flushItemUI() {
        updateNameUI()
        updateStartUI()
    }

    fun updateNameUI() {
        /**
         * 从task中获取到名字 或者从输入框获取到名字
         */
        if (curTaskName == null) {
            curTaskName = task.name
        }else {
            curTaskName = if (nameText?.visibility == View.VISIBLE) {
                nameText.text.toString()
            } else {
                nameTextEdit?.text.toString()
            }
        }

        /**
         * checkTaskBtn
         */
        var resId = R.drawable.ic_select
        if (task.state == TaskState.DONE) {
            val spannableString = SpannableString(curTaskName)
            spannableString.setSpan(
                StrikethroughSpan(),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (nameText?.visibility == View.VISIBLE) {
                nameText.text = spannableString
            } else {
                nameTextEdit?.setText(spannableString) /** 划线的效果 **/
            }
            resId = R.drawable.ic_select_check
        }else {
            if (nameText?.visibility == View.VISIBLE) {
                nameText.text = curTaskName
            } else {
                nameTextEdit?.setText(curTaskName)
            }
        }
        checkTaskBtn.setImageResource(resId)
    }

    fun updateStartUI() {
        var startResId = R.drawable.ic_shoucang
        if (FlagHelper.containsFlag(task.flag, Task.IS_START)) {
            startResId = R.drawable.ic_shoucang_check
        }
        setTaskStartBtn.setImageResource(startResId)
    }
}
```



#### 任务详情页的编辑操作

#####  任务的状态设计

任务详情页主要的操作包括：任务item的操作（标记完成、修改任务名、标记为重要），标记为我的一天，任务提醒，添加截止日期，重复（未实现），添加附件（未实现） 等。

> 任务item的操作同样封装在上面的 TaskItem中，直接调用即可，无需再实现

这里的有几个标记功能，标记为我的一天，标记为重要。因为不想新增一个字段来表示0或1的存储，这里将这两个属性为归为同一个字段flag，用int存储，用不同的位来表示对应字段的值，如：

* 当字段值为 1 时，说明标记为重要的；（01)
* 当字段值为 2 时，说明标记为我的一天；（10）
* 当字段值为 3 时，说明标记为重要的且是我的一天；（11）

```kotlin
/**
 * Flag 常量
 */
companion object {
    /** 设为重要的 **/
    const val IS_START = 1
    /** 设为我的一天 **/
    const val IN_ONE_DAY = 2
}
```

**其实就是位运算的一种，用二进制的位来表示不同状态下的真或假**。判断也就比较简单了，通过与，或运算即可：

```kotlin
object FlagHelper {

    /**
     * 添加标识
     */
    fun addFlag(flag: Int, newFlag : Int) : Int {
        return flag.or(newFlag)
    }

    /**
     * 移除标识
     */
    fun removeFlag(flag: Int, newFlag: Int) : Int {
        return flag.and(newFlag.inv())
    }

    /**
     * 判断是否包含该标识
     */
    fun containsFlag(flag: Int, checkFlag: Int) : Boolean {
        return flag.and(checkFlag) == checkFlag
    }
}
```

接下来只要用 `FlagHelper.containsFlag(task.flag, Task.IN_ONE_DAY)` 来判断该任务是否该状态，添加/删除也是同理调用该帮助类即可。



##### 提醒功能的设计

###### UI设计

提醒功能的UI是这样的，日期和时间有对应的DiaLog实现，也有Picker实现，那么只需要通过Button点击切换两个UI即可实现。

![image-20230511171140674](screenshot\自定义dateTimePicker diaLog弹窗.png)

我这里采用DiaLogFragment实现的，通过自定义的DtPickerDiaLogFragment 来管理Button与两个时间Picker组件。遇到的难点在于**在两个时间Picker组件选择好时间后，该怎么跟DiaLogFrament做通信呢**。这里使用了EventBus来做DiaLogFrament和两个时间picker组件对应的fragment做通信。实现如下：

* 日期选择器：DatePickerFragment.kt

```kotlin
class DatePickerFragment : BaseFragment() {

    private lateinit var dp : DatePicker
    lateinit var localDate : LocalDate

    override fun getResourceId() = R.layout.fragment_datepicker

    override fun initView(rootView: View) {
        dp = rootView.findViewById(R.id.datePicker)
    }

    override fun initEvent(rootView: View) {
        /**
         * The month that was set (0-11) for compatibility with java.util.Calendar.
         */
       dp.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
           localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
           EventBus.getDefault().post(DateTimeMessage(localDate))
           findNavController().navigate(R.id.switchTime)
       }
    }


}
```

* 时间选择器：TimePickerFragment.kt

```kotlin
class TimePickerFragment : BaseFragment() {
    override fun getResourceId() = R.layout.fragment_timepicker

    private lateinit var tp : TimePicker
    private lateinit var localTime: LocalTime

    override fun initView(rootView: View) {
        tp = rootView.findViewById(R.id.timePicker)
    }

    override fun initEvent(rootView: View) {
        tp.setOnTimeChangedListener { view, hourOfDay, minute ->
            localTime = LocalTime.of(hourOfDay,minute)
            EventBus.getDefault().post(DateTimeMessage(localTime))
        }
    }
}
```

* 日期和时间的选择器弹窗：DtPickerDiaLogFragment.kt

```kotlin
class DtPickerDiaLogFragment(private val dateTimeClick: DateTimeClickListener) : DialogFragment() {

    private var chooseDate: LocalDate? = null
    private var chooseTime: LocalTime? = null
    private var chooseDateTime : LocalDateTime? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("DtPickerDiaLogFragment","onCreateView")
        val curView = inflater.inflate(R.layout.dialog_datetime_picker, null)

        val navHostFragment : FragmentContainerView = curView.findViewById(R.id.fragment_container_view)
        val switchCalendar : Button = curView.findViewById(R.id.switchCalendar)
        val switchTime : Button = curView.findViewById(R.id.switchTime)
        val cancelDialog : TextView = curView.findViewById(R.id.cancelDialog)
        val saveDateTime : TextView = curView.findViewById(R.id.saveDateTime)

        switchCalendar.setOnClickListener {
            switchCalendar.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.light_blue))
            switchTime.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.gray))
            navHostFragment.findNavController().navigate(R.id.switchCalendar)
        }

        switchTime.setOnClickListener {
            switchCalendar.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.gray))
            switchTime.setTextColor(ContextCompat.getColor(MyToDoApplication.context, R.color.light_blue))
            navHostFragment.findNavController().navigate(R.id.switchTime)
        }

        cancelDialog.setOnClickListener {
            dialog?.dismiss()
        }

        saveDateTime.setOnClickListener {
            chooseDateTime = if (chooseDate == null && chooseTime == null) {
                LocalDateTime.now()
            }else if (chooseDate == null) {
                LocalDateTime.of(LocalDate.now(), chooseTime)
            }else if (chooseTime == null) {
                LocalDateTime.of(chooseDate, LocalTime.now())
            } else {
                LocalDateTime.of(chooseDate, chooseTime)
            }
            Log.d("","选中的时间为：$chooseDateTime")
            dateTimeClick.onSaveDateTimeClick(chooseDateTime!!)
            dialog?.dismiss()
        }

        // 注册
        EventBus.getDefault().register(this)
        return curView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initWindow()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)  // 注销
        super.onDestroy()
    }

    fun initWindow() {
        val window = dialog?.window
        window?.attributes?.width = 800 // 单位px
        window?.attributes?.height = 1450 // 单位px
        window?.attributes?.gravity = Gravity.CENTER    // 居中
    }

    fun getChooseTime() = chooseDateTime


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveDateTime(dateTimeMessage: DateTimeMessage) {
        if (dateTimeMessage.localDate != null) {
            chooseDate = dateTimeMessage.localDate!!
        }
        if (dateTimeMessage.localTime != null) {
            chooseTime = dateTimeMessage.localTime!!
        }
        Log.d("","接收到event消息，chooseDate=$chooseDate,chooseTime=$chooseTime")
    }

}
```

###### 提醒功能设计

提醒功能是采用WorkerManager + AlarmManager实现的，实现流程如下：、

![image-20230511171248615](screenshot\todo任务消息提醒流程.png)



1. 当选择好时间保存后就会提交一次性的后台任务；
2. Worker后台接收到任务后，检查提醒时间，没过期的话检查当前任务是否已经存在闹钟，有的话则取消；
3. 使用AlarmManager设置闹钟，保存当前任务和闹钟id的关系，方便下一次设置时取消该闹钟；
4. 保存下一个闹钟的提醒id，防止PendingIntent 的requestCode重复导致任务提醒失败。

实现如下：

```kotlin
class RemindWorker(context: Context, params: WorkerParameters) : Worker(context, params)  {

    companion object {
        val Tag = "RemindWorker"
    }

    private lateinit var alarmManager: AlarmManager

    @RequiresApi(Build.VERSION_CODES.S)
    override fun doWork(): Result {
        val taskByte = inputData.getByteArray(Constants.TASK_BYTE)
        val task = taskByte?.toObject() as Task
        val projectName = inputData.getString(Constants.PROJECT_NAME)
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.d(Tag,"需要提醒的任务为：task=$task, projectName=$projectName")
        if (LocalDateTime.now().isBefore(task.remindTime)) { // 未执行，发起广播
            alarmTask(task, projectName)
        }

        return Result.success()
    }

    private fun alarmTask(task: Task, projectName: String?) {
        val bundle = Bundle()
        bundle.putByteArray(Constants.TASK, task.toByteArray())
        bundle.putString(Constants.PROJECT_NAME, projectName)
        val intent = Intent(applicationContext, RemindAlarmReceiver::class.java).apply {
            putExtras(bundle)
        }
        val oldAlarmId = Repository.getInteger4Broad(task.id.toString())   // 找到旧的请求id，如果有值的话说明需要重设，取消旧闹钟
        var pi : PendingIntent
        if (oldAlarmId != 0 && LocalDateTime.now().isAfter(task.remindTime)) {
            // 取消闹钟，重设
            pi = PendingIntent.getBroadcast(applicationContext, oldAlarmId, intent, 0)
            alarmManager.cancel(pi)
        }
        var alarmId = Repository.getInteger4Alarm(Constants.ALARM_ID, 0)
        pi = PendingIntent.getBroadcast(applicationContext, alarmId, intent, 0)
        val triggerAtMillis = task.remindTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis , pi)

        Repository.setInteger4Broad(task.id.toString(), alarmId)
        Repository.setInteger4Alarm(Constants.ALARM_ID, ++alarmId)
        Log.d(Tag,
            "闹钟设置成功;taskName=${task.name};remindTime=${task.remindTime};;now=${System.currentTimeMillis()}"
        )
    }

}
```



闹钟时间到了后，通过broadcast广播。所以还需要用Recevier去接收，接收到广播后。发起前台通知即实现了任务提醒功能。

```kotlin
class RemindAlarmReceiver: BroadcastReceiver() {

    private val channelId = "remind"
    private val channelName = "任务提醒"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("RemindAlarmReceiver", "请求收到了.")
        val taskByteArray = intent.getByteArrayExtra(Constants.TASK)
        val task = taskByteArray?.toObject() as Task
        val projectName = intent.getStringExtra(Constants.PROJECT_NAME)
        Log.d("RemindAlarmReceiver","接收到任务 task=$task,projectName=$projectName")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 创建渠道
        // Android8.0 以上才有下面的API
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        
        val intent = Intent(context, EditTaskActivity::class.java).apply {
                putExtra(Constants.TASK, task)
                putExtra(Constants.PROJECT_NAME, projectName)
                setPackage("com.example.mytodo")
          }
           val alarmId = Repository.getAndSet4Alarm(Constants.ALARM_ID, 0)
           val pi = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)
           val notification = NotificationCompat.Builder(context, channelId)   // 必须传入已经创建好的渠道ID
                .setContentTitle("提醒")
                .setContentText(task.name)
                .setSmallIcon(R.drawable.todo)
                .setColor(Color.BLUE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pi)       // 设置内容点击的Intent
                .setAutoCancel(true)        // 点击后自动关闭
                .build()

           manager.notify(1, notification)
           Log.d("RemindAlarmReceiver", "通知发送成功；task=$task")
    }
}
```



#### 搜索功能

搜索功能的UI跟任务列表的UI大体相似，只是多了一个搜索栏。前面将任务列表fragment化了，直接复用。

实现简单就不说了。



### 最后

这是本人在学完Android后搞得第一个练手项目，其中很多编码方式不一定规范，有些功能也未实现（如帐号管理，任务云同步，项目可移动，任务可重新分组，任务可细分步骤等功能）。



还想说下Kotlin真的好用（比起Java)，比如扩展函数的特性。在这个app开发中，有个功能是当编辑框出现的时候，要自动弹出输入法。这里直接用扩展函数把View扩展，EditText这个组件就能直接用了，真方便。

```kotlin
/**
 * 显示软键盘
 * postDelayed：避免界面还没绘制完毕就请求焦点导致不弹出键盘
 */
fun View.showSoftInput(flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    postDelayed({
        requestFocus()
        val inManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inManager.showSoftInput(this, flags)
    },100)
}

/**
 * 隐藏软键盘
 */
fun View.hideSoftInputFromWindow(flags: Int = 0) {
    val inManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inManager.hideSoftInputFromWindow(this.windowToken, flags)
}

// 直接调用，看起来真优雅
editTaskName.showSoftInput()
```



贴下GitHub地址：https://github.com/yijiajia/MyTodo，有兴趣的欢迎给个start

