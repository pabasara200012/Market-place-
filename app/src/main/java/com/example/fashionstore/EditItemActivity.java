package com.example.fashionstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "extra_item";

    private EditText nameEdit, descriptionEdit, priceEdit;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Initialize views
        nameEdit = findViewById(R.id.editItemName);
        descriptionEdit = findViewById(R.id.editItemDescription);
        priceEdit = findViewById(R.id.editItemPrice);
        saveButton = findViewById(R.id.saveItemButton);

        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        setupEditForm(item);
    }

    private void setupEditForm(Item item) {
        if (item != null) {
            nameEdit.setText(item.getName());
            descriptionEdit.setText(item.getDescription());
            priceEdit.setText(String.valueOf(item.getPrice()));

            saveButton.setOnClickListener(v -> saveItemChanges(item));
        }
    }

    private void saveItemChanges(Item item) {
        String name = nameEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String priceStr = priceEdit.getText().toString().trim();

        if (!name.isEmpty() && !description.isEmpty() && !priceStr.isEmpty()) {
            double price = Double.parseDouble(priceStr);
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            // Save changes to database
            finish();
        }
    }
}
