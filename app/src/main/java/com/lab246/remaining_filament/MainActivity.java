package com.lab246.remaining_filament;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText txt_core_diameter = findViewById(R.id.core_diameter);
        EditText txt_full_radius = findViewById(R.id.full_radius);
        // ... other EditTexts

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start, int count, int after) {
                // Called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called as the text is being changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called after the text has changed
            }
        };

        txt_core_diameter.addTextChangedListener(textWatcher);
        txt_full_radius.addTextChangedListener(textWatcher);
        // ... add the listener to other EditTexts
    }


    private double calc(){
        return 0;
    }
}