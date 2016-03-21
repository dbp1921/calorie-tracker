package edu.upenn.cis350.group1.calorietracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.sql.Date;

public class InputActivity extends CalorieTrackerActivity {
    private String button;
    private static final int RESULT_OK = 400;
    private static final int ACTIVITY_DAILY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        button = getIntent().getStringExtra("BUTTON");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.in_screen_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.to_main) {
//            toMainMenu();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // Helper function to take user to main menu
    private void toMainMenu() {
        Intent menu = new Intent(this, MainActivity.class);
        startActivity(menu);
    }

    public void onSubmitClick(View v){
        // returning intent
        Intent i = new Intent();

        // EditText fields
        EditText calories = (EditText) findViewById(R.id.calories);
        EditText meal = (EditText) findViewById(R.id.meal);
        EditText sodium = (EditText) findViewById(R.id.sodium);
        EditText carbs = (EditText) findViewById(R.id.carbs);
        EditText protein = (EditText) findViewById(R.id.protein);

        int typeCode = 0;
        switch(button){
            case "button1":
                typeCode = 0;
                break;
            case "button2":
                typeCode = 1;
                break;
            case "button3":
                typeCode = 2;
                break;
            case "button4":
                typeCode = 3;
                break;


        }

        DatabaseHandler db = new DatabaseHandler(v.getContext());

        Meal thisMeal = new Meal(meal.getText().toString(), new Date(System.currentTimeMillis()),
                typeCode, 0);

        if (calories.length() != 0)
            thisMeal.setCalories(Integer.parseInt(calories.getText().toString()));
        if (carbs.length() != 0)
            thisMeal.setCarbs(Double.parseDouble(carbs.getText().toString()));
        if (protein.length() != 0)
            thisMeal.setProtein(Double.parseDouble(protein.getText().toString()));
        if (sodium.length() != 0)
            thisMeal.setSodium(Double.parseDouble(sodium.getText().toString()));

        db.addMeal(thisMeal);

        setResult(RESULT_OK, i);
        finish();

    }




}
