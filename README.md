# MyTodo
### 关于项目

​	使用Kotlin+MVVM实现的todo app，功能界面参考微软的Todo软件（只实现了部分功能，功能未完善）。

#### 功能特性

* 项目模块：添加/删除项目，项目负责管理todo任务
* 任务模块：添加/删除任务，标记任务完成情况，标记任务为重要，标记为我的一天，设置提醒时间（发送前台通知），设置过期时间。
* 搜索模块：依据任务名称模糊搜索。



#### 待实现功能

* 项目可移动，任务可重新分组。系统模块的默认数据



#### 技术栈

* Kotlin
* ViewModel + LiveData + Room + AlarmManager + WorkerManager
* navigation + DiaLog + 前台通知

