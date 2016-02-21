package edu.upenn.cis350.group1.calorietracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jussi Lundstedt on 2/20/2016.
 *
 * database handler for Calorie Tracker
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // database fields
    private static final int DATABASE_VERSION = 1; // database version
    private static final String DATABASE_NAME = "calorieTracker"; // database name

    // date table name & fields
    private static final String TABLE_DATES = "dates";
    private static final String DATES_KEY_ID = "id";
    private static final String DATES_KEY_DATE = "date";

    // meal table name & fields
    private static final String TABLE_MEALS = "meals";
    private static final String MEALS_KEY_ID = "id";
    private static final String MEALS_KEY_DATE_ID = "dateID";
    private static final String MEALS_KEY_TYPE = "mealType";
    private static final String MEALS_KEY_NAME = "name";
    private static final String MEALS_KEY_CALORIES = "calories";
    private static final String MEALS_KEY_PROTEIN = "protein";
    private static final String MEALS_KEY_CARBS = "carbs";
    private static final String MEALS_KEY_SODIUM = "sodium";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // enable foreign key constraints in the best way (API 16 required for top call)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            db.execSQL("PRAGMA foreign_keys=ON");
        }

        // SQL query to create date table
        String CREATE_DATES_TABLE = "CREATE TABLE " + TABLE_DATES + "("
                + DATES_KEY_ID + " INTEGER PRIMARY KEY,"
                + DATES_KEY_DATE + " INTEGER NOT NULL," +
                ")";
        // create dates table
        db.execSQL(CREATE_DATES_TABLE);

        // SQL query to create meals table
        String CREATE_MEALS_TABLE = "CREATE TABLE " + TABLE_MEALS + "("
                + MEALS_KEY_ID + " INTEGER PRIMARY KEY,"
                + MEALS_KEY_DATE_ID + " INTEGER,"
                + MEALS_KEY_TYPE + " INTEGER,"
                + MEALS_KEY_NAME + " TEXT,"
                + MEALS_KEY_CALORIES + " INTEGER,"
                + MEALS_KEY_PROTEIN + " REAL,"
                + MEALS_KEY_CARBS + " REAL,"
                + MEALS_KEY_SODIUM + " REAL,"
                + "FOREIGN KEY(" + MEALS_KEY_DATE_ID + ") REFERENCES " + TABLE_DATES + "(" + DATES_KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_MEALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: onUpgrade needs to be figured out
    }

    // add meal to database
    public void addMeal(Meal meal) {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();

        // get date of meal
        Date d = meal.getDateEaten();

        // insert date into database, doesn't matter if date already exists, method returns id either way
        int dateID = addDate(d);

        // create a content values bundle for insertion
        ContentValues values = new ContentValues();
        values.put(MEALS_KEY_DATE_ID, dateID);
        values.put(MEALS_KEY_TYPE, meal.getTypeCode());
        values.put(MEALS_KEY_NAME, meal.getName());
        // only insert these if they've been inserted
        if (meal.getCalories() != 0) values.put(MEALS_KEY_CALORIES, meal.getCalories());
        if (meal.getProtein() != 0.0) values.put(MEALS_KEY_PROTEIN, meal.getProtein());
        if (meal.getCarbs() != 0.0) values.put(MEALS_KEY_CARBS, meal.getCarbs());
        if (meal.getSodium() != 0.0) values.put(MEALS_KEY_SODIUM, meal.getSodium());

        // insert to meals table
        db.insert(TABLE_MEALS, null, values);
        db.close(); // close database connection
    }

    // add date to database - returns id of new date or if found in table already will return id
    // of existing date
    public int addDate(Date date) {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();

        // query if date already exists
        Cursor c = db.query(TABLE_DATES, null, DATES_KEY_DATE + "=?",
                new String[]{date.getTime() + ""}, null, null, null, null);

        int dateID = 0; // date ID to return
        if (c != null) { // check if cursor is null
            // if date not in table, insert date into table
            if (!c.moveToFirst()) {
                ContentValues dateContent = new ContentValues();
                dateContent.put(DATES_KEY_DATE, date.getTime());
                dateID = (int) db.insert(TABLE_DATES, null, dateContent);
            } else { // otherwise just get the id of the date
                c.moveToFirst();
                dateID = c.getInt(0);
            }
            c.close(); // close cursor
        }

        db.close(); // close database connection
        return dateID;
    }

    // get the ID of this date in the database, returns -1 if doesn't exist
    public int getDateID(Date date) {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();
        int dateID = -1; // date ID to return

        // query date
        Cursor c = db.query(TABLE_DATES, null, DATES_KEY_DATE + "=?",
                new String[]{date.getTime() + ""}, null, null, null, null);

        if (c != null) {
            if (!c.moveToFirst()) {
                dateID = c.getInt(0); // get date ID if date was found
            }
            c.close(); // close cursor
        }

        db.close(); // close database connection
        return dateID;
    }

    // get list of meals for a given date
    public List<Meal> getAllMeals(Date date) {
        // get db
        SQLiteDatabase db = this.getWritableDatabase();

        // create list of meals
        ArrayList<Meal> meals = new ArrayList<>();

        // first query date to get dateID
        int dateID = getDateID(date);
        if (dateID != -1) { // if date doesn't exist return empty list otherwise get to doing stuff
            Cursor c = db.query(TABLE_MEALS, null, MEALS_KEY_DATE_ID + "=?",
                    new String[] { dateID + ""}, null, null, MEALS_KEY_ID + " ASC");

            if (c.moveToFirst()) {
                do {
                    Meal m = new Meal(c.getString(3), date, c.getInt(2), c.getInt(0));
                    if (!c.isNull(4)) m.setCalories(c.getInt(4));
                    if (!c.isNull(5)) m.setProtein(c.getDouble(5));
                    if (!c.isNull(6)) m.setCarbs(c.getDouble(6));
                    if (!c.isNull(7)) m.setCarbs(c.getDouble(7));
                    meals.add(m);
                } while (c.moveToNext());
            }

            c.close();
        }

        db.close();
        return meals;
    }

    // get meal with given ID
    public Meal getMeal(int id) {
        // TODO: implement, not useful at this stage
        return null;
    }

    // update existing meal
    public void updateMeal(Meal meal) {
        // TODO: implement
    }

    // delete existing meal
    public void deleteMeal(Meal meal) {
        // TODO: implement
    }
}
