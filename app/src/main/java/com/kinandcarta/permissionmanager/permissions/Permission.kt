package com.kinandcarta.permissionmanager.permissions

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_PHONE_NUMBERS
import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint

@SuppressLint("InlinedApi")
sealed class Permission(vararg val permissions: PermissionModel) {
    object RequiredPermissions : Permission(
        PermissionModel(CAMERA, "CAMERA"),
        PermissionModel(READ_PHONE_NUMBERS, "READ_PHONE_NUMBERS"),
        PermissionModel(READ_PHONE_STATE, "READ_PHONE_STATE"),
        PermissionModel(READ_SMS, "READ_SMS"),
        PermissionModel(ACCESS_FINE_LOCATION, "ACCESS_FINE_LOCATION"),
        PermissionModel(ACCESS_COARSE_LOCATION, "ACCESS_COARSE_LOCATION"),
        PermissionModel(WRITE_EXTERNAL_STORAGE, "WRITE_EXTERNAL_STORAGE"),
        PermissionModel(READ_EXTERNAL_STORAGE, "READ_EXTERNAL_STORAGE")
    )

//    // Individual permissions
//    object Camera : Permission(PermissionModel(CAMERA))
//
//    // Grouped permissions
//    object DevicePermissions : Permission(
//        PermissionModel(READ_PHONE_NUMBERS), PermissionModel(READ_PHONE_STATE), PermissionModel(READ_SMS)
//    )
//
//    object LocationPermissions : Permission(
//        PermissionModel(ACCESS_FINE_LOCATION), PermissionModel(ACCESS_COARSE_LOCATION)
//    )
//
//    object StoragePermissions : Permission(
//        PermissionModel(WRITE_EXTERNAL_STORAGE), PermissionModel(READ_EXTERNAL_STORAGE)
//    )
}

