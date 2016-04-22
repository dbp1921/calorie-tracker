package edu.upenn.cis350.group1.calorietracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.Date;

public class InputActivity extends CalorieTrackerActivity {
    private boolean existing; // whether editing existing meal or adding a new meal
    private int mealID; // meal id of this meal
    private Meal m; // Meal object being edited
    private DatabaseHandler db; // database handler
    private Date date; // date meal was eaten
    private static final int RESULT_OK = 400; // result ok code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // set up database handler and get intent extras
        db = new DatabaseHandler(getApplicationContext());
        existing = getIntent().getBooleanExtra("EXISTING", false);
        mealID = getIntent().getIntExtra("MEAL_ID", -1);
        long intentDate = getIntent().getLongExtra("date", System.currentTimeMillis());
        date = new Date(intentDate);


        if (existing) {
            // fetch meal from db
            m = db.getMeal(mealID);
            int type = m.getTypeCode();

            // set spinner value to the correct one
            Spinner mealTypeSpinner = (Spinner) findViewById(R.id.mealtype_spinner);
            mealTypeSpinner.setSelection(type);

            // EditText fields
            EditText calories = (EditText) findViewById(R.id.calories);
            EditText meal = (EditText) findViewById(R.id.meal);
            EditText sodium = (EditText) findViewById(R.id.sodium);
            EditText carbs = (EditText) findViewById(R.id.carbs);
            EditText protein = (EditText) findViewById(R.id.protein);

            // set text fields to the correct values
            meal.setText(m.getName());
            calories.setText(Integer.toString(m.getCalories()));
            sodium.setText(Double.toString(m.getSodium()));
            carbs.setText(Double.toString(m.getCarbs()));
            protein.setText(Double.toString(m.getProtein()));
        } else {
            // hide delete button if this meal is not new
            Button deleteButton = (Button) findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.INVISIBLE);
        }
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

    // click handler for submit button
    public void onSubmitClick(View v){
        // returning intent
        Intent i = new Intent();

        // EditText fields
        EditText calories = (EditText) findViewById(R.id.calories);
        EditText meal = (EditText) findViewById(R.id.meal);
        EditText sodium = (EditText) findViewById(R.id.sodium);
        EditText carbs = (EditText) findViewById(R.id.carbs);
        EditText protein = (EditText) findViewById(R.id.protein);

        if (!existing) {
            // get spinner value and set meal type correctly
            Spinner mealTypeSpinner = (Spinner) findViewById(R.id.mealtype_spinner);
            int typeCode = mealTypeSpinner.getSelectedItemPosition();

            Meal thisMeal = new Meal(meal.getText().toString(), date, typeCode, 0);

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
        } else {
            // get spinner value and set meal type correctly
            Spinner mealTypeSpinner = (Spinner) findViewById(R.id.mealtype_spinner);
            m.setType(mealTypeSpinner.getSelectedItemPosition());
            if (calories.length() != 0)
                m.setCalories(Integer.parseInt(calories.getText().toString()));
            if (carbs.length() != 0)
                m.setCarbs(Double.parseDouble(carbs.getText().toString()));
            if (protein.length() != 0)
                m.setProtein(Double.parseDouble(protein.getText().toString()));
            if (sodium.length() != 0)
                m.setSodium(Double.parseDouble(sodium.getText().toString()));

            db.updateMeal(m);

            setResult(RESULT_OK, i);
            finish();
        }

    }

    // click handler for click of delete button
    public void onDeleteClick(View v){
        // returning intent
        Intent i = new Intent();

        if (existing) {
            db.deleteMeal(mealID);
            setResult(RESULT_OK, i);
            finish();
        } else {
            finish();
        }
    }
}
