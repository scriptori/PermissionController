package me.scriptori.pc.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.scriptori.pc.databinding.RecycleviewItemBinding
import me.scriptori.pc.permissions.PermissionModel
import timber.log.Timber

class PermissionViewAdapter(
    val permissions: MutableList<PermissionModel> = mutableListOf(),
    private val callback: (PermissionModel) -> Unit = {}
) : RecyclerView.Adapter<PermissionViewAdapter.PermissionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = RecycleviewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        val permission  = permissions[position]
        holder.binding.apply {
            permissionName.text = root.context.getString(permission.displayLabelId)
            enablePermissionButton.setOnClickListener {
                Timber.d("Enable ${root.context.getString(permission.displayLabelId)} invoked!")
                callback(permission)
            }
        }
    }

    override fun getItemCount(): Int {
        return permissions.size
    }

    inner class PermissionViewHolder(binding: RecycleviewItemBinding) :
        BindingViewHolder<RecycleviewItemBinding>(binding)
}
