package me.scriptori.pc.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.scriptori.pc.R

class PermissionListViewModel : ViewModel() {
    var permissionList = MutableLiveData<List<PermissionModel>>(emptyList())

    @SuppressLint("InlinedApi")
    private val _requiredPermissions = listOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionModel(
                R.string.phone_permission_label,
                permissions = listOf(
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE
                )
            )
        } else {
            PermissionModel(
                R.string.phone_permission_label,
                permissions = listOf(Manifest.permission.READ_PHONE_STATE)
            )
        },
        PermissionModel(
            R.string.sms_permission_label,
            permissions = listOf(Manifest.permission.READ_SMS)
        ),
        PermissionModel(
            R.string.location_permission_label,
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ),
        PermissionModel(
            R.string.file_and_media_permission_label,
            permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    )
    val requiredPermission: List<PermissionModel> by lazy {
        _requiredPermissions.filter { it.condition }
    }

    val requiredPermissionStrings = requiredPermission.map { it.permissions }.flatten().toTypedArray()
}
