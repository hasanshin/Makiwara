package ru.i_fighter.makiwara1;


import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Camera;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.hardware.camera2.CameraMetadata.FLASH_MODE_OFF;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.getExponent;
import static java.lang.Math.sqrt;
import static ru.i_fighter.makiwara1.SettingsActivity.EXTRA_ADDRESS;
//import static java.lang.Math.toIntExact;



public class PowerActivity extends AppCompatActivity {
//-------------------------------------------------------------------
//ОБЪЯВЛЯЕМ ПЕРЕМЕННЫЕ
//----------------------------------------------------------------
private BluetoothDevice mDevice;
int packet_tick = 0;
    TextView txtPower, txtTimePower, txtReaction, txtKickPower;
    TextView txtTimeOfPower, txtWordReaction, txtDimensReaction, txtProm;
    TextView txtElapsTime;
//-----------------------------------------------------------------------------
    final int MAX_STREAMS = 5;
    //final String LOG_TAG = "myLogs";
    SoundPool sp;
    //int soundIdShot;
    int soundIdExplosion = 0;
    int soundSirena = 0;
//------------------------------------------------------------------
// FLASH
 //------------------------------------------------------------
private android.hardware.Camera camera;
    private boolean isFlashOn;
    //private boolean hasFlash;
    android.hardware.Camera.Parameters params;
 //-------------------------------------------------------------
    ImageButton imgButtonLink;
//------------------------------------------------------------------------
    Animation anim = null;
//-----------------------------------------------------------------------------------
    String address = null;
//---------------------------------------------------------------------------------
   //Button btnOnLed, btnOffLed;
   Button btnStart, btnStop, btnRes, btnReactionOff, btnReactionOn;
   Button btnS, btnE;
//-------------------------------------------------------------------------------------
   //Switch switchReaction;
//-------------------------------------------------------------------------------------
     int state_timer = 0;
    int number_punch = 0;
    int PunchPowerTimer = 0;
    int state_btn_reaction = 3;
    //int PunchPowerTimerSumma = 0;
    double PunchPowerTimerSumma = 0;
    int TimeOfPunchPowerTimer = 0;
    int TimeOfPunchPowerTimerSumma = 0;

    //long Delta = 0;
    //int int_Delta = 0;

    double[] PunchPowerTimerMatrix = new double[5000];
    //int[] IntPunchPowerTimerMatrix = new int[5000];


    int[] TimeOfPunchPowerTimerMatrix = new int[5000];


    double[] TimeOfReaction = new double[5000];
    //int[] IntTimeOfReaction = new int[5000];
    double PunchPowerTimerAverage = 0;

    double TimeOfReactionAver = 0;
    //int IntTimeOfReactionAver = 0;
    int TimeOfPunchPowerTimerAverage = 0;
    int k = 0;
    int p = 0;

    long elapsedMillis = 0;
    //int int_elapsedMillis = 0;

    long ReactionelapsedMillis = 0;
    long RelapsedMillis = 0;
    //long ReactionelapsedMillis_2 = 0;
    //long ReactionelapsedMillis_3 = 0;

    //int ReactionelapsedMillis = 0;

    long timer1_tick = 0;
    //int int_timer1_tick = 0;

    int number_bytes = 0;
    int readBufTemp1 = 0;
    byte readBufTemp2 = 0;
    byte b0 =0;
    byte b1 =0;
    byte b2 =0;
    byte b3 =0;
    byte b4 =0;
    byte b5 =0;
    byte b6 =0;
    byte b7 =0;
    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    double PunchPowerTimerDouble = 0;
 //--------------------------------------------------
 //
 //---------------------------------------------------
    int state_punch = 0;

//--------------------------------------------------------------------------------------
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //BluetoothSocket mmSocket;
    ConnectBT connectBT;
    private OutputStream mmOutStream;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //static final UUID myUUID = UUID.fromString  ("0000fe41-8e22-4541-9d4c-21edae82ed19");
    final int RECIEVE_MESSAGE = 1;        // Статус для Handler
    private ConnectedThread mConnectedThread;
    private Chronometer mChronometer;
    static Handler h;

//---------------------------------------------------------------------------------------
    public static int maxValueGraf = 0;
    public static double[] KickPowerGraf = new double[100];
    //public static int[] IntKickPowerGraf = new int[100];

    public static int[] KickTimePowerGraf = new int [100];
//-------------------------------------------------------------------------------
//display parameters
//-------------------------------------------------------------------------
    int width_display;
    int hight_display;
    public static double display_diagonal = 0;
//-----------------------------------------------------------------------
    //long state_chroonometer1 = 1;
    //long state_chroonometer2 = 0;
    Timer timer;
    int random_timer = 0;


    static final String TAG = "MyApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        // открываем подключение к БД
        db = new DB(this);
        db.open();
// получаем курсор
        cursor = db.getAllData();
        startManagingCursor(cursor);

        // формируем столбцы сопоставления
        //String[] from = new String[] { DB.COLUMN_IMG, DB.COLUMN_TXT };
        //int[] to = new int[] { R.id.textViewDate, R.id.tvTextResults };

//--------------------------------------------------------------------------
// НАХОДИМ ЭЛЕМЕНТЫ
//---------------------------------------------------------------------------
        //txtUpPower = (TextView)findViewById(R.id.txtUpPower);
        //txtViewCharge = (TextView)findViewById(R.id.txtViewCharge);

        txtKickPower = (TextView)findViewById(R.id.txtKickPower);
        txtPower = (TextView)findViewById(R.id.txtPower);
        //txtDimensPower = (TextView)findViewById(R.id.txtDimensPower);
        txtElapsTime = (TextView)findViewById(R.id.txtElapsTime);

        txtTimeOfPower = (TextView)findViewById(R.id.txtTimeOfPower);
        txtTimePower = (TextView)findViewById(R.id.txtTimePower);
        //txtKickSpeed = (TextView)findViewById(R.id.txtKickSpeed);

        //txtDownTimePower = (TextView)findViewById(R.id.txtDownTimePower);
        txtWordReaction = (TextView)findViewById(R.id.txtWordReaction);
        txtReaction = (TextView)findViewById(R.id.txtReaction);
        txtDimensReaction = (TextView)findViewById(R.id.txtDimensReaction);
        txtProm = (TextView)findViewById(R.id.txtProm);

//-------------------------------------------------------------------------------
        imgButtonLink = (ImageButton)findViewById(R.id.imgButtonLink);
        //imgBtnHelp = (ImageButton)findViewById(R.id.imgBtnHelp);
        //imgBtnChrono = (ImageButton)findViewById(R.id.imgBtnChrono);
        //imgBtnResults = (ImageButton)findViewById(R.id.imgBtnResults);
//--------------------------------------------------------------------------------
        btnRes = (Button)findViewById(R.id.btnRes);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        btnReactionOff = (Button)findViewById(R.id.btnReactionOff);
        btnReactionOn = (Button)findViewById(R.id.btnReactionOn);

