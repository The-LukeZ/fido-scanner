# FIDO Scanner

An Android QR scanner app to scan FIDO URI QR codes and trigger the authentication flow for cross-device passkey operations.

## Overview

FIDO Scanner is a minimal yet powerful Android application that scans QR codes to detect FIDO URIs (fido://) used for cross-device passkey authentication. When your phone's default camera doesn't recognize FIDO QR codes, this app provides a dedicated solution.

## Features

- **QR Code Scanning**: Uses ML Kit and CameraX for reliable QR code detection
- **FIDO URI Detection**: Automatically recognizes `fido://` scheme URIs
- **Intent Handling**: Registers as a handler for FIDO URI scheme
- **User-Friendly UI**: Simple interface with scan button and result display
- **Permission Management**: Handles camera permissions gracefully
- **Cross-Device Support**: Compatible with Android 14+ and HyperOS

## Requirements

- Android 14 (API level 34) or higher
- Camera permission
- Internet permission (for FIDO operations)

## Technology Stack

- **Language**: Kotlin
- **Minimum SDK**: 34 (Android 14)
- **Target SDK**: 34
- **Dependencies**:
  - AndroidX Core KTX
  - Material Components
  - CameraX (Camera2, Lifecycle, View)
  - ML Kit Barcode Scanning
  - ZXing Core

## Installation

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 8 or higher
- Android SDK with API level 34

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/The-LukeZ/fido-scanner.git
   cd fido-scanner
   ```

2. Open the project in Android Studio

3. Sync Gradle files (Android Studio will prompt automatically)

4. Build the project:
   ```bash
   ./gradlew build
   ```

5. Run on device or emulator:
   ```bash
   ./gradlew installDebug
   ```

## Usage

### Scanning QR Codes

1. Launch the FIDO Scanner app
2. Tap the "Scan QR Code" button
3. Grant camera permission if prompted
4. Point your camera at a QR code
5. The app will automatically detect and display the scanned content

### Handling FIDO URIs

When a QR code containing a FIDO URI (starting with `fido://`) is detected:

1. The app will display a dialog showing the detected FIDO URI
2. You can choose to:
   - **Open**: Launch the URI with the system (will prompt for app selection)
   - **Copy**: Copy the URI to clipboard
   - **Dismiss**: Close the dialog

### Intent Filter

The app registers an intent filter for the `fido://` scheme, allowing it to handle FIDO URIs from:
- Other apps
- Web browsers
- System-wide URI handlers

## Project Structure

```
fido-scanner/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/theLukeZ/fidoscanner/
│   │       │   └── MainActivity.kt
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   ├── colors.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Key Components

### MainActivity.kt

The main activity handles:
- Camera initialization and lifecycle
- QR code detection using ML Kit
- FIDO URI parsing and handling
- Permission requests
- UI state management

### AndroidManifest.xml

Defines:
- Camera permissions
- Internet permissions
- Intent filter for `fido://` scheme
- Application metadata

### Layout (activity_main.xml)

Contains:
- CameraX PreviewView for live camera feed
- MaterialButton for scan control
- TextViews for instructions and results

## Permissions

The app requires the following permissions:

- **CAMERA**: Required for scanning QR codes
- **INTERNET**: Required for potential FIDO operations

Permissions are requested at runtime following Android best practices.

## FIDO URI Scheme

The app handles URIs with the `fido://` scheme as specified in FIDO2 specifications for cross-device authentication flows.

Example FIDO URI:
```
fido://webauthn?challenge=xyz&origin=https://example.com
```

## HyperOS Compatibility

The app is tested and compatible with Xiaomi's HyperOS, which is based on Android 14. All features work as expected on HyperOS devices.

## Development

### Building for Release

1. Generate a signing key:
   ```bash
   keytool -genkey -v -keystore fido-scanner.keystore -alias fido-scanner -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Update `app/build.gradle.kts` with signing configuration

3. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### Camera Not Starting

- Ensure camera permission is granted
- Check if another app is using the camera
- Restart the app

### QR Code Not Detected

- Ensure good lighting conditions
- Hold the camera steady
- Keep the QR code within the camera frame
- Try moving closer or further from the QR code

### FIDO URI Not Opening

- Check if you have an app installed that can handle FIDO URIs
- Try copying the URI and opening it manually
- Verify the URI format is correct

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the MIT License.

## Acknowledgments

- ZXing project for QR code processing
- Google ML Kit for barcode scanning
- AndroidX CameraX for camera functionality

## Contact

For issues, questions, or suggestions, please open an issue on GitHub.

---

Built with ❤️ for the FIDO community
