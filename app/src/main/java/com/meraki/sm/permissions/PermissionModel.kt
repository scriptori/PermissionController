package com.meraki.sm.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.StringRes
import com.meraki.sm.R

data class PermissionModel(
    val name: String,
    @StringRes val displayLabelId: Int,
    @StringRes val rationaleId: Int = R.string.default_permission_rationale,
    val condition: Boolean = true,
    var status: PermissionStatus = PermissionStatus.DENIED
) {
    fun setStatus(isGranted: Boolean) {
        status = if (isGranted) PermissionStatus.GRANTED else PermissionStatus.DENIED
    }
}

enum class PermissionStatus(@StringRes val value: Int) {
    DENIED(R.string.permission_status_denied),
    GRANTED(R.string.permission_status_granted);
}

@SuppressLint("InlinedApi")
val requiredPermissions = mutableListOf(
    PermissionModel(
        Manifest.permission.CAMERA,
        R.string.access_camera_permission,
        R.string.access_camera_permission_rationale
    ),
    PermissionModel(
        Manifest.permission.READ_PHONE_NUMBERS,
        R.string.read_phone_number_permission,
        condition = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    ),
    PermissionModel(Manifest.permission.READ_PHONE_STATE, R.string.read_phone_state_permission),
    PermissionModel(Manifest.permission.READ_SMS, R.string.read_sms_permission),
    PermissionModel(Manifest.permission.ACCESS_FINE_LOCATION, R.string.access_fine_location_permission),
    PermissionModel(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.access_coarse_location),
    PermissionModel(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.write_external_storage),
    PermissionModel(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.read_external_storage)
)

val deniedPermissions
    get() = requiredPermissions.filter { it.status == PermissionStatus.DENIED }

val requiredPermissionNames = requiredPermissions.map { it.name }.toList().toTypedArray()
