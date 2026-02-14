# FIDO Scanner - Quick Start Guide

## What is FIDO Scanner?

FIDO Scanner is an Android app that scans QR codes to detect FIDO URIs (`fido://`) used for cross-device passkey authentication. It's designed for situations where your device's default camera doesn't recognize FIDO QR codes.

## Key Features

✅ **QR Code Scanning** - Uses CameraX + ML Kit for reliable scanning
✅ **FIDO URI Detection** - Automatically recognizes `fido://` scheme
✅ **Intent Handling** - Registered as FIDO URI handler
✅ **Simple UI** - One button to scan, clear results display
✅ **Broad Compatibility** - Android 8.0+ (API 26+)

## Quick Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/The-LukeZ/fido-scanner.git
   cd fido-scanner
   ```

2. **Open in Android Studio**
   - File → Open → Select the `fido-scanner` directory
   - Wait for Gradle sync to complete

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click Run (▶️) or press Shift+F10
   - Grant camera permission when prompted

## How to Use

### Scanning a QR Code

1. Open the app
2. Tap **"Scan QR Code"**
3. Grant camera permission (first time only)
4. Point camera at QR code
5. Result displays automatically

### Handling FIDO URIs

When a FIDO URI is detected:
- **Open**: Launches the URI with system handler
- **Copy**: Copies URI to clipboard
- **Dismiss**: Closes dialog

## Technical Details

### Architecture

```
MainActivity.kt
├── Camera Management (CameraX)
├── QR Code Analysis (ML Kit)
├── FIDO URI Handling
└── Permission Management
```

### Dependencies

- **CameraX**: Camera preview and lifecycle
- **ML Kit**: Barcode scanning
- **ZXing**: QR code format support
- **Material Components**: Modern UI

### Permissions

- `CAMERA` - Required for scanning
- `INTERNET` - For FIDO operations

## Project Structure

```
fido-scanner/
├── app/
│   ├── build.gradle.kts          # App dependencies
│   └── src/main/
│       ├── AndroidManifest.xml   # Permissions & intents
│       ├── java/.../MainActivity.kt
│       └── res/
│           ├── layout/           # UI layouts
│           ├── values/           # Strings, colors, themes
│           └── xml/              # Backup rules
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Project settings
└── README.md                     # Full documentation
```

## Common Issues

### Camera Not Starting
- Check camera permission is granted
- Ensure no other app is using camera
- Restart the app

### QR Code Not Detected
- Ensure good lighting
- Hold camera steady
- Keep QR code in frame
- Try different distances

### Build Errors
```bash
# Clean and rebuild
./gradlew clean build

# Sync Gradle
./gradlew --refresh-dependencies
```

## Development

### Building for Debug
```bash
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### Building for Release
```bash
./gradlew assembleRelease
# APK location: app/build/outputs/apk/release/app-release.apk
```

### Running Tests
```bash
./gradlew test              # Unit tests
./gradlew connectedCheck    # Instrumented tests
```

## Compatibility

- **Minimum**: Android 8.0 (API 26)
- **Target**: Android 14 (API 34)
- **Tested**: Android 14, HyperOS
- **Architecture**: All (ARM, ARM64, x86, x86_64)

## Security

✅ No known vulnerabilities in dependencies
✅ Runtime permission handling
✅ No sensitive data storage
✅ Safe intent handling

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License

MIT License - See LICENSE file

## Support

For issues or questions:
- Open an issue on GitHub
- Check the README.md for detailed docs

---

**Version**: 1.0
**Last Updated**: 2024
