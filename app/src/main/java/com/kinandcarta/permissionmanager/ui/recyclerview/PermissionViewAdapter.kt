package com.kinandcarta.permissionmanager.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kinandcarta.permissionmanager.databinding.RecycleviewItemBinding
import com.kinandcarta.permissionmanager.permissions.Permission

class PermissionViewAdapter(
    private  val context: Context,
    private val permissionList: MutableList<Permission> = mutableListOf()
) : RecyclerView.Adapter<PermissionViewAdapter.PermissionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = RecycleviewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        val permission  = permissionList[position]
        holder.binding.permissionname.text = permission.permissions[0].name
        holder.binding.permissionStatus.text = context.getString(permission.permissions[0].status.value)
    }

    override fun getItemCount(): Int {
        return permissionList.size
    }

    inner class PermissionViewHolder(binding: RecycleviewItemBinding) :
        BindingViewHolder<RecycleviewItemBinding>(binding)
}