package edu.upenn.cis350.group1.calorietracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jibreel on 2/21/16.
 */
public class CustomCalendarView extends LinearLayout {




    // row of day labels
    private LinearLayout weekDays;

    // back button (last month)
    private ImageView prevButton;

    // forward button (next month)
    private ImageView nextButton;

    // Month and Year display
    private TextView calendarMonth;

    // Grid display of dates in selected month
    private GridView calendarGrid;

    // Calendar object to keep track of currently displayed month
    private Calendar dateHolder;

    // Number of days to include in grid
    static final int NUM_DAYS =  42;

    // Format for dates
    static final String DATE_FORMAT = "MMMM yyyy";


    /**
     * Single argument contructor takes in only Context
     * @param context
     */
    public CustomCalendarView(Context context) {
        this(context, null);
    }

    /**
     * Two argument constructor takes in Context and AttributeSet
     * @param context
     * @param attrs
     */
    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dateHolder = Calendar.getInstance();
        inflateCalendar(context);
        updateCalendar();
    }


    /**
     * Private method to inflate calendar components and initialize instance variables
     * containing layout components.
     * @param context
     */
    private void inflateCalendar(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.calendar_layout, this);

        calendarMonth = (TextView) findViewById(R.id.curr_month);
        weekDays =(LinearLayout) findViewById(R.id.days_of_the_week);
        prevButton = (ImageView) findViewById(R.id.last_month);
        nextButton = (ImageView) findViewById(R.id.next_month);
        calendarGrid = (GridView) findViewById(R.id.days_grid);

    }

    /**
     * helper method of updateCalendar() to add the calculated dates to the
     * @param dates
     */
    private void displayDates(List<Date> dates) {

        
    }


    /**
     * Called when calendar is first displayed or month is changed, calculates
     * which days to display for current month
     */
    private void updateCalendar() {

        //List for each date in the view
        List<Date> dates = new ArrayList<>();

        Calendar calendar = (Calendar) dateHolder.clone();

        //Find first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        //What day of the week is the first of the month
        int firstVisibleDate = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //Display current month at top of view
        calendarMonth.setText(new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime()));

        //Add the days of the last month that will bring us to a full week
        calendar.add(Calendar.DAY_OF_MONTH, 0 - firstVisibleDate);

        //Add the rest of the days to the list, totalling 6 weeks
        while(dates.size() < NUM_DAYS) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        displayDates(dates);
    }

}
