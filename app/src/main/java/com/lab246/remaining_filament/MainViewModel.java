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

    // LiveData for UI to observe - single active values
    private final MutableLiveData<String> coreDiameter = new MutableLiveData<>();
    private final MutableLiveData<String> fullRadius = new MutableLiveData<>();
    private final MutableLiveData<String> emptyRadius = new MutableLiveData<>();
    private final MutableLiveData<String> slotWidth = new MutableLiveData<>();
    private final MutableLiveData<String> filamentDiameter = new MutableLiveData<>();
    private final MutableLiveData<String> remainingFilament = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedSpool = new MutableLiveData<>();

    // Store all 4 spool data groups
    private final String[][] spoolData = new String[4][5]; // [spool_index][parameter_index]
    private final String[] paramKeys = {"core_diameter", "full_radius", "empty_radius", "slot_width", "filament_diameter"};
    private final String[] defaultValues = {"80", "60", "22", "62", "1.75"}; // core_diameter, full_radius, empty_radius, slot_width, filament_diameter

    public MainViewModel(Application application) {
        super(application);
        prefs = application.getSharedPreferences("MyPrefs", Application.MODE_PRIVATE);
        editor = prefs.edit();

        // --- START OF MIGRATION LOGIC ---
        // Check if we've already run this migration. The default value is 0.
        int migrationVersion = prefs.getInt("migration_version", 0);

        // If migrationVersion is less than 1, it means this is an old user.
        if (migrationVersion < 1) {
            migrateOldData(); // Run our one-time migration function
            // After migrating, update the version so this code never runs again.
            editor.putInt("migration_version", 1).apply();
        }
        // --- END OF MIGRATION LOGIC ---

        // The rest of your constructor remains the same
        loadAllSpoolValues();
        int initialSpool = prefs.getInt("selected_spool", 0);
        selectedSpool.setValue(initialSpool);
        loadActiveSpoolValues(initialSpool);
    }

    // Add this new private method to the MainViewModel class
    private void migrateOldData() {
        // Check if old data actually exists to be migrated. We'll check for an old, unique key.
        if (prefs.contains("core_diameter")) {
            // Old data exists. Let's move it to the "Spool 1" (index 0) slot.
            editor.putString("core_diameter_spool0", prefs.getString("core_diameter", defaultValues[0]));
            editor.putString("full_radius_spool0", prefs.getString("full_radius", defaultValues[1]));
            editor.putString("empty_radius_spool0", prefs.getString("empty_radius", defaultValues[2]));
            editor.putString("slot_width_spool0", prefs.getString("slot_width", defaultValues[3]));
            editor.putString("filament_diameter_spool0", prefs.getString("filament_diameter", defaultValues[4]));

            // Optional: Clean up the old, now-orphaned keys to keep SharedPreferences tidy.
            editor.remove("core_diameter");
            editor.remove("full_radius");
            editor.remove("empty_radius");
            editor.remove("slot_width");
            editor.remove("filament_diameter");

            // We commit the changes immediately and synchronously using commit()
            // because the rest of the app's startup depends on this data being there.
            editor.commit();
        }
    }


    // Getters for the LiveData
    public LiveData<String> getCoreDiameter() { return coreDiameter; }
    public LiveData<String> getFullRadius() { return fullRadius; }
    public LiveData<String> getEmptyRadius() { return emptyRadius; }
    public LiveData<String> getSlotWidth() { return slotWidth; }
    public LiveData<String> getFilamentDiameter() { return filamentDiameter; }
    public LiveData<String> getRemainingFilament() { return remainingFilament; }
    public LiveData<Integer> getSelectedSpool() { return selectedSpool; }


    // Load all spool values from SharedPreferences
    private void loadAllSpoolValues() {
        for (int spool = 0; spool < 4; spool++) {
            for (int param = 0; param < 5; param++) {
                String key = paramKeys[param] + "_spool" + spool;
                spoolData[spool][param] = prefs.getString(key, defaultValues[param]);
            }
        }
    }

    // Load values for the active spool into LiveData
    private void loadActiveSpoolValues(int spoolIndex) {
        if (spoolIndex >= 0 && spoolIndex < 4) {
            coreDiameter.setValue(spoolData[spoolIndex][0]);
            fullRadius.setValue(spoolData[spoolIndex][1]);
            emptyRadius.setValue(spoolData[spoolIndex][2]);
            slotWidth.setValue(spoolData[spoolIndex][3]);
            filamentDiameter.setValue(spoolData[spoolIndex][4]);
            updateRemainingFilament(); // Recalculate based on loaded values
        }
    }

    // Update the currently active spool data and recalculate remaining filament
    public void updateCoreDiameter(String value) {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        spoolData[currentSpool][0] = value != null ? value : defaultValues[0];
        coreDiameter.setValue(value != null ? value : defaultValues[0]);
        updateRemainingFilament();
    }

    public void updateFullRadius(String value) {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        spoolData[currentSpool][1] = value != null ? value : defaultValues[1];
        fullRadius.setValue(value != null ? value : defaultValues[1]);
        updateRemainingFilament();
    }

    public void updateEmptyRadius(String value) {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        spoolData[currentSpool][2] = value != null ? value : defaultValues[2];
        emptyRadius.setValue(value != null ? value : defaultValues[2]);
        updateRemainingFilament();
    }

    public void updateSlotWidth(String value) {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        spoolData[currentSpool][3] = value != null ? value : defaultValues[3];
        slotWidth.setValue(value != null ? value : defaultValues[3]);
        updateRemainingFilament();
    }

    public void updateFilamentDiameter(String value) {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        spoolData[currentSpool][4] = value != null ? value : defaultValues[4];
        filamentDiameter.setValue(value != null ? value : defaultValues[4]);
        updateRemainingFilament();
    }


    public void updateSpoolSelection(int spoolIndex) {
        // Update selected spool
        selectedSpool.setValue(spoolIndex);

        // Load values for the new spool
        loadActiveSpoolValues(spoolIndex);

        // Save the new selected spool preference
        editor.putInt("selected_spool", spoolIndex).apply();
    }

    private void updateRemainingFilament() {
        try {
            double cd = Double.parseDouble(coreDiameter.getValue());
            double fr = Double.parseDouble(fullRadius.getValue());
            double er = Double.parseDouble(emptyRadius.getValue());
            double sw = Double.parseDouble(slotWidth.getValue());
            double fd = Double.parseDouble(filamentDiameter.getValue());

            double result = calc(cd, fr, er, sw, fd);
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");
            remainingFilament.setValue(decimalFormat.format(result));
        } catch (NumberFormatException | NullPointerException e) {
            remainingFilament.setValue("0.00");
        }
        saveValues();
    }

    private void saveValues() {
        int currentSpool = selectedSpool.getValue() != null ? selectedSpool.getValue() : 0;
        if (currentSpool >= 0 && currentSpool < 4) {
            // The spoolData array is already up-to-date thanks to the update...() methods.
            // We just need to persist the changes for the current spool.
            for (int param = 0; param < 5; param++) {
                String key = paramKeys[param] + "_spool" + currentSpool;
                String value = spoolData[currentSpool][param];
                // Ensure we don't save null values
                if (value != null) {
                    editor.putString(key, value);
                } else {
                    editor.putString(key, defaultValues[param]);
                }
            }
            editor.apply();
        }
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
