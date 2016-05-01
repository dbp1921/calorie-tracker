package edu.upenn.cis350.group1.calorietracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CalendarActivity extends CalorieTrackerActivity {
    private static DatabaseHandler dbHandler; // database handler for underlying database
    private static final int RESULT_OK = 400;
    private static final int ACTIVITY_CALENDAR = 1;
    private static final String DATE_KEY = "date";
    private Date date; // need this for AlertDialog
    private double weight; // need this for AlertDialog
    private double water; // need this for AlertDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        // create database handler
        dbHandler = new DatabaseHandler(this.getApplicationContext());

        // populate list view for the initial date
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
        calendarView.setDataBaseHandler(dbHandler);
        Date date = new Date(calendarView.getDate());
        populateListView(date);
        populateIntakeSummary(date);

        // set listview click listener to enable editing meals from calendar
        ListView list = (ListView) findViewById(R.id.daily_summary);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ListAdapter adapter = (ListAdapter) parent.getAdapter();

                Cursor c = (Cursor) adapter.getItem(position);

                int mealID = c.getInt(c.getColumnIndex("_id"));

                Intent mealEditingScreen = new Intent(CalendarActivity.this, InputActivity.class);
                mealEditingScreen.putExtra("EXISTING", true);
                mealEditingScreen.putExtra("MEAL_ID", mealID);
                startActivityForResult(mealEditingScreen, ACTIVITY_CALENDAR);
            }
        });

        // create change listener for calendar so that list view is populated with day's meals
        calendarView.setOnDateChangeListener(new CustomCalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CustomCalendarView view, int year, int month, int dayOfMonth) {
                Date d = new Date(year - 1900, month, dayOfMonth);
                populateListView(d);
                populateIntakeSummary(d);
            }
        });
    }

    // populate list view
    public void populateListView(Date date) {
        // get cursor and list view objects
        Cursor c = dbHandler.getAllMealsCursor(date);
        ListView list = (ListView) findViewById(R.id.daily_summary);

        // if query was empty nothing found and empty the listview
        if (c == null || c.getCount() <= 0) {
            list.setAdapter(null);
            return;
        }

        // column names and view ids to update
        String[] arrayColumns = new String[] {"name", "calories", "_id"};
        int[] viewIDs = {R.id.textview_meal_title, R.id.textview_meal_calories};

        // update the adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.meal_item, c,
                arrayColumns, viewIDs);

        // display it in the listview
        list.setAdapter(adapter);

    }

    // click handler for meal button
    public void onMealButtonClick(View v) {
        // get date currently in calendar view
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
        date = new Date(calendarView.getDate());

        Intent inputActivity = new Intent(CalendarActivity.this, InputActivity.class);
        inputActivity.putExtra(DATE_KEY, calendarView.getDate());

        startActivityForResult(inputActivity, ACTIVITY_CALENDAR);
    }

    // click handler for weight button
    public void onWeightButtonClick(View v) {
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
        date = new Date(calendarView.getDate());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Weight for " + date.toString());

        // Set up the input
        final EditText input = new EditText(this);

        // set input type and hint
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (dbHandler.getWeight(date) == -1) {
            input.setHint(Double.toString(0.0));
        } else {
            input.setText(Double.toString(dbHandler.getWeight(date)));
        }

        dialog.setView(input);

        // Set up the buttons
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weight = Double.parseDouble(input.getText().toString());
                if (weight > 0.0) {
                    dbHandler.setWeightForDate(date, weight);
                } else {
                    dialog.cancel();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    // click handler for water button
    public void onWaterButtonClick(View v) {
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
        date = new Date(calendarView.getDate());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Water intake on " + date.toString());

        // Set up the input
        final EditText input = new EditText(this);

        // set input type and hint
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        double waterIntake = dbHandler.getWater(date);
        if (waterIntake == -1) {
            input.setHint(Double.toString(0.0));
        } else {
            input.setText(Double.toString(waterIntake));
        }

        dialog.setView(input);

        // Set up the buttons
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                water = Double.parseDouble(input.getText().toString());
                if (water > 0.0) {
                    dbHandler.setWaterForDate(date, water);
                    populateIntakeSummary(date);

                } else {
                    dialog.cancel();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void populateIntakeSummary(Date date) {
        //get all meals for the given date
        List<Meal> meals = dbHandler.getAllMealsList(date);

        //create doubles to hold the nutrition information for all
        double cals = 0;
        double prot = 0;
        double sod = 0;
        double carbs = 0;
        double water;

        //Add nutrition information from each of the day's meals
        for (Meal m : meals) {
            cals += m.getCalories();
            prot += m.getProtein();
            sod += m.getSodium();
            carbs += m.getCarbs();
        }

        water = dbHandler.getWater(date);

        //find the TextView for each of the nutrition items
        TextView calsVal = (TextView) findViewById(R.id.cals_val);
        TextView protVal = (TextView) findViewById(R.id.prot_val);
        TextView sodVal = (TextView) findViewById(R.id.sod_val);
        TextView carbsVal = (TextView) findViewById(R.id.carb_val);
        TextView waterVal = (TextView) findViewById(R.id.water_val);

        //create strings to show that there is either no information, or to display the amount of
        // calories eaten
        String numCals = (cals == 0) ? "--" : "" + cals;
        String numProt = (prot == 0) ? "--" : "" + prot;
        String numSod = (sod == 0) ? "--" : "" + sod;
        String numCarbs = (carbs == 0) ? "--" : "" + carbs;
        String numWater = (water == -1) ? "--" : "" + water;

        //Set the textViews with the strings created above
        calsVal.setText(numCals);
        protVal.setText(numProt);
        sodVal.setText(numSod);
        carbsVal.setText(numCarbs);
        waterVal.setText(numWater);

        //Find the user specified max values for each nutrition item
        int calMax = dbHandler.getSetting("calories");
        int protMax = dbHandler.getSetting("protein");
        int sodMax = dbHandler.getSetting("sodium");
        int carbMax = dbHandler.getSetting("carbs");

        //If the user has not specified max values, default to presets
        calMax = (calMax > 0) ? calMax : SettingsActivity.caloricDefault;
        protMax = (protMax > 0) ? calMax : SettingsActivity.proteinDefault;
        sodMax = (sodMax > 0) ? sodMax : SettingsActivity.sodiumDefault;
        carbMax = (carbMax > 0) ? carbMax : SettingsActivity.carbDefault;

        Log.v("calMax", calMax + "");
        Log.v("protMax", protMax + "");
        Log.v("sodMax", sodMax + "");
        Log.v("carbMax", carbMax + "");

        //change text color for each of the nutrition items based on its value
        if (cals > 0 && cals <= calMax) {
            calsVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (cals > calMax) {
            calsVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            calsVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (prot > 0 && prot <= protMax) {
            protVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (prot > protMax) {
            protVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            protVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (sod > 0 && sod <= sodMax) {
            sodVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (sod > sodMax) {
            sodVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            protVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (carbs > 0 && carbs <= carbMax) {
            carbsVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (carbs > carbMax) {
            carbsVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            carbsVal.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

    // called when a new meal is input using InputActivity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        // if launched InputActivity returned properly then update list
        if(requestCode == ACTIVITY_CALENDAR && resultCode == RESULT_OK){
            // populate list view for the initial date
            CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
            Date date = new Date(calendarView.getDate());
            populateListView(date);
            populateIntakeSummary(date);
        }
    }
}
