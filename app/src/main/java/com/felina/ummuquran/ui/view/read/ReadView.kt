package com.felina.ummuquran.ui.view.read

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felina.ummuquran.ui.view.dashboard.ShimmerItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadView(
    id: String,
    userViewModel: ReadViewModel = koinViewModel()) {
    val users by userViewModel.ayah.collectAsState()
    val isLoading by userViewModel.loading.collectAsState()
    val activity = (LocalContext.current as? Activity)
    LaunchedEffect(Unit) {
        userViewModel.fetchAyah(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    } else {
                        Text(
                            text = "$id. ${users?.surahName ?: "---"}",
                            style = TextStyle(
                                fontSize = 24.sp, // Slightly larger for emphasis
                                fontWeight = FontWeight.Bold, // Bold for title emphasis
                                color = Color.DarkGray, // Retain a minimalist black color
                                letterSpacing = 1.5.sp // Adds subtle spacing for elegance
                            ),

                        )
                    } },
                navigationIcon = {
                    IconButton(onClick = { activity?.onBackPressed(); }) { // Tombol kembali
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Ikon panah kembali
                            contentDescription = "Kembali ke Dashboard"
                        )
                    }
                }
                )
        }
    ) { paddingValues ->
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(paddingValues)
//        ) {
//            // Bismillah Text
//            Text(
//                text = "﷽",
//                style = MaterialTheme.typography.headlineLarge.copy(
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 36.sp
//                ),
//                color = Color(0xFF61481C),
//                textAlign = TextAlign.Center
//            )
//        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                items(10) {
                    ShimmerPlaceholder()
                }
            } else {
                items(users?.english?.size ?: 0) { index ->
                    val user = users?.arabic1[index]
                    val english = users?.english[index]

                    Column(
                        horizontalAlignment = Alignment.Start,

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = user ?: "",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 32.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(0xFF61481C),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        // Translation text
                        Text(
                            text = "(${index + 1}) $english",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 24.sp),
                            color = Color(0xFF61481C),
                            textAlign = TextAlign.Start,
                        )

                        // Bottom control bar
                    }

                }
            }
          }
        }
    }

@Composable
fun ShimmerPlaceholder() {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Simulated shimmer block for Arabic text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated shimmer block for English translation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}
