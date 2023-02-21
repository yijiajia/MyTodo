package com.example.mytodo.ui.project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.logic.domain.Constants
import com.example.mytodo.logic.mapper.ProjectVo
import com.example.mytodo.ui.task.TasksMainActivity

class DefaultProjectAdapter(private val context : Context, val defaultList : List<ProjectVo>) : RecyclerView.Adapter<DefaultProjectAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val icon : ImageView = view.findViewById(R.id.default_icon)
        val title : TextView = view.findViewById(R.id.default_title)
        val taskNum : TextView = view.findViewById(R.id.default_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.default_project_item,parent,false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val project = defaultList[holder.adapterPosition]
            val intent = Intent(context, TasksMainActivity::class.java).apply {
                putExtra(Constants.PROJECT_NAME,project.sign.signName)
                putExtra(Constants.PROJECT_SIGN,project.sign)
            }
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = defaultList[position]
        // 设值
        holder.icon.setImageResource(project.imageId)
        holder.title.text = project.sign.signName
        if(project.num > 0 ){
            holder.taskNum.text = project.num.toString()
        }
    }

    override fun getItemCount() = defaultList.size

}