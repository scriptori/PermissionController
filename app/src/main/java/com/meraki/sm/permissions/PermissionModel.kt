package com.meraki.sm.permissions

import androidx.annotation.StringRes
import com.meraki.sm.R

data class PermissionModel(
    val name: String,
    @StringRes val displayLabelId: Int,
    @StringRes val rationaleId: Int = R.string.default_permission_rationale,
    val permissions: ArrayList<String>,
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
