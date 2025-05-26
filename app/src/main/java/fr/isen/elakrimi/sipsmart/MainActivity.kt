package fr.isen.elakrimi.sipsmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import fr.isen.elakrimi.sipsmart.components.NavBar
import fr.isen.elakrimi.sipsmart.components.NavItem
import fr.isen.elakrimi.sipsmart.connexion.LoginScreen
import fr.isen.elakrimi.sipsmart.connexion.SignUpScreen
import fr.isen.elakrimi.sipsmart.screen.WelcomeScreen
import fr.isen.elakrimi.sipsmart.ui.theme.SIPSMARTTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = FirebaseAuthViewModel()

        setContent {
            SIPSMARTTheme {

                var currentScreen by remember { mutableStateOf("welcome") }
                var selectedNavItem by remember { mutableStateOf(NavItem.Home) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                val authState by viewModel.authState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Auth state listener
                LaunchedEffect(authState) {
                    when (authState) {
                        is FirebaseAuthViewModel.AuthState.Success -> currentScreen = "home"
                        is FirebaseAuthViewModel.AuthState.Error -> {
                            errorMessage = (authState as FirebaseAuthViewModel.AuthState.Error).errorMessage
                        }
                        else -> Unit
                    }
                }

                // Snackbar for errors
                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        errorMessage = null
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (currentScreen == "home") {
                            NavBar(
                                selectedItem = selectedNavItem,
                                onItemSelected = { selectedNavItem = it }
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->

                    when (currentScreen) {
                        "welcome" -> WelcomeScreen(
                            onNavigate = { currentScreen = "signup" },
                            onLoginClick = { currentScreen = "login" }
                        )

                        "signup" -> SignUpScreen(
                            viewModel = viewModel,
                            onAlreadyHaveAccountClick = { currentScreen = "login" },
                            onSignUpError = { errorMessage = it }
                        )

                        "login" -> LoginScreen(
                            viewModel = viewModel,
                            onSignUpClick = { currentScreen = "signup" },
                            onLoginError = { errorMessage = it }
                        )

                        "home" -> HomePage(
                            selectedNavItem = selectedNavItem,
                            onNavItemSelected = { selectedNavItem = it },
                            onLogout = {
                                viewModel.signOut()
                                currentScreen = "welcome"
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
