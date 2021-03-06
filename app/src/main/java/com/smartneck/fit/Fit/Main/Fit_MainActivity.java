package com.smartneck.fit.Fit.Main;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartneck.fit.BleDialog.BleAdapter;
import com.smartneck.fit.BleDialog.BleItem;
import com.smartneck.fit.Fit.Chart.Fit_ChartActivity;
import com.smartneck.fit.Fit.CustomerSupport.Controller.Fit_CustomerSupportListActivity;
import com.smartneck.fit.Fit.ErrorReport.Fit_ErrorReportActivity;
import com.smartneck.fit.Fit.Fit_AppVersionActivity;
import com.smartneck.fit.Fit.Fit_LoginActivity;
import com.smartneck.fit.Fit.Fit_MeasureActivity;
import com.smartneck.fit.Fit.Notice.Fit_NoticeListActivity;
import com.smartneck.fit.Fit.Review.Fit_ReviewActivity;
import com.smartneck.fit.Fit.UserInfoEdit.Fit_UserInfoEditListActivity;
import com.smartneck.fit.Fit.util.Fit_Address;
import com.smartneck.fit.Fit.util.Fit_BluetoothUtils;
import com.smartneck.fit.Fit.util.Fit_BottomNavigationHelper;
import com.smartneck.fit.Fit.util.Fit_Commend;
import com.smartneck.fit.Fit.util.Fit_Constants;
import com.smartneck.fit.Fit.util.Fit_HttpConnect;
import com.smartneck.fit.Fit.util.Fit_Param;
import com.smartneck.fit.Fit.util.Fit_ProgressDialog;
import com.smartneck.fit.Fit.util.Fit_StringUtils;
import com.smartneck.fit.Fit.util.User.Fit_Preset;
import com.smartneck.fit.Fit.util.User.Fit_User;
import com.smartneck.fit.GlobalApplication;
import com.smartneck.fit.R;
import com.smartneck.fit.util.Commend;
import com.smartneck.fit.util.ProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.smartneck.fit.Fit.Fit_BrowserActivity.isBrowser;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.CFG_HEIGHT;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.CFG_WEIGHT;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.CFG_WEIGHT_MAX;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.isMeasure;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.moveExercise;
import static com.smartneck.fit.Fit.Fit_MeasureActivity.moveProcedure;
import static com.smartneck.fit.Fit.Main.Fit_WeightSettingFragment.currentWeight;
import static com.smartneck.fit.Fit.Main.Fit_WeightSettingFragment.isMove;
import static com.smartneck.fit.Fit.Main.Fit_WeightSettingFragment.isWeight;
import static com.smartneck.fit.Fit.util.Fit_Constants.CFG_IS_EXERCISE;
import static com.smartneck.fit.Fit.util.User.Fit_Preset.DEVICE_NAME;
import static com.smartneck.fit.Fit.util.User.Fit_Preset.getEntrySelection;
import static com.smartneck.fit.Fit.util.User.Fit_User.language;

public class Fit_MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static MediaPlayer mp;
    public static Context mContext;
    public static Activity activity;
    public static boolean isLogout;
    static int fragIndex;
    Handler handler;
    ImageView test;
    NavigationView nav_view;
    BottomNavigationView bottomNavigationView;

    Fit_HomeFragment fragment_home;
    Fit_ExerciseFragment fragment_exercise;
    Fit_ProcedureFragment fragment_procedure;
    Fit_ProcedureDefaultFragment fragment_procedure_default;
    //    SeatSettingFragment fragment_seat;
//    WeightSettingFragment fragment_weight;
//    HeightFragment fragment_height;
    final int rssiDistance = -90;
    boolean isOpen;
    static boolean isMain;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    static boolean is_82 = false;
    static boolean isSeatMove = false;
    static boolean isWeightMove = false;
    static boolean isClick = false;
    public static int tabPos = 4;
    static String currentLocation = "";

    public static String protocolType = "";
    private boolean SettingOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragIndex = 0;
        Log.d("????????????", " ");
        Log.d("????????????", " ");
        Log.d("????????????", " ");
        Log.d("????????????", "???????????????????????????????????????????????????????????? START ????????????????????????????????????????????????????????????");
        Log.d("????????????", " ");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fit_main);
        getEntrySelection(this);
        Fit_Preset.soundType = "female";
        mContext = this;
        activity = this;
        handler = new Handler();
        isLogout = false;

        test = findViewById(R.id.toolbar_home);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomNavigation("HOME");
            }
        });
        test.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ????????????
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                isOpen = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                isOpen = true;

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        View navigation_view = nav_view.getHeaderView(0);

        // ??????

        bottomNavigationView = findViewById(R.id.navigation);
        Fit_BottomNavigationHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // mConnected = true;

                switch (item.getItemId()) {


                    case R.id.navigation_exercise:
                        setBottomNavigation("EXERCISE");
                        break;

                    case R.id.navigation_procedure:
                        setBottomNavigation("PROCEDURE_DEFAULT");

                        break;
//                    case R.id.navigation_height:
//                        setBottomNavigation("HEIGHT");
//
//
//                        break;
//                    case R.id.navigation_weight:
//                        setBottomNavigation("WEIGHT");
//
//                        fragment_weight = new WeightSettingFragment();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_weight, "weight").commit();
//
//                        break;

                }
                return true;
            }
        });


        // TODO: 2020-06-03 ???????????? ???????????? ?????????
        int IS_ACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int IS_ACCESS_FINE_LOCATION = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= 29) {
            if (IS_ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
                setInit();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            }
        } else {
            if (IS_ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED && IS_ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
                setInit();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("??????", "----- onRequestPermissionsResult: " + requestCode + " / " + permissions.length + " / " + grantResults.length);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setInit();
                else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("");
    }

    @Override
    public void onResume() {
        Log.d("??????", "----- ChartActivity onResume");
        tabPos = 4;
        isMeasure = false;
        //???????????? ?????????????????? ????????? ????????? ??? ?????????????????? ??????
        getUserPreset();
//        if (CFG_MEMBER_NAME == null) {
//            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
//            CFG_MEMBER_NAME = preferences.getString("name", "");
//        }
        if (isBrowser) {
            setBottomNavigation("HOME");
            isBrowser = false;
        }

        //?????? ??? ??????????????? ??????
        if (moveExercise) {
            moveExercise = false;
            setBottomNavigation("EXERCISE");
        } else if (moveProcedure) {
            moveProcedure = false;
            setBottomNavigation("PROCEDURE_DEFAULT");
        }

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("??????", "----- ChartActivity onDestroy");
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
        if (mConnected) {
            fragment_home = null;
            setDisconnect();
        }


    }

    @Override
    public void onBackPressed() {
        Log.d("??????", "----- onBackPressed");
        if (isOpen) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return;
        }


        if (tabPos == 3) {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                setBottomNavigation("HOME");

//            Constants.init();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), getString(R.string.toast_back2), Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (!currentLocation.equals("home")) {
            setBottomNavigation("HOME");
            return;
        }


        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            System.exit(0);

            if (mConnected) setDisconnect();
//            Constants.init();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), getString(R.string.toast_back), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("??????", "----- onKeyDown: " + keyCode);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // ??????

//        if (id == R.id.nav_alarm) {
//            Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
//            startActivity(intent);
//        } else
        if (id == R.id.nav_user_info) {
            //???????????? ??????
            Intent intent = new Intent(getApplicationContext(), Fit_UserInfoEditListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_device_info) {
            Intent intent = new Intent(getApplicationContext(), Fit_AppVersionActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_statistics) {

            Intent intent = new Intent(getApplicationContext(), Fit_ChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            isLogout = true;
            Fit_User.setAutologinState(this, false);
            Intent intent = new Intent(getApplicationContext(), Fit_LoginActivity.class);
            startActivity(intent);
            finish();
//            setMemberLogout(getApplicationContext());

        }
//        else if (id == R.id.nav_management) {
//            //?????? ????????? ?? ?????? ??
//        }
        else if (id == R.id.nav_notice) {
            Intent intent = new Intent(getApplicationContext(), Fit_NoticeListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_review) {
            Intent intent = new Intent(getApplicationContext(), Fit_ReviewActivity.class);
            startActivity(intent);
            //            setBrowser(Constants.User.token, "/review");
        } else if (id == R.id.nav_inquery) {
            //????????????
            Intent intent = new Intent(getApplicationContext(), Fit_CustomerSupportListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_error) {
            Intent intent = new Intent(getApplicationContext(), Fit_ErrorReportActivity.class);
            startActivity(intent);
        }

//        else if (id == R.id.nav_height) {
//            if (isConnectedBle()) {
//                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
//                intent.putExtra("height", true);
//                startActivity(intent);
//            }
//
//        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public boolean isInternet() {
        Log.d("??????", "----- isInternet");

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnected() == true || mobile.isConnected() == true) {
            return true;
        } else {
            return false;
        }
    }


    public void setInit() {
        Log.d("??????", "----- setInit");
        setDesktopIcon();

        // Home
        fragment_home = new Fit_HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_home, "FRAG_HOME").commit();

        // Internet
        Fit_Constants.CFG_IS_INTERNET = isInternet();

        if (Fit_Constants.CFG_IS_INTERNET == false) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_internet), Toast.LENGTH_SHORT).show();

            finish();

            return;
        }

        // BLE
        Fit_Constants.CFG_IS_BLUETOOTH = isBluetooth();

        if (Fit_Constants.CFG_IS_BLUETOOTH == true) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        }

