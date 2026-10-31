# IoT Companion Modernization & Universal Bluetooth Device Manager

Comprehensive architectural plan to overhaul the IoT Companion application into a modern, compact experience with a collapsible sandwich navigation menu, modularize all device simulations into dedicated isolated component files, and introduce a universal Bluetooth device management system supporting any connected hardware.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and UX choices have been confirmed through Phase 1 clarification:
> - **Navigation & Layout**: Modern compact title header featuring a collapsible sandwich menu (`Icons.Default.Menu`) that smoothly reveals device environments and settings without consuming permanent screen height.
> - **Simulation Modularity**: Extracting all embedded simulations (`SmartwatchSimulationView`, `EInkHomeDisplayView`, `AutomotiveClusterView`) out of the monolithic 733-line `TownsquareIotCompanionApp.kt` into separate, isolated Kotlin files.
> - **Universal Bluetooth Management**: Dedicated manager showing device name, category type, battery percentage with visual gauge, connection status, signal strength (RSSI), and device management settings (pairing, auto-connect, renaming, notification forwarding, and unpairing).

---

## 1. Overview & Core Concept

- **What It Does**: Transforms the IoT Companion from an oversized single-file simulator into a modular, production-grade connected hardware hub. Users can simulate specialized Townsquare companion experiences (smartwatches, in-car displays, E-Ink dashboards, MP3 audio, remote workstations) and manage any real or simulated Bluetooth peripheral connected to their device.
- **Target Persona**: Civic participants, motorists, transit commuters, smart home enthusiasts, and wearable tech users wanting a cohesive companion center.
- **Key Value**: Eliminates source code truncation and file bloat, maximizes vertical screen real estate with a compact header and sandwich menu, and delivers comprehensive Bluetooth device lifecycle management.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Collapsible Sandwich Menu Navigation**:
   - The user opens the IoT Companion and sees a sleek, compact top header: `[← Back] [☰ Menu] IoT Companion [⚡ 4 Connected]`.
   - Tapping the sandwich menu button slides down/in a compact grid/drawer of available environments and tools (Bluetooth Manager, Smartwatch OS, Car Dashboard, Automotive Cluster, E-Ink Hub, MP3 Player, Remote Workstation).
   - Selecting any environment immediately routes to that view and closes the menu automatically.

2. **Universal Bluetooth Device Hub Flow**:
   - Selecting "Bluetooth Devices" presents an overview of connected and paired peripherals.
   - Each card displays:
     - Hardware Type Icon (Headphones, Smartwatch, Car Audio, Gamepad, Sensor, Laptop)
     - Device Name & Unique MAC/Identifier
     - Real-Time Battery Bar (e.g. `85% 🔋`) with color thresholds (Green > 40%, Amber 20-40%, Red < 20%)
     - Signal Strength RSSI Meter (e.g. `-48 dBm • Excellent`)
     - Quick "Connected / Disconnected" switch
   - Tapping "Manage" opens a Device Settings Dialog to:
     - Rename the device
     - Toggle auto-reconnect
     - Enable low-latency transmission
     - Toggle Townsquare notification and audio forwarding
     - Unpair / Forget device
   - Tapping "+ Pair New Device" triggers a simulated radar scan for nearby Bluetooth devices with 1-tap pairing.

3. **Isolated Device Simulations**:
   - **Smartwatch OS**: Dedicated titanium AMOLED bezel with interactive watch faces (Classic Chrono, Retro Terminal, Neon Cyberpunk) and wrist tiles (News, Audio, Voice Memo, Health Telemetry).
   - **E-Ink Bedside Hub**: High-contrast paper aesthetic with civic bulletins, clock, and dual indoor/outdoor weather telemetry.
   - **Automotive Cluster**: Minimalist high-contrast in-car heads-up display with speedometer, media, and turn-by-turn guidance.

### Visual Identity & Theme
- **Theme Palette**: Deep Dark Slate (`0xFF0B111E`), Dark Surface (`0xFF131D2E`), Neon Cyan (`0xFF00D2FF`), Mint Teal (`0xFF00E676`), Warm Amber (`0xFFFFB300`), and Electric Coral (`0xFFFF5252`).
- **Layout Precision**: Strictly respects the 8.dp grid, 48.dp minimum touch targets, and edge-to-edge safe area insets.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Modular File Architecture for Simulations**:
  - *Chosen Approach*: Move `IotSmartwatchView.kt`, `IotEInkDisplayView.kt`, and `IotAutomotiveClusterView.kt` into separate files in `com.example.ui.plus.iot`.
  - *Why*: Reduces `TownsquareIotCompanionApp.kt` from 733 lines to under 200 lines, adhering to strict clean architecture guidelines (< 500 lines per file) and preventing IDE truncation.

