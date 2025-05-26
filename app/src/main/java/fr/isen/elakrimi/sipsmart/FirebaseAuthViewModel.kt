package fr.isen.elakrimi.sipsmart

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FirebaseAuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signUp(email: String, password: String, fullName: String) {
        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Met à jour le nom complet de l'utilisateur
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _authState.value = AuthState.Success(user)
                        } else {
                            _authState.value = AuthState.Error(updateTask.exception?.message ?: "Profile update failed")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign up failed")
                }
            }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success(auth.currentUser)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        _authState.value = AuthState.Unauthenticated
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Unauthenticated : AuthState()
        data class Success(val user: com.google.firebase.auth.FirebaseUser?) : AuthState()
        data class Error(val errorMessage: String) : AuthState()
    }
}
