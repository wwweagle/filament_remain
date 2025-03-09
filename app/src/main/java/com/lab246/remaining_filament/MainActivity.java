package com.lab246.remaining_filament;

import android.os.Bundle;
import android.text.Editable;
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

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setupTextWatchers();
    }


    private double calc(double core_diameter, double full_radius, double empty_radius, double slot_width, double filament_diameter){
        double avg_cyc_len=((core_diameter+full_radius-empty_radius)*3.14159);
        double layers=Math.floor((full_radius-empty_radius)/filament_diameter);
        double cyc_per_layer=Math.floor(slot_width/filament_diameter);
        double result=avg_cyc_len*layers*cyc_per_layer/1000;
        if (result<0) {
            return 0;
        }else{
            return result;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText txt_core_diameter = findViewById(R.id.core_diameter);
        EditText txt_full_radius = findViewById(R.id.full_radius);
        EditText txt_empty_radius = findViewById(R.id.empty_radius);
        EditText txt_slot_width = findViewById(R.id.slot_width);
        EditText txt_filament_diameter = findViewById(R.id.filament_diameter);

        outState.putString("core_diameter", txt_core_diameter.getText().toString());
        outState.putString("full_radius", txt_full_radius.getText().toString());
        outState.putString("empty_radius", txt_empty_radius.getText().toString());
        outState.putString("slot_width", txt_slot_width.getText().toString());
        outState.putString("filament_diameter", txt_filament_diameter.getText().toString());
    }

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

        if (savedInstanceState != null) {
            EditText txt_core_diameter = findViewById(R.id.core_diameter);
            EditText txt_full_radius = findViewById(R.id.full_radius);
            EditText txt_empty_radius = findViewById(R.id.empty_radius);
            EditText txt_slot_width = findViewById(R.id.slot_width);
            EditText txt_filament_diameter = findViewById(R.id.filament_diameter);

            txt_core_diameter.setText(savedInstanceState.getString("core_diameter"));
            txt_full_radius.setText(savedInstanceState.getString("full_radius"));
            txt_empty_radius.setText(savedInstanceState.getString("empty_radius"));
            txt_slot_width.setText(savedInstanceState.getString("slot_width"));
            txt_filament_diameter.setText(savedInstanceState.getString("filament_diameter"));
        }

        setupTextWatchers();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            EditText txt_core_diameter = findViewById(R.id.core_diameter);
            EditText txt_full_radius = findViewById(R.id.full_radius);
            EditText txt_empty_radius = findViewById(R.id.empty_radius);
            EditText txt_slot_width = findViewById(R.id.slot_width);
            EditText txt_filament_diameter = findViewById(R.id.filament_diameter);

            txt_core_diameter.setText(savedInstanceState.getString("core_diameter"));
            txt_full_radius.setText(savedInstanceState.getString("full_radius"));
            txt_empty_radius.setText(savedInstanceState.getString("empty_radius"));
            txt_slot_width.setText(savedInstanceState.getString("slot_width"));
            txt_filament_diameter.setText(savedInstanceState.getString("filament_diameter"));
        }
    }


    private void setupTextWatchers() {
        EditText txt_core_diameter = findViewById(R.id.core_diameter);
        EditText txt_full_radius = findViewById(R.id.full_radius);
        EditText txt_empty_radius = findViewById(R.id.empty_radius);
        EditText txt_slot_width = findViewById(R.id.slot_width);
        EditText txt_filament_diameter = findViewById(R.id.filament_diameter);
        TextView txt_remaining_filament = findViewById(R.id.txt_remaining_filament);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called as the text is being changed
                try {
                    double core_diameter = Double.parseDouble(txt_core_diameter.getText().toString());
                    double full_radius = Double.parseDouble(txt_full_radius.getText().toString());
                    double empty_radius = Double.parseDouble(txt_empty_radius.getText().toString());
                    double slot_width = Double.parseDouble(txt_slot_width.getText().toString());
                    double filament_diameter = Double.parseDouble(txt_filament_diameter.getText().toString());

                    double result = calc(core_diameter, full_radius, empty_radius, slot_width, filament_diameter);
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Example format
                    String formattedNumber = decimalFormat.format(result);

                    txt_remaining_filament.setText(formattedNumber);
                } catch (NumberFormatException e) {
                    // Handle the exception if needed
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called after the text has changed
            }
        };

        txt_core_diameter.addTextChangedListener(textWatcher);
        txt_full_radius.addTextChangedListener(textWatcher);
        txt_empty_radius.addTextChangedListener(textWatcher);
        txt_slot_width.addTextChangedListener(textWatcher);
        txt_filament_diameter.addTextChangedListener(textWatcher);
    }


}