- **Decision 2: Universal Bluetooth Device Abstraction**:
  - *Chosen Approach*: Implement a flexible `BluetoothDeviceModel` capable of representing audio devices, wearables, vehicle interfaces, input peripherals, and smart home IoT beacons.
  - *Why*: The user explicitly requested managing Bluetooth devices "regardless of the type of the connected device". A unified model with category-specific metadata accommodates all current and future Bluetooth hardware.

- **Decision 3: Sandwich Menu Header over Permanent Horizontal Tabs**:
  - *Chosen Approach*: Replace the tall 6-item `ScrollableTabRow` with a compact title header and an expandable sandwich menu drawer.
  - *Why*: Saves ~60dp of valuable screen height on mobile devices while providing a cleaner, modern presentation.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                   TownsquareIotCompanionApp.kt                         │
│  [Header: Back Button + Sandwich Menu Button + Title + Status Pill]    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ (Sandwich Menu Selection)
          ┌─────────────────────────┼─────────────────────────┐
          ▼                         ▼                         ▼
┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│ IotBluetooth      │     │ IotSmartwatch     │     │ IotCarDashboard   │
│ ManagerView.kt    │     │ View.kt           │     │ View.kt           │
│ • Paired Devices  │     │ • AMOLED Watch    │     │ • Gauge Clusters  │
│ • Battery Status  │     │ • Watch Faces     │     │ • Media & Engine  │
│ • Signal (RSSI)   │     │ • Complications   │     │ • Night Mode      │
│ • Device Settings │     │ • Wrist Tiles     │     └───────────────────┘
│ • Scan & Pair     │     └───────────────────┘               │
└───────────────────┘               │                         ▼
          │                         ▼               ┌───────────────────┐
          │               ┌───────────────────┐     │ IotAutomotive     │
          │               │ IotEInkDisplay    │     │ ClusterView.kt    │
          │               │ View.kt           │     │ • Speedometer HUD │
          │               │ • High-Contrast   │     │ • Turn Guidance   │
          │               │ • Daily Bulletin  │     └───────────────────┘
          │               │ • Climate Radar   │               │
          │               └───────────────────┘               ▼
          ▼                                         ┌───────────────────┐
┌───────────────────┐                               │ IotMp3Player /    │
│ IotBluetooth      │                               │ RemoteComputer    │
│ Models.kt         │                               │ View.kt           │
│ • Device Type     │                               │ • Retro Audio     │
│ • Battery Level   │                               │ • Terminal Shell  │
│ • Settings Config │                               └───────────────────┘
└───────────────────┘
```

### New & Refactored Component Files:
1. `app/src/main/java/com/example/ui/plus/iot/IotBluetoothModels.kt`:
   - Enums: `BluetoothDeviceType` (`AUDIO_HEADPHONES`, `AUDIO_SPEAKER`, `SMARTWATCH`, `FITNESS_BAND`, `CAR_AUDIO_OBD`, `KEYBOARD_MOUSE`, `GAME_CONTROLLER`, `IOT_SENSOR_BEACON`, `GENERIC_BLE`).
   - Data classes: `BluetoothDeviceEntity`, `BluetoothDeviceSettings`, `BluetoothScanState`.
2. `app/src/main/java/com/example/ui/plus/iot/IotBluetoothManagerView.kt`:
   - Interactive UI with device cards, battery gauges, RSSI signal indicators, device settings modal, and device scan radar dialog.
3. `app/src/main/java/com/example/ui/plus/iot/IotSmartwatchView.kt`:
   - Standalone smartwatch hardware frame, 3 watch faces (Classic Chrono, Retro Terminal, Neon Cyberpunk), and 5 wrist complication tiles.
4. `app/src/main/java/com/example/ui/plus/iot/IotEInkDisplayView.kt`:
   - Standalone paperwhite high-contrast display simulation.
5. `app/src/main/java/com/example/ui/plus/iot/IotAutomotiveClusterView.kt`:
   - Standalone in-car automotive heads-up dashboard display.
6. `app/src/main/java/com/example/ui/plus/iot/TownsquareIotCompanionApp.kt`:
   - Overhauled main coordinator with compact header, collapsible sandwich menu, and lightweight navigation state.