        btnE = (Button)findViewById(R.id.btnE);
        btnS = (Button)findViewById(R.id.btnS);
//--------------------------------------------------------------
        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundIdExplosion = sp.load(PowerActivity.this,R.raw.explosion,1);
        soundSirena = sp.load(PowerActivity.this,R.raw.sirena,1);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                //sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
            }
        });

        //soundIdExplosion = sp.load(this, R.raw.explosion, 1);
        //soundGetReady = sp.load(this, R.raw.be_ready_russian, 1);
       // soundGetReady = sp.load(this, R.raw.get_ready_and_start, 1);
//--------------------------------------------------------------------------------
        //switchReaction = (Switch) findViewById(R.id.switchReaction);
//--------------------------------------------------------------------------------
        mChronometer = (Chronometer) findViewById(R.id.chronometer5);
        //btnOffLed = (Button)findViewById(R.id.btnOffLed);
        //btnOnLed = (Button)findViewById(R.id.btnOnLed);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                 elapsedMillis = SystemClock.elapsedRealtime()- mChronometer.getBase();


                /*if (elapsedMillis > 5000) {
                    String strElapsedMillis = "Прошло больше 5 секунд";
                    Toast.makeText(getApplicationContext(),
                            strElapsedMillis, Toast.LENGTH_SHORT)
                            .show();
                }*/
            }
        });
//-----------------------------------------------------------------------------
// узнаем размеры экрана из класса Display устанавливаем отступы
//------------------------------------------------------------------------------

        display_adapter();

//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ imgButtonLink
//------------------------------------------------------------------------
             imgButtonLink.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                     imgButtonLink.startAnimation(anim);
                     Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kickpower.ru"));
                     startActivity(browser);
                 }
             });
//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ imgBtnHelp
//------------------------------------------------------------------------
        /*imgBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                imgBtnHelp.startAnimation(anim);
                //Intent i = new Intent(PowerActivity.this, HelpActivity.class);
                //startActivity(i);
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kickpower.ru/help"));
                startActivity(browser);

            }
        });*/
//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnS
//------------------------------------------------------------------------
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(state_timer == 2 ){
                    //ReactionelapsedMillis = elapsedMillis;
                    ReactionelapsedMillis = SystemClock.elapsedRealtime();
                    String kk = Long.toString(ReactionelapsedMillis);
                    txtProm.setText(kk);
                }*/
                Intent i = new Intent(PowerActivity.this, LapaActivity.class);
                startActivity(i);
            }
        });
        //---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnE
//------------------------------------------------------------------------
        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state_timer == 2 ){
                    //ReactionelapsedMillis = elapsedMillis - ReactionelapsedMillis;
                    //ReactionelapsedMillis = elapsedMillis;

                    long ReactionelapsedMillis1 = SystemClock.elapsedRealtime();
                    ReactionelapsedMillis = ReactionelapsedMillis1 - ReactionelapsedMillis;
                    String kk = Long.toString(ReactionelapsedMillis);
                    txtElapsTime.setText(kk);
                    //ReactionelapsedMillis = 0;
                }
            }
        });
 //---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ imgBtnHelp
//------------------------------------------------------------------------
        /*imgBtnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                imgBtnResults.startAnimation(anim);
                //turnOffLed();
                Intent u = new Intent(PowerActivity.this, ResActivity.class);
                startActivity(u);
            }
        });*/
//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnRes
//------------------------------------------------------------------------
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                btnRes.startAnimation(anim);
                //turnOffLed();
                Intent u = new Intent(PowerActivity.this, ResActivity.class);
                startActivity(u);
            }
        });

//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnStart
//------------------------------------------------------------------------
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
               // anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                //btnStart.startAnimation(anim);
                btnStart.setVisibility(View.INVISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();

                //------------------------------------------------
                //
                //--------------------------------------



                    /*if (address!=null) */state_timer = 2;
                state_punch = 0;
                if (address==null){
                    Toast.makeText(getApplicationContext(), R.string.txtToastBtSettings, Toast.LENGTH_SHORT).show();

                }
                number_punch = 0;
                PunchPowerTimer = 0;
                PunchPowerTimerSumma = 0;
                TimeOfPunchPowerTimer = 0;
                TimeOfPunchPowerTimerSumma = 0;
                TimeOfReactionAver = 0;
                for (k = 0; k<5000; k++) PunchPowerTimerMatrix[k] = 0;
                for (k = 0; k<5000; k++) TimeOfPunchPowerTimerMatrix[k] = 0;
                for (k = 0; k<100; k++) KickPowerGraf[k] = 0;
                p = 0;

                //String t = Integer.toString(state_timer);

                if(state_btn_reaction==2){

                    //sp.play(soundIdExplosion, 1, 1, 0, 1, 1);

                   // timeReactionFunc();
                    //timer1_tick = 0;
                      timer = new Timer();
                      final TimerTask task = new TimerTask() {
                          @Override
                          public void run() {
                              //Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kickpower.ru"));
                              //startActivity(browser);
                              getCamera();
                              //while(state_timer==2)  sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                              //if(state_timer==2)  sp.play(soundSirena, 1, 1, 0, 0, 1);
                              turnOnFlash();
                              //timer1();
                              //ReactionelapsedMillis = elapsedMillis;
                              RelapsedMillis=SystemClock.elapsedRealtime();


                          }
                      };

                      final Random random = new Random();
                      random_timer = random.nextInt(5000);
                      timer.schedule(task, (2000 + random_timer));



                }




            }
        });



//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnStop
//------------------------------------------------------------------------
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
                //btnStop.startAnimation(anim);
                btnStop.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                mChronometer.stop();
                state_timer = 3;
                state_punch = 1;
