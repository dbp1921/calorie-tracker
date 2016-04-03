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
    }

    public void saveSettings(View v) {
        EditText setting = (EditText) findViewById(R.id.calorie_limit);
        if (setting.length() != 0) caloricLimit = Integer.parseInt(setting.getText().toString());
        setting.setText(Integer.toString(caloricLimit));
        db.updateSettings(calorieKey, caloricLimit);

        finish();

    }

    public void showProgress(View v){
        Intent i = new Intent(SettingsActivity.this, ProgressActivity.class);
        startActivity(i);

    }

}
