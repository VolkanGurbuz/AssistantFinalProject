package com.volkangurbuz.intro;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by volkan on 09.05.2017.
 */

public class NaturalLanguageProcess {

    HttpClient client;
    HttpPost post;
    List<NameValuePair> parameters;
    final private String token = "mmllH8BPGKgwbCtYsdLUMpBiEFuAUD5v";


    //final String[] araclar = {"ner", "morphanalyzer", "isturkish", "morphgenerator", "tokenizer", "normalize", "deasciifier", "Vowelizer"};


    //that array for the questions types
    final String[] sorutipleri = {" ", "nedir", "nerede", "kim", "kimdir"};

    public NaturalLanguageProcess() {
        client = new DefaultHttpClient();
        post = new HttpPost("http://tools.nlp.itu.edu.tr/SimpleApi");
        parameters = new ArrayList<NameValuePair>(3);
    }


    /**
     * the method
     * Called when the the message enter from the user, we should check the word isTurkish or not,
     * after that we return the result for the message to the user if it is not.
     *
     * @return
     * @throws IOException
     */
    public String isTurkish(String sentences) throws IOException {

        parameters.clear();

        parameters.add(new BasicNameValuePair("tool", "isturkish"));
        parameters.add(new BasicNameValuePair("input", sentences));
        parameters.add(new BasicNameValuePair("token", token));

        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
        HttpResponse resp = null;
        try {
            resp = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String normalizationSentence = EntityUtils.toString(resp.getEntity());
        return normalizationSentence;

    }


    public String normalization(String sentences) throws IOException {
        parameters.clear();

        parameters.add(new BasicNameValuePair("tool", "normalize"));
        parameters.add(new BasicNameValuePair("input", sentences));
        parameters.add(new BasicNameValuePair("token", token));

        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
        HttpResponse resp = null;
        try {
            resp = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String normalizationSentence = EntityUtils.toString(resp.getEntity());
        return normalizationSentence;

    }


    /**
     * tum cumlelerin turkce olup olmad?g?na bak
     * eger turkuce kelime yok ise bu yabanci kelimeleri  bir diziye at
     * @param cumle
     * @return
     * @throws IOException
     */

    public ArrayList<String> noTurkishForAllSentence(String cumle) throws IOException {

        ArrayList<String> list = new ArrayList<>();
        list.clear();
        StringTokenizer st = new StringTokenizer(cumle, " ");

        while (st.hasMoreTokens()) {

            String kelime = normalization(st.nextToken());
            if (isTurkish(kelime).equals("false")) {

                list.add(kelime);
            }

        }

        return list;

    }


    public String morphologicAnalizer(String sentences) throws IOException {
        parameters.clear();

        parameters.add(new BasicNameValuePair("tool", "morphanalyzer"));
        parameters.add(new BasicNameValuePair("input", sentences));
        parameters.add(new BasicNameValuePair("token", token));

        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
        HttpResponse resp = null;
        try {
            resp = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String normalizationSentence = EntityUtils.toString(resp.getEntity());
        return normalizationSentence;

    }


    public String namedEntityRecognizer(String sentences) throws IOException {
        parameters.clear();

        parameters.add(new BasicNameValuePair("tool", "ner"));
        parameters.add(new BasicNameValuePair("input", "<DOC> <DOC>+BDTag" + sentences + "<DOC> <DOC>+EDTag"));
        parameters.add(new BasicNameValuePair("token", token));

        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
        HttpResponse resp = null;
        try {
            resp = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String normalizationSentence = EntityUtils.toString(resp.getEntity());
        return normalizationSentence;

    }

    /**
     * get question type
     */

    String neSorusu = null;

    public String soruTipiBul(String cumle) throws IOException {
        String soruTipi = " ";
        StringTokenizer st = new StringTokenizer(cumle, " ");
        int soruTipiIndex = 0;

        if (isMeaning(cumle)) {

            while (st.hasMoreTokens()) {

                String kelime = st.nextToken();

                for (int i = 0; i < sorutipleri.length; i++) {

                    if (kelime.equals(sorutipleri[i])) {
                        soruTipiIndex = i;
                        soruTipi = sorutipleri[soruTipiIndex];
                        neSorusu = cumle.replace(soruTipi, "");
                    }

                }


            }

        }


        return soruTipi;
    }


    public String[] entities = {"person", "location", "organization", "date", "time", "money", "percentage"};


    private AnswerProcess answerProcess;

    public String cevapBul(String cumle) throws IOException {
        String tip = soruTipiBul(cumle);
        String cevap = "";

        answerProcess = new AnswerProcess();
        String morp = morphologicAnalizer(cumle);
        String entity = namedEntityRecognizer(morp);

        // String pipeline = pipelineNoisy(chat.extract);


        switch (tip) {

            case "nedir":
                // cevap = chat.ilkCumleAl(chat.extract);

                cevap = answerProcess.getInfo(cumle);


                break;


            case "kim":

               /* morp = morphologicAnalizer(cumle);
                entity = namedEntityRecognizer(morp);

                for (int i = 0; i < entities.length; i++) {

                    if (entity.contains("B-PERSON") && pipeline.contains("SUBJECT")) {

                        //cevap = chat.ilkCumleAl(chat.extract);
                    }

                }*/

                break;

            case "nerede":
            case "ne zaman":

                morp = morphologicAnalizer(cumle);
                entity = namedEntityRecognizer(morp);

                for (int i = 0; i < entities.length; i++) {

                    if (entity.contains("B-TIME") || entity.contains("B-LOCATION")) {

                        // cevap = chat.ilkCumleAl(chat.extract);
                    }


                }

                break;
        }
        return cevap;

    }


    /**
     * gelen  cumle icerisinde anlamli kelimeler var mi yok mu bunlari  buluyor
     */

    public String yabanciKelimeler = "";


    public boolean isMeaning(String mesaj) {
        boolean isOk = false;
        try {

            if (!noTurkishForAllSentence(mesaj).isEmpty()) {

                for (String s : noTurkishForAllSentence(mesaj)) {
                    yabanciKelimeler += s + " ";

                }

            } else
                isOk = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOk;
    }


    /**
     * kelımeyı turkce karakterlerden arındırıyor
     */

    public String getEnglishforTurkish(String kelimecik) {

        String newWord = kelimecik.toLowerCase();

        newWord = newWord.replace('ö', 'o');
        newWord = newWord.replace('ü', 'u');
        newWord = newWord.replace('?', 'g');
        newWord = newWord.replace('?', 's');
        newWord = newWord.replace('?', 'i');
        newWord = newWord.replace('ç', 'c');
        return newWord;
    }

    public String gettheNeedWord(String sentence) throws IOException {

        String deneme= getEnglishforTurkish(sentence.replace(soruTipiBul(sentence), " "));
        String[] parsed = deneme.split("[ ]+");
        int wordCount = parsed.length;
        String newString = "";

        if (wordCount > 1) {

            for (String x : parsed) {

                newString += x + "_";

            }

            newString = newString.substring(0, newString.length() - 1);


        }

        else {

            newString = getEnglishforTurkish(sentence.replace(soruTipiBul(sentence), " "));
        }
        return newString;

    }


}