turnOffFlash();
                if (address==null){
                    //Toast.makeText(getApplicationContext(), R.string.txtToastBtSettings, Toast.LENGTH_SHORT).show();
                    /*PunchPowerTimer =200;
                    PunchPowerTimerDouble = 3.923 + PunchPowerTimer * 1.055;
                    String strPunchPower = new DecimalFormat("#0.00").format(PunchPowerTimerDouble);

                    //String strPunchPower = Double.toString(PunchPowerTimerDouble);
                    txtPower.setText(strPunchPower);*/
                    //KickPowerGraf[0]=14646;
                }
                if (address!=null) {
                    //int PunchPowerTimerMax = 0;
                    double PunchPowerTimerMax = 0;
                    int TimeOfPunchPowerTimerMin = TimeOfPunchPowerTimerMatrix[1];

                    for (k = 0; k < 5000; k++) {
                        if (PunchPowerTimerMatrix[k] > 0) {

                            KickPowerGraf[k] = PunchPowerTimerMatrix[k];
                            maxValueGraf = k;

                        }
                    }
                    for (k = 0; k < 5000; k++) {
                        if (PunchPowerTimerMatrix[k] > PunchPowerTimerMax) {
                            PunchPowerTimerMax = PunchPowerTimerMatrix[k];


                        }
                        if (PunchPowerTimerMatrix[k] > 0){
                            PunchPowerTimerSumma = PunchPowerTimerSumma + PunchPowerTimerMatrix[k];
                        }

                        if (TimeOfPunchPowerTimerMatrix[k] > 0&&TimeOfPunchPowerTimerMatrix[k] < 200){
                            KickTimePowerGraf[k] = TimeOfPunchPowerTimerMatrix[k];
                            TimeOfPunchPowerTimerSumma = TimeOfPunchPowerTimerSumma + TimeOfPunchPowerTimerMatrix[k];
                        }

                        if(TimeOfReaction[k]>0){
                            TimeOfReactionAver = TimeOfReactionAver+TimeOfReaction[k];
                        }
                    }

                    //int PunchPowerTimerAverage = PunchPowerTimerSumma / p;
                    if(p!=0) {
                        PunchPowerTimerAverage = PunchPowerTimerSumma / p;

                    TimeOfPunchPowerTimerAverage = TimeOfPunchPowerTimerSumma / p;
                    TimeOfReactionAver = TimeOfReactionAver/p;
                }
                    for (k = 0; k < 5000; k++) {

                        if (TimeOfPunchPowerTimerMatrix[k]!=0&&TimeOfPunchPowerTimerMatrix[k] < TimeOfPunchPowerTimerMin)
                            TimeOfPunchPowerTimerMin = TimeOfPunchPowerTimerMatrix[k];

                    }


                    long round_time = elapsedMillis / 1000;
                    //получаем текущую дату
                    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
                    String date = df.format(new Date());


                    txtProm.setText(R.string.txtRoundTime);
                    String a = txtProm.getText().toString();

                    //txtProm.setTextColor(Color.parseColor("0xFFFF5040"));
                    //txtProm.setTextColor(0xFFFF5040);
                    txtProm.setText(R.string.txtNumberPunch);
                    String b = txtProm.getText().toString();

                    txtProm.setText(R.string.txtMaxPower);
                    String c = txtProm.getText().toString();


                    txtProm.setText(R.string.txtAverPower);
                    String d = txtProm.getText().toString();

                    txtProm.setText(R.string.txtMinKickSpeed);
                    String e = txtProm.getText().toString();

                    txtProm.setText(R.string.txtAverKickSpeed);
                    String f = txtProm.getText().toString();

                    txtProm.setText(R.string.txtAverageReactionTime);
                    String g = txtProm.getText().toString();

                    //записываем в базу данных результаты
                        /*db.addRec(R.string.txtRoundTime+ round_time + " sec " + R.string.txtNumberPunch+ number_punch + R.string.txtMaxPower + PunchPowerTimerMax + " kGf "
                                + R.string.txtAverPower + PunchPowerTimerAverage + " kGf " + R.string.txtMinKickSpeed + TimeOfPunchPowerTimerMin + " mSec"
                                + R.string.txtAverKickSpeed + TimeOfPunchPowerTimerAverage + " mSec ", date);*/
                    /*db.addRec("Round time = "+ round_time+ " sec " + " Number of Punch = "+ number_punch +" Max Power = "+ PunchPowerTimerMax + " kGf "
                            + " Average Power = " + PunchPowerTimerAverage + " kGf "+" Min KickSpeed = "+ TimeOfPunchPowerTimerMin+ " mSec"
                            + " Average KickSpeed = " + TimeOfPunchPowerTimerAverage + " mSec ", date);*/
                    //String strAverPower = new DecimalFormat("#0.00").format(PunchPowerTimerAverage);
                    String strAverPower = new DecimalFormat("#0").format(PunchPowerTimerAverage);
                    String strBestPower = new DecimalFormat("#0").format(PunchPowerTimerMax);
                    //String strBestPower = new DecimalFormat("#0.00").format(PunchPowerTimerMax);
                    String strTimeOfReactionAver = new DecimalFormat("#0").format(TimeOfReactionAver);
                    //String strTimeOfReactionAver = new DecimalFormat("#0.00").format(TimeOfReactionAver);
                   /* db.addRec(a+ round_time + " sec               " + b+ number_punch +"                   "+ c + PunchPowerTimerMax + " kGf                   "
                            + d + PunchPowerTimerAverage + " kGf                           " + e + TimeOfPunchPowerTimerMin + " mSec                            "
                            + f + TimeOfPunchPowerTimerAverage + " mSec", date);*/
                    if(state_btn_reaction==3) {
                        db.addRec(a + round_time + " sec            " + b + number_punch + "            " + c + strBestPower + " kGf            "
                                + d + strAverPower + " kGf              " + e + TimeOfPunchPowerTimerMin + " mSec                  "
                                + f + TimeOfPunchPowerTimerAverage + " mSec", date);

                        cursor.requery();
                    }
                    if(state_btn_reaction==2) {
                        db.addRec(a + round_time + " sec            " + b + number_punch + "            "
                                + d + strAverPower + " kGf              "
                                + f + TimeOfPunchPowerTimerAverage + " mSec " + g +strTimeOfReactionAver+"  mSec" , date);

                        cursor.requery();
                    }
                }


            }

            //Toast.makeText(getApplicationContext(), "state_chroonometer1 "+state_chroonometer1, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "state_chroonometer2 "+state_chroonometer2, Toast.LENGTH_LONG).show();

        });

