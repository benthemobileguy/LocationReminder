package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, private val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String?>()
    val reminderDescription = MutableLiveData<String?>()
    val reminderSelectedLocationStr = MutableLiveData<String?>()
    val latitude = MutableLiveData<Double?>()
    val longitude = MutableLiveData<Double?>()
    val reminderId = MutableLiveData<String?>()
    val remindingLocationRange = MutableLiveData(30)

    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        latitude.value = null
        longitude.value = null
        reminderId.value = null
    }

    /*Sets the livedata to the reminder to be edited*/
    fun editReminder(reminderData: ReminderDataItem){

        reminderTitle.value = reminderData.title
        reminderDescription.value = reminderData.description
        reminderSelectedLocationStr.value = reminderData.location
        latitude.value = reminderData.latitude
        longitude.value = reminderData.longitude
        reminderId.value = reminderData.id

    }

    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
        }
    }

    fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            onClear()
            navigationCommand.value = NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToReminderListFragment())
        }
    }

    fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

}



