package com.volkangurbuz.intro;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by volkan on 09.05.2017.
 */

public class Asistan {

    private String asistanName;
    private double asistanVersion;
    protected User u = Register.u;

    private final String[] neyapiyorsun = {"bitlerimi iyilestirmekle mesgulum", "bu aralar deep learning dolaylarinda geziyorum"
            , "ben aslinda yogum be gulum", "iyiyim iyi dipcik gibi, takilmaca", "kitap okuyorum sen ne yapiyorsun?"};
    private final String[] chatSozleri = {"anlaşılan o ki sadece konusmak istiyorsun?", "canın mı sıkkın?", "keyifli gördüm seni"};
    private final String[] anlamadimCevap = {"Üzgünüm Anlayamadım", "Anlaşılmıyorsun bu aralar.", "Neden seni anlayamıyorum?", "Anlamsız mesajların beni yoruyor"};
    private final String[] nasilsinCevap = {"Teşekkür ederim", "Bitlerim iyi durumda.", "Çok mersi"};
    private final String[] yardimCevap = {"Nasıl yardımcı olabilirim?", "Benden ne istersiniz?", "Buyrunuz", "Yine ne istiyorsun, gelme üstüme bu aralar..."};

    public Asistan(double asistanVersion, String asistanName) {
        this.asistanVersion = asistanVersion;
        this.asistanName = asistanName;
    }


    public String[] getChatSozleri() {
        return chatSozleri;
    }

    public Asistan() {
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public String getAsistanName() {
        return asistanName;
    }

    public void setAsistanName(String asistanName) {
        this.asistanName = asistanName;
    }

    public double getAsistanVersion() {
        return asistanVersion;
    }

    public void setAsistanVersion(double asistanVersion) {
        this.asistanVersion = asistanVersion;
    }


    public String anlayamadim() {
        Random r = new Random();

        //  return Resources.getSystem().getStringArray(R.array.anlamadimCevap)[r.nextInt(4)];

        return anlamadimCevap[r.nextInt(4)];
        // return anlamadimCevap[3];
    }

    public String nasilsin() {
        Random r = new Random();

        return nasilsinCevap[r.nextInt(2)];

    }

    public String userProfilDegerlenirme() {

        String userpuanlari = "Kultur ve Sanat puaniniz: " + u.getUserKsanat() + "\n" + "Bilim ve Teknoloji puaniniz  " + u.getUserBTeknoloji() +
                "\n" + "Çevresel ilişki puaniniz:  " + u.getUserOrtam();

        return userpuanlari;
    }

    public String ihtiyac() {
        Random r = new Random();

        return yardimCevap[r.nextInt(4)];


    }

    public String showNeyapiyorMessages() {

        return neyapiyorsun[new Random().nextInt(4)];
    }


    public String pointGelistir(String topic) {
        String message = "";
        if (u.getUserKsanat() == 100) {
            message = "tebrikler kultur sanat puaniniz cok iyi durumda, sevindim";
        }
        if (u.getUserBTeknoloji() == 100) {
            message = "tebrikler teknoloji puaniniz cok iyi durumda, sevindim";
        }
        if (u.getUserOrtam() == 100) {
            message = "tebrikler cevresel iliskiler puaniniz cok iyi durumda, sevindim";
        }


        switch (topic) {
            case "kultursanat":
                u.setUserKsanat(u.getUserKsanat() + 1);
                message = "kultur sanat puaniniz artiyor :) ";
                break;
            case "teknoloji":
                u.setUserKsanat(u.getUserBTeknoloji() + 1);
                message = "teknoloji puaniniz artiyor :) ";
                break;
            case "cevresel":
                u.setUserKsanat(u.getUserOrtam() + 1);
                message = "cevresel iliskiler puaniniz artiyor :) ";
                break;

        }
        return message;

    }


}