//        imgBtnChrono.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                anim = AnimationUtils.loadAnimation(PowerActivity.this,R.anim.myscale);
//                imgBtnChrono.startAnimation(anim);
//                //Intent i = new Intent(PowerActivity.this, HelpActivity.class);
//                //startActivity(i);
//                if (state_chroonometer1>state_chroonometer2){
//                    /*String strElapsedMillis = "Start timer";
//                    Toast.makeText(getApplicationContext(),
//                            strElapsedMillis, Toast.LENGTH_SHORT)
//                            .show();*/
//
//                    mChronometer.setBase(SystemClock.elapsedRealtime());
//                    mChronometer.start();
//                    /*if (address!=null) */state_timer = 2;
//                    if (address==null){
//                        Toast.makeText(getApplicationContext(), R.string.txtToastBtSettings, Toast.LENGTH_SHORT).show();
//
//                    }
//                    number_punch = 0;
//                    PunchPowerTimer = 0;
//                    PunchPowerTimerSumma = 0;
//                    TimeOfPunchPowerTimer = 0;
//                    TimeOfPunchPowerTimerSumma = 0;
//                    for (k = 0; k<5000; k++) PunchPowerTimerMatrix[k] = 0;
//                    for (k = 0; k<5000; k++) TimeOfPunchPowerTimerMatrix[k] = 0;
//                    p = 0;
//                    state_chroonometer2++;
//                    //Toast.makeText(getApplicationContext(),"number_punch=" + number_punch, Toast.LENGTH_SHORT).show();
//                }
//
//                if (state_chroonometer2>state_chroonometer1){
//                    /*String strElapsedMillis = "Stop timer";
//                    Toast.makeText(getApplicationContext(),
//                            strElapsedMillis, Toast.LENGTH_SHORT)
//                            .show();*/
//
//                    //mChronometer.setBase(SystemClock.elapsedRealtime());
//
//                    mChronometer.stop();
//                    state_timer = 3;
//                    state_chroonometer1 = state_chroonometer1 + 3;
//                    if (address==null){
//                        Toast.makeText(getApplicationContext(), R.string.txtToastBtSettings, Toast.LENGTH_SHORT).show();
//
//                    }
//                    if (address!=null) {
//                        int PunchPowerTimerMax = 0;
//
//                            int TimeOfPunchPowerTimerMin = TimeOfPunchPowerTimerMatrix[1];
//
//
//
//                        for (k = 0; k < 5000; k++) {
//                            if (PunchPowerTimerMatrix[k] > PunchPowerTimerMax) {
//                                PunchPowerTimerMax = PunchPowerTimerMatrix[k];
//
//                            }
//                            if (PunchPowerTimerMatrix[k] > 0){
//                                PunchPowerTimerSumma = PunchPowerTimerSumma + PunchPowerTimerMatrix[k];
//                            }
//
//                            if (TimeOfPunchPowerTimerMatrix[k] > 0&&TimeOfPunchPowerTimerMatrix[k] < 200){
//                                TimeOfPunchPowerTimerSumma = TimeOfPunchPowerTimerSumma + TimeOfPunchPowerTimerMatrix[k];
//                            }
//                        }
//
//                        int PunchPowerTimerAverage = PunchPowerTimerSumma / p;
//                        int TimeOfPunchPowerTimerAverage = TimeOfPunchPowerTimerSumma / p;
//
//                        for (k = 0; k < 5000; k++) {
//
//                            if (TimeOfPunchPowerTimerMatrix[k]!=0&&TimeOfPunchPowerTimerMatrix[k] < TimeOfPunchPowerTimerMin)
//                                TimeOfPunchPowerTimerMin = TimeOfPunchPowerTimerMatrix[k];
//
//                        }
//
//
//                        long round_time = elapsedMillis / 1000;
//                        //получаем текущую дату
//                        DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
//                        String date = df.format(new Date());
//
//                        txtProm.setText(R.string.txtRoundTime);
//                        String a = txtProm.getText().toString();
//
//                        txtProm.setText(R.string.txtNumberPunch);
//                        String b = txtProm.getText().toString();
//
//                        txtProm.setText(R.string.txtMaxPower);
//                        String c = txtProm.getText().toString();
//
//                        txtProm.setText(R.string.txtAverPower);
//                        String d = txtProm.getText().toString();
//
//                        txtProm.setText(R.string.txtMinKickSpeed);
//                        String e = txtProm.getText().toString();
//
//                        txtProm.setText(R.string.txtAverKickSpeed);
//                        String f = txtProm.getText().toString();
//
//                        //записываем в базу данных результаты
//                        /*db.addRec(R.string.txtRoundTime+ round_time + " sec " + R.string.txtNumberPunch+ number_punch + R.string.txtMaxPower + PunchPowerTimerMax + " kGf "
//                                + R.string.txtAverPower + PunchPowerTimerAverage + " kGf " + R.string.txtMinKickSpeed + TimeOfPunchPowerTimerMin + " mSec"
//                                + R.string.txtAverKickSpeed + TimeOfPunchPowerTimerAverage + " mSec ", date);*/
//                    /*db.addRec("Round time = "+ round_time+ " sec " + " Number of Punch = "+ number_punch +" Max Power = "+ PunchPowerTimerMax + " kGf "
//                            + " Average Power = " + PunchPowerTimerAverage + " kGf "+" Min KickSpeed = "+ TimeOfPunchPowerTimerMin+ " mSec"
//                            + " Average KickSpeed = " + TimeOfPunchPowerTimerAverage + " mSec ", date);*/
//                        db.addRec(a+ round_time + " sec               " + b+ number_punch +"                   "+ c + PunchPowerTimerMax + " kGf                   "
//                                + d + PunchPowerTimerAverage + " kGf                           " + e + TimeOfPunchPowerTimerMin + " mSec                            "
//                                + f + TimeOfPunchPowerTimerAverage + " mSec", date);
//                    }
//
//
//                }
//
//                state_chroonometer2++;
//                //Toast.makeText(getApplicationContext(), "state_chroonometer1 "+state_chroonometer1, Toast.LENGTH_LONG).show();
//                //Toast.makeText(getApplicationContext(), "state_chroonometer2 "+state_chroonometer2, Toast.LENGTH_LONG).show();
//            }
//
//        });

//----------------------------------------------------------------------------------------------
//
//----------------------------------------------------------------------------------------------
        connectBT = new ConnectBT();

        if (address==null){
            Toast.makeText(getApplicationContext(), R.string.txtToastBtSettings, Toast.LENGTH_LONG).show();

        }
//---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnReactionOn
//------------------------------------------------------------------------
        btnReactionOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                state_btn_reaction = 2;
                btnReactionOn.setVisibility(View.INVISIBLE);
                btnReactionOff.setVisibility(View.VISIBLE);
            }
        });
        //---------------------------------------------------------------------
//        НАЖАТИЕ НА КНОПКУ btnReactionOn
//------------------------------------------------------------------------
        btnReactionOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOffFlash();
                state_btn_reaction = 3;
                btnReactionOff.setVisibility(View.INVISIBLE);
                btnReactionOn.setVisibility(View.VISIBLE);
            }
        });
//-------------------------------------------------------------------------------
//          ON LED
//--------------------------------------------------------------------------------
        /*btnOnLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //turnOnLed();
            }
        });*/
//-------------------------------------------------------------------------------
//          Off LED
//--------------------------------------------------------------------------------
       /* btnOffLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //turnOffLed();
            }
        });*/

