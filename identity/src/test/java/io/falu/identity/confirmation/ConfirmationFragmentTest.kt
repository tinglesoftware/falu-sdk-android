package io.falu.identity.confirmation

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import io.falu.identity.IdentityVerificationResult
import io.falu.identity.IdentityVerificationResultCallback
import io.falu.identity.R
import io.falu.identity.databinding.FragmentConfirmationBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = "AndroidManifest.xml")
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ConfirmationFragmentTest {
    private val mockVerificationResultCallback = mock<IdentityVerificationResultCallback>()

    @Test
    fun testButtonClickFinishesWithComplete() {
        launchConfirmationFragment { binding, _ ->
            binding.buttonFinish.callOnClick()

            verify(mockVerificationResultCallback).onFinishWithResult(
                eq(IdentityVerificationResult.Succeeded)
            )
        }
    }

    private fun launchConfirmationFragment(block: (binding: FragmentConfirmationBinding, navController: TestNavHostController) -> Unit) =
        launchFragmentInContainer(themeResId = R.style.Theme_MaterialComponents) {
            ConfirmationFragment()
        }.onFragment {

            it.callback = mockVerificationResultCallback

            val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

            navController.setGraph(R.navigation.identity_verification_nav_graph)

            navController.setCurrentDestination(R.id.fragment_welcome)

            Navigation.setViewNavController(it.requireView(), navController)

            block(FragmentConfirmationBinding.bind(it.requireView()), navController)
        }
}