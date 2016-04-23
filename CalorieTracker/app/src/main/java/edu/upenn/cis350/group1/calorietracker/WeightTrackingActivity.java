package edu.upenn.cis350.group1.calorietracker;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jibreel on 4/3/16.
 */
public class WeightTrackingActivity extends CalorieTrackerActivity{

    private DatabaseHandler dbHandler;
    private GraphView graph;
    private static DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_weight_tracking);

        //create database handler
        dbHandler = new DatabaseHandler(this.getApplicationContext());

        //find graph view
        graph = (GraphView) findViewById(R.id.graph);

        //weightTest();
        buildGraphAndList();
    }

    private void buildGraphAndList() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        ListView list = (ListView) findViewById(R.id.weight_table);

        // column names and view ids to update
        String[] arrayColumns = new String[] {"date", "weight", "_id"};
        int[] viewIDs = {R.id.textview_weight_date, R.id.textview_weight_value};

        MatrixCursor c = new MatrixCursor(arrayColumns);

        Map<Long, Double> entries = new TreeMap<>();

        for (int i = 0; i < 30; i++) {
            long millis = calendar.getTimeInMillis();
            Date date = new Date(millis);
            double weight = dbHandler.getWeight(date);
            if (weight > 0) {
                entries.put(millis, weight);
                String[] values = {date.toString(), df.format(weight) + " lbs",
                        Integer.toString(dbHandler.getDateID(date))};
                c.addRow(values);
                Log.v("Values in rows", c.getCount() + "");
            }

            calendar.add(Calendar.DATE, 1);
        }

        if (entries.isEmpty()) return;

        // update the adapter
        Log.v("Setting list adapter", "True");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.weight_item, c,
                arrayColumns, viewIDs);
        list.setAdapter(adapter);

        ArrayList<Long> entryDates = new ArrayList<>(entries.keySet());
        Collections.sort(entryDates);

        Long[] dates = entryDates.toArray(new Long[entryDates.size()]);
        DataPoint[] dataPoints = new DataPoint[dates.length];

        for (int i = 0; i < dataPoints.length; i++) {
            dataPoints[i] = new DataPoint(dates[i], entries.get(dates[i]));
        }

        //Set graph to display dates on X axis
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        //manual X bounds
        graph.getViewport().setMinX(dates[0]);
        graph.getViewport().setMaxX(dates[dates.length - 1]);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    private void weightTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        for (int i = 0; i < 30; i++){
            if (Math.random() < (1.0 / 3)) {
                Date date = new Date(calendar.getTimeInMillis());
                double weight = Math.random() * 20 + 160;
                dbHandler.setWeightForDate(date, weight);
            }
            calendar.add(Calendar.DATE, 1);
        }
    }
}
