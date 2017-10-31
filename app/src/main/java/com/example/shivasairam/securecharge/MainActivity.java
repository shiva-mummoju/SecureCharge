package com.example.shivasairam.securecharge;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    boolean secure_charge = false;
    boolean charging = false;
    boolean broken = false;
    boolean full_battery = false;
    boolean main_alarm_preference = true;
    boolean bf_alarm_preference = true;
    boolean level_reached = false;
//    BatteryManager batteryManager;
    float level_code;
    ImageView mChargingImageView;
    ImageView start;
    ImageView stop;

    IntentFilter mChargingIntentFilter;
//    making a instance of the broadcastreceiver
    CharginBroadcastReceiver mChargingReceiver;

    MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
//        set up the shared prefernces at the settings at the back
        setupSharedPreferences();

//            instantiaite the intentfilter in the oncreate method
//        now the intent filter is equipped to recieve the events only regarding the power diconnected and
//        power connected
//        now making the broadcast reciever
//
        mChargingIntentFilter = new IntentFilter();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mChargingIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        mChargingImageView = (ImageView) findViewById(R.id.imageButton);
        mChargingImageView.setBackgroundDrawable(null);
        start = (ImageView) findViewById(R.id.start);
        start.setBackgroundDrawable(null);
        stop = (ImageView) findViewById(R.id.stop);
        stop.setBackgroundDrawable(null);

//        initializing the broadcast receiver
        mChargingReceiver = new CharginBroadcastReceiver();

//        creating a sticky intent which gets the intent from the android.
//        registering using null
//        from that we extract the status of the battery charging or not

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

        if(level == 100){
            TextView textView = new TextView(this);
            textView = (TextView) findViewById(R.id.battery_status);
            textView.setText("Battery Status: Full");
            charging = false;
            full_battery = true;
        }else if (status == BatteryManager.BATTERY_STATUS_CHARGING){
            charging = true;
            change_main_activity();
        }else{
            charging = false;
            change_main_activity();
        }



        registerReceiver(mChargingReceiver,mChargingIntentFilter);

//        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.siren);

//        change_main_activity();

    }



//    for getting the settings option on the top right corner
//    inflating it

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }
//giving functionality to the settings button
//    we set up the setupshared prefernces here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent startSettingsActivity  = new Intent(this,SettingsActivity.class);
            startActivity(startSettingsActivity);
            setupSharedPreferences();
        }
        if(id == R.id.about){
            FragmentManager fm = getFragmentManager();
            AboutFragment dialogFragment = new AboutFragment ();

            dialogFragment.show(fm,"About Secure Charge");
        }

        return super.onOptionsItemSelected(item);
    }



//reading from shared preferences and doing something withh that values
    private void setupSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        main_alarm_preference = sharedPreferences.getBoolean("mainalarm",true);
        bf_alarm_preference = sharedPreferences.getBoolean("bfalarm",true);
        level_code = Float.parseFloat(sharedPreferences.getString("level","100"));



//        code to handle the saved prefernces
//take values and call funcions
//        this becomes very important to implement. Have to think about what to do here.

//to makesure the object gets the events.
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
//to make sure the settinggs get changed immediatlely after change
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key == "mainalarm"){
            main_alarm_preference = sharedPreferences.getBoolean(key,true);
        }
        if(key == "bfalarm"){
            bf_alarm_preference = sharedPreferences.getBoolean(key,true);
        }
        if(key == "level"){
            level_code = Float.parseFloat(sharedPreferences.getString("level","100"));
        }
    }





//    method when clicked on start button
    public void onstartbuttonpressed(View view){
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        secure_charge = true;
        if(bf_alarm_preference) {


            if (level == 100) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, R.raw.shiva_alarm);
                mediaPlayer.start();
                NotificationUtils.remindUserBecauseChargerUnplugged(this, "Charging completed!", "Please unplug charger");
                relativeLayout.setBackgroundResource(R.color.green);
            }
        }
        if(level_reached){
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.shiva_alarm);
            mediaPlayer.start();
            NotificationUtils.remindUserBecauseChargerUnplugged(this, "Battery Level Reached", "Battery has reached" + level_code + " please unplug!");
            relativeLayout.setBackgroundResource(R.color.green);

        }
        NotificationUtils.remindUserBecauseChargerUnplugged(this,"Secure Charge Started","Sit back and enjoy!");
        TextView t = new TextView(this);
        t = (TextView)findViewById(R.id.secure_charge_status);
        t.setText("Secure Charge Status: Running");


             if (charging && !broken && !full_battery) {
                 relativeLayout.setBackgroundResource(R.color.green);
             }
             if (!charging && !broken && !full_battery) {//changing the backgruind back to blue when the charigng is not there
                 relativeLayout.setBackgroundResource(R.color.blue);
             }

        secure_charge = true;

    }
