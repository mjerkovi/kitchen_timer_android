package com.dealfaro.luca.KitchenTImer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public class RecentTimes {
        private int num_seconds;
        private long time_added;

        public RecentTimes (){
            num_seconds = 0;
            time_added = 0;
        }

        public int getNum_seconds(){
            return this.num_seconds;
        }

        public long getTime_added() {
            return this.time_added;
        }

        public void setNum_seconds(int sec) {
            this.num_seconds = sec;
        }

        public void setTime_added(long tm) {
            this.time_added = tm;
        }
    }


    static final private String LOG_TAG = "test2017app1";

    // Counter for the number of seconds.
    private int seconds = 0;

    // Countdown timer.
    private CountDownTimer timer = null;

    // One second.  We use Mickey Mouse time.
    private static final int ONE_SECOND_IN_MILLIS = 1000;

    /*
     * Stores the info of the recent buttons.
     * RecentTimes[0] -> Leftmost button
     * RecentTimes[1] -> Center button
     * RecentTimes[2] -> Rightmost button
     */
    private RecentTimes[] recent_arr = new RecentTimes[3];

    // Flag that indicates whether to update time or not
    private boolean to_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recent_arr[0] = new RecentTimes();
        recent_arr[1] = new RecentTimes();
        recent_arr[2] = new RecentTimes();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTime();
    }

    public void onClickPlus(View v) {
        to_update = true;
        seconds += 60;
        displayTime();
    };

    public void onClickMinus(View v) {
        to_update = true;
        seconds = Math.max(0, seconds - 60);
        displayTime();
    };

    public void onReset(View v) {
        to_update = true;
        seconds = 0;
        cancelTimer();
        displayTime();
    }

    public void onClickStart(View v) {
        if (seconds == 0) {
            cancelTimer();
        }
        if (timer == null) {
            // If the recent buttons need to be updated(i.e. after a + or - has been pressed)
            // then update the recent buttons and set to_update to false
            if(to_update) {
                updateRecentButtons();
                to_update = false;
            }
            // We create a new timer.
            timer = new CountDownTimer(seconds * ONE_SECOND_IN_MILLIS, ONE_SECOND_IN_MILLIS) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d(LOG_TAG, "Tick at " + millisUntilFinished);
                    seconds = Math.max(0, seconds - 1);
                    displayTime();
                }

                @Override
                public void onFinish() {
                    seconds = 0;
                    timer = null;
                    displayTime();
                    to_update = true;
                }
            };
            timer.start();
        }
    }

    public void onClickStop(View v) {
        cancelTimer();
        displayTime();
    }

    /*
     * If the leftmost button is pressed. Check to see if the number of seconds stored in it's
     * corresponding recent_arr is 0. If it's 0 do nothing. Otherwise update the timer to the
     * number of seconds stored in it's corresponding recent_arr entry and start the timer.
     */
    public void onClickRecent1(View v) {
        to_update = true;
        if(recent_arr[0].getNum_seconds() == 0){
            return;
        }
        seconds = recent_arr[0].getNum_seconds();
        onClickStart(null);
    }

    public void onClickRecent2(View v) {
        to_update = true;
        if(recent_arr[1].getNum_seconds() == 0){
            return;
        }
        seconds = recent_arr[1].getNum_seconds();
        onClickStart(null);
    }

    public void onClickRecent3(View v) {
        to_update = true;
        if(recent_arr[2].getNum_seconds() == 0){
            return;
        }
        seconds = recent_arr[2].getNum_seconds();
        onClickStart(null);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // Updates the time display.
    private void displayTime() {
        Log.d(LOG_TAG, "Displaying time " + seconds);
        TextView v = (TextView) findViewById(R.id.display);
        int m = seconds / 60;
        int s = seconds % 60;
        v.setText(String.format("%d:%02d", m, s));
        // Manages the buttons.
        Button stopButton = (Button) findViewById(R.id.button_stop);
        Button startButton = (Button) findViewById(R.id.button_start);
        startButton.setEnabled(timer == null && seconds > 0);
        stopButton.setEnabled(timer != null && seconds > 0);
    }

    public void updateRecentButtons(){
        // Look to see if a button with the time already exists
        for(int i = 0; i < 3; ++i) {
            if(recent_arr[i].getNum_seconds() == seconds){
                // if it already exists, then update the time_added field
                recent_arr[i].setTime_added(System.currentTimeMillis());
                displayRecentButtons();
                return;
            }
        }
        // first need to find least recent button or any button with seconds == 0
        // and add the current seconds to the button
        long least_recent_time = Long.MAX_VALUE;
        int least_recent_index = -1;
        int button_to_replace = 0;
        for(; button_to_replace < 3; ++button_to_replace) {
            if(recent_arr[button_to_replace].getNum_seconds() == 0) {
                least_recent_index = -1;
                break;
            }
            if(recent_arr[button_to_replace].getTime_added() < least_recent_time) {
                least_recent_time = recent_arr[button_to_replace].getTime_added();
                least_recent_index = button_to_replace;
            }
        }
        if(least_recent_index != -1) {
            button_to_replace = least_recent_index;
        }
        recent_arr[button_to_replace].setNum_seconds(seconds);
        recent_arr[button_to_replace].setTime_added(System.currentTimeMillis());

        // now display the times on the buttons
        displayRecentButtons();
    }

    public void displayRecentButtons() {
        // find all three of the recent buttons in the view
        Button recent1 = (Button) findViewById(R.id.recent1);
        Button recent2 = (Button) findViewById(R.id.recent2);
        Button recent3 = (Button) findViewById(R.id.recent3);

        /*
         * Now display all of the buttons. If the corresponding recent_arr entry for the button
         * has a 0 for the num_seconds field, then don't display anything in the text of the button
         */
        if(recent_arr[0].getNum_seconds() == 0) {
            recent1.setText("");
        }else{
            int m = recent_arr[0].getNum_seconds() / 60;
            int s = recent_arr[0].getNum_seconds() % 60;
            recent1.setText(String.format("%d:%02d", m, s));
        }

        if(recent_arr[1].getNum_seconds() == 0) {
            recent2.setText("");
        }else{
            int m = recent_arr[1].getNum_seconds() / 60;
            int s = recent_arr[1].getNum_seconds() % 60;
            recent2.setText(String.format("%d:%02d", m, s));
        }

        if(recent_arr[2].getNum_seconds() == 0) {
            recent3.setText("");
        }else{
            int m = recent_arr[2].getNum_seconds() / 60;
            int s = recent_arr[2].getNum_seconds() % 60;
            recent3.setText(String.format("%d:%02d", m, s));
        }
    }
}
