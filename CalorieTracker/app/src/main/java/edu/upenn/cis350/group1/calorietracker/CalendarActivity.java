package edu.upenn.cis350.group1.calorietracker;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.Date;

public class CalendarActivity extends CalorieTrackerActivity {
    private static DatabaseHandler dbHandler; // database handler for underlying database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        // create database handler
        dbHandler = new DatabaseHandler(this.getApplicationContext());

        // populate list view for the initial date
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendar);
        calendarView.setDataBaseHandler(dbHandler);
        populateListView(new Date(calendarView.getDate()));

        // create change listener for calendar so that list view is populated with day's meals
        calendarView.setOnDateChangeListener(new CustomCalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CustomCalendarView view, int year, int month, int dayOfMonth) {
                Date d = new Date(year - 1900, month, dayOfMonth);
                Log.v("CalendarActivity", "Date set to " + d.toString());
                populateListView(d);
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
        String[] arrayColumns = new String[] {"name", "calories"};
        int[] viewIDs = {R.id.textview_meal_title, R.id.textview_meal_calories};

        // update the adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.meal_item, c,
                arrayColumns, viewIDs);

        // display it in the listview
        list.setAdapter(adapter);

    }
}
