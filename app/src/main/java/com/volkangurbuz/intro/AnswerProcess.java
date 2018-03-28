package com.volkangurbuz.intro;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by VolkanGurbuz on 12/4/2017.
 */

public class AnswerProcess extends AppCompatActivity {

    public final static String[] ANSWERWORDS = {"alarm", "hava", "bilgi", "ara", "mesaj"};
    public final static String[] CUMLESONU = {"yap", "ver", "kur", "yolla"};

    public final static String[] ISTEKTIPI = {"teknoloji", "spor", "gundem", "seyahat"};

    public final static int ASISTAN = 1;
    public final static int KULLANICI = 0;


    private NaturalLanguageProcess nlp;
    private Asistan asistan;
    private MainChat mainChat;

    public AnswerProcess() {
        nlp = new NaturalLanguageProcess();
        asistan = new Asistan();
        mainChat = new MainChat();
    }

    public static int getASISTAN() {
        return ASISTAN;
    }

    public static int getKULLANICI() {
        return KULLANICI;
    }

    public NaturalLanguageProcess getNlp() {
        return nlp;
    }

    public void setNlp(NaturalLanguageProcess nlp) {
        this.nlp = nlp;
    }

    public Asistan getAsistan() {
        return asistan;
    }

    public void setAsistan(Asistan asistan) {
        this.asistan = asistan;
    }


    public String getInfo(String what) {

        String url = "https://tr.wikipedia.org/wiki/" + what;

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements paragraphs = doc.select(".mw-content-ltr p, .mw-content-ltr li");

        Element firstParagraph = paragraphs.first();
        Element lastParagraph = paragraphs.last();
        Element p;
        int i = 1;
        p = firstParagraph;

        return p.text();
    }


    /**
     * chech the sentence's words are turkish or not
     *
     * @param mesaj
     * @return
     */


    boolean isQuestion(String sentence) throws IOException {

        return !nlp.soruTipiBul(sentence).equals(" ");
    }

    /**
     * kullanici soru sormuyor ise ihtiyaci olan ne olnu tespit et, hava, alarm, bilgi ver vs.
     *
     * @param sentence
     * @return
     */
    String[] mesaj = {"", ""};
    public String kim = "";

    public String userChatNeeds(String cumle) throws IOException {

        String userNeeds = "";
        int index = -1;

        StringTokenizer st = new StringTokenizer(cumle, " ");

        while (st.hasMoreElements()) {
            String s = st.nextToken();
            for (int i = 0; i < ANSWERWORDS.length; i++) {

                if (s.equals(ANSWERWORDS[i])) {

                    index = i;

                    if (s.equals("ara")) {
                        userNeeds = ANSWERWORDS[index];
                    }

                    if (s.equals("mesaj")) {
                        userNeeds = ANSWERWORDS[index];
                        mesaj[0] = getMesaj(cumle)[0];
                        mesaj[1] = getMesaj(cumle)[1];
                    }

                } else {
                    kim = s;
                }

            }

            for (int i = 0; i < CUMLESONU.length; i++) {

                if (s.equals(CUMLESONU[i])) {

                    userNeeds = ANSWERWORDS[index];

                }

            }

        }


        return userNeeds;
    }

    String[] getMesaj(String cumle) {
        String[] mesaj = {"", ""};

        String[] split = cumle.split(" ");
        mesaj[0] = split[1]; //kim
        mesaj[1] = split[2]; ///mesaj
        return mesaj;
    }


    public boolean isNeyapiyor(String sentence) {

        boolean isOkey = false;

        if (sentence.equals("ne yapiyorsun") || sentence.equals("ne yapıyorsun")) {

            isOkey = true;
        }

        return isOkey;
    }




}
