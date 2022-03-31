package com.meraki.sm.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meraki.sm.R

class PermissionListViewModel : ViewModel() {
    var permissionList = MutableLiveData<MutableList<PermissionModel>>(mutableListOf())

    @SuppressLint("InlinedApi")
    private val _requiredPermissions = mutableListOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionModel(
                "Phone",
                R.string.phone_permission_label,
                permissions = arrayListOf(
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE
                )
            )
        } else {
            PermissionModel(
                "Phone",
                R.string.phone_permission_label,
                permissions = arrayListOf(Manifest.permission.READ_PHONE_STATE)
            )
        },
        PermissionModel(
            "SMS",
            R.string.sms_permission_label,
            permissions = arrayListOf(Manifest.permission.READ_SMS)
        ),
        PermissionModel(
            "Location",
            R.string.location_permission_label,
            permissions = arrayListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ),
        PermissionModel(
            "Files and Media",
            R.string.file_and_media_permission_label,
            permissions = arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    )
    val requiredPermission: List<PermissionModel> by lazy {
        _requiredPermissions.filter { it.condition }
    }

    val requiredPermissionStrings = requiredPermission.map { it.permissions }.flatten().toTypedArray()

    fun getDeniedPermissions(): MutableList<PermissionModel> = requiredPermission.filter {
        it.status == PermissionStatus.DENIED
    }.toMutableList()
}
