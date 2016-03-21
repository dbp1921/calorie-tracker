package edu.upenn.cis350.group1.calorietracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        // create database handler
        dbHandler = new DatabaseHandler(getApplicationContext());

        // update and expand Daily list view
        updateAndExpandListView();
    }

    public void onClick(View v){
        Intent i = new Intent(DailyActivity.this, InputActivity.class);
        switch (v.getId()){
            case R.id.button1:
                i.putExtra("BUTTON", "button1");
                break;
            case R.id.button2:
                i.putExtra("BUTTON", "button2");
                break;
            case R.id.button3:
                i.putExtra("BUTTON", "button3");
                break;
            case R.id.button4:
                i.putExtra("BUTTON", "button4");
                break;
        }

        startActivityForResult(i, ACTIVITY_DAILY);
    }

    // delete item
    public void deleteItemFromList(View v) {

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
        return new SimpleExpandableListAdapter(getApplicationContext(), parentMapList,
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
