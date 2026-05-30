package com.example.scanstock;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanstock.database.AppDatabase;
import com.example.scanstock.database.Item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    EditText etBarcode, etName, etQuantity, etNotes;
    Spinner spinnerCategory;
    Button btnSave, btnCancel;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Connect views
        etBarcode = findViewById(R.id.etBarcode);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etNotes = findViewById(R.id.etNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup database
        db = AppDatabase.getInstance(this);

        // Setup category spinner
        String[] categories = {"General", "Books", "Groceries", "Electronics", "Clothing", "Other"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Auto fill barcode from scanner
        String barcode = getIntent().getStringExtra("barcode");
        if (barcode != null) {
            etBarcode.setText(barcode);
        }

        // Save button
        btnSave.setOnClickListener(v -> saveItem());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveItem() {
        String barcode = etBarcode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Please enter item name");
            return;
        }
        if (quantityStr.isEmpty()) {
            etQuantity.setError("Please enter quantity");
            return;
        }

        // Create item
        Item item = new Item();
        item.barcode = barcode;
        item.name = name;
        item.quantity = Integer.parseInt(quantityStr);
        item.notes = notes;
        item.category = category;
        item.dateAdded = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Save to database
        db.itemDao().insert(item);

        Toast.makeText(this, "✅ " + name + " saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}