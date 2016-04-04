package edu.upenn.cis350.group1.calorietracker;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends CalorieTrackerActivity {

    public static final String calorieKey = "calories";
    public static int caloricLimit;
    public static final String proteinKey = "protein";
    public static int proteinLimit;
    public static final String carbKey = "carbs";
    public static int carbLimit;
    public static final String fatKey = "fat";
    public static int fatLimit;
    public static final String sodiumKey = "sodium";
    public static int sodiumLimit;
    private DatabaseHandler db;
    //test changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHandler(getApplicationContext());

        EditText setting = (EditText) findViewById(R.id.calorie_limit);
        caloricLimit = db.getSetting(calorieKey);
        if (caloricLimit == -1) {
            caloricLimit = 2000;
            db.addSetting(calorieKey, caloricLimit);
        }
        setting.setText(Integer.toString(caloricLimit));

        EditText settingProt = (EditText) findViewById(R.id.protein_limit);
        proteinLimit = db.getSetting(proteinKey);
        if (proteinLimit == -1) {
            proteinLimit = 50;
            db.addSetting(proteinKey, proteinLimit);
        }
        settingProt.setText(Integer.toString(proteinLimit));

        EditText settingCarb = (EditText) findViewById(R.id.carb_limit);
        carbLimit = db.getSetting(carbKey);
        if (carbLimit == -1) {
            carbLimit = 50;
            db.addSetting(carbKey, carbLimit);
        }
        settingCarb.setText(Integer.toString(carbLimit));

        EditText settingFat = (EditText) findViewById(R.id.fat_limit);
        fatLimit = db.getSetting(fatKey);
        if (fatLimit == -1) {
            fatLimit = 50;
            db.addSetting(fatKey, fatLimit);
        }
        settingFat.setText(Integer.toString(fatLimit));

        EditText settingSodium = (EditText) findViewById(R.id.sodium_limit);
        sodiumLimit = db.getSetting(sodiumKey);
        if (sodiumLimit == -1) {
            sodiumLimit = 50;
            db.addSetting(sodiumKey, sodiumLimit);
        }
        settingSodium.setText(Integer.toString(sodiumLimit));
    }

    public void saveSettings(View v) {
        EditText setting = (EditText) findViewById(R.id.calorie_limit);
        if (setting.length() != 0) caloricLimit = Integer.parseInt(setting.getText().toString());
        setting.setText(Integer.toString(caloricLimit));
        db.updateSettings(calorieKey, caloricLimit);

        EditText settingProt = (EditText) findViewById(R.id.protein_limit);
        if (settingProt.length() != 0) proteinLimit = Integer.parseInt(settingProt.getText().toString());
        settingProt.setText(Integer.toString(proteinLimit));
        db.updateSettings(proteinKey, proteinLimit);

        EditText settingFat = (EditText) findViewById(R.id.fat_limit);
        if (settingFat.length() != 0) fatLimit = Integer.parseInt(settingFat.getText().toString());
        settingFat.setText(Integer.toString(fatLimit));
        db.updateSettings(fatKey, fatLimit);

        EditText settingSodium = (EditText) findViewById(R.id.sodium_limit);
        if (settingSodium.length() != 0) sodiumLimit = Integer.parseInt(settingSodium.getText().toString());
        settingSodium.setText(Integer.toString(sodiumLimit));
        db.updateSettings(sodiumKey, sodiumLimit);

        EditText settingCarb = (EditText) findViewById(R.id.carb_limit);
        if (settingCarb.length() != 0) carbLimit = Integer.parseInt(settingCarb.getText().toString());
        settingCarb.setText(Integer.toString(carbLimit));
        db.updateSettings(carbKey, carbLimit);
        
        finish();

    }

    public void showProgress(View v){
        Intent i = new Intent(SettingsActivity.this, ProgressActivity.class);
        startActivity(i);

    }

}
