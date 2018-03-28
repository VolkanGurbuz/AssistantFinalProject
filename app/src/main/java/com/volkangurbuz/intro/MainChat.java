package com.volkangurbuz.intro;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


public class MainChat extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;


    private ArrayList<HashMap<String, Object>> chatList = null;

    private String[] from = {"image", "text"};
    private int[] to = {R.id.chatlist_image_me, R.id.textView3, R.id.imageView3, R.id.textView5};
    private int[] layout = {R.layout.message, R.layout.others};


    public final static int ASISTAN = 1;
    public final static int KULLANICI = 0;


    protected ListView chatListView = null;
    protected ImageView chatSendButton = null, chatVoiceButton = null;
    protected EditText editText = null;

    protected MyChatAdapter adapter = null;


    protected String kullaniciAd;

    public void setChatList(ArrayList<HashMap<String, Object>> chatList) {
        this.chatList = chatList;
    }

    public MainChat() {

        chatList = new ArrayList<HashMap<String, Object>>();
    }


    private NaturalLanguageProcess nlp;
    private Asistan asistan;
    private DatabaseOperations dt;
    private AnswerProcess answerProcess;
    private boolean ilkasama = false;
    // private GetInfo bilgi;

    private static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deneme);

        //  weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        askForContactPermission();
        messagePermission();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        answerProcess = new AnswerProcess();
        nlp = new NaturalLanguageProcess();

        asistan = new Asistan();

        //  bilgi = new GetInfo();


        dt = new DatabaseOperations(this, null, null, 1);

        if (isPressedToGetMessages) {

            try {
                addHashToList(mesajlariAl());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else {

            kullaniciAd = OneFragment.eTuserAd.getText().toString();

            ilkAsamaMesajlar();
            askForContactPermission();
            messagePermission();


        }

        chatListView = findViewById(R.id.chat_list);
        chatSendButton = findViewById(R.id.buttonSend);
        adapter = new MyChatAdapter(this, chatList, layout, from, to);
        editText = findViewById(R.id.MessageeditText);
        chatVoiceButton = findViewById(R.id.sendVoiceMessage);

        editText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        chatListView.setAdapter(adapter);

        getTechInfoFromWebsite("teknoloji");
        getSporInfoFromWebsite("spor");
        getGundemInfoFromWebsite("gundem");
        getKulturInfoFromWebsite("seyahat");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    public void getTimer() {

        // her 30 dakika da mesaj veriyor
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                setNotification();

            }
        }, 0, 3000);

    }


    /**
     * text mesaj gonderme butonu listener
     * @param v
     * @throws IOException
     */

    public void sendTextMessage(View v) throws IOException {
        String myWord, asistanWord = null;


        myWord = (editText.getText() + "");

        //nlp.setValue(myWord);


        if (myWord.length() == 0)
            return;

        editText.setText("");


        if (/*nlp.isMeaning(myWord) &&*/ asistan.getAsistanName() == null) {
            addTextToList(myWord, KULLANICI);
            asistan.setAsistanName(myWord);
            asistanWord = "artık bana " + myWord + " olarak seslenebilirsiniz";
            addTextToList(asistanWord, ASISTAN);
        }

        //  else
        //return;

        if (!ilkasama) {
            asistanWord = "nasılsınız?";
            addTextToList(asistanWord, ASISTAN);

            if (myWord.toLowerCase().contains("sen")) {
                addTextToList(myWord, KULLANICI);
                asistanWord = asistan.nasilsin();
                addTextToList(asistanWord, ASISTAN);
                addTextToList("bu arada kişisel değerlendirme puanlarınız şu şekilde: ", ASISTAN);
                addTextToList(asistan.userProfilDegerlenirme(), ASISTAN);
                addTextToList(asistan.ihtiyac(), ASISTAN);

                ilkasama = true;
            }


        } else if (answerProcess.isQuestion(myWord)) {
            addTextToList(myWord, KULLANICI);
            addTextToList("bilgiyi bulmaya calisiyorum efendim, biraz bekleyelim.", ASISTAN);
            String word = nlp.gettheNeedWord(myWord);


            addTextToList(answerProcess.getInfo(word), ASISTAN);


        } else if (isBilgiIstedi(myWord)) {

            addTextToList(asistan.ihtiyac(), ASISTAN);

            addTextToList(myWord, KULLANICI);

            addTextToList(getInfoOneByOne(myWord)[0], ASISTAN);
            addTextToList(getInfoOneByOne(myWord)[1], ASISTAN);

            getTimer();

        } else if (!answerProcess.isQuestion(myWord)) {

            addTextToList(asistan.ihtiyac(), ASISTAN);
            addTextToList(myWord, KULLANICI);
            String anprocess = answerProcess.userChatNeeds(myWord);


            chatProcess(anprocess);


        } else if (answerProcess.isNeyapiyor(myWord)) {

            addTextToList(asistan.showNeyapiyorMessages(), ASISTAN);

        }


        else {
            addTextToList(myWord, KULLANICI);
            addTextToList(asistan.anlayamadim(), ASISTAN);


        }


        /**
         * veri listesini en altta olucak sekilde gunceller
         */
        adapter.notifyDataSetChanged();
        chatListView.setSelection(chatList.size() - 1);

    }

    //speech recognizer
    public Intent intent;
    public static final int request_code_voice = 1;
    /**
     * sesli mesaj gonderme butonu listener
     */

    String speechString = "";

    public void sendVoiceMessage(View v) {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // intent i oluşturduk sesi tanıyabilmesi için
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        try {


            startActivityForResult(intent, request_code_voice);

            // activityi başlattık belirlediğimiz sabit değer ile birlikte


        } catch (ActivityNotFoundException e) {
            // activity bulunamadığı zaman hatayı göstermek için alert dialog kullandım
            e.printStackTrace();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainChat.this);
            builder.setMessage("Üzgünüz Telefonunuz bu sistemi desteklemiyor!!!")
                    .setTitle("Hata")
                    .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();


            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case request_code_voice: {

                if (resultCode == RESULT_OK && data != null) {
                    addTextToList(asistan.ihtiyac(), ASISTAN);
                    ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechString = speech.get(0);


                    String anprocess = null;
                    try {
                        addTextToList(speechString, KULLANICI);

                         if (isBilgiIstedi(speechString)) {


                            addTextToList(speechString, KULLANICI);


                            //getTechInfoFromWebsite(bilgiver(myWord));

                            addTextToList(getInfoOneByOne(speechString)[0], ASISTAN);
                            addTextToList(getInfoOneByOne(speechString)[1], ASISTAN);
                            //   addTextToList(bilgi, ASISTAN);



                        }

                        else{
                        anprocess = answerProcess.userChatNeeds(speechString);
                        chatProcess(anprocess);}

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    adapter.notifyDataSetChanged();
                    chatListView.setSelection(chatList.size() - 1);

                }
                break;
            }

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deneme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    boolean isPressedToGetMessages = false;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mesajKaydet) {
            // Handle the camera action
            try {
                mesajlariKaydet(chatList);

                Toast.makeText(this, "mesajlarınız kaydedildi", Toast.LENGTH_SHORT).show();
                isPressedToGetMessages = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_mesajlariYukle) {

            try {

                setChatList(mesajlariAl());

                Toast.makeText(this, "mesajlarınız yüklendi", Toast.LENGTH_SHORT).show();
                isPressedToGetMessages = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_mesajsil) {

            adapter.clearData();
            adapter.notifyDataSetChanged();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void addTextToList(String text, int who) {


        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("person", who);
        map.put("image", who == KULLANICI ? R.drawable.person : R.drawable.contact1);
        map.put("text", text);
        chatList.add(map);
    }

    protected void addHashToList(ArrayList<HashMap<String, Object>> maps) {

        for (HashMap<String, Object> map : maps) {
            chatList.add(map);
        }
    }


    protected void mesajlariKaydet(ArrayList<HashMap<String, Object>> hash) throws IOException {

        FileOutputStream fileOutputStream = openFileOutput("personobject", Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(hash);
        objectOutputStream.close();
    }


    protected ArrayList<HashMap<String, Object>> mesajlariAl() throws IOException, ClassNotFoundException {

        ArrayList<HashMap<String, Object>> newList;

        FileInputStream fileInputStream = openFileInput("personobject");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        newList = new ArrayList<>((ArrayList<HashMap<String, Object>>) objectInputStream.readObject());
        return newList;
    }


    public void ilkAsamaMesajlar() {
        addTextToList("Merhaba " + kullaniciAd + " Hoşgeldin", ASISTAN);
        addTextToList("Önce bana bir isim bulalım ", ASISTAN);
        addTextToList("Bana nasıl seslenmek istersiniz? ", ASISTAN);

    }


    TimePickerDialog timePickerDialog;
    final static int RQS_1 = 1;


    public void alarmAyarla() {

        openTimePickerDialog(false);

    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            setAlarm(calSet);
        }
    };

        public void setAlarm(Calendar calSet) {

            Intent intent = new Intent(this, alarmEkrani.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am =
                    (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                    pendingIntent);

        }

    public void chatProcess(String cumle) {

        switch (cumle) {


            case "alarm":

                alarmAyarla();
                addTextToList("alarm olusruldu, umarim etkisi olur :) ", ASISTAN);
                break;

            case "ara":

                String kim = answerProcess.kim;

                aramaYap(getPhoneNumber(kim, MainChat.this));
                addTextToList("arama gerceklestiriliyor...", ASISTAN);
                addTextToList("umarim guzel bir konusma gerceklestirilmistir.", ASISTAN);

                break;

            case "mesaj":

                gonderSMS(getPhoneNumber(answerProcess.mesaj[0], MainChat.this), answerProcess.mesaj[1]);

                addTextToList("mesajiniz gonderilmistir. :) ", ASISTAN);
                break;

            case "hava":
                addTextToList(currentLatitude + " " + currentLongitude, ASISTAN);

                getWeather();

                addTextToList(weather, ASISTAN);

                break;

          /*  case "bilgi":
                addTextToList("elimde soyle bir bilgi var!", ASISTAN);
                addTextToList(getInfoOneByOne(), ASISTAN);

                break;*/


        }

    }


    String weather = "";

    public String getWeather() {


        WeatherFunction.placeIdTask asyncTask = new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String weather_city, String weather_temperature, String weather_updatedOn) {
                weather = "konumuz olan " + weather_city + " zaman itibari ile " + weather_temperature + " derece sicaklik olculmustur";

            }
        });
        asyncTask.execute(currentLatitude + "", currentLongitude + "");

        return weather;
    }


    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "Unsaved";
        return ret;
    }

    public static final int PERMISSION_REQUEST_CONTACT = 100;

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void aramaYap(String numara) {

        Intent aramaIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numara));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 10);
            return;
        } else {

            startActivity(aramaIntent);
        }


    }

    public void gonderSMS(String numara, String mesaj) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(numara, null, mesaj, null, null);


    }

    private static final int messageReq = 2;

    public void messagePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, messageReq);

    }

    private boolean isPermission() {
        int resultSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        return resultSms == PackageManager.PERMISSION_GRANTED;
    }

    //haber listemiz
    ArrayList<HaberModel> teknolojiListesi, sporListesi, gundemListesi, kulturListesi;

    protected void getTechInfoFromWebsite(String topic) {


        teknolojiListesi = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Lütfen bekleyiniz, bilgileri getiriyorum...");


        final Gson gson = new Gson();
        String url = "https://api.hurriyet.com.tr/v1/articles?%24filter=Path%20eq%20'/" + topic + "/'";

        RequestQueue requestQueue = Volley.newRequestQueue(MainChat.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {/*dönen cevabı parse edeceğimiz yer*/
                        pd.dismiss();
                        JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            HaberModel haberModel = gson.fromJson(jsonArray.get(i), HaberModel.class);
                            teknolojiListesi.add(haberModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {/*hata mesajını alacağımız yer*/
                Log.e("responseErr", error.getMessage());
                pd.dismiss();
            }
        }) {
            //sağ tık->generate->override methods->getHeaders()'ı seçiyoruz
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accept", "application/json");
                params.put("apikey", "9118c37f952a4559b7ae0c4472cd9ef1");
                return params;
            }
        };
        requestQueue.add(stringRequest);
        pd.show();


    }

    protected void getSporInfoFromWebsite(String topic) {


        sporListesi = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Lütfen bekleyiniz, bilgileri getiriyorum...");


        final Gson gson = new Gson();
        String url = "https://api.hurriyet.com.tr/v1/articles?%24filter=Path%20eq%20'/" + topic + "/'";

        RequestQueue requestQueue = Volley.newRequestQueue(MainChat.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {/*dönen cevabı parse edeceğimiz yer*/
                        pd.dismiss();
                        JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            HaberModel haberModel = gson.fromJson(jsonArray.get(i), HaberModel.class);
                            sporListesi.add(haberModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {/*hata mesajını alacağımız yer*/
                Log.e("responseErr", error.getMessage());
                pd.dismiss();
            }
        }) {
            //sağ tık->generate->override methods->getHeaders()'ı seçiyoruz
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accept", "application/json");
                params.put("apikey", "9118c37f952a4559b7ae0c4472cd9ef1");
                return params;
            }
        };
        requestQueue.add(stringRequest);
        pd.show();


    }

    protected void getGundemInfoFromWebsite(String topic) {


        gundemListesi = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Lütfen bekleyiniz, bilgileri getiriyorum...");


        final Gson gson = new Gson();
        String url = "https://api.hurriyet.com.tr/v1/articles?%24filter=Path%20eq%20'/" + topic + "/'";

        RequestQueue requestQueue = Volley.newRequestQueue(MainChat.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {/*dönen cevabı parse edeceğimiz yer*/
                        pd.dismiss();
                        JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            HaberModel haberModel = gson.fromJson(jsonArray.get(i), HaberModel.class);
                            gundemListesi.add(haberModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {/*hata mesajını alacağımız yer*/
                Log.e("responseErr", error.getMessage());
                pd.dismiss();
            }
        }) {
            //sağ tık->generate->override methods->getHeaders()'ı seçiyoruz
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accept", "application/json");
                params.put("apikey", "9118c37f952a4559b7ae0c4472cd9ef1");
                return params;
            }
        };
        requestQueue.add(stringRequest);
        pd.show();


    }

    protected void getKulturInfoFromWebsite(String topic) {


        kulturListesi = new ArrayList<>();

        final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Lütfen bekleyiniz, bilgileri getiriyorum...");


        final Gson gson = new Gson();
        String url = "https://api.hurriyet.com.tr/v1/articles?%24filter=Path%20eq%20'/" + topic + "/'";

        RequestQueue requestQueue = Volley.newRequestQueue(MainChat.this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {/*dönen cevabı parse edeceğimiz yer*/
                        pd.dismiss();
                        JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            HaberModel haberModel = gson.fromJson(jsonArray.get(i), HaberModel.class);
                            kulturListesi.add(haberModel);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {/*hata mesajını alacağımız yer*/
                Log.e("responseErr", error.getMessage());
                pd.dismiss();
            }
        }) {
            //sağ tık->generate->override methods->getHeaders()'ı seçiyoruz
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("accept", "application/json");
                params.put("apikey", "9118c37f952a4559b7ae0c4472cd9ef1");
                return params;
            }
        };
        requestQueue.add(stringRequest);
        pd.show();


    }


    int index = 0;

    public String[] getInfoOneByOne(String cumle) {

        String info = "";
        String puan = "";

        String bilgi = bilgiver(cumle);


        switch (bilgi) {

            case "teknoloji":
                if (index > teknolojiListesi.size()) {

                    index = 0;
                }

                info = teknolojiListesi.get(index).getDescription();

                index++;

                puan = asistan.pointGelistir("teknoloji");

                break;

            case "spor":
                if (index > sporListesi.size()) {

                    index = 0;
                }

                info = sporListesi.get(index).getDescription();

                index++;

                puan = asistan.pointGelistir("cevresel");

                break;

            case "politika":

                if (index > gundemListesi.size()) {

                    index = 0;
                }

                info = gundemListesi.get(index).getDescription();
                puan = asistan.pointGelistir("cevresel");
                puan = asistan.pointGelistir("kultursanat");
                index++;

                break;
            case "kultur":
                if (index > kulturListesi.size()) {

                    index = 0;
                }

                info = kulturListesi.get(index).getDescription();

                index++;
                puan = asistan.pointGelistir("kultursanat");
                break;


        }

/*
        if (index > teknolojiListesi.size()) {

            index = 0;
        }

        String info = teknolojiListesi.get(index).getDescription();

        index++;
        teknolojiListesi.clear();*/

        String[] allInfo = {"", ""};
        allInfo[0] = info;
        allInfo[1] = puan;

        return allInfo;
    }


    boolean isBilgiIstedi(String bilgi) {
        boolean isOk = false;


        String s = bilgi;

        int index = s.indexOf(' ');

        s = s.substring(index + 1, s.length());

        if (s.equals("hakkında bilgi ver")) {
            isOk = true;

        }


        return isOk;
    }


    /**
     * ne konuda bilgi onu tespit ediyor
     *
     * @param bilgi
     * @return
     */


    String bilgiver(String bilgi) {

        String s = bilgi;

        int index = s.indexOf(' ');

        return s.substring(0, index);
    }


    public void setNotification() {

        int i = 0;

           /* Bildirime tıklanınca açılacak activity */
        Intent Oku_intent = new Intent(this, MainChat.class);
        PendingIntent p_oku = PendingIntent.getActivity(this, 0, Oku_intent, 0);
/* Sil butonu için Broadcast mesajı  */
        Intent Sil_intent = new Intent();
        Sil_intent.setAction("com.deneme.uyg.DEL_INTENT");

        PendingIntent p_sil = PendingIntent.getBroadcast(this, 0, Sil_intent, 0);
/* Bildirimi oluşturmaya başlıyoruz */

        Notification bildirim = new Notification.Builder(this)

                .setContentTitle("yeni bilginiz var!").setContentText(teknolojiListesi.get(i+1).getDescription())
                .setSmallIcon(R.drawable.logo).setContentIntent(p_oku)
                .addAction(R.drawable.common_google_signin_btn_icon_light, "Sil", p_sil).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        bildirim.flags |= Notification.FLAG_AUTO_CANCEL;
        bildirim.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(0, bildirim);
        i++;

    }


}











