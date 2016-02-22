package edu.upenn.cis350.group1.calorietracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jibreel on 2/21/16.
 *
 * Custom calendar view to support showing if there's data for a particular date
 */
public class CustomCalendarView extends LinearLayout {


    //Context
    private Context context;

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

    //Date Change Listener
    private OnDateChangeListener listener;


    interface OnDateChangeListener {

        void onSelectedDayChange(CustomCalendarView view, int year, int month,
                                                  int dayOfMonth);
    }

    private class GridAdapter extends BaseAdapter {

        ArrayList<Calendar> dates;
        Context context;
        DatabaseHandler dbHandler;
        LayoutInflater inflater;

        //Constructor for gridAdapter
        public GridAdapter(Context context, ArrayList<Calendar> dates, DatabaseHandler dbHandler){
            this.dates = dates;
            this.context = context;
            this.dbHandler = dbHandler;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dates.size();
        }

        @Override
        public Object getItem(int position) {
            return dates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Calendar date = dates.get(position);
            TextView view = (convertView != null) ? (TextView) convertView :
                    (TextView) inflater.inflate(R.layout.date_view, parent, false);
            view.setText(String.valueOf(date.getTime().getDate()));

            view.setTag(R.id.date_key, date.clone());
            Log.v("tag set as ", date.getTime().toString());

            //TODO change date color based on database information
//            if (dbHandler.getDateID(new java.sql.Date(date.getTimeInMillis())) != -1) {
//                view.setTextColor(Color.CYAN);
//            }


            if(date.getTime().getYear() == Calendar.getInstance().getTime().getYear()) {
                if (date.getTime().getMonth() == Calendar.getInstance().getTime().getMonth()) {
                    view.setTextColor(Color.BLACK);

                    if (date.getTime().getDate() == Calendar.getInstance().getTime().getDate()) {
                        view.setTextColor(Color.MAGENTA);
                    }
                }
            }

            if(date.getTime().getYear() == dateHolder.getTime().getYear()) {
                if (date.getTime().getMonth() == dateHolder.getTime().getMonth()) {
                    if (date.getTime().getDate() == dateHolder.getTime().getDate()) {
                        view.setTypeface(null, Typeface.BOLD);
                    }
                }
            }

            return view;
        }
    }

    /**
     * Single argument constructor takes in only Context
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
        this.context = context;
        dateHolder = Calendar.getInstance();
        inflateCalendar(context);
        setListeners();
        updateCalendar();
    }


    /**
     * Private method to inflate calendar components and initialize instance variables
     * containing layout components.
     * @param context
     */
    private void inflateCalendar(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.custom_calendar_view, this);

        calendarMonth = (TextView) findViewById(R.id.curr_month);
        weekDays =(LinearLayout) findViewById(R.id.days_of_the_week);
        prevButton = (ImageView) findViewById(R.id.last_month);
        nextButton = (ImageView) findViewById(R.id.next_month);
        calendarGrid = (GridView) findViewById(R.id.days_grid);

    }

    private void setListeners() {
        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateHolder.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateHolder.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });
    }

    /**
     * Called when calendar is first displayed or month is changed, calculates
     * which days to display for current month
     */
    private void updateCalendar() {

        //List for each date in the view
        ArrayList<Calendar> dates = new ArrayList<>();

        Calendar calendar = (Calendar) dateHolder.clone();

        if (listener != null) {
            listener.onSelectedDayChange(this, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
                    calendar.get(calendar.DAY_OF_MONTH));
        }

        //Find first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);


        //What day of the week is the first of the month
        int firstVisibleDate = calendar.get(Calendar.DAY_OF_WEEK) - 1;


        //Display current month at top of view
        calendarMonth.setText(new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime()));

        //Add the days of the last month that will bring us to a full week
        calendar.add(Calendar.DAY_OF_YEAR, -firstVisibleDate);


        //Add the rest of the days to the list, totalling 6 weeks
        while(dates.size() < NUM_DAYS) {
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        calendarGrid.setAdapter(new GridAdapter(getContext(), dates,
                new DatabaseHandler(context.getApplicationContext())));


        calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                //textView.setTypeface(null, Typeface.BOLD);
                Calendar selectedDate = (Calendar) textView.getTag(R.id.date_key);
                dateHolder = selectedDate;
                updateCalendar();
            }
        });
    }

    /**
     * Returns the current date (in milliseconds) as a long
     * @return
     */
    public long getDate() {
        return dateHolder.getTimeInMillis();
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        this.listener = listener;
    }




}
