package edu.upenn.cis350.group1.calorietracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DailyActivity extends CalorieTrackerActivity {
    private DatabaseHandler dbHandler; // database handler

    // keys for various maps
    private static final String KEY_MEAL_NAME = "mealName";
    private static final String KEY_MEAL_TYPE = "type";
    private static final String KEY_MEAL_CALORIES = "calories";
    private static final String KEY_MEAL_ID = "mealID";
    private static final int RESULT_OK = 400;
    private static final int ACTIVITY_DAILY = 1;


    // rigid meal type array inherited from Meal.java
    private static final String[] types = {"Breakfast", "Lunch", "Dinner", "Snack"};

    private Date todaysDate; // need this for AlertDialog
    private double weight; // need this for AlertDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        // create database handler
        dbHandler = new DatabaseHandler(getApplicationContext());

        // update and expand Daily list view
        updateAndExpandListView();

        ExpandableListView view = (ExpandableListView) findViewById(R.id.daily_list);
        view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();

                HashMap<String, String> data =
                        (HashMap) adapter.getChild(groupPosition, childPosition);

                int mealID = Integer.parseInt(data.get(KEY_MEAL_ID));

                Log.v("ExpandableListView", "MealID retrieved: " + mealID);

                Intent i = new Intent(DailyActivity.this, InputActivity.class);
                i.putExtra("EXISTING", true);
                i.putExtra("MEAL_ID", mealID);
                startActivityForResult(i, ACTIVITY_DAILY);
                return true;
            }
        });
    }

    // click handler for adding new meal from Daily Screen
    public void onMealButtonClick(View v) {
        Intent mealInputScreen = new Intent(DailyActivity.this, InputActivity.class);

        startActivityForResult(mealInputScreen, ACTIVITY_DAILY);
    }

    // click handler for setting weight from Daily Screen
    public void onWeightButtonClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        todaysDate = new Date(System.currentTimeMillis());
        dialog.setTitle("Weight for " + todaysDate.toString());

        // Set up the input
        final EditText input = new EditText(this);

        // set input type and hint
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (dbHandler.getWeight(todaysDate) == -1) {
            input.setHint(Double.toString(0.0));
        } else {
            input.setText(Double.toString(dbHandler.getWeight(todaysDate)));
        }

        dialog.setView(input);
        // Set up the buttons
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weight = Double.parseDouble(input.getText().toString());
                dbHandler.setWeightForDate(todaysDate, weight);
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

    // fetch and prepare data for the listview
    private ExpandableListAdapter prepareListData() {
        // get current time
        Date date = new Date(System.currentTimeMillis());

        // get list of meals
        List<Meal> meals = dbHandler.getAllMealsList(date);

        // list of maps to hold categories of meals
        ArrayList<HashMap<String, String>> parentMapList = new ArrayList<>();

        // add a map entry for every parent to the above list
        for (int i = 0; i < types.length; i++) {
            HashMap<String, String> group = new HashMap<>();
            group.put(KEY_MEAL_TYPE, types[i]);
            parentMapList.add(group);
        }

        // parameters required by list adapter - shows value of key "type in mealtype_header
        int groupLayout = R.layout.meal_group;
        String[] groupFrom = new String[] { KEY_MEAL_TYPE };
        int[] groupTo = new int [] { R.id.mealtype_header };

        // create list of list of maps for child view's purposes
        ArrayList<ArrayList<HashMap<String, String>>> childListOfListOfMaps = new ArrayList<>();

        // create empty lists of maps for children of each category
        for (int i = 0; i < parentMapList.size(); i++) {
            ArrayList<HashMap<String, String>> children = new ArrayList<>();
            childListOfListOfMaps.add(children);
        }

        // add each individual meal as a child of appropriate parent
        for (int i = 0; i < meals.size(); i++) {
            Meal m = meals.get(i);
            int typeCode = m.getTypeCode();
            ArrayList<HashMap<String, String>> listOfMaps = childListOfListOfMaps.get(typeCode);

            HashMap<String, String> mealProperties = new HashMap<>();
            mealProperties.put(KEY_MEAL_NAME, m.getName());
            mealProperties.put(KEY_MEAL_CALORIES, String.valueOf(m.getCalories()));
            mealProperties.put(KEY_MEAL_ID, String.valueOf(m.getMealID()));

            listOfMaps.add(mealProperties);
        }

        // parameters required by list adapter
        int childLayout = R.layout.meal_item;
        String[] childFrom = new String[] { KEY_MEAL_NAME, KEY_MEAL_CALORIES };
        int[] childTo = new int [] { R.id.textview_meal_title, R.id.textview_meal_calories };

        // return the actual adapter
        return new SimpleExpandableListAdapter(DailyActivity.this, parentMapList,
                groupLayout, groupFrom, groupTo, childListOfListOfMaps, childLayout, childFrom,
                childTo);
    }

    // update data in list view and expand categories with data
    private void updateAndExpandListView() {
        // get adapter and view
        ExpandableListAdapter adapter = prepareListData();
        ExpandableListView view = (ExpandableListView) findViewById(R.id.daily_list);

        // show the actual view
        view.setAdapter(adapter);

        // expand categories that contain data
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (adapter.getChildrenCount(i) > 0) view.expandGroup(i);
        }
    }

    // called when a new meal is input using InputActivity
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        // if launched InputActivity returned properly then update list
        if(requestCode == ACTIVITY_DAILY && resultCode == RESULT_OK){
            updateAndExpandListView();
        }
    }
}
