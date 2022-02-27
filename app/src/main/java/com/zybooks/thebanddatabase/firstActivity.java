package com.zybooks.thebanddatabase;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class firstActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private MediaPlayer doorSdPlayer;
    private Context context;
    private EditText userNameEText;
    private EditText passWordEText;
    private ImageView lockDoorView;
    private LogInSQL logSql;

    private boolean passOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        context = this;

        userNameEText =findViewById(R.id.userNameETextID);
        passWordEText = findViewById(R.id.pWordETextID);
        lockDoorView = findViewById(R.id.lockDoorViewID);
        lockDoorView.setImageResource(R.drawable.locked);

        logSql = new LogInSQL(getApplicationContext());
        //long rawID = logSql.addUser("wan", "pass");

        Spinner spinner = findViewById(R.id.sMusicSpinID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.songs_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String songName;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                songName = (String) adapterView.getItemAtPosition(i);

                if (mMediaPlayer == null) {
                    if (songName.contains("Funky")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.funkytown);
                        mMediaPlayer.start();
                    }
                    if (songName.contains("Take")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.aha);
                        mMediaPlayer.start();
                    }

                    if (songName.contains("Hotel")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.hotel_california);
                        mMediaPlayer.start();
                    }
                } else {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    if (songName.contains("Funky")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.funkytown);
                        mMediaPlayer.start();
                    }
                    if (songName.contains("Take")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.aha);
                        mMediaPlayer.start();
                    }

                    if (songName.contains("Hotel")) {
                        mMediaPlayer = MediaPlayer.create(context, R.raw.hotel_california);
                        mMediaPlayer.start();
                    }
                }
                //Log.d(TAG, "onItemSelected: =============="  + songName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void toBankData(View view) {
        if(passOrNot == true) {
            Intent intent = new Intent(this, BankInfosActivity.class);
            startActivity(intent);
        }
    }

    public void toSaveCheckFun(View view) {
        if(passOrNot == true) {
            Intent intent = new Intent(this, SaveCheckActivity.class);
            startActivity(intent);
        }
    }

    public void TurnOffMusicFun(View view) {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void logInBuFun(View view) {
        String uName = userNameEText.getText().toString();
        String pWord = passWordEText.getText().toString();
        //passOrNot = logSql.getData(uName);
//        if (passOrNot) {
//            lockDoorView.setImageResource(R.drawable.unlock);
//        } else {
//            lockDoorView.setImageResource(R.drawable.locked);
//        }

        Thread thread = new Thread(() -> {
            //userNameEText.setText("##################");
            passOrNot = logSql.getData(uName);
            //lockDoorView.setImageResource(R.drawable.unlock);
            Log.d(TAG, "logInBuFun: &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        });
        thread.start();
        if (passOrNot) {
            lockDoorView.setImageResource(R.drawable.unlock);
            doorSdPlayer = null;
            doorSdPlayer = MediaPlayer.create(context, R.raw.door_sound);
            doorSdPlayer.start();
        } else {
            lockDoorView.setImageResource(R.drawable.locked);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOutMenuID) {
            lockDoorView.setImageResource(R.drawable.locked);
            doorSdPlayer = null;
            doorSdPlayer = MediaPlayer.create(context, R.raw.door_sound);
            doorSdPlayer.start();
            passOrNot = false;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncThread extends AsyncTask {

        protected Integer doInBackground(String uName) {

            return 0;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }

}