//method when stop button is clicked
    public void onstopbuttonpressed(View view){
        secure_charge = false;
        broken = false;
        NotificationUtils.remindUserBecauseChargerUnplugged(this,"Secure Charge Stopped!","Press to restart it.");
        TextView t = new TextView(this);
        t = (TextView)findViewById(R.id.secure_charge_status);
        t.setText("Secure Charge Status: Not Running");

        secure_charge = false;
        if(mediaPlayer != null){
            mediaPlayer.stop();

        }
        broken = false;

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);

        if(level == 100){
            change_background("green");
        }else{
            change_background("blue");
        }
        level_reached = false;
    }

//method which changes the state ofthe textview in the main.xml
    public void change_battery_status_text(){
        TextView t = new TextView(this);
        t = (TextView) findViewById(R.id.battery_status);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        if(charging){
            t.setText("Battery Status: Charging");
        }
        else if(level == 100){
            t.setText("Battery Status: Full");
        }else{
            t.setText("Battery Status: Not Charging");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        change_battery_status_text();
//        change_main_activity();
    }

    //registering the receiver to receievt events when the activity onResume
//    and removing the receiver during onPause
    @Override
    protected void onResume() {
        super.onResume();
        change_battery_status_text();
//            change_main_activity();
    }

    public void on_secure_charge_broken(){
            if(main_alarm_preference) {


                NotificationUtils.remindUserBecauseChargerUnplugged(this, "Secure Charge Broken!", "Press plugin in the charger_grey");
                // write code for using ringtone service
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, R.raw.shiva_alarm);
                mediaPlayer.start();
            }
        broken = true;
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        relativeLayout.setBackgroundResource(R.color.red);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mChargingReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mChargingReceiver);
//        remove the event listener for prefernes change
       PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void change_background(String color){
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        if(color == "green") {
            relativeLayout.setBackgroundResource(R.color.green);
        }else if(color == "red"){
            relativeLayout.setBackgroundResource(R.color.red);
        }else if(color == "blue"){
            relativeLayout.setBackgroundResource(R.color.blue);
        }
    }

    public void change_image_button(){
        if(charging){
            mChargingImageView.setImageResource(R.drawable.ic_battery_charging_full_black_24dp);
        }else{
            mChargingImageView.setImageResource(R.drawable.ic_battery_full_black_24dp);
        }
    }


////creating a class which acts like a broadcast receiver
////    all the events come here and are handled here in onreceive()
    public void change_main_activity(){
        change_image_button();
        if(secure_charge && charging && !full_battery) {
            if(mediaPlayer!=null) {
                mediaPlayer.stop();
            }
            change_background("green");
            change_battery_status_text();
        }
        else if(secure_charge && !charging && !full_battery  ){
            on_secure_charge_broken();
            change_battery_status_text();
        }
        else if(!secure_charge && charging && !full_battery ){
            // do nothing
            change_background("blue");
            change_battery_status_text();
        }
        else if(!secure_charge && !charging && !full_battery ){
            //do nothing
            change_background("blue");
            change_battery_status_text();
        }
        else if(full_battery && secure_charge ){
            change_background("green");
        }else if(full_battery && !secure_charge ){
            change_background("green");
        }

    }



    public void onReachingFullBattery() {
        if(level_reached == true){
            if (secure_charge) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, R.raw.shiva_alarm);
                mediaPlayer.start();
            }
            NotificationUtils.remindUserBecauseChargerUnplugged(this, "Battery Level Reached", "Battery has reached" + level_code + " please unplug!");
            change_background("green");
            TextView textView = new TextView(this);
            textView = (TextView) findViewById(R.id.battery_status);
            textView.setText("Battery Status: Reached Level " + level_code);
            return;
        }
        if (bf_alarm_preference) {

            if (secure_charge) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(this, R.raw.shiva_alarm);
                mediaPlayer.start();
            }

            NotificationUtils.remindUserBecauseChargerUnplugged(this, "Battery Full", "Battery has reached 100% please unplug!");
        }
        change_background("green");
        TextView textView = new TextView(this);
        textView = (TextView) findViewById(R.id.battery_status);
        textView.setText("Battery Status: Full");
    }

//      Creating inner Broadcast Reciever
    public class CharginBroadcastReceiver extends BroadcastReceiver{
       public CharginBroadcastReceiver() {
           super();
       }
        @Override
        public void onReceive(Context context, Intent intent) {


            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            if(level == 100 || level == level_code){
                if(level == level_code){
                    level_reached = true;
                }
                charging = false;
                full_battery = true;
                onReachingFullBattery();
                return;
            } else if(status == BatteryManager.BATTERY_STATUS_CHARGING){
                charging = true;
                full_battery = false;
                change_main_activity();
                return;
            }else {
                charging = false;
                change_main_activity();
                return;
            }




//
//            String action = intent.getAction();
//            boolean isCharging = (action.endsWith(Intent.ACTION_POWER_CONNECTED));
//            charging = isCharging;
//            change_main_activity();






        }
    }

}
