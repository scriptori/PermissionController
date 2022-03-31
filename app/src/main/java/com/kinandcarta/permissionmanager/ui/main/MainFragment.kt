package com.kinandcarta.permissionmanager.ui.main

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kinandcarta.permissionmanager.databinding.MainFragmentBinding
import com.kinandcarta.permissionmanager.permissions.Permission
import com.kinandcarta.permissionmanager.permissions.PermissionManager
import com.kinandcarta.permissionmanager.ui.recyclerview.PermissionViewAdapter

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding by lazy { MainFragmentBinding.inflate(layoutInflater) }

    private val permissionManager = PermissionManager.from(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.permissionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PermissionViewAdapter(context)
        }

        binding.everything.setOnClickListener {
            permissionManager
                // Check all permissions without bundling them
                .request(Permission.RequiredPermissions)
                .rationale("System Manager requires permission for enrollment")
                .checkDetailedPermission { result ->
                    if (result.all { it.value }) {
                        success("System Manager has all the required permission for enrollment")
                    } else {
                        showErrorDialog(result)
                    }
                }
        }

        binding.clear.setOnClickListener {
            val manager = requireContext().getSystemService(ACTIVITY_SERVICE) as ActivityManager
            manager.clearApplicationUserData()
        }
    }

    private fun showErrorDialog(result: Map<String, Boolean>) {
        val message = result.entries.fold("") { message, entry ->
            message + "${entry.key} permission: ${entry.value}\n"
//            message + "${getErrorMessageFor(entry.key)}: ${entry.value}\n"
        }
        Log.i("TAG", message)
        AlertDialog.Builder(requireContext())
            .setTitle("Missing permissions")
            .setMessage(message)
            .show()
    }

//    private fun getErrorMessageFor(permission: Permission) = when (permission) {
//        Permission.Camera -> "Camera permission: "
//        Permission.LocationPermissions -> "Location permission"
//        Permission.StoragePermissions -> "Storage permission"
//        else -> "Unknown permission"
//    }

    private fun success(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .withColor(Color.parseColor("#09AF00"))
            .show()
    }

    private fun error(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .withColor(Color.parseColor("#B00020"))
            .show()
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }
}