//        getBleStatus();


        setEnable();
    }

    // ????????? ?????????

    public void setDeviceType(String DeviceType) {
        Log.d("??????", "----- ChartActivity setDeviceType: " + DeviceType);

        switch (DeviceType) {
            case "HOM":
                break;

            case "FIT":
                break;


        }


    }

    public void setMessageConfirm(String msg, String type, final String move) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);

        if (type.equals("stopExercise")) {
            ab.setMessage(msg).setCancelable(
                    false).setPositiveButton(R.string.dialog_exercise_quit_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).setNegativeButton(getString(R.string.dialog_exercise_quit_confilm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    setMessage(StringUtils.getCommand("44 3A 01"));
                    //timerHandler.sendEmptyMessage(TIMER_STOP);
                    if (move.equals("home")) {
                        fragment_home = new Fit_HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_home, "FRAG_HOME").commit();
                    } else if (move.equals("procedure")) {
                        fragment_procedure = new Fit_ProcedureFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_procedure, "FRAG_PROCEDURE").commit();
                    }

                    CFG_IS_EXERCISE = false;
                }
            });
        } else {
            ab.setMessage(msg).setCancelable(
                    false).setPositiveButton(R.string.dialog_exercise_quit_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

        }

        AlertDialog alert = ab.create();
        alert.show();
    }

    public void setMessageConfirm(String msg) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);


        ab.setMessage(msg).setCancelable(
                false).setPositiveButton(R.string.dialog_exercise_quit_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        AlertDialog alert = ab.create();
        alert.setTitle("??????");
        alert.setIcon(R.drawable.ic_baseline_error_outline_24px);
        alert.show();
    }

    public void setDesktopIcon() {
        Log.d("??????", "----- setDesktopIcon");

        SharedPreferences pref = getSharedPreferences("NECKTHPOWER", MODE_PRIVATE);

        if (pref.getString("SHORTCUT", "").isEmpty()) {
            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);

            shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            shortcutIntent.setClassName(this, getClass().getName());
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Smart Neck");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
            intent.putExtra("duplicate", false);
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            sendBroadcast(intent);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("SHORTCUT", "1");
            editor.commit();
        }
    }

    public void setBottomNavigation(String menu) {
        Log.d("??????", "----- setBottomNavigation: " + menu);

        switch (menu) {
            case "HOME":
//                for (int i = 0; i < 4; i++){
//                    bottomNavigationView.getMenu().getItem(i).setChecked(false);
//
//                }

                test.setVisibility(View.GONE);
                fragment_home = new Fit_HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_home, "FRAG_HOME").commitAllowingStateLoss();
                ;
                break;
            case "EXERCISE":
                bottomNavigationView.getMenu().getItem(1).setChecked(true);

                fragment_exercise = new Fit_ExerciseFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_exercise, "FRAG_EXERCISE").commit();
                test.setVisibility(View.VISIBLE);

                break;

            case "PROCEDURE":
                bottomNavigationView.getMenu().getItem(0).setChecked(true);

                fragment_procedure = new Fit_ProcedureFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_procedure, "FRAG_PROCEDURE").commit();
                test.setVisibility(View.VISIBLE);

                break;

//            case "WEIGHT":
//                bottomNavigationView.getMenu().getItem(1).setChecked(true);
//                fragment_weight = new WeightSettingFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_weight, "weight").commit();
//                test.setVisibility(View.VISIBLE);
//
//                break;

//            case "SEAT":
//                bottomNavigationView.getMenu().getItem(0).setChecked(true);
//                bottomNavigationView.setVisibility(View.GONE);
//                fragment_seat = new SeatSettingFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_seat, "seat").commit();
//                test.setVisibility(View.GONE);
//
//                break;

            case "PROCEDURE_DEFAULT":
                fragment_procedure_default = new Fit_ProcedureDefaultFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_procedure_default, "FRAG_PROCEDURE_DEFAULT").commit();
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                test.setVisibility(View.VISIBLE);

                break;

