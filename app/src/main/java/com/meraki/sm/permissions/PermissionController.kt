package com.meraki.sm.permissions

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.meraki.sm.R
import java.lang.ref.WeakReference

class PermissionController private constructor(private val fragment: WeakReference<Fragment>) {
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<String, Boolean>) -> Unit = {}

    private val permissionCheck =
        fragment.get()?.registerForActivityResult(RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    companion object {
        fun from(fragment: Fragment) = PermissionController(WeakReference(fragment))
    }

    fun rationale(description: String): PermissionController {
        rationale = description
        return this
    }

    fun request(permission: PermissionModel, callback: (Map<String, Boolean>) -> Unit) {
        this.detailedCallback = callback
        handleSinglePermissionRequest(permission)
    }

    fun updateRequiredPermissionsStatus() {
        fragment.get()?.let { fragment ->
            requiredPermissions.forEach { permission ->
                permission.setStatus(hasPermission(fragment, permission.name))
            }
        }
    }

    fun checkPermissions(callback: (Map<String, Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    private fun handleSinglePermissionRequest(permission: PermissionModel) {
        fragment.get()?.let { fragment ->
            when {
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment, ::requestPermissions, permission)
                else -> requestPermissions(permission)
            }
        }
    }

    private fun handlePermissionRequest() {
        fragment.get()?.let { fragment ->
            when {
                areAllPermissionsGranted(fragment) -> sendPositiveResult()
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
            .setMessage(rationale ?: fragment.getString(R.string.dialog_permission_default_message))
            .setCancelable(false)
            .setPositiveButton(fragment.getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissionCallback(permission)
            }
            .show()
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(requiredPermissionNames.associate { it to true })
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
        permissionCheck?.launch(if (permission != null) arrayOf(permission.name) else requiredPermissionNames)
    }

    private fun areAllPermissionsGranted(fragment: Fragment) =
        requiredPermissions.all { it.isGranted(fragment) }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        requiredPermissions.any { it.requiresRationale(fragment) }

    private fun PermissionModel.isGranted(fragment: Fragment) =
        hasPermission(fragment, this.name)

    private fun PermissionModel.requiresRationale(fragment: Fragment) =
        fragment.shouldShowRequestPermissionRationale(this.name)

    private fun hasPermission(fragment: Fragment, permission: String) =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
}
