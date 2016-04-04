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

public class CalendarActivity extends CalorieTrackerActivity {
    private static DatabaseHandler dbHandler; // database handler for underlying database
    private static final int RESULT_OK = 400;
    private static final int ACTIVITY_CALENDAR = 1;
    private Date date; // need this for AlertDialog
    private double weight; // need this for AlertDialog

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

        // for now hide meal button
        Button mealButton = (Button) findViewById(R.id.calendar_meal_button);
        mealButton.setVisibility(View.INVISIBLE);

        ListView list = (ListView) findViewById(R.id.daily_summary);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ListAdapter adapter = (ListAdapter) parent.getAdapter();

                Cursor c = (Cursor) adapter.getItem(position);

                c.moveToFirst();

                int mealID = c.getInt(c.getColumnIndex("_id"));

                Log.v("ListView", "MealID retrieved: " + mealID);

                Intent i = new Intent(CalendarActivity.this, InputActivity.class);
                i.putExtra("EXISTING", true);
                i.putExtra("MEAL_ID", mealID);
                startActivityForResult(i, ACTIVITY_CALENDAR);
            }
        });

        // create change listener for calendar so that list view is populated with day's meals
        calendarView.setOnDateChangeListener(new CustomCalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CustomCalendarView view, int year, int month, int dayOfMonth) {
                Date d = new Date(year - 1900, month, dayOfMonth);
                Log.v("CalendarActivity", "Date set to " + d.toString());
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

    public void onMealButtonClick(View v) {

    }

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
                Log.v("Saved date", date.toString());
                Log.v("Saved weight", Double.toString(weight));
                dbHandler.setWeightForDate(date, weight);
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
        List<Meal> meals = dbHandler.getAllMealsList(date);
        double cals = 0;
        double prot = 0;
        double sod = 0;
        double carbs = 0;

        for (Meal m : meals) {
            cals += m.getCalories();
            prot += m.getProtein();
            sod += m.getSodium();
            Log.v("Sodium Value", sod + "");
            carbs += m.getCarbs();
        }

        TextView calsVal = (TextView) findViewById(R.id.cals_val);
        TextView protVal = (TextView) findViewById(R.id.prot_val);
        TextView sodVal = (TextView) findViewById(R.id.sod_val);
        TextView carbsVal = (TextView) findViewById(R.id.carb_val);

        String numCals = (cals == 0) ? "--" : "" + cals;
        String numProt = (prot == 0) ? "--" : "" + prot;
        String numSod = (sod == 0) ? "--" : "" + sod;
        String numCarbs = (carbs == 0) ? "--" : "" + carbs;

        calsVal.setText(numCals);
        protVal.setText(numProt);
        sodVal.setText(numSod);
        carbsVal.setText(numCarbs);

        if (cals > 0 && cals <= 2000) {
            calsVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (cals > 2000) {
            calsVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            calsVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (prot > 0 && prot <= 400) {
            protVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (prot > 400) {
            protVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            protVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (sod > 0 && sod <= 200) {
            sodVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (sod > 200) {
            sodVal.setTextColor(getResources().getColor(R.color.aboveLimit));
        } else {
            protVal.setTextColor(getResources().getColor(R.color.colorText));
        }

        if (carbs > 0 && carbs <= 400) {
            carbsVal.setTextColor(getResources().getColor(R.color.belowLimit));
        } else if (carbs > 400) {
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