//            case "HEIGHT":
//                fragment_height = new HeightFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment_height, "FRAG_HEIGHT").commit();
//                bottomNavigationView.getMenu().getItem(0).setChecked(true);
//                test.setVisibility(View.VISIBLE);
//                break;


        }
    }

    private void getUserPreset() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Fit_HttpConnect httpConnect = new Fit_HttpConnect();
                Fit_Param param = new Fit_Param();
                param.add("token", Fit_User.token);
                param.add("memberNo", String.valueOf(Fit_User.MemberNo));
                Fit_Address address = new Fit_Address();
                if (httpConnect.httpConnect(param.getParam(), address.getLoadUserPreset()) == 200) {
                    if (!httpConnect.getReceiveMessage().equals("fail")) {
                        Fit_Preset.getPresetJson(getApplicationContext(), httpConnect.getReceiveMessage());
                    }
                }

                Log.d("??????", "response code: " + httpConnect.getResponseCode());
                Log.d("??????", "receive message : " + httpConnect.getReceiveMessage());
            }
        }).start();


    }

    public static void setAudio(String Code) {
        // TODO: 2019-05-10 audioStop() ????????? ??????

        Log.d("??????", "----- setAudio: " + Code);
        Context context = GlobalApplication.getAppContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("sound", MODE_PRIVATE);
        String gender = sharedPreferences.getString("sound", "female");

        SoundPool sound;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            sound = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
        } else {
            sound = new SoundPool(1,         // ?????? ??????????????? ??????
                    AudioManager.STREAM_MUSIC, // ????????? ??????
                    0);        // ?????? - ?????????:0

        }


        if (mp != null && mp.isPlaying()) {
            mp.stop();
//            mp.release();
        }
        final int count[] = new int[16];

        switch (Code) {
            case "please_measure":
                // ?????? ????????? ????????? ????????? ???????????? ????????????. (1???)
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_please_measure);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_please_measure);

                } else if (language.equals("zh")) {

//                    mp = MediaPlayer.create(context, R.raw.tmp_cn_s);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_please_measure);

                }
                break;

            case "chair":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_chiar);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_chiar);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_chair);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_chiar);

                }
                break;
            case "height":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_height);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_height);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_height);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_height);

                }
                break;

            case "weight":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_weight);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_weight);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_weight);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_weight);

                }
                break;


            case "exercise_setting":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_exericse_setting);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exericse_setting);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_exercise_setting);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exericse_setting);

                }
                break;
            case "exercise_start":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_exercise_start);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exercise_start);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_exercise_start);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exercise_start);

                }
                break;
            case "exercise_finish":
                if (language.equals("ko")) {
                    mp = MediaPlayer.create(context, R.raw.female_kr_script_exercise_finish);

                } else if (language.equals("en")) {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exercise_finish);

                } else if (language.equals("zh")) {

                    mp = MediaPlayer.create(context, R.raw.tmp_cn_script_exercise_finish);
                } else {
                    mp = MediaPlayer.create(context, R.raw.female_us_script_exercise_finish);

                }
                CFG_IS_EXERCISE = false;
                break;

            case "count_start":
                // ?????????
                Log.d("??????", "setAudio: countstart");
                final int countStart = sound.load(context, R.raw.count, 0);
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(countStart, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;

            case "count_complete":
                // ?????????
                Log.d("??????", "setAudio: count");
                final int soundCount = sound.load(context, R.raw.count_complete, 0);
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(soundCount, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;

            case "count_progress":
                // ?????????
                Log.d("??????", "setAudio: count");
                final int countProgress = sound.load(context, R.raw.count_progress, 0);
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(countProgress, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;

            case "f1":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[1] = sound.load(context, R.raw.female_kr_1, 0);
                } else if (language.equals("en")) {
                    count[1] = sound.load(context, R.raw.female_us_1, 0);
                } else if (language.equals("zh")) {

                    count[1] = sound.load(context, R.raw.tmp_cn_1, 0);
                } else {
                    count[1] = sound.load(context, R.raw.female_us_1, 0);

                }
                final int count1f = count[1];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count1f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f2":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[2] = sound.load(context, R.raw.female_kr_2, 0);
                } else if (language.equals("en")) {
                    count[2] = sound.load(context, R.raw.female_us_2, 0);
                } else if (language.equals("zh")) {

                    count[2] = sound.load(context, R.raw.tmp_cn_2, 0);
                } else {
                    count[2] = sound.load(context, R.raw.female_us_2, 0);

                }
                final int count2f = count[2];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count2f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f3":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[3] = sound.load(context, R.raw.female_kr_3, 0);
                } else if (language.equals("en")) {
                    count[3] = sound.load(context, R.raw.female_us_3, 0);
                } else if (language.equals("zh")) {

                    count[3] = sound.load(context, R.raw.tmp_cn_3, 0);
                } else {
                    count[3] = sound.load(context, R.raw.female_us_3, 0);

                }
                final int count3f = count[3];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count3f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f4":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[4] = sound.load(context, R.raw.female_kr_4, 0);
                } else if (language.equals("en")) {
                    count[4] = sound.load(context, R.raw.female_us_4, 0);
                } else if (language.equals("zh")) {

                    count[4] = sound.load(context, R.raw.tmp_cn_4, 0);
                } else {
                    count[4] = sound.load(context, R.raw.female_us_4, 0);

                }
                final int count4f = count[4];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count4f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f5":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[5] = sound.load(context, R.raw.female_kr_5, 0);
                } else if (language.equals("en")) {
                    count[5] = sound.load(context, R.raw.female_us_5, 0);
                } else if (language.equals("zh")) {

                    count[5] = sound.load(context, R.raw.tmp_cn_5, 0);
                } else {
                    count[5] = sound.load(context, R.raw.female_us_5, 0);

                }
                final int count5f = count[5];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count5f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f6":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[6] = sound.load(context, R.raw.female_kr_6, 0);
                } else if (language.equals("en")) {
                    count[6] = sound.load(context, R.raw.female_us_6, 0);
                } else if (language.equals("zh")) {

                    count[6] = sound.load(context, R.raw.tmp_cn_6, 0);
                } else {
                    count[6] = sound.load(context, R.raw.female_us_6, 0);

                }
                final int count6f = count[6];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count6f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f7":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[7] = sound.load(context, R.raw.female_kr_7, 0);
                } else if (language.equals("en")) {
                    count[7] = sound.load(context, R.raw.female_us_7, 0);
                } else if (language.equals("zh")) {

                    count[7] = sound.load(context, R.raw.tmp_cn_7, 0);
                } else {
                    count[7] = sound.load(context, R.raw.female_us_7, 0);

                }
                final int count7f = count[7];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count7f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f8":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[8] = sound.load(context, R.raw.female_kr_8, 0);
                } else if (language.equals("en")) {
                    count[8] = sound.load(context, R.raw.female_us_8, 0);
                } else if (language.equals("zh")) {

                    count[8] = sound.load(context, R.raw.tmp_cn_8, 0);
                } else {
                    count[8] = sound.load(context, R.raw.female_us_8, 0);

                }
                final int count8f = count[8];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count8f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f9":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[9] = sound.load(context, R.raw.female_kr_9, 0);
                } else if (language.equals("en")) {
                    count[9] = sound.load(context, R.raw.female_us_9, 0);
                } else if (language.equals("zh")) {

                    count[9] = sound.load(context, R.raw.tmp_cn_9, 0);
                } else {
                    count[9] = sound.load(context, R.raw.female_us_9, 0);

                }
                final int count9f = count[9];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count9f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f10":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[10] = sound.load(context, R.raw.female_kr_10, 0);
                } else if (language.equals("en")) {
                    count[10] = sound.load(context, R.raw.female_us_10, 0);
                } else if (language.equals("zh")) {

                    count[10] = sound.load(context, R.raw.tmp_cn_10, 0);
                } else {
                    count[10] = sound.load(context, R.raw.female_us_10, 0);

                }
                final int count10f = count[10];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count10f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f11":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[11] = sound.load(context, R.raw.female_kr_11, 0);
                } else if (language.equals("en")) {
                    count[11] = sound.load(context, R.raw.female_us_11, 0);
                } else if (language.equals("zh")) {

                    count[11] = sound.load(context, R.raw.tmp_cn_11, 0);
                } else {
                    count[11] = sound.load(context, R.raw.female_us_11, 0);

                }
                final int count11f = count[11];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count11f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f12":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[12] = sound.load(context, R.raw.female_kr_12, 0);
                } else if (language.equals("en")) {
                    count[12] = sound.load(context, R.raw.female_us_12, 0);
                } else if (language.equals("zh")) {

                    count[12] = sound.load(context, R.raw.tmp_cn_12, 0);
                } else {
                    count[12] = sound.load(context, R.raw.female_us_12, 0);

                }
                final int count12f = count[12];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count12f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f13":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[13] = sound.load(context, R.raw.female_kr_13, 0);
                } else if (language.equals("en")) {
                    count[13] = sound.load(context, R.raw.female_us_13, 0);
                } else if (language.equals("zh")) {

                    count[13] = sound.load(context, R.raw.tmp_cn_13, 0);
                } else {
                    count[13] = sound.load(context, R.raw.female_us_13, 0);

                }
                final int count13f = count[13];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count13f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f14":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[14] = sound.load(context, R.raw.female_kr_14, 0);
                } else if (language.equals("en")) {
                    count[14] = sound.load(context, R.raw.female_us_14, 0);
                } else if (language.equals("zh")) {

                    count[14] = sound.load(context, R.raw.tmp_cn_14, 0);
                } else {
                    count[14] = sound.load(context, R.raw.female_us_14, 0);

                }
                final int count14f = count[14];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count14f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "f15":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[15] = sound.load(context, R.raw.female_kr_15, 0);
                } else if (language.equals("en")) {
                    count[15] = sound.load(context, R.raw.female_us_15, 0);
                } else if (language.equals("zh")) {

                    count[15] = sound.load(context, R.raw.tmp_cn_15, 0);
                } else {
                    count[15] = sound.load(context, R.raw.female_us_15, 0);

                }
                final int count15f = count[15];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count15f, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m1":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[1] = sound.load(context, R.raw.male_kr_1, 0);
                } else if (language.equals("en")) {
                    count[1] = sound.load(context, R.raw.male_us_1, 0);
                } else if (language.equals("zh")) {

                    count[1] = sound.load(context, R.raw.tmp_cn_1, 0);
                } else {
                    count[1] = sound.load(context, R.raw.male_us_1, 0);

                }
                final int count1m = count[1];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count1m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m2":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[2] = sound.load(context, R.raw.male_kr_2, 0);
                } else if (language.equals("en")) {
                    count[2] = sound.load(context, R.raw.male_us_2, 0);
                } else if (language.equals("zh")) {

                    count[2] = sound.load(context, R.raw.tmp_cn_2, 0);
                } else {
                    count[2] = sound.load(context, R.raw.male_us_2, 0);

                }
                final int count2m = count[2];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count2m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m3":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[3] = sound.load(context, R.raw.male_kr_3, 0);
                } else if (language.equals("en")) {
                    count[3] = sound.load(context, R.raw.male_us_3, 0);
                } else if (language.equals("zh")) {

                    count[3] = sound.load(context, R.raw.tmp_cn_3, 0);
                } else {
                    count[3] = sound.load(context, R.raw.male_us_3, 0);

                }
                final int count3m = count[3];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count3m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m4":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[4] = sound.load(context, R.raw.male_kr_4, 0);
                } else if (language.equals("en")) {
                    count[4] = sound.load(context, R.raw.male_us_4, 0);
                } else if (language.equals("zh")) {

                    count[4] = sound.load(context, R.raw.tmp_cn_4, 0);
                } else {
                    count[4] = sound.load(context, R.raw.male_us_4, 0);

                }
                final int count4m = count[4];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count4m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m5":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[5] = sound.load(context, R.raw.male_kr_5, 0);
                } else if (language.equals("en")) {
                    count[5] = sound.load(context, R.raw.male_us_5, 0);
                } else if (language.equals("zh")) {

                    count[5] = sound.load(context, R.raw.tmp_cn_5, 0);
                } else {
                    count[5] = sound.load(context, R.raw.male_us_5, 0);

                }
                final int count5m = count[5];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count5m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m6":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[6] = sound.load(context, R.raw.male_kr_6, 0);
                } else if (language.equals("en")) {
                    count[6] = sound.load(context, R.raw.male_us_6, 0);
                } else if (language.equals("zh")) {

                    count[6] = sound.load(context, R.raw.tmp_cn_6, 0);
                } else {
                    count[6] = sound.load(context, R.raw.male_us_6, 0);

                }
                final int count6m = count[6];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count6m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m7":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[7] = sound.load(context, R.raw.male_kr_7, 0);
                } else if (language.equals("en")) {
                    count[7] = sound.load(context, R.raw.male_us_7, 0);
                } else if (language.equals("zh")) {

                    count[7] = sound.load(context, R.raw.tmp_cn_7, 0);
                } else {
                    count[7] = sound.load(context, R.raw.male_us_7, 0);

                }
                final int count7m = count[7];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count7m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m8":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[8] = sound.load(context, R.raw.male_kr_8, 0);
                } else if (language.equals("en")) {
                    count[8] = sound.load(context, R.raw.male_us_8, 0);
                } else if (language.equals("zh")) {

                    count[8] = sound.load(context, R.raw.tmp_cn_8, 0);
                } else {
                    count[8] = sound.load(context, R.raw.male_us_8, 0);

                }
                final int count8m = count[8];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count8m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m9":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[9] = sound.load(context, R.raw.male_kr_9, 0);
                } else if (language.equals("en")) {
                    count[9] = sound.load(context, R.raw.male_us_9, 0);
                } else if (language.equals("zh")) {

                    count[9] = sound.load(context, R.raw.tmp_cn_9, 0);
                } else {
                    count[9] = sound.load(context, R.raw.male_us_9, 0);

                }
                final int count9m = count[9];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count9m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m10":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[10] = sound.load(context, R.raw.male_kr_10, 0);
                } else if (language.equals("en")) {
                    count[10] = sound.load(context, R.raw.male_us_10, 0);
                } else if (language.equals("zh")) {

                    count[10] = sound.load(context, R.raw.tmp_cn_10, 0);
                } else {
                    count[10] = sound.load(context, R.raw.male_us_10, 0);

                }
                final int count10m = count[10];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count10m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m11":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[11] = sound.load(context, R.raw.male_kr_11, 0);
                } else if (language.equals("en")) {
                    count[11] = sound.load(context, R.raw.male_us_11, 0);
                } else if (language.equals("zh")) {

                    count[11] = sound.load(context, R.raw.tmp_cn_11, 0);
                } else {
                    count[11] = sound.load(context, R.raw.male_us_11, 0);

                }
                final int count11m = count[11];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count11m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m12":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[12] = sound.load(context, R.raw.male_kr_12, 0);
                } else if (language.equals("en")) {
                    count[12] = sound.load(context, R.raw.male_us_12, 0);
                } else if (language.equals("zh")) {

                    count[12] = sound.load(context, R.raw.tmp_cn_12, 0);
                } else {
                    count[12] = sound.load(context, R.raw.male_us_12, 0);

                }
                final int count12m = count[12];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count12m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m13":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[13] = sound.load(context, R.raw.male_kr_13, 0);
                } else if (language.equals("en")) {
                    count[13] = sound.load(context, R.raw.male_us_13, 0);
                } else if (language.equals("zh")) {

                    count[13] = sound.load(context, R.raw.tmp_cn_13, 0);
                } else {
                    count[13] = sound.load(context, R.raw.male_us_13, 0);

                }
                final int count13m = count[13];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count13m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m14":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[14] = sound.load(context, R.raw.male_kr_14, 0);
                } else if (language.equals("en")) {
                    count[14] = sound.load(context, R.raw.male_us_14, 0);
                } else if (language.equals("zh")) {

                    count[14] = sound.load(context, R.raw.tmp_cn_14, 0);
                } else {
                    count[14] = sound.load(context, R.raw.male_us_14, 0);

                }
                final int count14m = count[14];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count14m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
            case "m15":
                // ?????????
                Log.d("??????", "setAudio: count");
                if (language.equals("ko")) {
                    count[15] = sound.load(context, R.raw.male_kr_15, 0);
                } else if (language.equals("en")) {
                    count[15] = sound.load(context, R.raw.male_us_15, 0);
                } else if (language.equals("zh")) {
                    count[14] = sound.load(context, R.raw.tmp_cn_15, 0);
                } else {
                    count[15] = sound.load(context, R.raw.male_us_15, 0);

                }
                final int count15m = count[15];
                sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(final SoundPool soundPool, int audio, int status) {

                        soundPool.play(count15m, 100, 100, 0, 0, 1.0f);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                soundPool.release();
                            }
                        }).start();
                    }
                });
                break;
