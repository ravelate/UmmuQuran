package com.felina.ummuquran.ui.view.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.felina.ummuquran.ui.source.CalendarDataSource
import com.felina.ummuquran.ui.source.CalendarUiModel
import com.felina.ummuquran.ui.view.NavDestination
import com.felina.ummuquran.ui.view.read.ShimmerPlaceholder
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(
    navController: NavHostController,
    userViewModel: DashboardViewModel = koinViewModel()) {
    val users by userViewModel.ramadan.collectAsState()
    val isLoading by userViewModel.loading.collectAsState()
    val dataSource = CalendarDataSource()
    var calendarUiModel: CalendarUiModel by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    val itemNav = listOf(NavDestination.dashboard, NavDestination.quran)
    LaunchedEffect(Unit) {
        val dateNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        }
        userViewModel.fetchRamadan(dateNow)
    }

    Scaffold(
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
                                restoreState = false
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4C94AD),
                            Color(0xFF4C94AD),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        )
                    ),
                )
                .padding(paddingValues)
        ) {
            Column (
                modifier = Modifier.padding(0.dp,20.dp)
            ) {
                Header(
                    data = calendarUiModel,
                    onPrevClickListener = { startDate ->
                        val finalStartDate = startDate.minusDays(1)
                        calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
                    },
                    onNextClickListener = { endDate ->
                        val finalStartDate = endDate.plusDays(2)
                        calendarUiModel = dataSource.getData(startDate = finalStartDate, lastSelectedDate = calendarUiModel.selectedDate.date)
                    }
                )
                Content(
                    data = calendarUiModel,
                    onDateClickListener = { date ->
                        calendarUiModel = calendarUiModel.copy(
                            selectedDate = date,
                            visibleDates = calendarUiModel.visibleDates.map {
                                it.copy(
                                    isSelected = it.date.isEqual(date.date)
                                )
                            }
                        )},
                    userViewModel
                )
            }
            Card (
                shape = RoundedCornerShape(35.dp,35.dp,0.dp,0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(vertical = 20.dp)
                ) {
                    if (isLoading) {
                        items(10) {
                            ShimmerPlaceholder()
                        }
                    } else {
                        items(users.size) { index ->
                            val user = users[index]

                            TaskItem(title = user.title, subtitle = user.startTime, isChecked = user.isDone) {
                                userViewModel.fetchUpdateRamadanIsDone(user.id)
                                userViewModel.fetchRamadan(user.date)
                            }

                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit) {
    Row (
        modifier = Modifier.padding(15.dp)
    ){
        Column (
            modifier = Modifier
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            Text(
                text = formatIslamicDate(data.selectedDate.date),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = data.selectedDate.date.format(DateTimeFormatter.ofPattern("MMMM, yyyy")),
                fontSize = 12.sp,
                color = Color.White
            )
        }
        IconButton(onClick = {
            onPrevClickListener(data.startDate.date)

        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        IconButton(onClick = {
            onNextClickListener(data.endDate.date)
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    userViewModel: DashboardViewModel
) {
    LazyRow (
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(
            items = data.visibleDates,
            key = { date -> date.day }
        ) { date ->
            ContentItem(
                date,
                onDateClickListener,
                userViewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
    userViewModel: DashboardViewModel
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                date.date
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .let { formattedDate ->
                        onClickListener(date)
                        userViewModel.fetchRamadan(formattedDate)
                    }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (date.isSelected)
                Color(0xFF8ACAD4)
            else
                Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .size(width = 48.dp, height = 58.dp)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.day,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontSize = 12.sp
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TaskItem(title: String, subtitle: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val alpha = if (isChecked) 0.5f else 1f
    val backgroundColor = if (isChecked) Color(0xFFF0F0F0) else Color.White
    val titleColor = if (isChecked) Color.Gray else Color.Black
    val subtitleColor = if (isChecked) Color(0xFFB0B0B0) else Color(0xFFA0A0A0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 20.dp)
            .alpha(alpha),
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 14.sp
                )
            }

            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4C94AD),
                    uncheckedColor = Color(0xFF787878),
                    checkmarkColor = Color.White
                )
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatIslamicDate(selectedDate: LocalDate): String {
    val hijrahDate = HijrahDate.from(selectedDate)
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'AH'", Locale.US)
    return hijrahDate.format(formatter)
}
