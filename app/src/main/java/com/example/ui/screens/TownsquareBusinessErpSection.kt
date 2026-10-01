package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.UUID

// ==========================================
// 1. DATA MODELS FOR PERSONAL BUSINESS ERP
// ==========================================

enum class WorkTaskPriority(val label: String, val color: Color) {
    URGENT("Urgent", CoralRed),
    HIGH("High", WarmAmber),
    MEDIUM("Medium", NeonCyan),
    NORMAL("Normal", Color(0xFF64B5F6))
}

enum class WorkTaskStatus(val label: String, val icon: ImageVector) {
    IN_PROGRESS("In Progress", Icons.Default.PlayArrow),
    REVIEW("Under Review", Icons.Default.Visibility),
    COMPLETED("Completed", Icons.Default.CheckCircle)
}

data class OrganizationWorkOrder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val organization: String,
    val department: String,
    val assignedBy: String,
    val deadline: String,
    val priority: WorkTaskPriority,
    var status: WorkTaskStatus,
    var progressPercent: Int, // 0 to 100
    val description: String,
    var workNotes: String = "",
    var deliverableFilename: String? = null,
    val billableHoursBudget: Double = 8.0,
    var hoursLogged: Double = 0.0
)

data class ErpResourceAsset(
    val id: String,
    val name: String,
    val category: String,
    val serialNumber: String,
    val status: String,
    val returnDue: String,
    val location: String,
    val isCheckedOutByMe: Boolean
)

data class ErpPurchaseRequisition(
    val id: String,
    val poNumber: String,
    val itemDescription: String,
    val vendor: String,
    val totalAmount: Double,
    val costCenter: String,
    var status: String, // "Approved", "Pending Review", "Processed"
    val dateRequested: String
)

data class TeamDirectoryMember(
    val name: String,
    val role: String,
    val email: String,
    val extension: String,
    val status: String,
    val isManager: Boolean = false
)