//----------------------------------------------------------------------------------------------
// ПРЕРЫВАНИЕ ПО ПРИХОДУ ПО БЛЮТУС
//----------------------------------------------------------------------------------------------
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //Log.d("log", "Я ЗДЕСЬ");
                //btnOffLed.setEnabled(false);
                //btnOnLed.setEnabled(false);

                switch (msg.what) {

                    case RECIEVE_MESSAGE:                                                  // если приняли сообщение в Handler
                        byte[] readBuf = (byte[]) msg.obj;
                       /* ++packet_tick;
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println(" readBuf[0]================ "+readBuf[0]);
                        System.out.println(" readBuf[1]================ "+readBuf[1]);
                        System.out.println(" readBuf[2]================ "+readBuf[2]);
                        System.out.println(" readBuf[3]================ "+readBuf[3]);
                        System.out.println(" readBuf[4]================ "+readBuf[4]);
                        System.out.println(" readBuf[5]================ "+readBuf[5]);
                        System.out.println(" readBuf[6]================ "+readBuf[6]);
                        System.out.println(" readBuf[7]================ "+readBuf[7]);
                        System.out.println(" readBuf[8]================ "+readBuf[8]);
                        System.out.println(" readBuf[9]================ "+readBuf[9]);
                        System.out.println(" readBuf[10]================ "+readBuf[10]);
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                        System.out.println(" ______________________packet tick = -----------------    " + packet_tick);*/
                        b0 = readBuf[0];
                        b1 = readBuf[1];
                        b2 = readBuf[2];
                        b3 = readBuf[3];
                        b4 = readBuf[4];
                        b5 = readBuf[5];
                        b6 = readBuf[6];
                        b7 = readBuf[7];
                       /* if(number_bytes==9){
                            b0 = readBuf[0];
                            b1 = readBuf[1];
                            b2 = readBuf[2];
                            b3 = readBuf[3];
                            b4 = readBuf[4];
                            b5 = readBuf[5];
                            b6 = readBuf[6];
                            b7 = readBuf[7];
                            System.out.println(" 111111111111111111111111111111111111111111111111111111");
                            System.out.println(" =================9 байтов============================");
                            System.out.println(" 111111111111111111111111111111111111111111111111111111");
                            System.out.println(" readBuf[0]================ "+readBuf[0]+ "  b0 = " + b0);
                            System.out.println(" readBuf[1]================ "+readBuf[1]+ "  b1 = " + b1);
                            System.out.println(" readBuf[2]================ "+readBuf[2]+ "  b2 = " + b2);
                            System.out.println(" readBuf[3]================ "+readBuf[3]+ "  b3 = " + b3);
                            System.out.println(" readBuf[4]================ "+readBuf[4]+ "  b4 = " + b4);
                            System.out.println(" readBuf[5]================ "+readBuf[5]+ "  b5 = " + b5);
                            System.out.println(" readBuf[6]================ "+readBuf[6]+ "  b6 = " + b6);
                            System.out.println(" readBuf[7]================ "+readBuf[7]+ "  b7 = " + b7);
                            System.out.println(" readBuf[8]================ "+readBuf[8]);
                            System.out.println(" readBuf[9]================ "+readBuf[9]);
                            System.out.println(" readBuf[10]================ "+readBuf[10]);
                            System.out.println(" 111111111111111111111111111111111111111111111111111111");
                            System.out.println(" =================END === END 9 байтов============================");
                            System.out.println(" 111111111111111111111111111111111111111111111111111111");
                           }
                        if(number_bytes==8){
                            b0 = readBuf[0];
                            b1 = readBuf[1];
                            b2 = readBuf[2];
                            b3 = readBuf[3];
                            b4 = readBuf[4];
                            b5 = readBuf[5];
                            b6 = readBuf[6];
                            b7 = readBuf[7];
                            System.out.println(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            System.out.println(" =================8 байтов============================");
                            System.out.println(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            System.out.println(" readBuf[0]================ "+readBuf[0]+ "  b0 = " + b0);
                            System.out.println(" readBuf[1]================ "+readBuf[1]+ "  b1 = " + b1);
                            System.out.println(" readBuf[2]================ "+readBuf[2]+ "  b2 = " + b2);
                            System.out.println(" readBuf[3]================ "+readBuf[3]+ "  b3 = " + b3);
                            System.out.println(" readBuf[4]================ "+readBuf[4]+ "  b4 = " + b4);
                            System.out.println(" readBuf[5]================ "+readBuf[5]+ "  b5 = " + b5);
                            System.out.println(" readBuf[6]================ "+readBuf[6]+ "  b6 = " + b6);
                            System.out.println(" readBuf[7]================ "+readBuf[7]+ "  b7 = " + b7);
                            System.out.println(" readBuf[8]================ "+readBuf[8]);
                            System.out.println(" readBuf[9]================ "+readBuf[9]);
                            System.out.println(" readBuf[10]================ "+readBuf[10]);
                            System.out.println(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            System.out.println(" =================END === END 8 байтов============================");
                            System.out.println(" ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        }
                        if(number_bytes==10){
                            b0 = readBuf[0];
                            b1 = readBuf[1];
                            b2 = readBuf[2];
                            b3 = readBuf[3];
                            b4 = readBuf[4];
                            b5 = readBuf[5];
                            b6 = readBuf[6];
                            b7 = readBuf[7];
                            System.out.println(" ======================================================");
                            System.out.println(" =================10 байтов============================");
                            System.out.println(" ======================================================");
                            System.out.println(" readBuf[0]================ "+readBuf[0]+ "  b0 = " + b0);
                            System.out.println(" readBuf[1]================ "+readBuf[1]+ "  b1 = " + b1);
                            System.out.println(" readBuf[2]================ "+readBuf[2]+ "  b2 = " + b2);
                            System.out.println(" readBuf[3]================ "+readBuf[3]+ "  b3 = " + b3);
                            System.out.println(" readBuf[4]================ "+readBuf[4]+ "  b4 = " + b4);
                            System.out.println(" readBuf[5]================ "+readBuf[5]+ "  b5 = " + b5);
                            System.out.println(" readBuf[6]================ "+readBuf[6]+ "  b6 = " + b6);
                            System.out.println(" readBuf[7]================ "+readBuf[7]+ "  b7 = " + b7);
                            System.out.println(" readBuf[8]================ "+readBuf[8]);
                            System.out.println(" readBuf[9]================ "+readBuf[9]);
                            System.out.println(" readBuf[10]================ "+readBuf[10]);
                            System.out.println(" ===================================================================");
                            System.out.println(" =================END === END  10 байтов============================");
                            System.out.println(" ===================================================================");
                        }*/

                        //--------------------------------------------------------
                         // РЕЖИМ УДАРА
                        //------------------------------------------------------------

                        //if(/*(b0+b1+b2)!=readBufTemp1&&*/readBuf[0]==83&&readBuf[9]==69&&state_btn_reaction==3) {
                            if((b1+b2)!=readBufTemp1) {
                            readBufTemp1 = b1+b2;
                           /* b0 = readBuf[0];
                            b1 = readBuf[1];
                            b2 = readBuf[2];
                            b3 = readBuf[3];
                            b4 = readBuf[4];
                            b5 = readBuf[5];
                            b6 = readBuf[6];
                            b7 = readBuf[7];*/
                           // mChronometer.stop();
                            //СИЛА УДАРА
                            //int int_b1 = b1;
                            //int_b1 = abs(int_b1);
                            // b1 = (byte) int_b1;

                            /*System.out.println(" readBuf[0]================ "+readBuf[0]+ "  b0 = " + b0);
                            System.out.println(" readBuf[1]================ "+readBuf[1]+ "  b1 = " + b1);
                            System.out.println(" readBuf[2]================ "+readBuf[2]+ "  b2 = " + b2);
                            System.out.println(" readBuf[3]================ "+readBuf[3]+ "  b3 = " + b3);
                            System.out.println(" readBuf[4]================ "+readBuf[4]+ "  b4 = " + b4);
                            System.out.println(" readBuf[5]================ "+readBuf[5]+ "  b5 = " + b5);
                            System.out.println(" readBuf[6]================ "+readBuf[6]+ "  b6 = " + b6);
                            System.out.println(" readBuf[7]================ "+readBuf[7]+ "  b7 = " + b7);
                            System.out.println(" readBuf[8]================ "+readBuf[8]);
                            System.out.println(" readBuf[9]================ "+readBuf[9]);
                            System.out.println(" readBuf[10]================ "+readBuf[10]);
                            System.out.println(" \r\n");*/


                           int PunchPowerHigh = b2 & 0xff;
                            PunchPowerHigh = PunchPowerHigh * 256;
                            int PunchPowerLow = b1 & 0xff;
                            int PunchPower = PunchPowerHigh + PunchPowerLow;


                            PunchPowerTimer = PunchPower;
                            if (PunchPower<=25) {
                                PunchPowerTimer = 0;
                                PunchPower=0;
                            }
                            if (PunchPower > 2000) {
                                //PunchPowerTimer = 0;
                            }/*else {
                                if (PunchPowerTimer > 120)
                                    PunchPowerTimerDouble = 145.4 * exp(PunchPowerTimer * 0.0033);
                                String strPunchPower = new DecimalFormat("#0.00").format(PunchPowerTimerDouble);

                                //String strPunchPower = Double.toString(PunchPowerTimerDouble);
                                txtPower.setText(strPunchPower);
                            }*/
                            if (PunchPower < 5000)  {


                                    //PunchPowerTimerDouble = 0.01 + PunchPowerTimer * 1.001;
                                    PunchPowerTimerDouble = PunchPowerTimer;
                                String strPunchPower = new DecimalFormat("#0").format(PunchPowerTimerDouble);

                                //String strPunchPower = Double.toString(PunchPowerTimerDouble);
                                //String strPunchPower = Integer.toString(PunchPowerTimer);
                                txtPower.setText(strPunchPower);
                            }
                            //ВРЕМЯ УДАРА
                            int TimeOfPunchHigh = b3 & 0xff;
                            TimeOfPunchHigh = TimeOfPunchHigh * 256;
                            int TimeOfPunchLow = b4 & 0xff;
                            int TimeOfPunch = TimeOfPunchHigh + TimeOfPunchLow;
                           /* if(TimeOfPunch>300) TimeOfPunch = TimeOfPunch/20;
                            TimeOfPunchPowerTimer = TimeOfPunch;
                            if (TimeOfPunch > 2000) TimeOfPunch = 0;
                            if(PunchPower==0)TimeOfPunch=0;
                            if(PunchPower!=0&&TimeOfPunch==0) TimeOfPunch = 21;*/
                            String strTimeOfPunch = Integer.toString(TimeOfPunch);
                            txtTimePower.setText(strTimeOfPunch);

                            //ВРЕМЯ ПРОСТОЙ ДВИГАТЕЛЬНОЙ РЕАКЦИИ

                            /*int TimeOfReactionHigh = readBuf[5] & 0xff;
                            TimeOfReactionHigh = TimeOfReactionHigh * 256;
                            int TimeOfReactionLow = readBuf[6] & 0xff;
                            int TimeOfReaction = TimeOfReactionHigh + TimeOfReactionLow;
                            if (TimeOfReaction > 5000) TimeOfReaction = 0;
                            String strTimeOfReaction = Integer.toString(TimeOfReaction);
                            txtReaction.setText(strTimeOfReaction);*/

                            //УРОВЕНЬ ЗАРЯДКИ
                            int charge_voltage_high = b7& 0xff;
                            charge_voltage_high = charge_voltage_high * 256;
                            int charge_voltage_low = b6& 0xff;
                            int charge_voltage = charge_voltage_high + charge_voltage_low;

                            //Toast.makeText(getApplicationContext(), "charge_voltage "+charge_voltage, Toast.LENGTH_LONG).show();
                            if (charge_voltage != 0 && charge_voltage < 1000) {

                                Toast.makeText(getApplicationContext(), R.string.txtToastCharge, Toast.LENGTH_SHORT).show();

                            }

                            //mChronometer.start();

                            if (state_timer == 2) {



                                //IntPunchPowerTimerMatrix[p] = PunchPowerTimer;
                                PunchPowerTimerMatrix[number_punch] = PunchPowerTimerDouble;

                                TimeOfPunchPowerTimerMatrix[p] = TimeOfPunchPowerTimer;
                                p++;
                                number_punch++;
                                //Toast.makeText(getApplicationContext(), "number punch = " + number_punch, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "readBuf[0]=" + readBuf[0], Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "readBuf[1]=" + readBuf[1], Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "readBuf[2]=" + readBuf[2], Toast.LENGTH_SHORT).show();
                            }

                            //readBuf[0]=0;
                            //readBuf[1]=0;

                        }


                        //=================================================================
                        // РЕЖИМ РЕАКЦИИ
                        //====================================================================

                        if(readBuf[1]!=readBufTemp2&&state_btn_reaction==2) {
                        //if(readBuf[0]!=0&&readBuf[1]!=0&&state_btn_reaction==2) {
                            readBufTemp2 = readBuf[1];

                            //Delta = timer1_tick;
                            //int_Delta = int_timer1_tick;
                            turnOffFlash();
                            //ReactionelapsedMillis_2 = elapsedMillis/7;
                            //double r2 = elapsedMillis/7;
                            //ReactionelapsedMillis_3 = ReactionelapsedMillis/7;
                            //double r3 = ReactionelapsedMillis/33;
                            //double r3 = RelapsedMillis/7;

                            //String x = Long.toString(ReactionelapsedMillis_2);
                            //txtElapsTime.setText(x);

                            //Delta = ReactionelapsedMillis_2 - ReactionelapsedMillis_3;//НАХОДИМ РАЗНИЦУ ВРЕМЕНИ - ВРЕМЯ РЕАКЦИИ
                            //Delta = Delta - ReactionelapsedMillis;//НАХОДИМ РАЗНИЦУ ВРЕМЕНИ - ВРЕМЯ РЕАКЦИИ

                            long d = SystemClock.elapsedRealtime();
                            d = (d - RelapsedMillis)/5;
                            //double d = elapsedMillis;

                            //Delta = elapsedMillis;
                            //Toast.makeText(getApplicationContext(), " elapsedMillis = " +elapsedMillis, Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getApplicationContext(), " ReactionelapsedMillis = " +ReactionelapsedMillis, Toast.LENGTH_SHORT).show();
                            //СИЛА УДАРА
                            int PunchPowerHigh = readBuf[2] & 0xff;
                            PunchPowerHigh = PunchPowerHigh * 256;
                            int PunchPowerLow = readBuf[1] & 0xff;
                            int PunchPower = PunchPowerHigh + PunchPowerLow;
                            PunchPowerTimer = PunchPower;
                            if (PunchPower > 2000) {
                                PunchPowerTimer = 0;
                            }/*else {
                                if (PunchPowerTimer > 120)
                                    PunchPowerTimerDouble = 145.4 * exp(PunchPowerTimer * 0.0033);
                                String strPunchPower = new DecimalFormat("#0.00").format(PunchPowerTimerDouble);

                                //String strPunchPower = Double.toString(PunchPowerTimerDouble);
                                txtPower.setText(strPunchPower);
                            }*/
                            else {


                                //PunchPowerTimerDouble = 3.923 + PunchPowerTimer * 1.055;
                                PunchPowerTimerDouble = PunchPowerTimer;
                                //String strPunchPower = new DecimalFormat("#0.00").format(PunchPowerTimerDouble);
                                String strPunchPower = new DecimalFormat("#0").format(PunchPowerTimerDouble);

                                //String strPunchPower = Double.toString(PunchPowerTimerDouble);
                                txtPower.setText(strPunchPower);
                            }
                            //ВРЕМЯ УДАРА
                            int TimeOfPunchHigh = readBuf[3] & 0xff;
                            TimeOfPunchHigh = TimeOfPunchHigh * 256;
                            int TimeOfPunchLow = readBuf[4] & 0xff;
                            int TimeOfPunch = TimeOfPunchHigh + TimeOfPunchLow;
                            TimeOfPunchPowerTimer = TimeOfPunch;
                            if (TimeOfPunch > 2000) TimeOfPunch = 0;
                            String strTimeOfPunch = Integer.toString(TimeOfPunch);
                            txtTimePower.setText(strTimeOfPunch);

                            //ВРЕМЯ ПРОСТОЙ ДВИГАТЕЛЬНОЙ РЕАКЦИИ


                            //String strTimeOfReaction = Long.toString(Delta);
                            //String strTimeOfReaction = Double.toString(d);
                            //String strTimeOfReaction = new DecimalFormat("#0.00").format(d);
                            String strTimeOfReaction = new DecimalFormat("#0").format(d);
                           // String strTimeOfReaction = Integer.toString(int_Delta);
                            txtReaction.setText(strTimeOfReaction);

                           /* int TimeOfReactionHigh = readBuf[5] & 0xff;
                            TimeOfReactionHigh = TimeOfReactionHigh * 256;
                            int TimeOfReactionLow = readBuf[6] & 0xff;
                            int TimeOfReaction = TimeOfReactionHigh + TimeOfReactionLow;
                            if (TimeOfReaction > 5000) TimeOfReaction = 0;
                            String strTimeOfReaction = Integer.toString(TimeOfReaction);
                            txtReaction.setText(strTimeOfReaction);*/

                            //УРОВЕНЬ ЗАРЯДКИ
                            /*int charge_voltage_high = readBuf[7] & 0xff;
                            charge_voltage_high = charge_voltage_high * 256;
                            int charge_voltage_low = readBuf[8] & 0xff;
                            int charge_voltage = charge_voltage_high + charge_voltage_low;

                            //Toast.makeText(getApplicationContext(), "charge_voltage "+charge_voltage, Toast.LENGTH_LONG).show();
                            if (charge_voltage != 0 && charge_voltage < 1000) {

                                //Toast.makeText(getApplicationContext(), R.string.txtToastCharge, Toast.LENGTH_SHORT).show();

                            }*/

                            //-----------------------------------------------------------------------------------------------------------------
                            // ЕСЛИ НЕ НАЖАТА КНОПКА СТОП
                            //================================================================

                            if (state_timer == 2) {



                                //PunchPowerTimerMatrix[p] = PunchPowerTimer;
                                PunchPowerTimerMatrix[number_punch] = PunchPowerTimerDouble;
                                TimeOfPunchPowerTimerMatrix[p] = TimeOfPunchPowerTimer;
                                TimeOfReaction[p] = d;
                                number_punch++;
                                p++;
                                //Toast.makeText(getApplicationContext(), "number punch = " + number_punch, Toast.LENGTH_SHORT).show();
                                // Toast.makeText(getApplicationContext(), "readBuf[0]=" + readBuf[0], Toast.LENGTH_SHORT).show();
                                // Toast.makeText(getApplicationContext(), "readBuf[1]=" + readBuf[1], Toast.LENGTH_SHORT).show();
                                // Toast.makeText(getApplicationContext(), "readBuf[2]=" + readBuf[2], Toast.LENGTH_SHORT).show();
                                //-----------------------------------------------------------
                                //ВКЛЮЧАЕМ ВСПЫШКУ, ЗАПУСКАЕМ ОТСЧЕТ ВРЕМЕНИ
                                //------------------------------------------------------------
                                timer = new Timer();
                                final TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        //Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kickpower.ru"));
                                        //startActivity(browser);
                                        getCamera();
                                        //while(state_timer==2)  sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                                        //if(state_timer==2)  sp.play(soundSirena, 1, 1, 0, 0, 1);
                                        //ReactionelapsedMillis = elapsedMillis;
                                        RelapsedMillis = SystemClock.elapsedRealtime();
                                        //elapsedMillis = 0;
                                        turnOnFlash();
                                        //timer1_tick = 0;
                                        //timer1();


                                    }
                                };

                                final Random random = new Random();
                                random_timer = random.nextInt(5000);
                                timer.schedule(task, (3000 + random_timer));
                            }



                        }

                        //String strCharge_voltage = Integer.toString(charge_voltage);
                        //txtViewCharge.setText(strCharge_voltage);
                        //txtAddress.setText(address);
                        //btnOffLed.setEnabled(true);
                        //btnOnLed.setEnabled(true);
                        break;
                }
            };
        };//Handler h

    }
    //========================================
    //function for adaption display
    //========================================
    private void display_adapter (){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        hight_display = metricsB.heightPixels;
        width_display = metricsB.widthPixels;
        display_diagonal = sqrt(abs(hight_display*hight_display - width_display*width_display));
        int density  = metricsB.densityDpi;
        double high_dens = (double)hight_display/density;
        double width_dens = (double)width_display/density;
        double resolution = high_dens*width_dens;
        int text_size1 = 0;
        int text_size2 = 0;
        if(resolution>=9&&resolution<13) {
            text_size1 = 35;
            text_size2 = 50;


            txtKickPower.setTextSize(text_size1);
            txtTimeOfPower.setTextSize(text_size1);
            txtWordReaction.setTextSize(text_size1);
            txtDimensReaction.setTextSize(text_size1);

            txtPower.setTextSize(text_size2);
            txtTimePower.setTextSize(text_size2);
            txtReaction.setTextSize(text_size2);
            imgButtonLink.setBackgroundResource(R.drawable.btnlogolink_hdpi);
        }
        if(resolution>=13&&resolution<18) {
            text_size1 = 30;
            text_size2 = 70;


            txtKickPower.setTextSize(text_size1);
            txtTimeOfPower.setTextSize(text_size1);
            txtWordReaction.setTextSize(text_size1);
            txtDimensReaction.setTextSize(25);

            txtPower.setTextSize(text_size2);
            txtTimePower.setTextSize(text_size2);
            txtReaction.setTextSize(text_size2);
            imgButtonLink.setBackgroundResource(R.drawable.btnlogolink_hdpi);
        }
        if(resolution>=18) {
            text_size1 =45 ;
            text_size2 = 100;


            txtKickPower.setTextSize(text_size1);
            txtTimeOfPower.setTextSize(text_size1);
            txtWordReaction.setTextSize(text_size1);
            txtDimensReaction.setTextSize(25);

            txtPower.setTextSize(text_size2);
            txtTimePower.setTextSize(text_size2);
            txtReaction.setTextSize(text_size2);
            imgButtonLink.setBackgroundResource(R.drawable.btnlogolink_xhdpi);
        }

    }
