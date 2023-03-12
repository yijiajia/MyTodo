package com.example.mytodo.ui.project

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.constants.ProjectSign
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.ui.task.TasksMainActivity

class CustomProjectAdapter(val context: Context, val customProjectList: List<Project>, val viewModel: ProjectViewModel) : RecyclerView.Adapter<CustomProjectAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val icon : ImageView = view.findViewById(R.id.custom_icon)
        val title : TextView = view.findViewById(R.id.custom_title)
        val taskNum : TextView = view.findViewById(R.id.custom_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_project_item,parent,false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val project = customProjectList[holder.adapterPosition]
            val intent = Intent(context, TasksMainActivity::class.java).apply {
                putExtra(Constants.PROJECT_ID,project.id)
                putExtra(Constants.PROJECT_NAME,project.title)
                // TODO 添加默认模块的projectSign参数 or 将默认模块的数据在初始化时导入db
                if (project.id == 0L) {
                    val sign: ProjectSign = when(project.title) {
                        ProjectSign.INTRO.signName -> ProjectSign.INTRO
                        ProjectSign.ZAHUO.signName -> ProjectSign.ZAHUO
                        else -> throw IllegalArgumentException("sign arg is error;project=$project")
                    }
                    putExtra(Constants.PROJECT_SIGN,sign)
                }
            }
            context.startActivity(intent)
        }
        // 长按删除
        holder.itemView.setOnLongClickListener {
            val project = customProjectList[holder.adapterPosition]
            val builder = AlertDialog.Builder(context)
            val diaLogView = LayoutInflater.from(context).inflate(R.layout.dialog_manager_project,parent,false)
            val delProjectBtn : Button = diaLogView.findViewById(R.id.delProject)

            builder.setView(diaLogView)
               /* .setPositiveButton("确定") { dialog, _ ->
                    "确定了".showToast()
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    "取消了".showToast()
                    dialog.dismiss()
                }*/
            val dialog = builder.create()

            delProjectBtn.setOnClickListener {
                Log.d(Constants.MAIN_PAGE_TAG,"准备删除项目;id=${project.id};viewModel=$viewModel")
                viewModel.delProjectById(project.id)
                dialog.dismiss()
            }

            dialog.show()
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = customProjectList[position]
        // 设值
        holder.icon.setImageResource(project.imageId)
        holder.title.text = project.title
        if(project.num > 0 ){
            holder.taskNum.text = project.num.toString()
        }
    }

    override fun getItemCount() = customProjectList.size

}