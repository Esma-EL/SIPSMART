package fr.isen.elakrimi.sipsmart.connexion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel
import fr.isen.elakrimi.sipsmart.R  // N’oublie pas d’importer ton R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: FirebaseAuthViewModel,
    onSignUpClick: () -> Unit,
    onLoginError: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF98E8E))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo en haut, agrandi
        Image(
            painter = painterResource(id = R.drawable.logo_sipsmart),
            contentDescription = "Logo SipSmart",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(0.7f),  // Largeur à 70% de l'écran (ajuste comme tu veux)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.White) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.White) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(color = Color.White)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(email, password)
                } else {
                    onLoginError("Please fill all fields")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                text = "LOGIN",
                color = Color(0xFFF98E8E),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Sign up",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSignUpClick() },
            textAlign = TextAlign.Center
        )
    }
}
