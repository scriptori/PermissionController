package me.scriptori.pc.permissions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.fragment.app.Fragment
import me.scriptori.pc.R
import timber.log.Timber
import java.lang.StringBuilder
import java.lang.ref.WeakReference

class PermissionController private constructor(
    private val _fragment: WeakReference<Fragment>,
    private val viewModel: PermissionListViewModel
) {
    companion object {
        fun from(fragment: Fragment, viewModel: PermissionListViewModel) =
            PermissionController(WeakReference(fragment), viewModel)
    }

    private val fragment by lazy {
        _fragment.get() ?: throw IllegalStateException("Fragment has been cleared or released!")
    }
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<String, Boolean>) -> Unit = {}

    private val permissionCheck = fragment.registerForActivityResult(RequestMultiplePermissions()) { results ->
        sendResultAndCleanUp(results)
    }

    fun areAllPermissionsGranted(context: Context) =
        viewModel.requiredPermission.all { it.areGranted(context) }

    fun checkPermissions(context: Context, callback: (Map<String, Boolean>) -> Unit = { handleResults(context, it) }) {
        this.detailedCallback = callback
        when {
            areAllPermissionsGranted(context) -> sendPositiveResult()
            shouldShowPermissionRationale(fragment) -> displayRationale(context, ::requestPermissions)
            else -> requestPermissions()
        }
    }

    fun getDeniedPermissions(context: Context): List<PermissionModel> = viewModel.requiredPermission.filter {
        !it.areGranted(context)
    }.toMutableList()

    fun requestMissingPermission(permission: PermissionModel) {
        when {
            hasPermissions(fragment.requireContext(), permission.permissions) ->
                sendResultAndCleanUp(mapOf(fragment.getString(permission.displayLabelId) to true))
            else -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
                intent.data = uri
                fragment.startActivity(intent)
            }
        }
    }

    fun updateMissingRequiredList(context: Context) {
        viewModel.permissionList.value = getDeniedPermissions(context)
    }

    /*
     * Private Methods
     */

    private fun cleanUp() {
        rationale = null
        callback = {}
        detailedCallback = {}
    }

    private fun displayRationale(
        context: Context,
        requestPermissionCallback: (PermissionModel?) -> Unit,
        permission: PermissionModel? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.dialog_permission_title))
            .setMessage(rationale ?: let {
                val deniedPermissions: String = StringBuilder("\n").also { sb ->
                    getDeniedPermissions(context).let { list ->
                        list.forEach { pm -> sb.append("\t- ${context.getString(pm.displayLabelId)}\n") }
                    }
                }.toString()
                context.getString(R.string.dialog_permission_default_message, deniedPermissions)
            })
            .setCancelable(true)
            .setPositiveButton(context.getString(R.string.dialog_permission_button_positive)) { _, _ ->
                requestPermissionCallback(permission)
            }
            .setNegativeButton(context.getString(R.string.dialog_permission_button_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleResults(context: Context, results: Map<String, Boolean>) {
        results.forEach { (permission, isGranted) ->
            Timber.d("The $permission permission has been ${if (isGranted) "granted" else "denied"}!")
        }
        updateMissingRequiredList(context)
    }

    private fun hasPermissions(context: Context, permissions: List<String>) =
        permissions.all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

    private fun requestPermissions(permission: PermissionModel? = null) {
        permissionCheck.launch(
            permission?.permissions?.toTypedArray() ?: viewModel.requiredPermissionStrings
        )
    }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(viewModel.requiredPermissionStrings.associate { it to true })
    }

    private fun sendResultAndCleanUp(results: Map<String, Boolean>) {
        callback(results.all { it.value })
        detailedCallback(results)
        cleanUp()
    }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        viewModel.requiredPermission.any { it.requiresRationale(fragment) }

    private fun PermissionModel.areGranted(context: Context) =
        hasPermissions(context, this.permissions)

    private fun PermissionModel.requiresRationale(fragment: Fragment) =
        this.permissions.all { fragment.shouldShowRequestPermissionRationale(it) }
}
