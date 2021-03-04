package com.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 01, 2021
 */
public class Crawler {

    private OkHttpClient mOkHttpClient = new OkHttpClient();
    private Pattern License = Pattern.compile("^License$");
    private Pattern HomePage = Pattern.compile("^HomePage$");

    public static void main(String[] args) throws IOException {
//        parse(new Crawler().get());
    }

    public Tuple2<String, String> get(URL url) throws IOException {
        Request request = new Request.Builder().get().url(url).build();
        return parse(mOkHttpClient.newCall(request).execute());
    }

    public Response get() throws IOException {
        URL url = new URL("https", "mvnrepository.com", "/artifact/org.jsoup/jsoup/1.13.1");
        Request request = new Request.Builder().get().url(url).build();
        return mOkHttpClient.newCall(request).execute();
    }

    private Tuple2<String, String> parse(Response response) throws IOException {
        Document document = Jsoup.parse(response.body().string());
        String license = ((TextNode) document.getElementsMatchingOwnText(License).first().nextElementSibling().child(0).childNode(0)).text();
        String website = null;
        try {
            website = document.getElementsMatchingOwnText(HomePage).first().nextElementSibling().child(0).attr("href");
        } catch (Exception e) {
            website = response.request().url().toString();
        }
        return Tuple.of(license, website);
    }
}
