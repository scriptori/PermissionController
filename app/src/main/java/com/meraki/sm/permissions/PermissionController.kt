package com.meraki.sm.permissions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.fragment.app.Fragment
import com.meraki.sm.R
import java.lang.StringBuilder
import java.lang.ref.WeakReference

class PermissionController private constructor(
    private val fragment: WeakReference<Fragment>,
    private val viewModel: PermissionListViewModel
) {
    companion object {
        fun from(fragment: Fragment, viewModel: PermissionListViewModel) =
            PermissionController(WeakReference(fragment), viewModel)
    }

    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<String, Boolean>) -> Unit = {}

    private val permissionCheck =
        fragment.get()?.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    fun requestSinglePermission(permission: PermissionModel, callback: (Map<String, Boolean>) -> Unit) {
        fragment.get()?.let { fragment ->
            when {
                hasPermissions(fragment.requireContext(), permission.permissions) ->
                    sendResultAndCleanUp(mapOf(permission.name to true))
                else -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
                    intent.data = uri
                    fragment.startActivity(intent)
                }
            }
        }
    }

    fun updateRequiredPermissionsStatus() {
        fragment.get()?.let { fragment ->
            viewModel.requiredPermission.forEach { permission ->
                permission.setStatus(hasPermissions(fragment.requireContext(), permission.permissions))
            }
        }
    }

    fun checkPermissions(callback: (Map<String, Boolean>) -> Unit) {
        fragment.get()?.let { fragment ->
            this.detailedCallback = callback
            when {
                areAllPermissionsGranted(fragment.requireContext()) -> sendPositiveResult()
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment, ::requestPermissions)
                else -> requestPermissions()
            }
        }
    }

    private fun displayRationale(
        fragment: Fragment,
        requestPermissionCallback: (PermissionModel?) -> Unit,
        permission: PermissionModel? = null
    ) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.dialog_permission_title))
            .setMessage(rationale ?: let {
                updateRequiredPermissionsStatus()
                val deniedPermissions: String = StringBuilder("\n").also { sb ->
                    viewModel.getDeniedPermissions().let { list ->
                        list.forEach { pm -> sb.append("\t- ${pm.name}\n") }
                    }
                }.toString()
                fragment.getString(R.string.dialog_permission_default_message, deniedPermissions)
            })
            .setCancelable(true)
            .setPositiveButton(fragment.getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissionCallback(permission)
            }
            .setNegativeButton(fragment.getString(R.string.dialog_permission_button_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(viewModel.requiredPermissionStrings.associate { it to true })
    }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults)
        cleanUp()
    }

    private fun cleanUp() {
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun requestPermissions(permission: PermissionModel? = null) {
        permissionCheck?.launch(
            permission?.permissions?.toTypedArray() ?: viewModel.requiredPermissionStrings
        )
    }

    fun areAllPermissionsGranted(context: Context) =
        viewModel.requiredPermission.all { it.areGranted(context) }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        viewModel.requiredPermission.any { it.requiresRationale(fragment) }

    private fun PermissionModel.areGranted(context: Context) =
        hasPermissions(context, this.permissions)

    private fun PermissionModel.requiresRationale(fragment: Fragment) =
        this.permissions.all { fragment.shouldShowRequestPermissionRationale(it) }

    private fun hasPermissions(context: Context, permissions: ArrayList<String>) =
        permissions.all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
}
