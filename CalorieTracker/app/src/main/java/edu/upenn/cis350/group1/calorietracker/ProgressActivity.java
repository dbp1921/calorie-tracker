package edu.upenn.cis350.group1.calorietracker;

import android.os.Bundle;

import android.widget.ProgressBar;
import android.widget.TextView;
import java.sql.Date;
import java.util.List;

/**
 * Created by joseovalle on 4/2/16.
 */
public class ProgressActivity extends CalorieTrackerActivity {

    private ProgressBar calories;
    private ProgressBar protein;
    private ProgressBar sodium;
    private ProgressBar carbs;

    private int progressStatus = 0;
    private TextView calorieText;
    private TextView proteinText;
    private TextView sodiumText;
    private TextView carbsText;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);


        int calorieStatus = 0;
        int proteinStatus = 0;
        int sodiumStatus = 0;
        int carbsStatus = 0;

        db = new DatabaseHandler(getApplicationContext());
        List<Meal> meals = db.getAllMealsList(new Date(System.currentTimeMillis()));
        for(Meal meal: meals){
            calorieStatus += meal.getCalories();
            proteinStatus += meal.getProtein();
            sodiumStatus += meal.getSodium();
            carbsStatus += meal.getCarbs();

        }

        calories = (ProgressBar) findViewById(R.id.progressBar1);
        protein = (ProgressBar) findViewById(R.id.progressBar2);
        sodium = (ProgressBar) findViewById(R.id.progressBar3);
        carbs = (ProgressBar) findViewById(R.id.progressBar4);


        calories.setMax(db.getSetting("calories"));
        protein.setMax(db.getSetting("protein"));
        sodium.setMax(db.getSetting("sodium"));
        carbs.setMax(db.getSetting("carbs"));

//        calories.setMax(2000);
//        protein.setMax(400);
//        sodium.setMax(200);
//        carbs.setMax(400);


        calorieText = (TextView) findViewById(R.id.textView1);
        proteinText = (TextView) findViewById(R.id.textView2);
        sodiumText = (TextView) findViewById(R.id.textView3);
        carbsText = (TextView) findViewById(R.id.textView4);



        calories.setProgress(calorieStatus);
        protein.setProgress(proteinStatus);
        sodium.setProgress(sodiumStatus);
        carbs.setProgress(carbsStatus);

        calorieText.setText("Calories " + calorieStatus + "/" + calories.getMax());
        proteinText.setText("Protein " + proteinStatus+"/"+protein.getMax());
        sodiumText.setText("Sodium " + sodiumStatus+"/"+sodium.getMax());
        carbsText.setText("Carbs " + carbsStatus+"/"+carbs.getMax());

    }



}
