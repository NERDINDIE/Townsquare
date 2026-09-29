package com.example.ui.plus.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class TerminalLine(val text: String, val type: LineType = LineType.OUTPUT)

enum class LineType {
    INPUT, OUTPUT, ERROR, SUCCESS
}

@Composable
fun CliDosSkin(modifier: Modifier = Modifier) {
    var commandInput by remember { mutableStateOf("") }
    var terminalColor by remember { mutableStateOf(Color(0xFF33FF33)) } // Default DOS Green
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Initialize console history
    val history = remember {
        mutableStateListOf(
            TerminalLine("TOWNSQUARE MS-DOS v2.86 - LICENSED BY SECURE LEDGER"),
            TerminalLine("COPYRIGHT (C) 1985-2026 TOWNSQUARE SYSTEM"),
            TerminalLine("TYPE 'help' FOR A LIST OF AVAILABLE SYSTEM COMMANDS."),
            TerminalLine("")
        )
    }

    // Process typed commands
    val processCommand = { cmd: String ->
        val trimmed = cmd.trim()
        if (trimmed.isNotEmpty()) {
            history.add(TerminalLine("> $trimmed", LineType.INPUT))

            val parts = trimmed.split(" ")
            val baseCmd = parts[0].lowercase()

            when (baseCmd) {
                "help" -> {
                    history.add(TerminalLine("SUPPORTED SYSTEM COMMANDS:", LineType.SUCCESS))
                    history.add(TerminalLine("  help               - Display command reference manual"))
                    history.add(TerminalLine("  news               - Feed query for latest local news dispatches"))
                    history.add(TerminalLine("  weather            - Telemetry request for meteorological parameters"))
                    history.add(TerminalLine("  state              - Status check on your secure civil ledger ID"))
                    history.add(TerminalLine("  audio              - Status check on active transmission streams"))
                    history.add(TerminalLine("  arcade             - High score report on active cabinet games"))
                    history.add(TerminalLine("  color [name]       - Reconfigure font color (green, amber, cyan, purple, white)"))
                    history.add(TerminalLine("  clear              - Wipe active console buffer"))
                    history.add(TerminalLine("  about              - Detail technical specifications of Terminal v2.86"))
                }
                "news" -> {
                    history.add(TerminalLine("ESTABLISHING CONNECTION TO CIVIC NEWS NETWORK...", LineType.OUTPUT))
                    history.add(TerminalLine("CIVIC NEWS: New Promenade opening tomorrow morning (Civic Press)", LineType.SUCCESS))
                    history.add(TerminalLine("WEATHER ALERT: Offshore squall expected at 9 PM (Harbor Desk)", LineType.ERROR))
                    history.add(TerminalLine("MARKETPLACE: Vintage audio equipment auction ending soon", LineType.OUTPUT))
                    history.add(TerminalLine("COMMUNITY: Old Town clocktower renovation begins (Town Council)", LineType.SUCCESS))
                }
                "weather" -> {
                    history.add(TerminalLine("METEOROLOGICAL TELEMETRY FROM HARBOR DESK:", LineType.OUTPUT))
                    history.add(TerminalLine("  Active Status : Squall warning active", LineType.ERROR))
                    history.add(TerminalLine("  Temperature   : 18 deg C", LineType.OUTPUT))
                    history.add(TerminalLine("  Barometer     : 1012.4 hPa (Dropping)", LineType.OUTPUT))
                    history.add(TerminalLine("  Anemometer    : 24.5 kts (North-East)", LineType.OUTPUT))
                }
                "state" -> {
                    history.add(TerminalLine("INSPECTING SECURE CIVIL LEDGER PROFILE...", LineType.OUTPUT))
                    history.add(TerminalLine("  National ID Passport       : VALID (Expires 2031)", LineType.SUCCESS))
                    history.add(TerminalLine("  Digital Driver's License   : ACTIVE (Secure Biometrics)", LineType.SUCCESS))
                    history.add(TerminalLine("  Pending Municipal Taxes    : FILED (Green Refund Pending)", LineType.SUCCESS))
                    history.add(TerminalLine("  Outstanding Civic Bills    : 1 PENDING ($85.00 Electric)", LineType.ERROR))
                }
                "audio" -> {
                    history.add(TerminalLine("AUDIO NODE HARDWARE CHANNELS ACTIVE:", LineType.OUTPUT))
                    history.add(TerminalLine("  Node 1: Local FM 104.2 (Tuned dial)", LineType.SUCCESS))
                    history.add(TerminalLine("  Node 2: Generative Synthesizer wave feed (Streaming)", LineType.SUCCESS))
                    history.add(TerminalLine("  Node 3: On-demand Classical sonatas (Chopin/Debussy)", LineType.OUTPUT))
                }
                "arcade" -> {
                    history.add(TerminalLine("LOCAL CABINET STATISTICS BOARD:", LineType.OUTPUT))
                    history.add(TerminalLine("  Cabinet 1: Space Invaders - High: 100 PTS", LineType.SUCCESS))
                    history.add(TerminalLine("  Cabinet 2: Block Breaker - High: 120 PTS", LineType.SUCCESS))
                    history.add(TerminalLine("  Tournament: Retro Cup season resets in 3 days.", LineType.OUTPUT))
                }
                "color" -> {
                    if (parts.size < 2) {
                        history.add(TerminalLine("USAGE: color [green/amber/cyan/purple/white]", LineType.ERROR))
                    } else {
                        when (parts[1].lowercase()) {
                            "green" -> {
                                terminalColor = Color(0xFF33FF33)
                                history.add(TerminalLine("TERMINAL RECONFIGURED TO GREEN PHOSPHOR", LineType.SUCCESS))
                            }
                            "amber" -> {
                                terminalColor = Color(0xFFFFB300)
                                history.add(TerminalLine("TERMINAL RECONFIGURED TO AMBER CRT", LineType.SUCCESS))
                            }
                            "cyan" -> {
                                terminalColor = Color(0xFF00E5FF)
                                history.add(TerminalLine("TERMINAL RECONFIGURED TO MODERN CYAN", LineType.SUCCESS))
                            }
                            "purple" -> {
                                terminalColor = Color(0xFFD500F9)
                                history.add(TerminalLine("TERMINAL RECONFIGURED TO RADICAL PURPLE", LineType.SUCCESS))
                            }
                            "white" -> {
                                terminalColor = Color.White
                                history.add(TerminalLine("TERMINAL RECONFIGURED TO MONOCHROME WHITE", LineType.SUCCESS))
                            }
                            else -> {
                                history.add(TerminalLine("UNKNOWN PALETTE. CHOOSE: green, amber, cyan, purple, or white", LineType.ERROR))
                            }
                        }
                    }
                }
                "clear" -> {
                    history.clear()
                }
                "about" -> {
                    history.add(TerminalLine("TECHNICAL SPECIFICATIONS:", LineType.SUCCESS))
                    history.add(TerminalLine("  OS Architecture   : MS-DOS compatible v2.86 x86emu"))
                    history.add(TerminalLine("  Host Superapp     : Townsquare Omni-Client"))
                    history.add(TerminalLine("  Font Family       : IBM BIOS Monospace"))
                    history.add(TerminalLine("  Network Protocol  : Municipal Decentralized Ledger Link"))
                }
                else -> {
                    history.add(TerminalLine("BAD COMMAND OR FILE NAME: '$baseCmd'. TYPE 'help' FOR LIST.", LineType.ERROR))
                }
            }
            history.add(TerminalLine(""))
            commandInput = ""

            // Auto-scroll to latest command
            coroutineScope.launch {
                delay(100)
                listState.animateScrollToItem(history.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050505)) // Pure retro dark terminal background
            .padding(16.dp)
            .testTag("cli_dos_skin_root")
    ) {
        // Preset Color Quick Toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CRT COLOR:",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            listOf(
                "GREEN" to Color(0xFF33FF33),
                "AMBER" to Color(0xFFFFB300),
                "CYAN" to Color(0xFF00E5FF),
                "PURPLE" to Color(0xFFD500F9),
                "WHITE" to Color.White
            ).forEach { (name, color) ->
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (terminalColor == color) color.copy(alpha = 0.2f) else Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(1.dp, color),
                    modifier = Modifier
                        .clickable { terminalColor = color }
                ) {
                    Text(
                        text = name,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Terminal Output Screen Area
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, terminalColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            color = Color.Black,
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(history) { line ->
                        val textColor = when (line.type) {
                            LineType.INPUT -> terminalColor
                            LineType.ERROR -> Color(0xFFFF5252)
                            LineType.SUCCESS -> Color(0xFF30D158)
                            else -> terminalColor.copy(alpha = 0.85f)
                        }

                        Text(
                            text = line.text,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = textColor
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Command Prompt Input Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "C:\\TOWNSQUARE>",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = terminalColor
            )

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("cli_prompt_input"),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = terminalColor
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = terminalColor,
                    unfocusedBorderColor = terminalColor.copy(alpha = 0.4f),
                    cursorColor = terminalColor
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { processCommand(commandInput) }
                ),
                placeholder = {
                    Text(
                        text = "type command...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = terminalColor.copy(alpha = 0.3f)
                    )
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = { processCommand(commandInput) },
                colors = ButtonDefaults.buttonColors(containerColor = terminalColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "RUN",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}
