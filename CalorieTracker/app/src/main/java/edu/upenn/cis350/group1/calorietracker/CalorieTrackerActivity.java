package edu.upenn.cis350.group1.calorietracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by jibreel on 3/20/16.
 */
public abstract class CalorieTrackerActivity extends AppCompatActivity{

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tracker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_today :
                toDaily(getCurrentFocus());
                break;
            case R.id.menu_calendar :
                toCalendar(getCurrentFocus());
                break;
            case R.id.menu_settings :
                toSettings(getCurrentFocus());
                break;
            case R.id.menu_weight :
                toWeight(getCurrentFocus());
                break;
            case R.id.menu_progress :
                toProgress(getCurrentFocus());
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void toDaily(View v) {
        Intent intent = new Intent(this, DailyActivity.class);
        startActivity(intent);
    }

    public void toCalendar(View v) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void toSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void toWeight(View v) {
        Intent intent = new Intent(this, WeightTrackingActivity.class);
        startActivity(intent);
    }

    public void toProgress(View v) {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }
}
