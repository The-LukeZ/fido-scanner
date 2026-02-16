# FIDO Scanner

An Android app to scan FIDO URI QR codes for cross-device passkey authentication.

## Overview

A simple QR code scanner to detect FIDO URIs (`fido://`) when the default camera app does not.

For example, you have a passkey cofigured for Github on your phone and you want to login on your PC,
your browser typically shows you a QR code to scan with your phone which _should_ open your password manager
so you can do the passkey challenge on your phone.

However for some reason, a phone's camera might not open the passowrd manager. This app helps you do just that.

## Features

- Scans QR codes using ML Kit and CameraX.
- Recognizes and handles `fido://` URIs.
- Registers as a system-wide handler for FIDO URIs.

## Requirements

- Android 8.0 (API 26) or higher.
- Camera permission.

## Tech Stack

- Kotlin
- Min SDK 26, Target SDK 34
- CameraX, ML Kit, ZXing

## Setup

1. Clone the repo.
2. Open in Android Studio.
3. Build and run.

---

It is not planned in the near future to make this app public - the following things need to happen so I make it a public app:

- Decent app icon (idk where I got the current one from)
- upgrade `targetSdk` version to latest version

It won't be added to the Play Store as of 2026, because Google can go eat dirt with their developer verification process.
