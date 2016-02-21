package edu.upenn.cis350.group1.calorietracker;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.Date;

public class CalendarActivity extends AppCompatActivity {
    private static DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        dbHandler = new DatabaseHandler(this.getApplicationContext());

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Date d = new Date(year, month, dayOfMonth);
                populateListView(d);
            }
        });
    }

    // show options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_menu, menu);
        return true;
    }

    public void populateListView(Date date) {
        Cursor c = dbHandler.getAllMealsCursor(date);

        ListView list = (ListView) findViewById(R.id.daily_summary);

        String[] arrayColumns = new String[] {"name", "calories"};
        int[] viewIDs = {R.id.textview_meal_title, R.id.textview_meal_calories};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.meal_item, c,
                arrayColumns, viewIDs);

        list.setAdapter(adapter);

    }
}
