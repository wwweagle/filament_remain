package com.lab246.remaining_filament;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText txtCoreDiameter;
    private EditText txtFullRadius;
    private EditText txtEmptyRadius;
    private EditText txtSlotWidth;
    private EditText txtFilamentDiameter;
    private TextView txtRemainingFilament;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        txtCoreDiameter = findViewById(R.id.core_diameter);
        txtFullRadius = findViewById(R.id.full_radius);
        txtEmptyRadius = findViewById(R.id.empty_radius);
        txtSlotWidth = findViewById(R.id.slot_width);
        txtFilamentDiameter = findViewById(R.id.filament_diameter);
        txtRemainingFilament = findViewById(R.id.txt_remaining_filament);

        // Set input types for decimal numbers
        txtCoreDiameter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtFullRadius.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtEmptyRadius.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtSlotWidth.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtFilamentDiameter.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Initialize SharedPreferences
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = prefs.edit();

        // Get initial values with defaults
        String coreDiameter = prefs.getString("core_diameter", "76");
        String fullRadius = prefs.getString("full_radius", "62");
        String emptyRadius = prefs.getString("empty_radius", "20");
        String slotWidth = prefs.getString("slot_width", "64");
        String filamentDiameter = prefs.getString("filament_diameter", "1.75");

        // Set initial values to EditTexts
        txtCoreDiameter.setText(coreDiameter);
        txtFullRadius.setText(fullRadius);
        txtEmptyRadius.setText(emptyRadius);
        txtSlotWidth.setText(slotWidth);
        txtFilamentDiameter.setText(filamentDiameter);

        // Save initial values to SharedPreferences (even if they are defaults)
        editor.putString("core_diameter", coreDiameter)
                .putString("full_radius", fullRadius)
                .putString("empty_radius", emptyRadius)
                .putString("slot_width", slotWidth)
                .putString("filament_diameter", filamentDiameter)
                .apply();

        // Update remaining filament immediately after setup
        updateRemainingFilament();

        setupTextWatchers();
    }

    private double calc(double core_diameter, double full_radius, double empty_radius, double slot_width, double filament_diameter) {
        double avg_cyc_len = ((core_diameter + full_radius - empty_radius) * Math.PI);
        double layers = Math.floor((full_radius - empty_radius) / filament_diameter);
        double cyc_per_layer = Math.floor(slot_width / filament_diameter);
        double result = avg_cyc_len * layers * cyc_per_layer / 1000;
        return Math.max(result, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("core_diameter", txtCoreDiameter.getText().toString());
        outState.putString("full_radius", txtFullRadius.getText().toString());
        outState.putString("empty_radius", txtEmptyRadius.getText().toString());
        outState.putString("slot_width", txtSlotWidth.getText().toString());
        outState.putString("filament_diameter", txtFilamentDiameter.getText().toString());
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateRemainingFilament();
            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("core_diameter", txtCoreDiameter.getText().toString())
                        .putString("full_radius", txtFullRadius.getText().toString())
                        .putString("empty_radius", txtEmptyRadius.getText().toString())
                        .putString("slot_width", txtSlotWidth.getText().toString())
                        .putString("filament_diameter", txtFilamentDiameter.getText().toString())
                        .apply();
            }
        };

        txtCoreDiameter.addTextChangedListener(textWatcher);
        txtFullRadius.addTextChangedListener(textWatcher);
        txtEmptyRadius.addTextChangedListener(textWatcher);
        txtSlotWidth.addTextChangedListener(textWatcher);
        txtFilamentDiameter.addTextChangedListener(textWatcher);
    }

    private void updateRemainingFilament() {
        try {
            double core_diameter = Double.parseDouble(txtCoreDiameter.getText().toString());
            double full_radius = Double.parseDouble(txtFullRadius.getText().toString());
            double empty_radius = Double.parseDouble(txtEmptyRadius.getText().toString());
            double slot_width = Double.parseDouble(txtSlotWidth.getText().toString());
            double filament_diameter = Double.parseDouble(txtFilamentDiameter.getText().toString());

            double result = calc(core_diameter, full_radius, empty_radius, slot_width, filament_diameter);
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            txtRemainingFilament.setText(decimalFormat.format(result));
        } catch (NumberFormatException e) {
            txtRemainingFilament.setText("0.00");
        }
    }
}
