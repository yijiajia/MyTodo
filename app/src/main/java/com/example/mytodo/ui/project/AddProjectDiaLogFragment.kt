package com.example.mytodo.ui.project

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.mytodo.R
import com.example.mytodo.logic.domain.constants.Constants
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.showToast

class AddProjectDiaLogFragment(val viewModel: ProjectViewModel) : DialogFragment() {

    /*internal lateinit var listener : NoticeDiaLogListener

    interface NoticeDiaLogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }
*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = requireActivity().layoutInflater.inflate(R.layout.dialog_add_project,null)
            builder.setView(view)
                .setTitle("创建组")
                .setPositiveButton(R.string.add_project) { dialog, _ ->
                    val editName: EditText? = view.findViewById(R.id.projectName)
                    val projectName = editName?.text.toString()
                    Log.d(Constants.MAIN_PAGE_TAG,"add project name is $projectName")
                    if ("null" == projectName && projectName.isEmpty()) {
                        "输入为空，不能保存".showToast()
                    }else {
                        viewModel.insert(Project(projectName,0,R.drawable.menu))
                       /* CoroutineScope(Dispatchers.IO).launch {
                            Repository.saveProject(Project(projectName,0,R.drawable.task))
                            // 使用liveData调用接口就完全不行
                           *//* viewModel.saveProject(Project(projectName,0,R.drawable.task))
                                .observe(viewLifecycleOwner){
                                    Log.d(Constants.MAIN_PAGE_TAG,"save project is suc")
                               }*//*
                        }*/
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                   dialog.dismiss()
                }
            builder.create()
        } ?: throw java.lang.IllegalStateException("Activity not found")
    }

}