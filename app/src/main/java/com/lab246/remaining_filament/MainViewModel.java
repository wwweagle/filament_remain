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
        coreDiameter.setValue(prefs.getString("core_diameter", "76"));
        fullRadius.setValue(prefs.getString("full_radius", "62"));
        emptyRadius.setValue(prefs.getString("empty_radius", "20"));
        slotWidth.setValue(prefs.getString("slot_width", "64"));
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

    private double calc(double core_diameter, double full_radius, double empty_radius, double slot_width, double filament_diameter) {
        double avg_cyc_len = ((core_diameter + full_radius - empty_radius) * Math.PI);
        double layers = Math.floor((full_radius - empty_radius) / filament_diameter);
        double cyc_per_layer = Math.floor(slot_width / filament_diameter);
        double result = avg_cyc_len * layers * cyc_per_layer / 1000;
        return Math.max(result, 0);
    }
}
