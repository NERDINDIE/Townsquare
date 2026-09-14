import re

content = open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "r").read()
start_marker = "private fun EpgGuideSection("
end_marker = "private fun TvOnDemandVaultSection"

start_idx = content.find(start_marker)
if start_idx == -1:
    print("Could not find start marker")
    exit(1)

# Find the end marker, and then backtrack to the previous @Composable
end_idx = content.find(end_marker, start_idx)
composable_idx = content.rfind("@Composable", start_idx, end_idx)
if composable_idx != -1 and composable_idx < end_idx - 100:
    end_idx = composable_idx

replacement = """private fun EpgGuideSection(
    channel: TvChannelEntity,
    channels: List<TvChannelEntity>,
    onSelectChannel: (String) -> Unit,
    onToggleReminder: (TvChannelEntity) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Electronic Program Guide (EPG)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Full broadcast timetable across Townsquare Central Television.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val verticalScroll = rememberScrollState()
        val horizontalScroll = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, com.example.ui.theme.DarkBorder, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            Row(modifier = Modifier.verticalScroll(verticalScroll)) {
                // Fixed Channels Column
                Column(
                    modifier = Modifier
                        .width(110.dp)
                        .background(com.example.ui.theme.DarkBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.5.dp, com.example.ui.theme.DarkBorder),
                        contentAlignment = Alignment.Center
                    ) {
                         Text("CHANNEL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    channels.forEach { ch ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .border(0.5.dp, com.example.ui.theme.DarkBorder)
                                .clickable { onSelectChannel(ch.id) }
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(text = ch.iconEmoji, fontSize = 16.sp)
                                Text(
                                    text = "CH ${ch.channelNumber}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(ch.themeColorHex)
                                )
                                Text(
                                    text = ch.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Horizontally Scrollable Timeline + Grid
                Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                    // Timeline
                    Row(modifier = Modifier.height(40.dp)) {
                        for (hour in 6..24) {
                            Box(
                                modifier = Modifier
                                    .width(240.dp) // 1 hour = 240dp (4dp / min)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(0.5.dp, com.example.ui.theme.DarkBorder),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = String.format("%02d:00", hour),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    // Program Rows
                    channels.forEach { ch ->
                        Row(modifier = Modifier.height(80.dp).border(0.5.dp, com.example.ui.theme.DarkBorder)) {
                            val schedule = getSampleScheduleForChannel(ch.channelNumber)
                            
                            if (schedule.isNotEmpty()) {
                                // Calculate initial spacer if the first show doesn't start at 06:00
                                val firstStart = parseStartTimeMinutes(schedule.first().timeSlot)
                                val offsetMins = maxOf(firstStart - 360, 0)
                                if (offsetMins > 0) {
                                    Spacer(modifier = Modifier.width((offsetMins * 4).dp))
                                }
                            }

                            schedule.forEach { item ->
                                val width = (item.durationMinutes * 4).dp
                                Box(
                                    modifier = Modifier
                                        .width(width)
                                        .fillMaxHeight()
                                        .border(0.5.dp, com.example.ui.theme.DarkBorder)
                                        .background(if (item.isLiveNow) Color(ch.themeColorHex).copy(alpha = 0.2f) else Color.Transparent)
                                        .padding(4.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (item.isLiveNow) Color(ch.themeColorHex) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.timeSlot,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (item.isLiveNow) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Surface(
                                                color = Color.Red,
                                                shape = RoundedCornerShape(2.dp)
                                            ) {
                                                Text(
                                                    text = "LIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
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
    }
}
"""

new_content = content[:start_idx] + replacement + "\n" + content[end_idx:]
with open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "w") as f:
    f.write(new_content)
print("done")
