package com.udacity.project4.locationreminders.data.local
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    private val reminder1 = ReminderDTO("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val reminder2 = ReminderDTO("Reminder2", "Description2", "location2", 2.0, 2.0, "2")
    private val reminder3 = ReminderDTO("Reminder3", "Description3", "location3", 3.0, 3.0, "3")


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertRemindersAndGetAll() = runBlockingTest {
        // GIVEN
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)


        // WHEN
        val loaded = database.reminderDao().getReminders()

        // THEN
        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(3))

    }


    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN

        database.reminderDao().saveReminder(reminder1)


        // WHEN
        val loaded = database.reminderDao().getReminderById(reminder1.id)

        // THEN
        MatcherAssert.assertThat<ReminderDTO>(loaded as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`(reminder1.title))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`(reminder1.description))
        MatcherAssert.assertThat(loaded.location, CoreMatchers.`is`(reminder1.location))
        MatcherAssert.assertThat(loaded.latitude, CoreMatchers.`is`(reminder1.latitude))
        MatcherAssert.assertThat(loaded.longitude, CoreMatchers.`is`(reminder1.longitude))
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(reminder1.id))

    }

    @Test
    fun insertRemindersAndDeleteAll()= runBlockingTest{
        // GIVEN - Insert a task.
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        database.reminderDao().deleteAllReminders()

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminders()

        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(0))

    }

    @Test
    fun insertRemindersAndDeleteReminderById()= runBlockingTest{
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        database.reminderDao().deleteReminderById(reminder1.id)

        val loaded = database.reminderDao().getReminders()
        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(2))
        MatcherAssert.assertThat(loaded[0].id, CoreMatchers.`is` (reminder2.id))

    }

    @Test
    fun returnsError()= runBlockingTest{
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        database.reminderDao().deleteReminderById(reminder1.id)

        val loaded = database.reminderDao().getReminders()
        MatcherAssert.assertThat(loaded.size, CoreMatchers.`is`(2))
        MatcherAssert.assertThat(loaded[0].id, CoreMatchers.`is` (reminder2.id))

    }


}
