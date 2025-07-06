package com.lab246.remaining_filament;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import com.lab246.remaining_filament.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private EditText currentFocusedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ensure EditText fields don't show keyboard when focused

        // Observe LiveData from ViewModel
        observeViewModel();
        // Setup listeners to update ViewModel
        setupTextWatchers();
        // Set up focus listeners to track the currently focused EditText
        setupFocusListeners();
        // Set up button click listeners
        setupButtonListeners();
    }

    /**
     * Formats a double to a string with a specified number of decimal places,
     * using Locale.US to ensure a consistent decimal separator ('.').
     * @param value The numeric value to format.
     * @param decimalPlaces The number of decimal places to display.
     * @return A locale-independent formatted string.
     */
    private String formatMeasurement(double value, int decimalPlaces) {
        // Create the format string dynamically, e.g., "%.0f", "%.2f"
        String formatPattern = "%." + decimalPlaces + "f";
        // Use Locale.US to guarantee the decimal separator is a period '.'
        return String.format(Locale.US, formatPattern, value);
    }



    private void observeViewModel() {
        viewModel.getCoreDiameter().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.coreDiameter.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                try {
                    double numericValue = Double.parseDouble(s);
                    // Format to no decimal places
                    String formattedValue = formatMeasurement(numericValue,0);
                    binding.calcLayout.coreDiameter.setText(formattedValue);
                } catch (NumberFormatException e) {
                    binding.calcLayout.coreDiameter.setText(s);
                }
            }
        });
        viewModel.getFullRadius().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.fullRadius.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                try {
                    double numericValue = Double.parseDouble(s);
                    // Format to no decimal places
                    String formattedValue = formatMeasurement(numericValue,0);
                    binding.calcLayout.fullRadius.setText(formattedValue);
                } catch (NumberFormatException e) {
                    binding.calcLayout.fullRadius.setText(s);
                }
            }
        });
        viewModel.getEmptyRadius().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.emptyRadius.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                try {
                    double numericValue = Double.parseDouble(s);
                    // Format to no decimal places
                    String formattedValue = formatMeasurement(numericValue,0);
                    binding.calcLayout.emptyRadius.setText(formattedValue);
                } catch (NumberFormatException e) {
                    binding.calcLayout.emptyRadius.setText(s);
                }
            }
        });
        viewModel.getSlotWidth().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.slotWidth.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                try {
                    double numericValue = Double.parseDouble(s);
                    // Format to no decimal places
                    String formattedValue = formatMeasurement(numericValue,0);
                    binding.calcLayout.slotWidth.setText(formattedValue);
                } catch (NumberFormatException e) {
                    binding.calcLayout.slotWidth.setText(s);
                }
            }
        });
        viewModel.getFilamentDiameter().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.filamentDiameter.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                try {
                    double numericValue = Double.parseDouble(s);
                    // Format to 2 decimal places
                    String formattedValue = formatMeasurement(numericValue,2);
                    binding.calcLayout.filamentDiameter.setText(formattedValue);
                } catch (NumberFormatException e) {
                    binding.calcLayout.filamentDiameter.setText(s);
                }
            }
        });
        viewModel.getRemainingFilament().observe(this, s -> {
            String currentDisplayValue = binding.calcLayout.txtRemainingFilament.getText().toString();
            if (!currentDisplayValue.equals(s)) {
                binding.calcLayout.txtRemainingFilament.setText(s);
            }
        });
    }

    private void setupTextWatchers() {
        binding.calcLayout.coreDiameter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { viewModel.updateCoreDiameter(s.toString()); }
        });
        binding.calcLayout.fullRadius.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { viewModel.updateFullRadius(s.toString()); }
        });
        binding.calcLayout.emptyRadius.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { viewModel.updateEmptyRadius(s.toString()); }
        });
        binding.calcLayout.slotWidth.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { viewModel.updateSlotWidth(s.toString()); }
        });
        binding.calcLayout.filamentDiameter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                String value = s.toString();
                viewModel.updateFilamentDiameter(value);
            }
        });
    }

    private void setupFocusListeners() {
        // Set up touch listeners for all EditText fields to track the last touched field
        setupFocusListener(binding.calcLayout.coreDiameter);
        setupFocusListener(binding.calcLayout.fullRadius);
        setupFocusListener(binding.calcLayout.emptyRadius);
        setupFocusListener(binding.calcLayout.slotWidth);
        setupFocusListener(binding.calcLayout.filamentDiameter);
    }
    
    private void setupFocusListener(EditText editText) {
        // Also add focus listener in case focus can still be gained
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // Reset all fields to regular font
                resetFontStyles();
                // Set focused field to bold
                editText.setTypeface(null, android.graphics.Typeface.BOLD);
                currentFocusedEditText = editText;
            }
        });
    }
    
    private void resetFontStyles() {
        binding.calcLayout.coreDiameter.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.calcLayout.fullRadius.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.calcLayout.emptyRadius.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.calcLayout.slotWidth.setTypeface(null, android.graphics.Typeface.NORMAL);
        binding.calcLayout.filamentDiameter.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void setupButtonListeners() {
        // Postpone the button setup until the layout is fully inflated
        binding.getRoot().post(() -> {
            // The button container is added as a child to the GridLayout (binding.main)
            GridLayout gridLayout = binding.main;
            
            // Find the button container which should be the last row/column in the grid
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View child = gridLayout.getChildAt(i);
                
                // Look for the LinearLayout that contains the buttons
                if (child instanceof LinearLayout buttonContainer) {

                    // Look for buttons in this container
                    for (int j = 0; j < buttonContainer.getChildCount(); j++) {
                        View buttonView = buttonContainer.getChildAt(j);
                        
                        if (buttonView instanceof Button button) {

                            // Check the button text to determine which one it is using string resources
                            String buttonText = button.getText().toString();

                            if (getString(R.string.button_minus_ten).equals(buttonText)) {
                                button.setOnClickListener(v -> handleMinusTenButtonClick());
                            } else if (getString(R.string.button_plus_ten).equals(buttonText)) {
                                button.setOnClickListener(v -> handlePlusTenButtonClick());
                            } else if (getString(R.string.button_plus_one).equals(buttonText)) {
                                button.setOnClickListener(v -> handlePlusOneButtonClick());
                            } else if (getString(R.string.button_minus_one).equals(buttonText)) {
                                button.setOnClickListener(v -> handleMinusOneButtonClick());
                            }

                        }
                    }
                }
            }
        });
    }

    private void handleMinusTenButtonClick() {
        if (currentFocusedEditText != null) {
            // Get the current value from the EditText as a string
            String currentValueStr = currentFocusedEditText.getText().toString().trim();
            
            // Parse the string to a numeric value for calculation
            double numericValue = 0.0; // Default value if parsing fails
            if (!currentValueStr.isEmpty()) {
                try {
                    numericValue = Double.parseDouble(currentValueStr);
                } catch (NumberFormatException e) {
                    // If parsing fails, use default value of 0.0
                    numericValue = 0.0;
                }
            }
            
            // Perform the arithmetic operation: subtract 10 for other fields, subtract 0.1 for filamentDiameter
            double result;
            if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                result = numericValue - 0.1;
            } else {
                result = numericValue - 10;
            }
            
            // Ensure the result is non-negative
            if (result < 0) {
                result = 0.0;
            }

            // Convert the result back to string for display
            String resultStr = String.valueOf(result);

            // Update the appropriate ViewModel property based on which EditText was focused
            if (currentFocusedEditText == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handlePlusTenButtonClick() {
        if (currentFocusedEditText != null) {
            // Get the current value from the EditText as a string
            String currentValueStr = currentFocusedEditText.getText().toString().trim();
            
            // Parse the string to a numeric value for calculation
            double numericValue = 0.0; // Default value if parsing fails
            if (!currentValueStr.isEmpty()) {
                try {
                    numericValue = Double.parseDouble(currentValueStr);
                } catch (NumberFormatException e) {
                    // If parsing fails, use default value of 0.0
                    numericValue = 0.0;
                }
            }
            
            // Perform the arithmetic operation: add 10 for other fields, add 0.1 for filamentDiameter
            double result;
            if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                result = numericValue + 0.1;
            } else {
                result = numericValue + 10;
            }
            
            // Ensure the result is non-negative
            if (result < 0) {
                result = 0.0;
            }

            // Convert the result back to string for display
            String resultStr = String.valueOf(result);

            // Update the appropriate ViewModel property based on which EditText was focused
            if (currentFocusedEditText == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handlePlusOneButtonClick() {
        if (currentFocusedEditText != null) {
            // Get the current value from the EditText as a string
            String currentValueStr = currentFocusedEditText.getText().toString().trim();
            
            // Parse the string to a numeric value for calculation
            double numericValue = 0.0; // Default value if parsing fails
            if (!currentValueStr.isEmpty()) {
                try {
                    numericValue = Double.parseDouble(currentValueStr);
                } catch (NumberFormatException e) {
                    // If parsing fails, use default value of 0.0
                    numericValue = 0.0;
                }
            }
            
            // Perform the arithmetic operation: add 1 for other fields, add 0.01 for filamentDiameter
            double result;
            if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                result = numericValue + 0.01;
            } else {
                result = numericValue + 1;
            }
            
            // Ensure the result is non-negative
            if (result < 0) {
                result = 0.0;
            }

            // Convert the result back to string for display
            String resultStr = String.valueOf(result);

            // Update the appropriate ViewModel property based on which EditText was focused
            if (currentFocusedEditText == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handleMinusOneButtonClick() {
        if (currentFocusedEditText != null) {
            // Get the current value from the EditText as a string
            String currentValueStr = currentFocusedEditText.getText().toString().trim();
            
            // Parse the string to a numeric value for calculation
            double numericValue = 0.0; // Default value if parsing fails
            if (!currentValueStr.isEmpty()) {
                try {
                    numericValue = Double.parseDouble(currentValueStr);
                } catch (NumberFormatException e) {
                    // If parsing fails, use default value of 0.0
                    numericValue = 0.0;
                }
            }
            
            // Perform the arithmetic operation: subtract 1 for other fields, subtract 0.01 for filamentDiameter
            double result;
            if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                result = numericValue - 0.01;
            } else {
                result = numericValue - 1;
            }
            
            // Ensure the result is non-negative
            if (result < 0) {
                result = 0.0;
            }

            // Convert the result back to string for display
            String resultStr = String.valueOf(result);

            // Update the appropriate ViewModel property based on which EditText was focused
            if (currentFocusedEditText == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedEditText == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
}