//----------------------------------------------------------------------------------
//Menu
//-----------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_power_activity,menu);

        return true;
    }
//----------------------------------------------------------------------------
//
//-----------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Disconnect) {
            Disconnect();
            return true;
        }
        if (id == R.id.Settings) {
            Intent activity = new Intent(PowerActivity.this, SettingsActivity.class);
            startActivityForResult(activity,1);

            return true;
        }
        if (id == R.id.Help) {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://kickpower.ru/help"));
            startActivity(browser);
            return true;
        }
        if (id == R.id.Results) {
            Intent u = new Intent(PowerActivity.this, ResActivity.class);
            startActivity(u);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//------------------------------------------------------------------------
//
//---------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        address = data.getStringExtra(EXTRA_ADDRESS);
        //Toast.makeText(getApplicationContext(), "я здесь  "+address, Toast.LENGTH_LONG).show();
        connectBT.execute();
    }
//--------------------------------------------------------------------------------
// fast way to call Toast
//--------------------------------------------------------------------------------------
    private void msg(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
//------------------------------------------------------------------------
//Disconnect()
//----------------------------------------------------------------------------
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            //msg("я здеся");
            try
            {

                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }
    //----------------------------------------------------------------------------------
//ConnectBT
//-------------------------------------------------------------------------------------
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(PowerActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {

                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    mDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    // MY_UUID = mDevice.getUuids()[0].getUuid();
                    // msg("MY_UUID    " + MY_UUID);
                    btSocket = mDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                }
                if (isCancelled()) return null;
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                //imageViewBluetooth.setVisibility(View.INVISIBLE);
                //textViewBTstatus.setText("Соединения нет");
                //textViewBTstatus.setVisibility(View.VISIBLE);
                msg("Connection Failed. Try again.");
                //finish();
            }
            else
            {
                //Log.d("log", "Я ЗДЕСЬ 4");
                //imageViewBluetooth.setVisibility(View.VISIBLE);
                //textViewBTstatus.setText("Соединено");
                //textViewBTstatus.setVisibility(View.VISIBLE);
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }//ConnectBT
//--------------------------------------------------------------------------------------
//ConnectedThread
//-------------------------------------------------------------------------------------
    private class ConnectedThread extends Thread {
        // private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        // private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

       public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Получаем кол-во байт и само собщение в байтовый массив "buffer"
                    number_bytes = bytes;
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Отправляем в очередь сообщений Handler
                } catch (IOException e) {
                    break;
                }
            }
        }
    }//ConnectedThread