//            case "01":
//                // ???????????? ????????? ?????? ?????? ???????????????. ???????????? ?????? ????????? ?????? ?????? ????????? ????????? ?????????.
//                mp = MediaPlayer.create(context, R.raw.audio_01);
//                break;


        }

        if (!CFG_IS_EXERCISE) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mp.start();

                }
            }).start();
        }


    }

    public static void audioStop() {
        if (mp != null) {
            mp.stop();
//            mp.release();
//            mp = null;
        }
    }


    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothLeScanner mBluetoothLeScanner;
    public ScanCallback mScanCallback;
    public static BluetoothGatt mBluetoothGatt;
    public Handler mHandler;

    public static boolean mScanning;
    public static boolean mConnected;
    public static boolean mEchoInitialized;

    public Map<String, BluetoothDevice> mScanResults;


//    public String getBleStatus() {
//        Log.d("??????", "----- getBleStatus");
//
//        // ???????????? ?????? ??????
//        if (!Constants.CFG_IS_BLUETOOTH) {
//            Constants.CFG_BLE_STATUS_CODE = "00";
//        } else {
//            int IS_ACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//            int IS_ACCESS_FINE_LOCATION = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//
//            // ???????????? ?????? ?????? ??????
//            if (IS_ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED || IS_ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
//
//                // ???????????? ????????? ??????
//                if (mBluetoothAdapter.isEnabled()) {
//                    Constants.CFG_BLE_STATUS_CODE = "03";
//
//                    // ?????? ?????? ??????
//                    if (mConnected) {
//                        Constants.CFG_BLE_STATUS_CODE = "05";
//                    }
//                } else {
//                    Constants.CFG_BLE_STATUS_CODE = "02";
//                }
//            } else {
//                Constants.CFG_BLE_STATUS_CODE = "01";
//            }
//        }
//
//        Log.d("??????", "- Constants.CFG_BLE_STATUS_CODE: " + Constants.CFG_BLE_STATUS_CODE);
//
//        return Constants.CFG_BLE_STATUS_CODE;
//    }

    public boolean isBluetooth() {
        Log.d("??????", "----- isBleProgress");

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

        if (ba != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setEnable() {
        Log.d("??????", "----- setEnable");

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, Fit_Constants.REQUEST_ENABLE_BT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("??????", "----- onActivityResult: " + resultCode);

        switch (requestCode) {
            case Fit_Constants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    if (!mConnected) {
                        fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                        fragment_home.setBleStatus(3);
                    }
                } else {
                    fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                    fragment_home.setBleStatus(2);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    BleAdapter bleAdapter;

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Fit_MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ble_list, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView cancel = view.findViewById(R.id.dialog_ble_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                fragment_home.setBleStatus(3);
                setScanStop();
                mScanning = false;
                dialog.dismiss();

            }
        });
        final ImageView retry = view.findViewById(R.id.dialog_ble_retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mScanning) {
                    setScanStart(true);
                }
//                else {
//                    Toast.makeText(ChartActivity.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        ListView bleList = view.findViewById(R.id.dialog_ble_listView);
        bleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (bleItems.get(position).getName().contains("SMARTNECK1")) {
//                    Constants.CFG_DEVICE_TYPE = "FIT";
//                } else if (bleItems.get(position).getName().contains("SMARTNECK2")) {
//                    Constants.CFG_DEVICE_TYPE = "HOM";
//                }
                setConnect(bleItems.get(position).getBluetoothDevice());
                setScanStop();
                dialog.dismiss();
            }
        });
        bleAdapter = new BleAdapter(bleItems, getLayoutInflater());
        bleList.setAdapter(bleAdapter);


        dialog.setCancelable(false);
        dialog.show();
    }

    public void setScanStart(boolean isDialog) {
        Log.d("??????", "----- setScanStart");
        bleItems.clear();
        fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
        fragment_home.setBleStatus(4);

        if (mScanning == true) {
            return;
        }

        mScanResults = new HashMap<>();
        mScanCallback = new BleScanCallback(mScanResults);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(mScanCallback);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("??????", "- mHandler.postDelayed run");

                if (!mConnected) {
                    setScanStop();

                    fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                    fragment_home.setBleStatus(6);
                }
            }
        }, Fit_Constants.SCAN_PERIOD);
        if (!isDialog) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showAlertDialog();

                        }
                    });
                }
            }).start();
        }

        mScanning = true;
    }

    public void setScanStop() {
        Log.d("??????", "----- setScanStop");

        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);

            // Scan Complete
            if (mScanResults.isEmpty()) {
                return;
            }
        }
        fragment_home.setBleStatus(4);

        mScanCallback = null;
        mScanning = false;
        mHandler = null;
    }

    public void setConnect(BluetoothDevice device) {
        Log.d("??????", "----- setConnect: " + device.getName().replace(" ", "") + " / " + device.getAddress());
        GattClientCallback gattClientCallback = new GattClientCallback();
        mBluetoothGatt = device.connectGatt(this, true, gattClientCallback);
//        mBluetoothGatt.requestMtu(60);
        if (device.getName().contains("SMARTNECK1")) {
            Fit_Constants.deviceType = "FIT";
        } else if (device.getName().contains("SMARTNECK2")) {
            Fit_Constants.deviceType = "HOM";
            Log.d("??????", " - - - - - - - - - - Current Device Type -> HOM");
        }
        DEVICE_NAME = device.getName().substring(9);
        Log.d("??????", " - - - - - DEVICE_NAME: " + DEVICE_NAME.substring(9) + " / DEVICE_TYPE: " + Fit_Constants.deviceType);
        final Fit_ProgressDialog progressDialog = new Fit_ProgressDialog(mContext, getLayoutInflater());
        progressDialog.show(getString(R.string.dialog_ble_connecting));
        new Thread(new Runnable() {
            @Override
            public void run() {
                float count = 0;
                while (!isBleProgress) {
                    count += 0.5f;
                    if (count == 20) {
                        progressDialog.dismiss();
                        ((Fit_MainActivity) Fit_MainActivity.mContext).setScanStop();
                        isBleProgress = true;
                    }
                    Log.d("??????", "isBleProgress: " + isBleProgress + "sec: " + count);
                    try {
                        Thread.sleep(499);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                progressDialog.dismiss();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isBleProgress = false;
            }
        }).start();
    }

    public void setDisconnect() {
        Log.d("??????", "----- setDisconnect");

        mConnected = false;
        mEchoInitialized = false;
        isBleProgress = false;
        mScanning = false;
        if (mBluetoothGatt != null) {
            //((ChartActivity)ChartActivity.mContext).setMessage(StringUtils.getCommand("44 3A 01"));

            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();

        }

//        if (Constants.CFG_FRAG_MODE == 0) {
        if (fragment_home != null) {
            fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
            fragment_home.setBleStatus(3);
        }

//        }
    }

    public static void setMessage(String message) {
        Log.d("??????", "----- setMessage: " + message);

        if (!mConnected || !mEchoInitialized) {
            Log.d("??????", "- mConnected: " + mConnected);
            Log.d("??????", "- mEchoInitialized: " + mEchoInitialized);

            return;
        }

        message = message.replaceAll(" ", "");

        byte[] messageBytes = Fit_StringUtils.hexStringToByteArray(message);

        if (messageBytes.length == 0) {
            Log.d("??????", "- setMessage: Unable to convert message to bytes.");

            return;
        }

        BluetoothGattService service = mBluetoothGatt.getService(Fit_Constants.CFG_CHARACTERISTIC_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic_write = service.getCharacteristic(Fit_Constants.CFG_CHARACTERISTIC_WRITE_UUID);

        characteristic_write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic_write.setValue(messageBytes);

        boolean success = mBluetoothGatt.writeCharacteristic(characteristic_write);

        if (success) {
//            Log.d("??????", "??? Sent Message: " + Fit_StringUtils.byteArrayInHexFormat(messageBytes));
        } else {
//            Log.d("??????", "??? Sent Message: Failed to write dat.");
        }
    }

    public static void setMessage(byte[] commend) {
        Log.d("??????", "----- sendCommend: " + commend);

        if (!mConnected || !mEchoInitialized) {
            Log.d("??????", "- mConnected: " + mConnected);
            Log.d("??????", "- mEchoInitialized: " + mEchoInitialized);

            return;
        }


        if (commend.length == 0) {
            Log.d("??????", "- setMessage: Unable to convert message to bytes.");

            return;
        }

        BluetoothGattService service = mBluetoothGatt.getService(Fit_Constants.CFG_CHARACTERISTIC_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic_write = service.getCharacteristic(Fit_Constants.CFG_CHARACTERISTIC_WRITE_UUID);

        characteristic_write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic_write.setValue(commend);

        boolean success = mBluetoothGatt.writeCharacteristic(characteristic_write);

        if (success) {
//            Log.d("??????", "??? Sent Message: " + Fit_StringUtils.byteArrayInHexFormat(commend));
        } else {
//            Log.d("??????", "??? Sent Message: Failed to write dat.");
        }
    }

    int sentCount = 0;

    public void setDeviceInit() {
        if (mConnected || mEchoInitialized) {

            final ProgressDialog progressDialog = new ProgressDialog(mContext, getLayoutInflater());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show(getString(R.string.dialog_ble_init));

                            Log.e("Go_EXERCISE","seat : "+Fit_Preset.seat+",\n setup : "+Fit_Preset.setup + ",\n weight : " + Fit_Preset.measureSetup);
                        }
                    });

                }
            }).start();

            //todo GO_EXERCISE ????????? ??????
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    setMessage(new Fit_Commend().sendGoExercise((byte) Fit_Preset.seat, (byte) Fit_Preset.setup));
//                    Log.e("Go_EXERCISE","seat : "+Fit_Preset.seat+", setup"+Fit_Preset.setup);
                    try {
                        while (true) {
                            Thread.sleep(300);
                            setMessage(new Fit_Commend().sendGoExercise((byte) Fit_Preset.seat, (byte) Fit_Preset.setup));
                            setMessage(new Fit_Commend().sendWeightMove((byte) Fit_Preset.measureSetup));
                            Log.e("??????", isWeightMove + "" + isSeatMove);
                            if (isWeightMove == false && isSeatMove == false) {
                                SettingOn = false;
                                Log.e("??????1", isWeightMove + "" + isSeatMove);
                                progressDialog.dismiss();
                                break;
                            } else {
                                Log.e("??????2", isWeightMove + "" + isSeatMove);
                                SettingOn = true;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }


    public void enableCharacteristicNotification(BluetoothGatt
                                                         gatt, BluetoothGattCharacteristic characteristic) {
        Log.d("??????", "----- enableCharacteristicNotification: " + characteristic.getUuid().toString());

        boolean success = gatt.setCharacteristicNotification(characteristic, true);

        if (success) {
            Log.d("??????", "- Characteristic notification set successfully for " + characteristic.getUuid().toString());

            if (Fit_BluetoothUtils.isEchoCharacteristic(characteristic)) {
                mEchoInitialized = true;
            }
        } else {
            Log.d("??????", "- Characteristic notification set failure for " + characteristic.getUuid().toString());
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Fit_Constants.CFG_CHARACTERISTIC_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    public static boolean isBleProgress;
    public static boolean isExProgress;
    public static boolean isHeiProgress;
    public static boolean isWeiProgress;
    static boolean isInit;

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();

        String CFG_RECEIVED_RAW = Fit_StringUtils.byteArrayInHexFormat2(bytes).replace("0x", "");
//        Log.d("??????", "??? CFG_RECEIVED_RAW: " + CFG_RECEIVED_RAW);

        String[] CFG_RECEIVED = CFG_RECEIVED_RAW.split(" ");


        if (CFG_RECEIVED[1].equals("3a")) {
            protocolType = "3a";
//            Log.d("??????", "readCharacteristic: Protocol Type = 3a");

            if (isMeasure) {
                ((Fit_MeasureActivity) Fit_MeasureActivity.mContext).setMeasure(CFG_RECEIVED);
            }


            if (CFG_RECEIVED[2].equals("84")) {

                if (CFG_RECEIVED[5].equals("00")) {
                    isSeatMove = false;
                } else if (CFG_RECEIVED[5].equals("01")) {
                    isSeatMove = true;
                }

                if (CFG_RECEIVED[6].equals("00")) {
                    isWeightMove = false;
                } else if (CFG_RECEIVED[6].equals("01")) {
                    isWeightMove = true;
                }

                if (tabPos == 0) {
                    if (!isClick) {
                        Fit_Preset.seat = Integer.parseInt(CFG_RECEIVED[3], 16);

                    } else {
                        if (CFG_RECEIVED[5].equals("00")) {
                            isClick = false;
                        }
                    }
                }
                if (tabPos == 1) {
//                if (!MeasureActivity.isHeight) {
//
//                }
//                Log.d("??????", "tabPos: " + tabPos);

//                    Log.d("??????", "isMove: " + isMove + "/ weight: " + Integer.parseInt(CFG_RECEIVED[8], 16) + " / current: " + currentWeight);
//
//                    if (isMove && currentWeight * 10 == Integer.parseInt(CFG_RECEIVED[8], 16)) {
//                        isMove = false;
//                        Log.d("??????", "isMove: " + isMove);
//                    }
//
                    CFG_HEIGHT[1] = Integer.parseInt(CFG_RECEIVED[4], 16);
                    CFG_WEIGHT[1] = Integer.parseInt(CFG_RECEIVED[8], 16);


                    if (CFG_WEIGHT[1] >= CFG_WEIGHT_MAX[1] && CFG_HEIGHT[1] > 0)
                        CFG_WEIGHT_MAX[1] = CFG_WEIGHT[1];



                    if (!isClick && !isWeightMove && !isSeatMove) {

                        Fit_Preset.setup = Integer.parseInt(CFG_RECEIVED[4], 16);


                    } else {
                        if (CFG_RECEIVED[6].equals("00")) {
                            isClick = false;
                        }
                    }
                }


//            Log.d("??????", "isSeatMove: " + isSeatMove);
//            Log.d("??????", "isWeightMove: " + isWeightMove);
//                Log.d("??????", "readCharacteristic: seat->" + Fit_Preset.seat);
            }

            if (!is_82 && CFG_RECEIVED[2].equals("82")) {
                is_82 = true;
            }

            if (!isExProgress && CFG_RECEIVED[2].equals("82") && !isMeasure) {
                isExProgress = true;
            }
            if (!isWeiProgress && CFG_RECEIVED[2].equals("82") && isMeasure) {
                isWeiProgress = true;
            }

            if (isWeight && CFG_RECEIVED[2].equals("82")) {
//                Log.d("??????", "isMove: " + isMove + "/ weight: " + Integer.parseInt(CFG_RECEIVED[8], 16) + " / current: " + currentWeight);

                if (isMove && currentWeight * 10 == Integer.parseInt(CFG_RECEIVED[8], 16)) {
                    isMove = false;
//                    Log.d("??????", "isMove: " + isMove);
                }

                CFG_HEIGHT[1] = Integer.parseInt(CFG_RECEIVED[4], 16);
                CFG_WEIGHT[1] = Integer.parseInt(CFG_RECEIVED[8], 16);

                if (CFG_WEIGHT[1] >= CFG_WEIGHT_MAX[1] && CFG_HEIGHT[1] > 0)
                    CFG_WEIGHT_MAX[1] = CFG_WEIGHT[1];
            }


            if (isWeightMove && CFG_RECEIVED[2].equals("83")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isWeightMove = false;
                    }
                }).start();
                return;
            }

            if (isMain && !isInit && CFG_RECEIVED[2].equals("84") && !isMeasure) {
                if (CFG_RECEIVED[5].equals("00") && CFG_RECEIVED[6].equals("00")) {

                    isInit = true;
                }

            }


            if (tabPos == 0) {
                Fit_HeightFragment frag = (Fit_HeightFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                frag.setHeight(CFG_RECEIVED);
            }
            if (tabPos == 1) {
//                if (!MeasureActivity.isHeight) {
//
//                }
//                Log.d("??????", "tabPos: " + tabPos);
//                    Log.d("??????", "isMove: " + isMove + "/ weight: " + Integer.parseInt(CFG_RECEIVED[8], 16) + " / current: " + currentWeight);

                if (isMove && currentWeight * 10 == Integer.parseInt(CFG_RECEIVED[8], 16)) {
                    isMove = false;
//                        Log.d("??????", "isMove: " + isMove);
                }

                CFG_HEIGHT[1] = Integer.parseInt(CFG_RECEIVED[4], 16);
                CFG_WEIGHT[1] = Integer.parseInt(CFG_RECEIVED[8], 16);

                if (CFG_WEIGHT[1] >= CFG_WEIGHT_MAX[1] && CFG_HEIGHT[1] > 0)
                    CFG_WEIGHT_MAX[1] = CFG_WEIGHT[1];
            }

            if (tabPos == 3) {

                Fit_ExerciseFragment frag = (Fit_ExerciseFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                frag.setExercise(CFG_RECEIVED);

            }
        } else if (CFG_RECEIVED[1].equals("3b")) {
            protocolType = "3b";
//            Log.d("??????", "readCharacteristic: Protocol Type = 3b");
            if (isMeasure) {
                ((Fit_MeasureActivity) Fit_MeasureActivity.mContext).setMeasure(CFG_RECEIVED);
            }

            //0 - 44
            //1 - 3B
            //2 - 82
            //3 - Current Seat
            //4 - is Seat Move
            //5 - Current Weight
            //6 - is Weight Move
            //7 - Weight Setup
            //8 - Height
            //9 - Check Sum

            if (CFG_RECEIVED[2].equals("82")) {
                if (!is_82) is_82 = true;
                CFG_WEIGHT[1] = Integer.parseInt(CFG_RECEIVED[5], 16);


                if (CFG_RECEIVED[4].equals("00")) {
                    isSeatMove = false;
                } else if (CFG_RECEIVED[4].equals("01")) {
                    isSeatMove = true;
                }

                if (CFG_RECEIVED[6].equals("00")) {
                    isWeightMove = false;
                } else if (CFG_RECEIVED[6].equals("01")) {
                    isWeightMove = true;
                }

                if (tabPos == 0) {
                    if (!isClick) {
                        Fit_Preset.seat = Integer.parseInt(CFG_RECEIVED[3], 16);

                    } else {
                        if (CFG_RECEIVED[4].equals("00")) {
                            isClick = false;
                        }
                    }
                } else if (tabPos == 1) {

                    if (!isClick && !isWeightMove && !isSeatMove) {

                        Fit_Preset.setup = Integer.parseInt(CFG_RECEIVED[7], 16);


                    } else {
                        if (CFG_RECEIVED[6].equals("00")) {
                            isClick = false;
                        }
                    }
                }


            }


            if (!isExProgress && !isMeasure) {
                isExProgress = true;
            }
            if (!isWeiProgress && isMeasure) {
                isWeiProgress = true;
            }

            if (isWeight) {
//                Log.d("??????", "isMove: " + isMove + "/ weight: " + Integer.parseInt(CFG_RECEIVED[8], 16) + " / current: " + currentWeight);

                if (isMove && currentWeight * 10 == Integer.parseInt(CFG_RECEIVED[5], 16)) {
                    isMove = false;
//                    Log.d("??????", "isMove: " + isMove);
                }

                CFG_HEIGHT[1] = Integer.parseInt(CFG_RECEIVED[8], 16);
                CFG_WEIGHT[1] = Integer.parseInt(CFG_RECEIVED[5], 16);

                if (CFG_WEIGHT[1] >= CFG_WEIGHT_MAX[1] && CFG_HEIGHT[1] > 0)
                    CFG_WEIGHT_MAX[1] = CFG_WEIGHT[1];
            }


            if (isWeightMove && CFG_RECEIVED[2].equals("83")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isWeightMove = false;
                    }
                }).start();
                return;
            }

            if (isMain && !isInit && !isMeasure) {
                if (CFG_RECEIVED[4].equals("00") && CFG_RECEIVED[6].equals("00")) {

                    isInit = true;
                }

            }


            if (tabPos == 0) {
                //                ((ChartActivity)ChartActivity.mContext).setConnect(bleItems.get(position).getBluetoothDevice());

                ((Fit_AfterMeasureActivity) Fit_AfterMeasureActivity.mContext).setHeight(CFG_RECEIVED);
//                HeightFragment frag = (HeightFragment) getSupportFragmentManager().findFragmentById(R.id.frame_measure_container);
//                frag.setHeight(CFG_RECEIVED);

            }

            if (tabPos == 3) {


                Fit_ExerciseFragment frag = (Fit_ExerciseFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                assert frag != null;
                frag.setExercise(CFG_RECEIVED);

            }
        }

    }

    public static ArrayList<BleItem> bleItems = new ArrayList();

    public class BleScanCallback extends ScanCallback {
        private Map<String, BluetoothDevice> mScanResults;


        BleScanCallback(Map<String, BluetoothDevice> scanResults) {
            mScanResults = scanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // Log.d(Constants."??????", "- BleScanCallback onScanResult");

            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Log.d(Constants."??????", "- BleScanCallback onBatchScanResults");

            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("??????", "- BleScanCallback onScanFailed: " + errorCode);
        }


        private void addScanResult(ScanResult result) {

            Log.d("??????", "- BleScanCallback addScanResult: " + result.getDevice().getAddress() + " / " + result.getDevice().getName() + " size : " + mScanResults.size());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    bleAdapter.notifyDataSetChanged();
                }
            });
            BluetoothDevice device = result.getDevice();

            mScanResults.put(device.getAddress(), device);

            if (bleItems.size() == 0 && device.getName() != null) {
                // TODO: 2019-10-18 Smartneck 1 , 2
                if (device.getName().contains("SMARTNECK1") || device.getName().contains("SMARTNECK2")) {

                    bleItems.add(new BleItem(device, device.getAddress(), device.getName()));
                }
            }

            for (int i = 0; i < bleItems.size(); i++) {

                if (device.getName() != null) {
                    if (device.getName().contains("SMARTNECK1") || device.getName().contains("SMARTNECK2")) {
                        for (int j = 0; j < bleItems.size(); j++) {
                            if (bleItems.get(j).getName().equals(device.getName())) {
                                return;
                            }
                        }
                        bleItems.add(new BleItem(device, device.getAddress(), device.getName()));

                    }
                }

            }

            for (int i = 0; i < bleItems.size(); i++) {
                Log.d("??????", "addScanResult: ArrayList" + bleItems.get(i).getAddress());
            }


        }
    }

    public class GattClientCallback extends BluetoothGattCallback {
        Timer mRssiTimer;

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //???????????? ?????? ???????????? ???????????? ?????? ??????
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.d("??????", String.format("BluetoothGatt ReadRssi[%d]", rssi));
//                if (rssi < rssiDistance) {
//                    Log.d("??????", "disconnect Device for rssi : " + rssi);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????\n ????????? ???????????????.", Toast.LENGTH_SHORT).show();
//                                    // TODO: 2019-05-21 rssi ?????? ?????? ?????? ??? ????????????
//                                    //setDisconnect();
//                                }
//                            });
//                        }
//                    }).start();
//
//                }

            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            // Log.d(Constants."??????", "----- GattClientCallback onConnectionStateChange: " + status + " / " + newState);

            super.onConnectionStateChange(gatt, status, newState);

            // TODO: 2019-05-22 ????????? bleprogress ??????  ??? initprogress ??????
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("??????", "----- GattClientCallback onConnectionStateChange BluetoothProfile.STATE_CONNECTED: " + newState);
                mConnected = true;
                isBleProgress = true;


                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        mBluetoothGatt.readRemoteRssi();
                    }
                };
                mRssiTimer = new Timer();
                mRssiTimer.schedule(task, 1000, 1000);
                fragment_home = (Fit_HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);
                fragment_home.setBleStatus(5);


                setDeviceInit();
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("??????", "----- GattClientCallback onConnectionStateChange BluetoothProfile.STATE_DISCONNECTED: " + newState);

                setDisconnect();
                return;
            }

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d("??????", "----- GattClientCallback onConnectionStateChange BluetoothGatt.GATT_FAILURE: " + status);

                setDisconnect();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("??????", "----- GattClientCallback onConnectionStateChange BluetoothGatt.!GATT_SUCCESS: " + status);

                setDisconnect();
                return;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("??????", "----- GattClientCallback onServicesDiscovered: " + status);

            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }

            BluetoothGattService service = mBluetoothGatt.getService(Fit_Constants.CFG_CHARACTERISTIC_SERVICE_UUID);
            BluetoothGattCharacteristic characteristic_notification = service.getCharacteristic(Fit_Constants.CFG_CHARACTERISTIC_NOTIFICATION_UUID);

            enableCharacteristicNotification(gatt, characteristic_notification);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d("??????", "----- GattClientCallback onDescriptorWrite: " + status);

            BluetoothGattCharacteristic characteristic = gatt.getService(Fit_Constants.CFG_CHARACTERISTIC_SERVICE_UUID).getCharacteristic(Fit_Constants.CFG_CHARACTERISTIC_WRITE_UUID);
            characteristic.setValue(new byte[]{1, 1});
            gatt.writeCharacteristic(characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // Log.d(Constants."??????", "----- GattClientCallback onCharacteristicWrite");
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d("??????", "onCharacteristicWrite: " + mConnected + " / " + mEchoInitialized);

            } else {
                setDisconnect();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("??????", "----- GattClientCallback onCharacteristicRead: " + status);

            super.onCharacteristicRead(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("??????", "----- GattClientCallback onCharacteristicRead BluetoothGatt.GATT_SUCCESS");

                readCharacteristic(characteristic);
            } else {
                setDisconnect();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Log.d(Constants."??????", "----- GattClientCallback onCharacteristicChanged: " + characteristic.getUuid().toString());

            super.onCharacteristicChanged(gatt, characteristic);

            readCharacteristic(characteristic);
        }
    }


}