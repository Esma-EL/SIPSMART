package fr.isen.elakrimi.sipsmart

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseAuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _hydrationGoal = MutableStateFlow("2.0L") // valeur par défaut
    val hydrationGoal: StateFlow<String> = _hydrationGoal

    fun updateHydrationGoal(goal: String) {
        _hydrationGoal.value = goal
    }
    fun saveHydrationGoalToFirebase(goal: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val user = auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.set(mapOf("hydrationGoal" to goal))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("Utilisateur non connecté"))
        }
    }

    fun fetchHydrationGoalFromFirebase() {
        val user = auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val goal = document.getString("hydrationGoal") ?: "2000 ml"
                    _hydrationGoal.value = goal
                }
                .addOnFailureListener {
                    _hydrationGoal.value = "2000 ml" // valeur par défaut
                }

        }
    }

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
    fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
    }
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Unauthenticated : AuthState()
        data class Success(val user: com.google.firebase.auth.FirebaseUser?) : AuthState()
        data class Error(val errorMessage: String) : AuthState()
    }
}


