package com.lab246.remaining_filament;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.DecimalFormat;

public class MainViewModel extends AndroidViewModel {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // LiveData for UI to observe
    private final MutableLiveData<String> coreDiameter = new MutableLiveData<>();
    private final MutableLiveData<String> fullRadius = new MutableLiveData<>();
    private final MutableLiveData<String> emptyRadius = new MutableLiveData<>();
    private final MutableLiveData<String> slotWidth = new MutableLiveData<>();
    private final MutableLiveData<String> filamentDiameter = new MutableLiveData<>();
    private final MutableLiveData<String> remainingFilament = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences("MyPrefs", Application.MODE_PRIVATE);
        editor = prefs.edit();
        loadInitialValues();
    }

    // Getters for the LiveData
    public LiveData<String> getCoreDiameter() { return coreDiameter; }
    public LiveData<String> getFullRadius() { return fullRadius; }
    public LiveData<String> getEmptyRadius() { return emptyRadius; }
    public LiveData<String> getSlotWidth() { return slotWidth; }
    public LiveData<String> getFilamentDiameter() { return filamentDiameter; }
    public LiveData<String> getRemainingFilament() { return remainingFilament; }

    private void loadInitialValues() {
        coreDiameter.setValue(prefs.getString("core_diameter", "80"));
        fullRadius.setValue(prefs.getString("full_radius", "60"));
        emptyRadius.setValue(prefs.getString("empty_radius", "22"));
        slotWidth.setValue(prefs.getString("slot_width", "62"));
        filamentDiameter.setValue(prefs.getString("filament_diameter", "1.75"));
        updateRemainingFilament(); // Initial calculation
    }

    public void updateCoreDiameter(String value) {
        coreDiameter.setValue(value);
        updateRemainingFilament();
    }

    public void updateFullRadius(String value) {
        fullRadius.setValue(value);
        updateRemainingFilament();
    }

    public void updateEmptyRadius(String value) {
        emptyRadius.setValue(value);
        updateRemainingFilament();
    }

    public void updateSlotWidth(String value) {
        slotWidth.setValue(value);
        updateRemainingFilament();
    }

    public void updateFilamentDiameter(String value) {
        filamentDiameter.setValue(value);
        updateRemainingFilament();
    }

    private void updateRemainingFilament() {
        try {
            double cd = Double.parseDouble(coreDiameter.getValue());
            double fr = Double.parseDouble(fullRadius.getValue());
            double er = Double.parseDouble(emptyRadius.getValue());
            double sw = Double.parseDouble(slotWidth.getValue());
            double fd = Double.parseDouble(filamentDiameter.getValue());

            double result = calc(cd, fr, er, sw, fd);
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            remainingFilament.setValue(decimalFormat.format(result));
        } catch (NumberFormatException | NullPointerException e) {
            remainingFilament.setValue("0.00");
        }
        saveValues();
    }

    private void saveValues() {
        editor.putString("core_diameter", coreDiameter.getValue())
              .putString("full_radius", fullRadius.getValue())
              .putString("empty_radius", emptyRadius.getValue())
              .putString("slot_width", slotWidth.getValue())
              .putString("filament_diameter", filamentDiameter.getValue())
              .apply();
    }

    /**
     * Public-facing calculation method that uses easy-to-measure dimensions.
     * It derives the current winding thickness and then calls the core calculation logic.
     *
     * @param core_diameter The diameter of the spool's central hub (mm).
     * @param full_winding_thickness The distance from the hub to the outer edge of the spool's flange (mm).
     * @param empty_space_thickness The distance from the top of the filament to the outer edge of the flange (mm).
     * @param slot_width The internal width of the spool where filament sits (mm).
     * @param filament_diameter The diameter of the filament itself (mm).
     * @return The estimated remaining length of the filament in meters.
     */
    private double calc(double core_diameter, double full_winding_thickness, double empty_space_thickness, double slot_width, double filament_diameter) {
        // Calculate the thickness of the filament that is actually on the spool.
        // This is the key step to bridge the measurement model with the physics model.
        double current_winding_thickness = full_winding_thickness - empty_space_thickness;

        // Ensure calculated thickness is not negative before passing to the core calculator.
        if (current_winding_thickness <= 0) {
            return 0;
        }

        // Call the accurate physics model with the derived thickness.
        return calculateLengthFromThickness(core_diameter, current_winding_thickness, slot_width, filament_diameter);
    }

    /**
     * Core logic to calculate filament length based on its physical dimensions on the spool.
     * Uses an iterative layer-by-layer summation, accounting for hexagonal packing.
     * This method is kept private as it requires a derived thickness parameter.
     */
    private double calculateLengthFromThickness(double core_diameter, double current_winding_thickness, double slot_width, double filament_diameter) {
        if (core_diameter <= 0 || current_winding_thickness <= 0 || slot_width <= 0 || filament_diameter <= 0) {
            return 0;
        }

        double total_length_mm = 0;
        double core_radius = core_diameter / 2.0;

        // The vertical distance each new layer adds in a hexagonal packing arrangement.
        double layer_height_increase = filament_diameter * Math.sqrt(3.0) / 2.0; // Approx 0.866 * filament_diameter

        // Calculate how many layers of filament are present.
        int num_layers = (int) Math.ceil(current_winding_thickness / layer_height_increase);

        // Calculate how many times the filament can wind side-by-side in the spool's width.
        double windings_per_layer = slot_width / filament_diameter;

        // Start at the radius of the center of the first layer of filament.
        double current_layer_radius = core_radius + (filament_diameter / 2.0);

        for (int i = 0; i < num_layers; i++) {
            double circumference = 2 * Math.PI * current_layer_radius;
            total_length_mm += circumference * windings_per_layer;

            // Move to the center of the next layer.
            current_layer_radius += layer_height_increase;
        }

        // Convert total length from mm to meters.
        double result_meters = total_length_mm / 1000.0;
        return Math.max(result_meters, 0);
    }

}
