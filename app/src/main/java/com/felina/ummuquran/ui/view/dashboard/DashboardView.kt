package com.felina.ummuquran.ui.view.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.felina.ummuquran.R
import com.felina.ummuquran.ui.source.CalendarDataSource
import com.felina.ummuquran.ui.source.CalendarUiModel
import com.felina.ummuquran.ui.view.NavDestination
import com.felina.ummuquran.ui.view.read.ShimmerPlaceholder
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
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

    var titleData by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var dateData by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        is24Hour = true
    )
    var timeSelected by remember { mutableStateOf("") }
    var priorityData by remember { mutableStateOf("") }

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
        },
        floatingActionButton = {
            if (!showDialog) {
                FloatingActionButton (
                    onClick = {
                        showDialog = true;
                    },
                    containerColor = Color.White, // Set FAB background color to white
                    contentColor = Color(0xFF4C94AD), // Set icon color to blue
                    modifier = Modifier.padding(16.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFF4C94AD)
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
                modifier = Modifier.padding(0.dp,20.dp,0.dp,0.dp)
            ) {
                Box() {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(280.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mosque), // Replace with your PNG resource
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop,
                        )
                    }
                   Column (
                       modifier = Modifier.padding(0.dp,30.dp,0.dp,0.dp)
                   ){
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
                           if(showDialog) {
                               Column (
                                   modifier = Modifier.weight(1f).padding(vertical = 20.dp)
                               ){
                                   if (showDatePicker) {
                                       MyDatePickerDialog(
                                           onDateSelected = { dateData = it },
                                           onDismiss = { showDatePicker = false }
                                       )
                                   }
                                   if (showTimePicker) {
                                       Dialog(
                                           onDismissRequest = { showTimePicker = false },
                                           properties = DialogProperties(usePlatformDefaultWidth = true)
                                       ) {
                                           ElevatedCard(
                                               modifier = Modifier
                                                   .background(color = MaterialTheme.colorScheme.surface,
                                                       shape = MaterialTheme.shapes.extraLarge),
                                               elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                               shape = MaterialTheme.shapes.extraLarge
                                           ) {
                                               Column(
                                                   modifier = Modifier.padding(16.dp),
                                               ) {
                                                   Text(
                                                       modifier = Modifier
                                                           .fillMaxWidth()
                                                           .padding(16.dp),
                                                       text = "Pilih Jam"
                                                   )
                                                   TimePicker(
                                                       state = timeState,
                                                       layoutType = TimePickerLayoutType.Vertical,
                                                   )
                                                   Row(
                                                       modifier = Modifier.fillMaxWidth(),
                                                       horizontalArrangement = Arrangement.End
                                                   ) {
                                                       Button(
                                                           modifier = Modifier.padding(end = 8.dp),
                                                           onClick = { showTimePicker = false }
                                                       ) {
                                                           Text(
                                                               text = "Cancel"
                                                           )
                                                       }
                                                       Button(
                                                           modifier = Modifier.padding(start = 8.dp),
                                                           onClick = {
                                                               timeSelected = formattedTime(timeState.hour, timeState.minute)
                                                               showTimePicker = false
                                                           }
                                                       ) {
                                                           Text(text = "OK")
                                                       }
                                                   }
                                               }
                                           }
                                       }
                                   }
                                   TaskForm(
                                       onTitleChange = {
                                           titleData = it
                                       },
                                       title = titleData,
                                       onDatePickerShow = {
                                           showDatePicker = it
                                       },
                                       date = dateData,
                                       onTimePickerShow = {
                                           showTimePicker = it
                                       },
                                       time = timeSelected,
                                       onPriorityChange = {
                                           priorityData = it
                                       },
                                       priority = priorityData,
                                       onSubmit = {
                                           if(it) {
                                               userViewModel.insertRamadan(
                                                   title = titleData,
                                                   date = dateData,
                                                   startTime = timeSelected,
                                                   priority = priorityData
                                               )
                                               userViewModel.fetchRamadan(calendarUiModel.selectedDate.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                           }
                                           showDialog = false
                                       }
                                   )
                               }
                           }else {
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
                text = date.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("id", "ID")),
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

@Composable
fun TaskForm(
    onTitleChange: (String) -> Unit,
    title: String,
    onDatePickerShow: (Boolean) -> Unit,
    date: String,
    onTimePickerShow: (Boolean) -> Unit,
    time: String,
    onPriorityChange: (String) -> Unit,
    priority: String,
    onSubmit: (Boolean) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nama Kegiatan", fontWeight = FontWeight.Bold, color = Color.Gray)
        OutlinedTextField(
            value = title,
            placeholder = { Text("Masukkan Nama Kegiatan", color = Color.Gray) },
            onValueChange = { onTitleChange(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Tanggal Kegiatan", fontWeight = FontWeight.Bold, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDatePickerShow(true) }
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(
                text = date.ifEmpty { "Pilih Tanggal" },
                color = if (date.isEmpty()) Color.Gray else Color.Black
            )
        }

        Text("Jam Kegiatan", fontWeight = FontWeight.Bold, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimePickerShow(true) }
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(
                text = time.ifEmpty { "Pilih Jam" },
                color = if (time.isEmpty()) Color.Gray else Color.Black
            )
        }

        Text("Prioritas", fontWeight = FontWeight.Bold, color = Color.Gray)
        PrioritySelector(priority, onPrioritySelected = { onPriorityChange(it) })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Adds space between buttons
        ) {
            Button(
                onClick = { onSubmit(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ) {
                Text(text = "Cancel")
            }

            Button(
                onClick = { onSubmit(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4C94AD),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
)  {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {})

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
fun formattedTime(hour: Int, minute: Int): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val time = LocalTime.of(hour, minute).format(formatter)
    return time
}

@Composable
fun PrioritySelector(selectedPriority: String, onPrioritySelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val priorities = listOf("High", "Medium", "Low")

        priorities.forEach { priority ->
            PriorityButton(
                text = priority,
                isSelected = priority == selectedPriority,
                onClick = { onPrioritySelected(priority) },
                modifier = Modifier.weight(1f) // Make buttons expand equally
            )
        }
    }
}

@Composable
fun PriorityButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4C94AD) else Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(4.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatIslamicDate(selectedDate: LocalDate): String {
    val hijrahDate = HijrahDate.from(selectedDate)
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'AH'", Locale.US)
    return hijrahDate.format(formatter)
}
