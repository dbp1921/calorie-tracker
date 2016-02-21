package edu.upenn.cis350.group1.calorietracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

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
    Calendar calendar = Calendar.getInstance();


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
        inflateCalendar(context);
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
     *
     */
    private void updateCalendar() {
        
    }
}
