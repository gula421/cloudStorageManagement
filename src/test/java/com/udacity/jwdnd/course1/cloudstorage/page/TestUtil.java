package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;

import java.security.SecureRandom;
import java.util.Base64;

public class TestUtil {

    public static String url = "http://hello.com";
    public static String url2 = "http://zoo.com";
    public static String url3 = "http://garden.com";
    public static String url4 = "http://kitchen.com";
    public static String username = "fruit";
    public static String username2 = "animal";
    public static String username3 = "plant";
    public static String username4 = "food";
    public static String password = "apple";
    public static String password2 = "dog";
    public static String password3 = "flower";
    public static String password4 = "pie";

    public static String createEncodedKey(){
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        return encodedKey;
    }
}
