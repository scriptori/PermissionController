package com.meraki.sm.ui.main

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.meraki.sm.R
import com.meraki.sm.databinding.MainFragmentBinding
import com.meraki.sm.permissions.PermissionController
import com.meraki.sm.permissions.PermissionListViewModel
import com.meraki.sm.permissions.PermissionModel
import com.meraki.sm.permissions.deniedPermissions
import com.meraki.sm.ui.recyclerview.PermissionViewAdapter
import timber.log.Timber

class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private val permissionManager = PermissionController.from(this)

    private val binding by lazy { MainFragmentBinding.inflate(layoutInflater) }
    private val adapter = PermissionViewAdapter(callback = ::requestSinglePermission)
    private val permissionListViewModel: PermissionListViewModel by lazy { PermissionListViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Start observing the permission list view model
        permissionListViewModel.permissionList.observe(viewLifecycleOwner, Observer {
            adapter.apply {
                permissionManager.updateRequiredPermissionsStatus()
                permissions.clear()
                permissions.addAll(deniedPermissions)
                notifyDataSetChanged()
            }
        })
        binding.apply {
            permissionRecyclerView.also { rv ->
                rv.layoutManager = LinearLayoutManager(root.context)
                rv.adapter = adapter
            }
            startEnrollment.setOnClickListener { checkPermissions() }
            clearPermissions.setOnClickListener {
                val manager = root.context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                manager.clearApplicationUserData()
            }
        }
    }

    private fun checkPermissions() {
        permissionManager
            .rationale(getString(R.string.system_manager_permission_rationale))
            .checkPermissions { handleResults(it) }
    }

    private fun requestSinglePermission(permission: PermissionModel) {
        permissionManager
            .rationale(getString(permission.rationaleId))
            .request(permission) { handleResults(it) }
    }

    private fun handleResults(result: Map<String, Boolean>) {
        result.forEach { entry ->
            Timber.d("entry: $entry")
        }
        permissionManager.updateRequiredPermissionsStatus()
        permissionListViewModel.permissionList.value = deniedPermissions
    }
}