//---------------------------------------------------------------------
// Call this from the main activity to send data to the remote device
//----------------------------------------------------------------------
   /*public void write(String message) {

        byte[] msgBuffer = message.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            msg("Error");
        }
    }*/
//---------------------------------------------------------------------
//
//----------------------------------------------------------------------
    /*private void turnOffLed(){
        if (btSocket!=null){
            try{
                btSocket.getOutputStream().write("s".toString().getBytes());
                //btnOffLed.setEnabled(false);
               //mConnectedThread.mmInStream.close();
                //mConnectedThread.mmSocket.getOutputStream().write(1);
                //btSocket.getOutputStream().write(1);
                  //mmOutStream.write('e');
            }
            catch (IOException e){
                msg("Error");
            }

        }
    }//turnOffLed()*/
//-----------------------------------------------------------------------
//
//-------------------------------------------------------------------------------
    /*private void turnOnLed(){
        if (btSocket!=null){
            try{

                btSocket.getOutputStream().write("1".toString().getBytes());
            }
            catch (IOException e){
                msg("Error");
            }
        }

    } //turnOnLed*/
//-----------------------------------------------------------------------
//ФУНКЦИЯ СИРЕНА
//--------------------------------------------------------------------------
    /*private void timeReactionFunc (){

        while (state_timer ==2){
            long Delta = elapsedMillis - ReactionelapsedMillis;
            final Random random = new Random();
            int random_timer= random.nextInt(3000);
            txtElapsTime.setText(state_timer);
            if(Delta>3000+random_timer){
                ReactionelapsedMillis = 0;
                while (state_punch!=1){
                   sp.play(soundIdExplosion, 1, 1, 0, 0, 1);

                }
            }

        }

    }*/

    //Получаем параметры камеры:
    private void getCamera() {
        if (camera == null) {
            try {
               // camera = Camera.open();

                camera = android.hardware.Camera.open();

                params = camera.getParameters();
            } catch (RuntimeException e) {
                Toast.makeText(getApplicationContext(), "Error flashlight", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * Включаем вспышку
    */
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;


        }
    }
    //Для выключения фонарика будет использоваться настройка на режим FLASH_MODE_OFF:
    /*
    * Выключаем фонарик
    */
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;


        }
    }
    //===================================================================================
    //timer 1
    //===================================================================================
    public void timer1 (){

        CountDownTimer start = new CountDownTimer(30000, 1) {

            public void onTick(long millisUntilFinished) {

                timer1_tick = millisUntilFinished;

                txtElapsTime.setText("seconds remaining: " + timer1_tick );

            }

            public void onFinish() {
                turnOffFlash();
                Toast.makeText(getApplicationContext(), "Too long wait! Faster!", Toast.LENGTH_SHORT).show();
            }
        }.start();

    }


}
