package fr.isen.elakrimi.sipsmart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.elakrimi.sipsmart.WelcomeScreen



@Composable
fun WelcomeScreen(
    onNavigate: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF98E8E))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "SipSmart",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_sipsmart),
                contentDescription = "Logo SipSmart",
                modifier = Modifier.size(200.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "SIGN UP FREE",
                    color = Color(0xFFF98E8E),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Already have an account? Log in.",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onLoginClick() },
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
