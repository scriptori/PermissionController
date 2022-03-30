package com.meraki.sm.ui.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.meraki.sm.databinding.MainFragmentBinding
import com.meraki.sm.permissions.PermissionController
import com.meraki.sm.permissions.PermissionListViewModel
import com.meraki.sm.ui.recyclerview.PermissionViewAdapter

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
        permissionListViewModel.permissionList.observe(viewLifecycleOwner, Observer { updateData() })

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

        permissionController.checkPermissions()
        permissionController.updateMissingRequiredList()
    }

    override fun onResume() {
        super.onResume()
        permissionController.updateMissingRequiredList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateData() {
        adapter.apply {
            permissionController.updateRequiredPermissionsStatus()
            permissions.clear()
            permissions.addAll(permissionListViewModel.getDeniedPermissions())
            notifyDataSetChanged()
        }
        binding.startEnrollment.isEnabled = permissionController.areAllPermissionsGranted(requireContext())
    }
}
