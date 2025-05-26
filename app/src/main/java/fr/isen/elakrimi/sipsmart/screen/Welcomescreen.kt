package fr.isen.elakrimi.sipsmart.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.elakrimi.sipsmart.R

@Composable
fun WelcomeScreen(
    onNavigate: () -> Unit,
    onLoginClick: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Décalage horizontal du logo : de -150.dp (à gauche) vers 0.dp (position normale)
    val offsetX by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-150).dp,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "offsetX"
    )

    // Opacité du logo : fade in
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

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
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
            Text(
                text = "SipSmart",
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_sipsmart),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .offset(x = offsetX) // décalage horizontal animé
                    .alpha(alphaAnim),  // fade in
                contentScale = ContentScale.Fit
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(8.dp)
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

