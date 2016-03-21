package edu.upenn.cis350.group1.calorietracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends CalorieTrackerActivity {

    public static final String calorieKey = "Calorie Limit";
    public static String caloricLimit;
    //test changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState != null) {
            EditText setting = (EditText) findViewById(R.id.calorie_limit);
            caloricLimit = savedInstanceState.getString(calorieKey);
            setting.setText(caloricLimit);
        } else {
            EditText setting = (EditText) findViewById(R.id.calorie_limit);
            caloricLimit = "2000";
            setting.setText(caloricLimit);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(calorieKey, caloricLimit);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void saveSettings(View v) {
        setContentView(R.layout.activity_settings);
        EditText setting = (EditText) findViewById(R.id.calorie_limit);
        caloricLimit = setting.getText().toString();
        setting.setText(caloricLimit);

        finish();

    }

}
