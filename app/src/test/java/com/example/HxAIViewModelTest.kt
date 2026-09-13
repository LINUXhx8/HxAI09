package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.HxAIViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HxAIViewModelTest {

    private lateinit var viewModel: HxAIViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = HxAIViewModel(app)
    }

    @Test
    fun testAppLockScreen_correctPasswordUnlocks() {
        assertFalse("App should be locked initially", viewModel.isAppUnlocked.value)

        // Attempt incorrect password
        val wrongAttempt = viewModel.attemptAppUnlock("WRONG_PWD")
        assertFalse(wrongAttempt)
        assertFalse(viewModel.isAppUnlocked.value)

        // Attempt correct password [U6992654341]
        val successAttempt = viewModel.attemptAppUnlock("U6992654341")
        assertTrue(successAttempt)
        assertTrue(viewModel.isAppUnlocked.value)
    }

    @Test
    fun testMediaAction_requiresCorrectPassword() {
        // Test upload password validation [4201451963H]
        val wrongUpload = viewModel.verifyUploadPassword("12345")
        assertFalse(wrongUpload)

        val correctUpload = viewModel.verifyUploadPassword("4201451963H")
        assertTrue(correctUpload)
        assertTrue(viewModel.showUploadMediaDialog.value)
    }

    @Test
    fun testDeleteAction_requiresCorrectPassword() {
        val wrongDelete = viewModel.verifyDeletePassword("00000")
        assertFalse(wrongDelete)

        val correctDelete = viewModel.verifyDeletePassword("4201451963H")
        assertTrue(correctDelete)
    }
}
