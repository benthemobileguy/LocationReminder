package com.udacity.project4.locationreminders.savereminder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

import org.hamcrest.core.Is.`is`

import kotlinx.coroutines.test.runBlockingTest

//@Config(sdk = [Build.VERSION_CODES.P]) // set the target sdk to P for test
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    // Use a fake data source to be injected into the viewmodel
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder1 = ReminderDataItem("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val reminder2_noTitle = ReminderDataItem("", "Description2", "location2", 2.0, 2.0, "2")
    private val reminder3_noLocation = ReminderDataItem("Reminder3", "Description3", "", 3.0, 3.0, "3")

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun onClear_clearsReminderLiveData(){

        //given
        saveReminderViewModel.reminderTitle.value = reminder1.title
        saveReminderViewModel.reminderDescription.value = reminder1.description
        saveReminderViewModel.reminderSelectedLocationStr.value = reminder1.location
        saveReminderViewModel.latitude.value = reminder1.latitude
        saveReminderViewModel.longitude.value = reminder1.longitude
        saveReminderViewModel.reminderId.value = reminder1.id

        //when
        saveReminderViewModel.onClear()

        //then
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderId.getOrAwaitValue(),
            `is`(CoreMatchers.nullValue())
        )

    }

    @Test
    fun editReminder_setsLiveDataOfReminderToBeEdited(){

        //when
        saveReminderViewModel.editReminder(reminder1)

        //then
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            `is`(reminder1.title)
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            `is`(reminder1.description)
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            `is`(reminder1.location)
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            `is`(reminder1.latitude)
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            `is`(reminder1.longitude)
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderId.getOrAwaitValue(),
            `is`(reminder1.id)
        )
    }

    @Test
    fun saveReminder_addsReminderToDataSource() = mainCoroutineRule.runBlockingTest{

        //when
        saveReminderViewModel.saveReminder(reminder1)
        val checkReminder = fakeDataSource.getReminder("1") as com.udacity.project4.locationreminders.data.dto.Result.Success

        //then
        MatcherAssert.assertThat(checkReminder.data.title, `is`(reminder1.title))
        MatcherAssert.assertThat(checkReminder.data.description, `is`(reminder1.description))
        MatcherAssert.assertThat(checkReminder.data.location, `is`(reminder1.location))
        MatcherAssert.assertThat(checkReminder.data.latitude, `is`(reminder1.latitude))
        MatcherAssert.assertThat(checkReminder.data.longitude, `is`(reminder1.longitude))
        MatcherAssert.assertThat(checkReminder.data.id, `is`(reminder1.id))

    }

    @Test
    fun saveReminder_checkLoading()= mainCoroutineRule.runBlockingTest{
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        //when
        saveReminderViewModel.saveReminder(reminder1)

        // Then loading indicator is shown
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), Is.`is`(true))

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then loading indicator is hidden
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            Is.`is`(false)
        )

    }

    @Test
    fun validateData_missingTitle_showSnackbarAndReturnFalse(){

        //when
        val validate = saveReminderViewModel.validateEnteredData(reminder2_noTitle)

        //then
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            Is.`is`(R.string.err_enter_title)
        )
        MatcherAssert.assertThat(validate, Is.`is`(false))
    }

    @Test
    fun validateData_missingLocation_showSnackbarAndReturnFalse(){

        //when
        val validate = saveReminderViewModel.validateEnteredData(reminder3_noLocation)

        //then
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            Is.`is`(R.string.err_select_location)
        )
        MatcherAssert.assertThat(validate, Is.`is`(false))
    }




}


