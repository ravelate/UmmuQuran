package com.felina.ummuquran.ui.view.quran

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.felina.ummuquran.MainActivity
import com.felina.ummuquran.ui.view.NavDestination
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranView(
    navController: NavHostController,
    userViewModel: QuranViewModel = koinViewModel()) {
    val users by userViewModel.surah.collectAsState()
    val hadith by userViewModel.hadith.collectAsState()
    val isLoading by userViewModel.loading.collectAsState()
    var isExpanded by remember { mutableStateOf(true) }
    val itemNav = listOf(NavDestination.dashboard, NavDestination.quran)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userViewModel.fetchSurah()
    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Al Qur'an",
                        style = TextStyle(
                            fontSize = 24.sp, // Slightly larger for emphasis
                            fontWeight = FontWeight.Bold, // Bold for title emphasis
                            color = Color.DarkGray, // Retain a minimalist black color
                            letterSpacing = 1.5.sp // Adds subtle spacing for elegance
                        ),
                        textAlign = TextAlign.Center // Centers the text for a balanced appearance
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar (
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.background),
                containerColor = Color.White
            ) {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination
                itemNav.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(screen.icon), contentDescription = screen.title
                            )
                        },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = Color(0xFF8ACAD4),
                            selectedTextColor = Color(0xFF8ACAD4),
                            selectedIndicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            disabledIconColor = Color.Gray,
                            disabledTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF3E4A55), // Warna background
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    // Header dengan tombol expand/minimize
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Daily Hadith",
                            color = Color(0xFFB0BEC5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f) // Untuk mengisi ruang kosong
                        )
                        Button(
                            onClick = { isExpanded = !isExpanded }, // Toggle expanded/minimized
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Minimize" else "Expand",
                                tint = Color.White
                            )
                        }
                    }

                    // Konten yang akan disembunyikan ketika minimized
                    if (isExpanded) {
                        Column {
                            Text(
                                text = hadith?.data?.hadith_english ?: "",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Start,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                text = if(isLoading) "" else "(${hadith?.data?.refno ?: ""})",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.End,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            Button(
                                onClick = {
                                    val intent = Intent()
                                    intent.action = Intent.ACTION_SEND
                                    intent.type="text/plain"
                                    intent.putExtra(Intent.EXTRA_TEXT, "${hadith?.data?.hadith_english} (${hadith?.data?.refno})");

                                    try {
                                        context.startActivity(Intent.createChooser(intent,"Bagikan dengan"))
                                    } catch (e: Exception) {
                                        // Menangani error jika tidak ada aplikasi yang mendukung
                                        Toast.makeText(MainActivity(), "Tidak ada aplikasi yang mendukung untuk berbagi", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF546E7A), // Warna background tombol
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Share",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                if (isLoading) {
                    items(20) { // Number of shimmer placeholders
                        ShimmerItem()
                    }
                } else {
                    items(users.size) { index ->
                        val user = users[index]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("read/${index + 1}")
                                }
                                .padding(10.dp, 5.dp)
                                .background(
                                    color = Color(0xFFF8F4EC),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(15.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Icon with number inside a star shape
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            color = Color(0xFFE6D8B5),
                                            shape = RoundedCornerShape(50)
                                        )
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Middle section with title and details
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = user.surahName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF61481C)
                                        )
                                        Text(
                                            text = " (${ user.surahNameTranslation })",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF8A795D)
                                        )
                                    }

                                    Text(
                                        text = "${user.revelationPlace} | ${user.totalAyah} Verses",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF8A795D)
                                    )
                                }

                                // Arabic name on the right
                                Text(
                                    text = user.surahNameArabic,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF61481C)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
@Composable
fun ShimmerItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
            .background(
                color = Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(color = Color.Gray.copy(alpha = 0.2f), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .background(color = Color.Gray.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .background(color = Color.Gray.copy(alpha = 0.2f))
                )
            }
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(20.dp)
                    .background(color = Color.Gray.copy(alpha = 0.2f))
            )
        }
    }
}