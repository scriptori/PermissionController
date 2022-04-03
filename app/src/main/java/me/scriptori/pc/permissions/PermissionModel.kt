package me.scriptori.pc.permissions

import androidx.annotation.StringRes
import me.scriptori.pc.R

data class PermissionModel(
    @StringRes val displayLabelId: Int,
    @StringRes val rationaleId: Int = R.string.default_permission_rationale,
    val permissions: List<String>,
    val condition: Boolean = true
)
