package com.example.scanstock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanstock.database.AppDatabase;
import com.example.scanstock.database.Item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button fabScan, btnExport;
    TextView tvTotalCount, tvLowCount, tvInStockCount;
    AppDatabase db;
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animate top section
        findViewById(R.id.recyclerView).setAlpha(0f);
        findViewById(R.id.recyclerView).animate().alpha(1f).setDuration(800).start();

        recyclerView = findViewById(R.id.recyclerView);
        fabScan = findViewById(R.id.fabScan);
        btnExport = findViewById(R.id.btnExport);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvLowCount = findViewById(R.id.tvLowCount);
        tvInStockCount = findViewById(R.id.tvInStockCount);

        db = AppDatabase.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadItems();

        fabScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(intent);
        });

        btnExport.setOnClickListener(v -> exportToCSV());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        List<Item> items = db.itemDao().getAllItems();

        int total = db.itemDao().getTotalCount();
        int low = db.itemDao().getLowStockCount();
        int inStock = total - low;

        tvTotalCount.setText(String.valueOf(total));
        tvLowCount.setText(String.valueOf(low));
        tvInStockCount.setText(String.valueOf(inStock));

        adapter = new ItemAdapter(this, items, db);
        recyclerView.setAdapter(adapter);
    }

    private void exportToCSV() {
        List<Item> items = db.itemDao().getAllItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "No items to export!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build CSV content
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Barcode,Quantity,Category,Notes,Date Added\n");
        for (Item item : items) {
            csv.append(item.name != null ? item.name : "").append(",")
                    .append(item.barcode != null ? item.barcode : "").append(",")
                    .append(item.quantity).append(",")
                    .append(item.category != null ? item.category : "").append(",")
                    .append(item.notes != null ? item.notes : "").append(",")
                    .append(item.dateAdded != null ? item.dateAdded : "").append("\n");
        }

        try {
            // Create real CSV file
            File csvFile = new File(getCacheDir(), "ScanStock_Inventory.csv");
            FileWriter writer = new FileWriter(csvFile);
            writer.write(csv.toString());
            writer.close();

            // Get URI using FileProvider
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    "com.example.scanstock.fileprovider",
                    csvFile
            );

            // Share the actual CSV file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ScanStock Inventory Export");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Export Inventory as CSV"));

        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}