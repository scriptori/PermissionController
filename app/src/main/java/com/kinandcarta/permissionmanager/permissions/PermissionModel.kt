package com.kinandcarta.permissionmanager.permissions

import androidx.annotation.StringRes
import com.kinandcarta.permissionmanager.R

data class PermissionModel(
    val name: String,
    val displayLabel: String,
    var status: PermissionStatus = PermissionStatus.UNDEFINED
)

enum class PermissionStatus(@StringRes val value: Int) {
    DENIED(R.string.permission_status_denied),
    GRANTED(R.string.permission_status_granted),
    UNDEFINED(R.string.permission_status_undefined);
}
