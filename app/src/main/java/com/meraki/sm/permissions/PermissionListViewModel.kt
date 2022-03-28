package com.meraki.sm.permissions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionListViewModel : ViewModel() {
    var permissionList = MutableLiveData(deniedPermissions)
}
