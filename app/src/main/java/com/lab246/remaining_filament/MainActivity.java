package com.lab246.remaining_filament;

import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import com.lab246.remaining_filament.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TextView currentFocusedTextView;

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

        // Observe LiveData from ViewModel
        observeViewModel();
        // Set up focus listeners to track the currently focused EditText
        setupFocusListeners();
        // Set up button click listeners
        setupButtonListeners();

        // Initialize ViewPager2 with adapter
        setupViewPager2();
    }

    private void setupViewPager2() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.dotsIndicator.attachTo(binding.viewPager);

    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return switch (position) {
                case 1 -> SpoolSelectorFragment.newInstance();
                default -> ImageViewFragment.newInstance();
            };
        }

        @Override
        public int getItemCount() {
            return 2; // Two panels: imageView and spool_selector
        }
    }

    public static class ImageViewFragment extends Fragment {
        public ImageViewFragment() {
            // Required empty public constructor
        }

        public static ImageViewFragment newInstance() {
            return new ImageViewFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.panel_image_view, container, false);
        }
    }

    public static class SpoolSelectorFragment extends Fragment {
        private MainViewModel viewModel;

        private RadioGroup radioGroup;

        public SpoolSelectorFragment() {
            // Required empty public constructor
        }

        public static SpoolSelectorFragment newInstance() {
            return new SpoolSelectorFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Get the shared ViewModel
            viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.panel_spool_selector, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Set up the spool selector radio buttons to work with the ViewModel
            radioGroup = view.findViewById(R.id.spool_selector);
            setupSpoolSelector();
            observeViewModel();
        }

        private void setupSpoolSelector() {
            // Set up a listener to notify the ViewModel when the user clicks a button
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int spoolIndex = 0;
                if (checkedId == R.id.spool1_radio) {

                    spoolIndex = 0;
                } else if (checkedId == R.id.spool2_radio) {
                    spoolIndex = 1;
                } else if (checkedId == R.id.spool3_radio) {
                    spoolIndex = 2;
                } else if (checkedId == R.id.spool4_radio) {
                    spoolIndex = 3;
                }
                // Tell the ViewModel about the user's selection
                viewModel.updateSpoolSelection(spoolIndex);
            });
        }

        private void observeViewModel() {
            // Observe the selectedSpool LiveData. This will fire on startup and on any change.
            viewModel.getSelectedSpool().observe(getViewLifecycleOwner(), selectedSpoolIndex -> {
                if (selectedSpoolIndex != null) {
                    int buttonIdToCheck = -1;
                    switch (selectedSpoolIndex) {
                        case 0: buttonIdToCheck = R.id.spool1_radio; break;
                        case 1: buttonIdToCheck = R.id.spool2_radio; break;
                        case 2: buttonIdToCheck = R.id.spool3_radio; break;
                        case 3: buttonIdToCheck = R.id.spool4_radio; break;
                    }
                    // Only update the UI if it's not already on the correct selection.
                    // This prevents an infinite loop with the OnCheckedChangeListener.
                    if (buttonIdToCheck != -1 && radioGroup.getCheckedRadioButtonId() != buttonIdToCheck) {
                        radioGroup.check(buttonIdToCheck);
                    }
                }
            });
        }
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

    // TextViews don't have text change listeners like EditText,
    // so we'll rely on click/touch handlers to set focus and button-based updates
    private void setupTextWatchers() {
        // TextViews don't support text change listeners, so this method is no longer needed
        // Input will be handled through the button system and focus tracking
    }

    private void setupFocusListeners() {
        // Set up touch listeners for all TextView fields to track the last touched field
        setupFocusListener(binding.calcLayout.coreDiameter);
        setupFocusListener(binding.calcLayout.fullRadius);
        setupFocusListener(binding.calcLayout.emptyRadius);
        setupFocusListener(binding.calcLayout.slotWidth);
        setupFocusListener(binding.calcLayout.filamentDiameter);
    }
    
    private void setupFocusListener(TextView textView) {
        // Add click listener to set focus
        textView.setOnClickListener(v -> {
            // Reset all fields to regular font
            resetFontStyles();
            // Set clicked field to bold
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            currentFocusedTextView = textView;
        });

        // Also add focus listener in case focus can still be gained
        textView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // Reset all fields to regular font
                resetFontStyles();
                // Set focused field to bold
                textView.setTypeface(null, android.graphics.Typeface.BOLD);
                currentFocusedTextView = textView;
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
        if (currentFocusedTextView != null) {
            // Get the current value from the TextView as a string
            String currentValueStr = currentFocusedTextView.getText().toString().trim();

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
            if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
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

            // Update the appropriate ViewModel property based on which TextView was focused
            if (currentFocusedTextView == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handlePlusTenButtonClick() {
        if (currentFocusedTextView != null) {
            // Get the current value from the TextView as a string
            String currentValueStr = currentFocusedTextView.getText().toString().trim();

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
            if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
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

            // Update the appropriate ViewModel property based on which TextView was focused
            if (currentFocusedTextView == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handlePlusOneButtonClick() {
        if (currentFocusedTextView != null) {
            // Get the current value from the TextView as a string
            String currentValueStr = currentFocusedTextView.getText().toString().trim();

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
            if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
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

            // Update the appropriate ViewModel property based on which TextView was focused
            if (currentFocusedTextView == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
    
    private void handleMinusOneButtonClick() {
        if (currentFocusedTextView != null) {
            // Get the current value from the TextView as a string
            String currentValueStr = currentFocusedTextView.getText().toString().trim();

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
            if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
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

            // Update the appropriate ViewModel property based on which TextView was focused
            if (currentFocusedTextView == binding.calcLayout.coreDiameter) {
                viewModel.updateCoreDiameter(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.fullRadius) {
                viewModel.updateFullRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.emptyRadius) {
                viewModel.updateEmptyRadius(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.slotWidth) {
                viewModel.updateSlotWidth(resultStr);
            } else if (currentFocusedTextView == binding.calcLayout.filamentDiameter) {
                viewModel.updateFilamentDiameter(resultStr);
            }
        }
    }
}
