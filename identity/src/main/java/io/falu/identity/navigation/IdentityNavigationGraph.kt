package io.falu.identity.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.falu.identity.IdentityVerificationViewModel
import io.falu.identity.R
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.api.models.UploadMethod
import io.falu.identity.capture.scan.DocumentScanViewModel
import io.falu.identity.screens.ConfirmationScreen
import io.falu.identity.screens.DocumentCaptureMethodsScreen
import io.falu.identity.screens.DocumentSelectionScreen
import io.falu.identity.screens.InitialLoadingScreen
import io.falu.identity.screens.WelcomeScreen
import io.falu.identity.screens.capture.ManualCaptureScreen
import io.falu.identity.screens.capture.ScanCaptureScreen
import io.falu.identity.screens.capture.UploadCaptureScreen
import io.falu.identity.screens.error.ErrorScreen
import io.falu.identity.screens.error.ErrorScreenButton
import io.falu.identity.screens.selfie.SelfieScreen
import io.falu.identity.selfie.FaceScanViewModel

@Composable
internal fun IdentityNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    identityViewModel: IdentityVerificationViewModel,
    documentScanViewModel: DocumentScanViewModel,
    faceScanViewModel: FaceScanViewModel,
    startDestination: String,
    navActions: IdentityVerificationNavActions = remember(navController) {
        IdentityVerificationNavActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(IdentityDestinations.INITIAL_ROUTE) {
            InitialLoadingScreen(identityViewModel = identityViewModel, navActions = navActions)
        }

        composable(IdentityDestinations.WELCOME_ROUTE) {
            WelcomeScreen(viewModel = identityViewModel, navActions = navActions)
        }

        composable(IdentityDestinations.CONFIRMATION_ROUTE) {
            ConfirmationScreen(viewModel = identityViewModel)
        }

        composable(IdentityDestinations.DOCUMENT_SELECTION_ROUTE) {
            DocumentSelectionScreen(
                viewModel = identityViewModel,
                navigateToCaptureMethods = { navActions.navigateToDocumentCaptureMethods(it) },
                navigateToError = {})
        }

        composable(IdentityDestinations.SELFIE_ROUTE) {
            SelfieScreen(
                viewModel = identityViewModel,
                faceScanViewModel = faceScanViewModel,
                navActions = navActions
            )
        }

        composable(
            IdentityDestinations.DOCUMENT_CAPTURE_METHODS_ROUTE,
            arguments = listOf(navArgument("documentType") {
                type = NavType.EnumType(IdentityDocumentType::class.java)
            })
        ) { entry ->
            val identityDocumentType = entry.arguments?.getSerializable("documentType") as IdentityDocumentType
            DocumentCaptureMethodsScreen(identityViewModel, identityDocumentType, navigateToCaptureMethod = {
                when (it) {
                    UploadMethod.AUTO -> navActions.navigateToScanCapture(documentType = identityDocumentType)
                    UploadMethod.MANUAL -> navActions.navigateToManualCapture(documentType = identityDocumentType)
                    UploadMethod.UPLOAD -> navActions.navigateToUploadCapture(documentType = identityDocumentType)
                }
            })
        }

        composable(
            IdentityDestinations.DOCUMENT_CAPTURE_METHOD_UPLOAD_ROUTE,
            arguments = listOf(navArgument("documentType") {
                type = NavType.EnumType(IdentityDocumentType::class.java)
            })
        ) { entry ->
            val identityDocumentType = entry.arguments?.getSerializable("documentType") as IdentityDocumentType
            UploadCaptureScreen(
                viewModel = identityViewModel,
                documentType = identityDocumentType,
                navActions = navActions
            )
        }

        composable(
            IdentityDestinations.DOCUMENT_CAPTURE_METHOD_MANUAL_ROUTE,
            arguments = listOf(navArgument("documentType") {
                type = NavType.EnumType(IdentityDocumentType::class.java)
            })
        ) { entry ->
            val identityDocumentType = entry.arguments?.getSerializable("documentType") as IdentityDocumentType
            ManualCaptureScreen(
                viewModel = identityViewModel,
                documentType = identityDocumentType,
                navActions = navActions
            )
        }

        composable(
            IdentityDestinations.DOCUMENT_CAPTURE_METHOD_SCAN_ROUTE,
            arguments = listOf(navArgument("documentType") {
                type = NavType.EnumType(IdentityDocumentType::class.java)
            })
        ) { entry ->
            val identityDocumentType = entry.arguments?.getSerializable("documentType") as IdentityDocumentType
            ScanCaptureScreen(
                viewModel = identityViewModel,
                documentScanViewModel = documentScanViewModel,
                documentType = identityDocumentType,
                navActions = navActions
            )
        }

        composable(
            ErrorDestination.ROUTE.route,
            arguments = ErrorDestination.ROUTE.arguments
        ) { entry ->

            ErrorScreen(
                title = ErrorDestination.errorTitle(entry) ?: "",
                desc = ErrorDestination.errorDescription(entry) ?: "",
                message = ErrorDestination.errorMessage(entry),
                primaryButton = ErrorScreenButton(
                    text = ErrorDestination.backButtonText(entry) ?: "",
                    onClick = {}
                ),
                secondaryButton = if (ErrorDestination.cancelFlow(entry)) {
                    ErrorScreenButton(text = stringResource(R.string.button_cancel), onClick = {})
                } else {
                    null
                }
            )
        }
    }
}