package TestCase;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nam on 20/04/2017.
 */
public class CMD {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        list.add("aaaaa");
        list.add("bbbbb");
        Gson gson = new Gson();
        String str = gson.toJson(list);
        System.out.println(str);
        List<String> l = gson.fromJson(str,ArrayList.class);
        System.out.println(l.get(0));
        System.out.println(l.get(1));
        System.out.println(l.size());
    }
}
