package io.falu.identity.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.navigation.IdentityDestinations.CONFIRMATION_ROUTE
import io.falu.identity.navigation.IdentityDestinations.DOCUMENT_CAPTURE_METHODS_ROUTE
import io.falu.identity.navigation.IdentityDestinations.DOCUMENT_CAPTURE_METHOD_MANUAL_ROUTE
import io.falu.identity.navigation.IdentityDestinations.DOCUMENT_CAPTURE_METHOD_SCAN_ROUTE
import io.falu.identity.navigation.IdentityDestinations.DOCUMENT_CAPTURE_METHOD_UPLOAD_ROUTE
import io.falu.identity.navigation.IdentityDestinations.DOCUMENT_SELECTION_ROUTE
import io.falu.identity.navigation.IdentityDestinations.SELFIE_ROUTE
import io.falu.identity.navigation.IdentityDestinations.WELCOME_ROUTE
import io.falu.identity.navigation.IdentityScreens.CONFIRMATION

private object IdentityScreens {
    const val INITIAL = "initial"
    const val WELCOME = "welcome"
    const val SUPPORT = "support"
    const val DOCUMENT_SELECTION = "document_selection"
    const val DOCUMENT_CAPTURE_METHODS = "document_capture_methods/{documentType}"
    const val DOCUMENT_CAPTURE_METHOD_SCAN = "document_capture_method_scan/{documentType}"
    const val DOCUMENT_CAPTURE_METHOD_MANUAL = "document_capture_method_manual/{documentType}"
    const val DOCUMENT_CAPTURE_METHOD_UPLOAD = "document_capture_method_upload/{documentType}"
    const val CONFIRMATION = "confirmation"
    const val SELFIE = "selfie"
    const val ERROR = "error/{workflow-error}"
}

internal class IdentityVerificationNavActions(private val navController: NavController) {
    fun navigateToWelcome() {
        navController.navigate(WELCOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToDocumentSelection() {
        navController.navigate(DOCUMENT_SELECTION_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToConfirmation() {
        navController.navigate(CONFIRMATION_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToDocumentCaptureMethods(documentType: IdentityDocumentType) {
        val bundle = Bundle().apply {
            putSerializable("documentType", documentType) // or putSerializable if using Serializable
        }

        navController.navigate(DOCUMENT_CAPTURE_METHODS_ROUTE.replace("{documentType}", documentType.name)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }

        navController.currentBackStackEntry?.arguments?.putAll(bundle)
    }

    fun navigateToScanCapture(documentType: IdentityDocumentType) {
        val bundle = Bundle().apply {
            putSerializable("documentType", documentType)
        }

        navController.navigate(DOCUMENT_CAPTURE_METHOD_SCAN_ROUTE.replace("{documentType}", documentType.name)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }

        navController.currentBackStackEntry?.arguments?.putAll(bundle)
    }

    fun navigateToManualCapture(documentType: IdentityDocumentType) {
        val bundle = Bundle().apply {
            putSerializable("documentType", documentType)
        }

        navController.navigate(DOCUMENT_CAPTURE_METHOD_MANUAL_ROUTE.replace("{documentType}", documentType.name)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }

        navController.currentBackStackEntry?.arguments?.putAll(bundle)
    }

    fun navigateToUploadCapture(documentType: IdentityDocumentType) {
        val bundle = Bundle().apply {
            putSerializable("documentType", documentType)
        }

        navController.navigate(DOCUMENT_CAPTURE_METHOD_UPLOAD_ROUTE.replace("{documentType}", documentType.name)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }

        navController.currentBackStackEntry?.arguments?.putAll(bundle)
    }

    fun navigateToSelfie() {
        navController.navigate(SELFIE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToError(error: ErrorDestination) {
        navController.navigateTo(error)
    }
}

object IdentityDestinations {
    const val INITIAL_ROUTE = IdentityScreens.INITIAL
    const val WELCOME_ROUTE = IdentityScreens.WELCOME
    const val DOCUMENT_SELECTION_ROUTE = IdentityScreens.DOCUMENT_SELECTION
    const val DOCUMENT_CAPTURE_METHODS_ROUTE = IdentityScreens.DOCUMENT_CAPTURE_METHODS
    const val DOCUMENT_CAPTURE_METHOD_SCAN_ROUTE = IdentityScreens.DOCUMENT_CAPTURE_METHOD_SCAN
    const val DOCUMENT_CAPTURE_METHOD_MANUAL_ROUTE = IdentityScreens.DOCUMENT_CAPTURE_METHOD_MANUAL
    const val DOCUMENT_CAPTURE_METHOD_UPLOAD_ROUTE = IdentityScreens.DOCUMENT_CAPTURE_METHOD_UPLOAD
    const val CONFIRMATION_ROUTE = CONFIRMATION
    const val SELFIE_ROUTE = IdentityScreens.SELFIE
    const val ERROR_ROUTE = IdentityScreens.ERROR
    const val SUPPORT_ROUTE = IdentityScreens.SUPPORT
}