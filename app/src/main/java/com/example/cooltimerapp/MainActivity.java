package com.example.cooltimerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timerView;
    SeekBar seekbar;
    boolean isTimerOn = false;
    Button button;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerView = findViewById(R.id.timerView);
        seekbar = findViewById(R.id.seekBar);

        seekbar.setMax(600);
        seekbar.setProgress(30);
        button = findViewById(R.id.button);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                long progressInMillis = i * 1000;
                updateTimer(progressInMillis);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void start(View view) {

        if(!isTimerOn){
            button.setText("STOP");
            seekbar.setEnabled(false);
            isTimerOn = true;
            countDownTimer = new CountDownTimer(seekbar.getProgress() * 1000 , 1000) {
                @Override
                public void onTick(long l) {
                    updateTimer(l);
                    seekbar.setProgress((int) (l/1000));
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound", true)) {
                        String melodyName = sharedPreferences.getString("timer_melody", "bell");
                        if(melodyName.equals("bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                            mediaPlayer.start();
                        } else if(melodyName.equals("alarm_siren")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                            mediaPlayer.start();
                        }else {
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                            mediaPlayer.start();
                        }

                    }
                    resetTimer();
                }
            };
            countDownTimer.start();
        }else {
            resetTimer();
        }


    }

    private void updateTimer(long l) {

        int minutes = (int)(l/1000/60);
        int seconds = (int) (l/1000 - (minutes * 60));

        String minutesString = "";
        String secondsString = "";
        if(minutes < 10){
            minutesString = "0" + minutes;
        }else {
            minutesString = String.valueOf(minutes);
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else {
            secondsString = String.valueOf(seconds);
        }

        timerView.setText(minutesString + " : " + secondsString);
    }
    private void resetTimer(){
        countDownTimer.cancel();
        timerView.setText("00 : 30");
        seekbar.setProgress(30);
        button.setText("START");
        seekbar.setEnabled(true);
        isTimerOn = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        } else if(id == R.id.action_about) {
            Intent openAbout = new Intent(this, AboutActivity.class);
            startActivity(openAbout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