// ==========================================
// 2. MAIN PERSONAL BUSINESS MANAGEMENT ERP
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareBusinessErpSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Active Organization State
    val organizations = listOf(
        "Townsquare Media Syndicate Ltd.",
        "Metropolitan Civic Infrastructure Corp.",
        "Old Quarter Artisan Cooperative",
        "Regional Harbor Logistics Trust"
    )
    var selectedOrgIndex by remember { mutableIntStateOf(0) }
    var isOrgMenuOpen by remember { mutableStateOf(false) }

    // Employee Profile Information
    val employeeName = "Alex Rivera"
    val employeeId = "TS-8842-OPERATIONAL"
    val employeeRole = "Senior Technical Editor & Field Bureau Chief"
    val clearanceLevel = "Level 4 Operational"

    // Punch Clock State
    var isOnDuty by remember { mutableStateOf(true) }
    var clockedInSeconds by remember { mutableLongStateOf(14250L) } // ~3h 57m

    LaunchedEffect(isOnDuty) {
        while (isOnDuty && isActive) {
            delay(1000L)
            clockedInSeconds++
        }
    }

    val hoursClocked = clockedInSeconds / 3600
    val minutesClocked = (clockedInSeconds % 3600) / 60
    val secondsClocked = clockedInSeconds % 60
    val clockTimeString = String.format("%02d:%02d:%02d", hoursClocked, minutesClocked, secondsClocked)

    // ERP Sub-Tabs
    var activeErpTab by remember { mutableIntStateOf(0) } // 0: Tasks/Work, 1: Timesheets, 2: Resources/Assets, 3: Requisitions/POs, 4: Team Directory

    // Work Orders Seed Data
    var workOrders by remember {
        mutableStateOf(
            listOf(
                OrganizationWorkOrder(
                    title = "Fall 2026 Editorial Coverage Directive & Staff Allocation",
                    organization = "Townsquare Media Syndicate Ltd.",
                    department = "Editorial Operations",
                    assignedBy = "Executive Editor Vance",
                    deadline = "Tomorrow, 5:00 PM",
                    priority = WorkTaskPriority.URGENT,
                    status = WorkTaskStatus.IN_PROGRESS,
                    progressPercent = 65,
                    description = "Draft staffing schedule, camera kit deployment, and priority live wire beats for the upcoming Promenade Grand Opening and Autumn Arts Festival.",
                    workNotes = "Section 1 and 2 draft finalized. Audio team assignments confirmed.",
                    deliverableFilename = "Fall_2026_Editorial_Directive_Draft.docx",
                    billableHoursBudget = 12.0,
                    hoursLogged = 7.5
                ),
                OrganizationWorkOrder(
                    title = "Radio Transmitter Relay 7-B Overnight Calibration Sign-off",
                    organization = "Townsquare Media Syndicate Ltd.",
                    department = "Broadcast Engineering",
                    assignedBy = "Chief Engineer Marcus Chen",
                    deadline = "Friday, 10:00 AM",
                    priority = WorkTaskPriority.HIGH,
                    status = WorkTaskStatus.REVIEW,
                    progressPercent = 90,
                    description = "Inspect telemetry frequency modulation logs on Transmitter 7-B after routine maintenance. Ensure emergency broadcast failover thresholds meet municipal standards.",
                    workNotes = "Relay frequency spectrum within 0.02% tolerance. Backup generator power tested.",
                    deliverableFilename = "Relay_7B_Telemetry_Report.pdf",
                    billableHoursBudget = 6.0,
                    hoursLogged = 5.0
                ),
                OrganizationWorkOrder(
                    title = "Quarterly Print Infrastructure & Solar Kiosk Supply Audit",
                    organization = "Metropolitan Civic Infrastructure Corp.",
                    department = "Physical Asset Logistics",
                    assignedBy = "Director Clara Rossi",
                    deadline = "Oct 06, 2026",
                    priority = WorkTaskPriority.MEDIUM,
                    status = WorkTaskStatus.IN_PROGRESS,
                    progressPercent = 35,
                    description = "Audit roll stock inventory, replacement touchscreen panels, and solar battery storage across 24 public news kiosks in Old Quarter.",
                    workNotes = "Inspected Pier 14 and Clocktower Square kiosks. 2 solar panels scheduled for cleaning.",
                    deliverableFilename = null,
                    billableHoursBudget = 16.0,
                    hoursLogged = 5.5
                ),
                OrganizationWorkOrder(
                    title = "Municipal Freedom of Information Gazette Digest Preparation",
                    organization = "Townsquare Media Syndicate Ltd.",
                    department = "Civic Records Archive",
                    assignedBy = "Mayoralty Records Guild",
                    deadline = "Completed Sep 28",
                    priority = WorkTaskPriority.NORMAL,
                    status = WorkTaskStatus.COMPLETED,
                    progressPercent = 100,
                    description = "Extract and index public tenders, city zoning modifications, and harbor transit tariffs for publication in the monthly Broadsheet Edition.",
                    workNotes = "Verified against official ledger. Published in Issue #187.",
                    deliverableFilename = "FOI_Gazette_Digest_Complete.pdf",
                    billableHoursBudget = 8.0,
                    hoursLogged = 8.0
                )
            )
        )
    }

    // Interactive "Do Work" Dialog State
    var activeWorkOrderForModal by remember { mutableStateOf<OrganizationWorkOrder?>(null) }
    var modalWorkNotes by remember { mutableStateOf("") }
    var modalProgress by remember { mutableFloatStateOf(0f) }
    var modalStatus by remember { mutableStateOf(WorkTaskStatus.IN_PROGRESS) }
    var modalDeliverableName by remember { mutableStateOf<String?>(null) }
    var modalHoursLogged by remember { mutableDoubleStateOf(0.0) }

    // Create New Task Dialog State
    var isCreateTaskOpen by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDepartment by remember { mutableStateOf("Editorial Operations") }
    var newTaskDeadline by remember { mutableStateOf("Friday, 5:00 PM") }
    var newTaskPriority by remember { mutableStateOf(WorkTaskPriority.HIGH) }
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskHoursBudget by remember { mutableStateOf("8.0") }

    // Requisitions Data
    var requisitions by remember {
        mutableStateOf(
            listOf(
                ErpPurchaseRequisition("req_1", "PO-2026-881", "Optical Lens Cleaning & High-Bandwidth XLR Leads", "ProBroadcast Supplies Co.", 145.50, "CC-MEDIA-TECH", "Approved", "Sep 28, 2026"),
                ErpPurchaseRequisition("req_2", "PO-2026-894", "Field Journalism Per Diem • Waterfront Festival", "Syndicate Travel Bureau", 85.00, "CC-TRAVEL-BUR", "Pending Review", "Sep 29, 2026"),
                ErpPurchaseRequisition("req_3", "PO-2026-870", "Satellite High-Gain Transponder Bandwidth Bucket", "AeroSat Municipal Link", 420.00, "CC-INFRA-NET", "Processed", "Sep 25, 2026")
            )
        )
    }
    var isCreateRequisitionOpen by remember { mutableStateOf(false) }
    var newReqItem by remember { mutableStateOf("") }
    var newReqVendor by remember { mutableStateOf("") }
    var newReqAmount by remember { mutableStateOf("") }
    var newReqCostCenter by remember { mutableStateOf("CC-MEDIA-TECH") }

    // Resources Assets Data
    var assets by remember {
        mutableStateOf(
            listOf(
                ErpResourceAsset("AST-401", "Sony FX6 Cinema ENG Camera Rig", "Broadcast Video", "SN-8841-A", "Assigned / Active", "Oct 05, 2026", "Locker 14-B (Field)", true),
                ErpResourceAsset("AST-209", "Sennheiser Dual Wireless Lavalier Kit", "Audio Gear", "SN-5520-X", "Assigned / Active", "Oct 05, 2026", "Locker 14-B (Field)", true),
                ErpResourceAsset("AST-110", "Townsquare Mobile Transmission Van 3", "Fleet Vehicle", "LIC-TS-MEDIA-03", "Stationary / Standby", "Maintenance OK", "North Harbor Garage", false),
                ErpResourceAsset("AST-705", "Dejero Cell-Bonded Video Backpack Unit", "Telemetry Relay", "SN-9912-D", "Available in Depot", "On Demand", "Main Central Tech Hub", false)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- 1. ENTERPRISE ORG & EMPLOYEE BADGE HEADER ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Organization Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isOrgMenuOpen = true }
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = organizations[selectedOrgIndex],
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Enterprise ERP Workspace • Tap to switch",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = DarkTextMuted)
                    }

                    // Organization Dropdown
                    DropdownMenu(
                        expanded = isOrgMenuOpen,
                        onDismissRequest = { isOrgMenuOpen = false }
                    ) {
                        organizations.forEachIndexed { idx, orgName ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = orgName,
                                        fontWeight = if (idx == selectedOrgIndex) FontWeight.Bold else FontWeight.Normal,
                                        color = if (idx == selectedOrgIndex) NeonCyan else Color.White
                                    )
                                },
                                onClick = {
                                    selectedOrgIndex = idx
                                    isOrgMenuOpen = false
                                    Toast.makeText(context, "Switched ERP workspace to $orgName", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    // On Duty / Punch Clock Status Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isOnDuty) Color(0xFF103622) else Color(0xFF331B1B),
                        border = BorderStroke(1.dp, if (isOnDuty) Color(0xFF30D158) else CoralRed),
                        modifier = Modifier.clickable {
                            isOnDuty = !isOnDuty
                            Toast.makeText(context, if (isOnDuty) "Clocked In: Shift Active" else "Clocked Out: Shift Paused", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isOnDuty) Color(0xFF30D158) else CoralRed,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnDuty) "ON DUTY" else "OFF DUTY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnDuty) Color(0xFF30D158) else CoralRed
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DarkBorder)

                // Employee Information & Clearance Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = employeeName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonCyan.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = clearanceLevel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = employeeRole,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                        Text(
                            text = "Staff ID: $employeeId",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                            color = DarkTextMuted
                        )
                    }

                    // Punch Clock Timer Widget
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CURRENT SHIFT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextMuted
                        )
                        Text(
                            text = clockTimeString,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isOnDuty) WarmAmber else Color.Gray
                        )
                    }
                }
            }
        }

        // --- 2. ERP KEY METRIC STATS ROW ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val totalHours = workOrders.sumOf { it.hoursLogged }
            val pendingOrders = workOrders.count { it.status != WorkTaskStatus.COMPLETED }

            ErpStatCard(
                title = "Work Orders",
                value = "$pendingOrders Pending",
                subtitle = "${workOrders.size} total tasks",
                icon = Icons.AutoMirrored.Filled.Assignment,
                iconColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )

            ErpStatCard(
                title = "Logged Time",
                value = "${String.format("%.1f", totalHours)} hrs",
                subtitle = "Weekly Target: 40h",
                icon = Icons.Default.AccessTime,
                iconColor = WarmAmber,
                modifier = Modifier.weight(1f)
            )

            ErpStatCard(
                title = "Open Requisitions",
                value = "$${String.format("%.0f", requisitions.sumOf { it.totalAmount })}",
                subtitle = "${requisitions.size} approvals",
                icon = Icons.Default.ReceiptLong,
                iconColor = Color(0xFF30D158),
                modifier = Modifier.weight(1f)
            )
        }

        // --- 3. ERP SUB-TAB NAVIGATION ---
        ScrollableTabRow(
            selectedTabIndex = activeErpTab,
            containerColor = DarkSurface,
            contentColor = NeonCyan,
            edgePadding = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeErpTab == 0,
                onClick = { activeErpTab = 0 },
                text = { Text("📋 Assigned Tasks & Work", fontSize = 11.sp, fontWeight = if (activeErpTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeErpTab == 1,
                onClick = { activeErpTab = 1 },
                text = { Text("⏱️ Timesheet & Hours", fontSize = 11.sp, fontWeight = if (activeErpTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeErpTab == 2,
                onClick = { activeErpTab = 2 },
                text = { Text("📦 Corporate Assets", fontSize = 11.sp, fontWeight = if (activeErpTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeErpTab == 3,
                onClick = { activeErpTab = 3 },
                text = { Text("📑 Requisitions & POs", fontSize = 11.sp, fontWeight = if (activeErpTab == 3) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeErpTab == 4,
                onClick = { activeErpTab = 4 },
                text = { Text("👥 Team Directory", fontSize = 11.sp, fontWeight = if (activeErpTab == 4) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 4. SUB-TAB VIEW CONTENT ---
        Box(modifier = Modifier.weight(1f)) {
            when (activeErpTab) {
                0 -> {
                    // TAB 0: ASSIGNED WORK ORDERS (SEE AND DO WORK)
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ORGANIZATION DELIVERABLES & ORDERS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Button(
                                onClick = { isCreateTaskOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(workOrders) { order ->
                                WorkOrderCard(
                                    order = order,
                                    onDoWork = {
                                        activeWorkOrderForModal = order
                                        modalWorkNotes = order.workNotes
                                        modalProgress = order.progressPercent / 100f
                                        modalStatus = order.status
                                        modalDeliverableName = order.deliverableFilename
                                        modalHoursLogged = order.hoursLogged
                                    },
                                    onQuickToggleComplete = {
                                        workOrders = workOrders.map {
                                            if (it.id == order.id) {
                                                val nextStatus = if (it.status == WorkTaskStatus.COMPLETED) WorkTaskStatus.IN_PROGRESS else WorkTaskStatus.COMPLETED
                                                val nextProg = if (nextStatus == WorkTaskStatus.COMPLETED) 100 else 50
                                                it.copy(status = nextStatus, progressPercent = nextProg)
                                            } else it
                                        }
                                        Toast.makeText(context, "Task status updated!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: TIMESHEET & BILLING
                    TimesheetErpView(
                        workOrders = workOrders,
                        clockTimeString = clockTimeString,
                        isOnDuty = isOnDuty,
                        onLogTime = { orderId, addedHours ->
                            workOrders = workOrders.map {
                                if (it.id == orderId) it.copy(hoursLogged = it.hoursLogged + addedHours) else it
                            }
                            Toast.makeText(context, "Logged $addedHours hrs to work order!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                2 -> {
                    // TAB 2: CORPORATE ASSET TRACKER
                    CorporateAssetsErpView(
                        assets = assets,
                        onRequestAsset = {
                            Toast.makeText(context, "Equipment requisition submitted to Asset Logistics", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                3 -> {
                    // TAB 3: REQUISITIONS & PURCHASE ORDERS
                    RequisitionsErpView(
                        requisitions = requisitions,
                        onOpenNewReq = { isCreateRequisitionOpen = true },
                        onApprovePo = { poId ->
                            requisitions = requisitions.map {
                                if (it.id == poId) it.copy(status = "Approved") else it
                            }
                            Toast.makeText(context, "PO signed and approved!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                4 -> {
                    // TAB 4: TEAM DIRECTORY & MEMOS
                    TeamDirectoryErpView()
                }
            }
        }
    }

    // --- MODAL 1: "DO WORK" & UPDATE DELIVERABLE DIALOG ---
    if (activeWorkOrderForModal != null) {
        val currentOrder = activeWorkOrderForModal!!
        AlertDialog(
            onDismissRequest = { activeWorkOrderForModal = null },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Work Order Execution Console",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = currentOrder.organization,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = currentOrder.priority.color.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, currentOrder.priority.color)
                    ) {
                        Text(
                            text = currentOrder.priority.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentOrder.priority.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = currentOrder.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentOrder.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Task Completion Progress:", fontSize = 11.sp, color = DarkTextSecondary)
                        Text("${(modalProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                    Slider(
                        value = modalProgress,
                        onValueChange = {
                            modalProgress = it
                            if (it >= 1.0f) modalStatus = WorkTaskStatus.COMPLETED
                            else if (it > 0.7f && modalStatus != WorkTaskStatus.REVIEW) modalStatus = WorkTaskStatus.REVIEW
                            else if (it < 0.7f) modalStatus = WorkTaskStatus.IN_PROGRESS
                        },
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Selection Row
                    Text("Execution Status:", fontSize = 11.sp, color = DarkTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WorkTaskStatus.entries.forEach { st ->
                            val isSel = modalStatus == st
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { modalStatus = st }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = st.icon,
                                        contentDescription = null,
                                        tint = if (isSel) NeonCyan else DarkTextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = st.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) NeonCyan else DarkTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Work Output & Log Notes
                    OutlinedTextField(
                        value = modalWorkNotes,
                        onValueChange = { modalWorkNotes = it },
                        label = { Text("Work Notes / Submission Report") },
                        placeholder = { Text("Summarize actions taken, interviews conducted, findings...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Deliverable File Attachment
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = modalDeliverableName ?: "No file attached",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (modalDeliverableName != null) Color.White else DarkTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (modalDeliverableName != null) "Verified Digital Deliverable" else "Tap attach to link file",
                                        fontSize = 9.sp,
                                        color = DarkTextSecondary
                                    )
                                }
                            }
                            TextButton(
                                onClick = {
                                    modalDeliverableName = "Signed_Deliverable_${System.currentTimeMillis() % 10000}.pdf"
                                    Toast.makeText(context, "Attached deliverable file", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(if (modalDeliverableName == null) "Attach File" else "Change", fontSize = 11.sp, color = NeonCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        workOrders = workOrders.map {
                            if (it.id == currentOrder.id) {
                                it.copy(
                                    workNotes = modalWorkNotes,
                                    progressPercent = (modalProgress * 100).toInt(),
                                    status = modalStatus,
                                    deliverableFilename = modalDeliverableName
                                )
                            } else it
                        }
                        Toast.makeText(context, "Work submitted to organization records!", Toast.LENGTH_SHORT).show()
                        activeWorkOrderForModal = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Save & Submit Work", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeWorkOrderForModal = null }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            }
        )
    }

    // --- MODAL 2: CREATE NEW WORK ORDER / TASK ---
    if (isCreateTaskOpen) {
        AlertDialog(
            onDismissRequest = { isCreateTaskOpen = false },
            title = { Text("Assign / Register Work Order", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Work Order Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskDepartment,
                        onValueChange = { newTaskDepartment = it },
                        label = { Text("Department / Division") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskDeadline,
                        onValueChange = { newTaskDeadline = it },
                        label = { Text("Deadline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskHoursBudget,
                        onValueChange = { newTaskHoursBudget = it },
                        label = { Text("Budgeted Hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskDescription,
                        onValueChange = { newTaskDescription = it },
                        label = { Text("Work Instructions & Objective") },
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            val newOrder = OrganizationWorkOrder(
                                title = newTaskTitle,
                                organization = organizations[selectedOrgIndex],
                                department = newTaskDepartment.ifBlank { "Editorial Operations" },
                                assignedBy = "Department Bureau Coordinator",
                                deadline = newTaskDeadline.ifBlank { "Next Monday" },
                                priority = newTaskPriority,
                                status = WorkTaskStatus.IN_PROGRESS,
                                progressPercent = 0,
                                description = newTaskDescription.ifBlank { "Complete assigned field deliverable." },
                                billableHoursBudget = newTaskHoursBudget.toDoubleOrNull() ?: 8.0
                            )
                            workOrders = listOf(newOrder) + workOrders
                            isCreateTaskOpen = false
                            newTaskTitle = ""
                            newTaskDescription = ""
                            Toast.makeText(context, "Work Order Registered!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Create Work Order", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreateTaskOpen = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            }
        )
    }

    // --- MODAL 3: SUBMIT NEW REQUISITION ---
    if (isCreateRequisitionOpen) {
        AlertDialog(
            onDismissRequest = { isCreateRequisitionOpen = false },
            title = { Text("New Purchase Requisition / PO", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newReqItem,
                        onValueChange = { newReqItem = it },
                        label = { Text("Item / Service Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newReqVendor,
                        onValueChange = { newReqVendor = it },
                        label = { Text("Supplier / Vendor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newReqAmount,
                        onValueChange = { newReqAmount = it },
                        label = { Text("Total Amount ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newReqCostCenter,
                        onValueChange = { newReqCostCenter = it },
                        label = { Text("Cost Center Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newReqAmount.toDoubleOrNull() ?: 0.0
                        if (newReqItem.isNotBlank() && amount > 0) {
                            val newPo = ErpPurchaseRequisition(
                                id = UUID.randomUUID().toString(),
                                poNumber = "PO-2026-${(100..999).random()}",
                                itemDescription = newReqItem,
                                vendor = newReqVendor.ifBlank { "Direct Procurement" },
                                totalAmount = amount,
                                costCenter = newReqCostCenter,
                                status = "Pending Review",
                                dateRequested = "Today"
                            )
                            requisitions = listOf(newPo) + requisitions
                            isCreateRequisitionOpen = false
                            newReqItem = ""
                            newReqVendor = ""
                            newReqAmount = ""
                            Toast.makeText(context, "Purchase Requisition Logged!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Submit for Approval", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreateRequisitionOpen = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            }
        )
    }
}

// ==========================================
// 3. SUPPORTING ERP SUB-COMPONENTS
// ==========================================

@Composable
private fun ErpStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 10.sp, color = DarkTextSecondary, fontWeight = FontWeight.Bold)
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = DarkTextMuted
            )
        }
    }
}

@Composable
private fun WorkOrderCard(
    order: OrganizationWorkOrder,
    onDoWork: () -> Unit,
    onQuickToggleComplete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, if (order.status == WorkTaskStatus.COMPLETED) Color(0xFF1B3D2B) else DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Organization, Priority, Quick Complete Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = order.priority.color.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, order.priority.color.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = order.priority.label.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = order.priority.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.department,
                        fontSize = 11.sp,
                        color = DarkTextSecondary
                    )
                }

                IconButton(
                    onClick = onQuickToggleComplete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (order.status == WorkTaskStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Complete",
                        tint = if (order.status == WorkTaskStatus.COMPLETED) Color(0xFF30D158) else DarkTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Work Title & Description
            Text(
                text = order.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (order.status == WorkTaskStatus.COMPLETED) DarkTextMuted else Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.description,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { order.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        order.progressPercent >= 100 -> Color(0xFF30D158)
                        order.progressPercent > 50 -> NeonCyan
                        else -> WarmAmber
                    },
                    trackColor = DarkSurfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${order.progressPercent}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Details & "Do Work" Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assigned by: ${order.assignedBy}",
                        fontSize = 10.sp,
                        color = DarkTextMuted
                    )
                    Text(
                        text = "Deadline: ${order.deadline}",
                        fontSize = 10.sp,
                        color = if (order.priority == WorkTaskPriority.URGENT) CoralRed else WarmAmber,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onDoWork,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (order.status == WorkTaskStatus.COMPLETED) DarkSurfaceVariant else NeonCyan,
                        contentColor = if (order.status == WorkTaskStatus.COMPLETED) Color.White else Color(0xFF003544)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (order.status == WorkTaskStatus.COMPLETED) Icons.Default.Edit else Icons.Default.Handyman,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (order.status == WorkTaskStatus.COMPLETED) "Review Work" else "Do Work",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TimesheetErpView(
    workOrders: List<OrganizationWorkOrder>,
    clockTimeString: String,
    isOnDuty: Boolean,
    onLogTime: (String, Double) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TIMESHEET & BILLABLE HOURS SUMMARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Pay Period",
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                            Text(
                                text = "Sep 16 - Sep 30, 2026",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WarmAmber.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "STATUS: OPEN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily breakdown bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("Mon" to 8.0, "Tue" to 8.5, "Wed" to 7.0, "Thu" to 8.0, "Fri" to 4.5)
                        days.forEach { (d, h) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${h}h", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DarkSurfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight((h / 10.0).toFloat().coerceIn(0.1f, 1.0f))
                                            .align(Alignment.BottomCenter)
                                            .background(NeonCyan)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = d, fontSize = 9.sp, color = DarkTextMuted)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "LOG HOURS TO ACTIVE WORK ORDERS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
        }

        items(workOrders) { order ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Logged: ${order.hoursLogged}h / Budget: ${order.billableHoursBudget}h",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onLogTime(order.id, 1.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = NeonCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+1.0h", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onLogTime(order.id, 2.5) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f), contentColor = NeonCyan),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+2.5h", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CorporateAssetsErpView(
    assets: List<ErpResourceAsset>,
    onRequestAsset: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ORGANIZATION INVENTORY & ASSETS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
            Button(
                onClick = onRequestAsset,
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Request Asset", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(assets) { asset ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, if (asset.isCheckedOutByMe) NeonCyan.copy(alpha = 0.5f) else DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (asset.isCheckedOutByMe) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceVariant
                            ) {
                                Text(
                                    text = if (asset.isCheckedOutByMe) "ASSIGNED TO ME" else "DEPOT STANDBY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (asset.isCheckedOutByMe) NeonCyan else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Category: ${asset.category} • S/N: ${asset.serialNumber}",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                        Text(
                            text = "Location: ${asset.location} • Return Due: ${asset.returnDue}",
                            fontSize = 11.sp,
                            color = WarmAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequisitionsErpView(
    requisitions: List<ErpPurchaseRequisition>,
    onOpenNewReq: () -> Unit,
    onApprovePo: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PURCHASE ORDERS & REQUISITIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
            Button(
                onClick = onOpenNewReq,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Requisition", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(requisitions) { req ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = req.poNumber,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                color = WarmAmber
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (req.status) {
                                    "Approved" -> Color(0xFF103622)
                                    "Processed" -> NeonCyan.copy(alpha = 0.2f)
                                    else -> WarmAmber.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = req.status.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (req.status) {
                                        "Approved" -> Color(0xFF30D158)
                                        "Processed" -> NeonCyan
                                        else -> WarmAmber
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = req.itemDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                        Text(
                            text = "Vendor: ${req.vendor} • Cost Center: ${req.costCenter}",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${String.format("%.2f", req.totalAmount)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )

                            if (req.status == "Pending Review") {
                                OutlinedButton(
                                    onClick = { onApprovePo(req.id) },
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF30D158)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Approve PO", fontSize = 10.sp, color = Color(0xFF30D158), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamDirectoryErpView() {
    val team = listOf(
        TeamDirectoryMember("Julian Vance", "Executive Editor & Publisher", "vance@townsquare.media", "Ext. 101", "In Office", isManager = true),
        TeamDirectoryMember("Marcus Chen", "Chief Broadcast Engineer", "m.chen@townsquare.media", "Ext. 204", "On Site Transmitter 7", isManager = true),
        TeamDirectoryMember("Elena Rostova", "Senior Investigative Correspondent", "e.rostova@townsquare.press", "Ext. 142", "Field Assignment"),
        TeamDirectoryMember("Aria Scott", "Waves Bureau Music Curator", "aria@waves.radio", "Ext. 312", "On Air Radio 1"),
        TeamDirectoryMember("Miles Holloway", "Vintage Sound & Electronics Archon", "m.holloway@townsquare.fm", "Ext. 408", "Analog Lab Workshop")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "ORGANIZATION PERSONNEL & COMMUNICATIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
        }

        items(team) { member ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (member.isManager) WarmAmber.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = member.name.take(1),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.isManager) WarmAmber else NeonCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = member.role,
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                            Text(
                                text = "${member.email} • ${member.extension}",
                                fontSize = 10.sp,
                                color = DarkTextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkSurfaceVariant
                    ) {
                        Text(
                            text = member.status,
                            fontSize = 9.sp,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
