package com.example.ui.plus.planner

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.plus.extensions.Android10SoundEffects
import com.example.ui.theme.*

enum class PlannerViewMode(val label: String, val icon: String) {
    CALENDAR_DAY("Calendar & Schedule", "📅"),
    TASK_MANAGER("Task Manager", "✅")
}

data class CalendarEvent(
    val id: String,
    val title: String,
    val timeSlot: String,
    val category: String,
    val location: String,
    val colorHex: Long = 0xFF00E5FF
)

enum class TaskPriority(val label: String, val colorHex: Long) {
    HIGH("High", 0xFFFF3B30),
    MEDIUM("Medium", 0xFFFF9F1C),
    LOW("Low", 0xFF30D158)
}

data class PlannerTask(
    val id: String,
    val title: String,
    val category: String,
    val priority: TaskPriority,
    var isCompleted: Boolean = false,
    val dueDate: String = "Today"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquarePlannerApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(PlannerViewMode.CALENDAR_DAY) }
    var selectedDay by remember { mutableIntStateOf(29) } // 29th September

    // Events Seed
    var events by remember {
        mutableStateOf(
            listOf(
                CalendarEvent("e1", "🏛️ District 4 Public Square Assembly", "09:00 - 10:30", "Civic", "Town Hall Chamber", 0xFF00E5FF),
                CalendarEvent("e2", "🌊 Waves Music Audio Stream Session", "11:00 - 12:00", "Media", "Sonic Studio B", 0xFFFF3B30),
                CalendarEvent("e3", "📚 Bookworm Literature Club Debate", "14:30 - 16:00", "Culture", "Old Quarter Guild", 0xFFFF9F1C),
                CalendarEvent("e4", "🗣️ Lingo Japanese Tutor Practice", "17:00 - 18:00", "Personal", "Polyglot Lounge", 0xFF30D158)
            )
        )
    }

    // Tasks Seed
    var tasks by remember {
        mutableStateOf(
            listOf(
                PlannerTask("t1", "File quarterly district civic permits", "Civic", TaskPriority.HIGH, isCompleted = false),
                PlannerTask("t2", "Review Animal Farm live news dispatch", "Media", TaskPriority.HIGH, isCompleted = true),
                PlannerTask("t3", "Sync Goodreads & Google third-party accounts", "Tech", TaskPriority.MEDIUM, isCompleted = true),
                PlannerTask("t4", "Complete Japanese Tutor coffee scenario", "Personal", TaskPriority.MEDIUM, isCompleted = false),
                PlannerTask("t5", "Tune packet radio antenna to 28.400 MHz", "Tech", TaskPriority.LOW, isCompleted = false)
            )
        )
    }

    // New Event Dialog State
    var isAddEventOpen by remember { mutableStateOf(false) }
    var newEventTitle by remember { mutableStateOf("") }
    var newEventTime by remember { mutableStateOf("12:00 - 13:00") }

    // New Task Dialog State
    var isAddTaskOpen by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("planner_back_btn")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Planner",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = WarmAmber
                                ) {
                                    Text(
                                        text = "PLUS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF261800),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Calendar • Schedule Planner • Task Manager",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (viewMode == PlannerViewMode.CALENDAR_DAY) {
                                isAddEventOpen = true
                            } else {
                                isAddTaskOpen = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("planner_add_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (viewMode == PlannerViewMode.CALENDAR_DAY) "Add Event" else "Add Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // View Switcher Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlannerViewMode.entries.forEach { mode ->
                    val isSel = viewMode == mode
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSel) NeonCyan else Color(0xFF334155)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewMode = mode
                                Android10SoundEffects.playTrackballClick()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(mode.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mode.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) NeonCyan else Color.White
                            )
                        }
                    }
                }
            }

            // Content Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (viewMode == PlannerViewMode.CALENDAR_DAY) {
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        // Month Strip & Day Selector Grid
                        Text(
                            text = "SEPTEMBER 2026",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items((1..30).toList()) { day ->
                                val isSel = day == selectedDay
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) NeonCyan else Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, if (isSel) NeonCyan else Color(0xFF334155)),
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            selectedDay = day
                                            Android10SoundEffects.playTrackballClick()
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "SEP",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color(0xFF003544) else Color.Gray
                                        )
                                        Text(
                                            text = "$day",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color(0xFF003544) else Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Schedule for Sep $selectedDay, 2026:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hourly Schedule Events List
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(events) { ev ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, Color(ev.colorHex).copy(alpha = 0.6f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(ev.colorHex).copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, Color(ev.colorHex))
                                        ) {
                                            Text(
                                                text = ev.timeSlot,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(ev.colorHex),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = ev.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "📍 ${ev.location} • [${ev.category}]",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TASK MANAGER VIEW
                    Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tasks (${tasks.count { it.isCompleted }}/${tasks.size} Completed)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tasks) { task ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, if (task.isCompleted) Color(0xFF334155) else Color(task.priority.colorHex).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        tasks = tasks.map { if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it }
                                        Android10SoundEffects.playTrackballClick()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { checked ->
                                                tasks = tasks.map { if (it.id == task.id) it.copy(isCompleted = checked) else it }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = NeonCyan,
                                                uncheckedColor = Color.Gray
                                            )
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (task.isCompleted) Color.Gray else Color.White,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(task.priority.colorHex).copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "Priority: ${task.priority.label}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(task.priority.colorHex),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Category: ${task.category}",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
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

        // Add Event Dialog
        if (isAddEventOpen) {
            AlertDialog(
                onDismissRequest = { isAddEventOpen = false },
                title = { Text("Add Schedule Event", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newEventTitle,
                            onValueChange = { newEventTitle = it },
                            placeholder = { Text("Event title...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newEventTime,
                            onValueChange = { newEventTime = it },
                            placeholder = { Text("Time slot (e.g. 14:00 - 15:00)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newEventTitle.isNotBlank()) {
                            val newEv = CalendarEvent(
                                id = "e_${System.currentTimeMillis()}",
                                title = newEventTitle,
                                timeSlot = newEventTime,
                                category = "Personal",
                                location = "Townsquare District 4",
                                colorHex = 0xFF00E5FF
                            )
                            events = events + newEv
                            newEventTitle = ""
                            isAddEventOpen = false
                            Android10SoundEffects.playTrackballClick()
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddEventOpen = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }

        // Add Task Dialog
        if (isAddTaskOpen) {
            AlertDialog(
                onDismissRequest = { isAddTaskOpen = false },
                title = { Text("Add New Task", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = { Text("Task description...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            val newT = PlannerTask(
                                id = "t_${System.currentTimeMillis()}",
                                title = newTaskTitle,
                                category = "General",
                                priority = TaskPriority.HIGH,
                                isCompleted = false
                            )
                            tasks = tasks + newT
                            newTaskTitle = ""
                            isAddTaskOpen = false
                            Android10SoundEffects.playTrackballClick()
                        }
                    }) {
                        Text("Save Task")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddTaskOpen = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = DarkSurfaceElevated
            )
        }
    }
}
