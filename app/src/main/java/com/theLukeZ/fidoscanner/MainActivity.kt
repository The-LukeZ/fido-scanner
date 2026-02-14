package com.theLukeZ.fidoscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.theLukeZ.fidoscanner.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalysis? = null
    private var isScanning = false
    
    companion object {
        private const val TAG = "FIDOScanner"
        private const val CAMERA_PERMISSION_CODE = 100
        private const val FIDO_SCHEME = "fido"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Handle intent if app was opened via FIDO URI
        handleIntent(intent)

        // Setup scan button
        binding.scanButton.setOnClickListener {
            if (isScanning) {
                stopScanning()
            } else {
                startScanning()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        // Check if app was opened via FIDO URI
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            val uri = intent.data!!
            if (uri.scheme == FIDO_SCHEME) {
                handleFidoUri(uri.toString())
            }
        }
    }

    private fun startScanning() {
        if (checkCameraPermission()) {
            isScanning = true
            binding.scanButton.text = "Stop Scanning"
            binding.instructionsText.visibility = View.GONE
            binding.previewView.visibility = View.VISIBLE
            binding.resultLabel.visibility = View.GONE
            binding.resultText.visibility = View.GONE
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun stopScanning() {
        isScanning = false
        binding.scanButton.text = getString(R.string.scan_button)
        binding.previewView.visibility = View.GONE
        binding.instructionsText.visibility = View.VISIBLE
        
        // Stop camera
        val cameraProvider = ProcessCameraProvider.getInstance(this)
        cameraProvider.addListener({
            cameraProvider.get().unbindAll()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analyzer for QR code scanning
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { qrCode ->
                        runOnUiThread {
                            handleQRCodeResult(qrCode)
                        }
                    })
                }

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(
                    this,
                    "Failed to start camera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun handleQRCodeResult(qrCode: String) {
        if (!isScanning) return

        Log.d(TAG, "QR Code detected: $qrCode")
        
        // Stop scanning after successful detection
        stopScanning()
        
        // Show result
        binding.resultLabel.visibility = View.VISIBLE
        binding.resultText.visibility = View.VISIBLE
        binding.resultText.text = qrCode

        // Check if it's a FIDO URI
        if (qrCode.startsWith("$FIDO_SCHEME:", ignoreCase = true)) {
            handleFidoUri(qrCode)
        }
    }

    private fun handleFidoUri(uri: String) {
        Log.d(TAG, "FIDO URI detected: $uri")
        
        // Show dialog for FIDO URI
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.fido_detected))
            .setMessage("URI: $uri\n\n${getString(R.string.fido_handling)}")
            .setPositiveButton("Open") { _, _ ->
                // Try to open the FIDO URI with the system
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    // Remove this app from the options to prevent infinite loop
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    
                    // Check if there's an app to handle this
                    val packageManager = packageManager
                    val activities = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        packageManager.queryIntentActivities(
                            intent,
                            android.content.pm.PackageManager.ResolveInfoFlags.of(0)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        packageManager.queryIntentActivities(intent, 0)
                    }
                    
                    if (activities.size > 1) {
                        // Multiple apps can handle this, let user choose
                        startActivity(Intent.createChooser(intent, "Open FIDO URI with"))
                    } else if (activities.size == 1) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "No app found to handle FIDO URI",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to open FIDO URI", e)
                    Toast.makeText(
                        this,
                        "Failed to open FIDO URI: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Copy") { _, _ ->
                // Copy to clipboard
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("FIDO URI", uri)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "URI copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Dismiss", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    // QR Code Analyzer using ML Kit
    private class QRCodeAnalyzer(
        private val onQRCodeDetected: (String) -> Unit
    ) : ImageAnalysis.Analyzer {

        private val scanner = BarcodeScanning.getClient()
        private var lastAnalyzedTimestamp = 0L

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            
            // Analyze at most once per 500ms for better responsiveness
            if (currentTimestamp - lastAnalyzedTimestamp >= 500) {
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            // Process only the first detected barcode to avoid multiple callbacks
                            if (barcodes.isNotEmpty()) {
                                val barcode = barcodes.first()
                                when (barcode.valueType) {
                                    Barcode.TYPE_TEXT,
                                    Barcode.TYPE_URL -> {
                                        barcode.rawValue?.let { value ->
                                            onQRCodeDetected(value)
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Barcode scanning failed", it)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                    
                    lastAnalyzedTimestamp = currentTimestamp
                } else {
                    imageProxy.close()
                }
            } else {
                imageProxy.close()
            }
        }
    }
}
