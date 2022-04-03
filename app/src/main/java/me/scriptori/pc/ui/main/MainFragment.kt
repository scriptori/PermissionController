package me.scriptori.pc.ui.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.scriptori.pc.databinding.MainFragmentBinding
import me.scriptori.pc.permissions.PermissionController
import me.scriptori.pc.permissions.PermissionListViewModel
import me.scriptori.pc.ui.recyclerview.PermissionViewAdapter

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding by lazy { MainFragmentBinding.inflate(layoutInflater) }
    private val permissionListViewModel: PermissionListViewModel by lazy { PermissionListViewModel() }
    private val permissionController = PermissionController.from(this, permissionListViewModel)
    private val adapter = PermissionViewAdapter(callback = permissionController::requestMissingPermission)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Start observing the permission list view model
        permissionListViewModel.permissionList.observe(viewLifecycleOwner) { updateData() }

        binding.apply {
            permissionRecyclerView.also { rv ->
                rv.layoutManager = LinearLayoutManager(root.context)
                rv.adapter = adapter
            }
            startEnrollment.apply {
                isEnabled = permissionController.areAllPermissionsGranted(root.context)
                setOnClickListener {
                    Toast.makeText(binding.root.context, "Initiating enrollment...", Toast.LENGTH_LONG).show()
                }
            }
            clearPermissions.setOnClickListener {
                val manager = root.context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                manager.clearApplicationUserData()
            }
        }

        permissionController.checkPermissions(binding.root.context)
        permissionController.updateMissingRequiredList(binding.root.context)
    }

    override fun onResume() {
        super.onResume()
        permissionController.updateMissingRequiredList(binding.root.context)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData() {
        adapter.apply {
            permissions.clear()
            permissions.addAll(permissionController.getDeniedPermissions(binding.root.context))
            notifyDataSetChanged()
        }
        binding.startEnrollment.isEnabled = permissionController.areAllPermissionsGranted(binding.root.context)
    }
}
