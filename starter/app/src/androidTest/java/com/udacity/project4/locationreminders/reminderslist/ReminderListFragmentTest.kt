package com.udacity.project4.locationreminders.reminderslist
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: KoinTest {

    private val dataSource: ReminderDataSource by inject()

    private val reminder1 = ReminderDTO("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val reminder2 = ReminderDTO("Reminder2", "Description2", "location2", 2.0, 2.0, "2")
    private val reminder3 = ReminderDTO("Reminder3", "Description3", "location3", 3.0, 3.0, "3")

    @Before
    fun initRepository() {
        stopKoin()
        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    get(),
                    get()
                )
            }
            single {
                FakeDataSource() as ReminderDataSource
            }
        }

        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }
    }

    @After
    fun cleanupDb() = runBlockingTest {
        dataSource.deleteAllReminders()
    }

    @Test
    fun reminderList_DisplayedInUi() = runBlockingTest{
        // GIVEN - Add active (incomplete) task to the DB
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.saveReminder(reminder3)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment(FragmentScenario.FragmentAction { fragment -> Navigation.setViewNavController(fragment.requireView(), navController) })
        //then
        onView(ViewMatchers.withText(reminder1.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminder2.description)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(ViewMatchers.withText(reminder3.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(IsNot.not(ViewMatchers.isDisplayed())))

    }

    @Test
    fun reminderList_noReminders() = runBlockingTest{
        // GIVEN - Add active (incomplete) task to the DB
        dataSource.deleteAllReminders()

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment(FragmentScenario.FragmentAction { fragment -> Navigation.setViewNavController(fragment.requireView(), navController) })
        //then
        onView(ViewMatchers.withText(R.string.no_data)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminder1.title)).check(ViewAssertions.doesNotExist())

    }

    @Test
    fun clickFab_navigateToReminderFragment() = runBlockingTest {

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment(FragmentScenario.FragmentAction { fragment -> Navigation.setViewNavController(fragment.requireView(), navController) })

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }


}
