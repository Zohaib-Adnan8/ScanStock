package com.example.scanstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScannerActivity extends AppCompatActivity {

    DecoratedBarcodeView barcodeScanner;
    Button btnFlash, btnManualConfirm, btnBack;
    EditText etManualBarcode;
    boolean isFlashOn = false;
    boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        barcodeScanner = findViewById(R.id.barcodeScanner);
        btnFlash = findViewById(R.id.btnFlash);
        btnManualConfirm = findViewById(R.id.btnManualConfirm);
        btnBack = findViewById(R.id.btnBack);
        etManualBarcode = findViewById(R.id.etManualBarcode);

        // Start scanning
        barcodeScanner.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && !scanned) {
                    scanned = true;
                    barcodeScanner.pause();
                    goToAddItem(result.getText());
                }
            }
        });

        // Flashlight toggle
        btnFlash.setOnClickListener(v -> {
            if (isFlashOn) {
                barcodeScanner.setTorchOff();
                btnFlash.setText("🔦 Toggle Flashlight");
                btnFlash.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#333355")));
                isFlashOn = false;
            } else {
                barcodeScanner.setTorchOn();
                btnFlash.setText("🔦 Flashlight ON ✅");
                btnFlash.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#F4C430")));
                isFlashOn = true;
            }
        });

        // Manual entry
        btnManualConfirm.setOnClickListener(v -> {
            String barcode = etManualBarcode.getText().toString().trim();
            if (barcode.isEmpty()) {
                Toast.makeText(this, "Please enter a barcode first",
                        Toast.LENGTH_SHORT).show();
            } else {
                goToAddItem(barcode);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanned = false;
        barcodeScanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }

    private void goToAddItem(String barcode) {
        Intent intent = new Intent(ScannerActivity.this, AddItemActivity.class);
        intent.putExtra("barcode", barcode);
        startActivity(intent);
    }
}