package man.animalize.ngdaypic.Base;


import android.text.Html;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import man.animalize.ngdaypic.Utility.Fetcher;

public class DayPicParsers {
    private static final String TAG = "DayPicParsers";

    // 去标签、unescape
    private static String removeTag(String html) {
        String s = html.replaceAll("<(?:[^\"'>]|\"[^\"]*\"|'[^']*')*>", "");
        return Html.fromHtml(s).toString().trim();
    }

    // 英文网站
    public static DayPicItem NGDayPicParser(Fetcher f) throws Exception {
        Log.i(TAG, "开始解析");
        DayPicItem item = new DayPicItem();

        String url = "http://photography.nationalgeographic.com/"
                + "photography/photo-of-the-day/";
        String html = f.getString(url, "UTF-8");
        if (html == null)
            throw new Exception("无法获得html");

        String pstr = "<div class=\"primary_photo\">.*?"
                + "<img src=\"([^\"]+)\".*?"
                + "<div id=\"caption\">\\s*"
                + "<p class=\"publication_time\">(.*?)</p>\\s*"
                + "<h2>(.*?)</h2>.*?"
                + "</p>\\s*<p[^>]*>(.*?)<!-- .article_text-->";

        Pattern pattern = Pattern.compile(pstr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            item.setPicurl("http:" + matcher.group(1));
            item.setTitle(removeTag(matcher.group(3)).replaceAll("[\\[\\]]", ""));
            item.setDate(removeTag(matcher.group(2)));
            item.setDescrip(removeTag(matcher.group(4)).replaceAll("[\\[\\]]", ""));
        } else {
            throw new Exception("无法用正则解析");
        }

        return item;
    }

    // 中文网站
    public static DayPicItem CNNGDayPicParser(Fetcher f) throws Exception {
        Log.i(TAG, "开始解析");
        DayPicItem item = new DayPicItem();

        String url = "http://www.nationalgeographic.com.cn/photography/";
        String html = f.getString(url, "UTF-8");
        if (html == null)
            throw new Exception("无法获得html,cn1");

        String pstr = "<p class=\"every-pic-p\">.*?"
                + "<a href=\"([^\"]+)\">";

        Pattern pattern = Pattern.compile(pstr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new Exception("无法用正则解析,cn1");
        }

        String url2 = "http://www.nationalgeographic.com.cn" +
                matcher.group(1);

        html = f.getString(url2, "UTF-8");
        if (html == null)
            throw new Exception("无法获得html,cn2");


        pstr = "<div class=\"title\">(?:每日一图：)?(.*?)</div>\\s*" +
                "<span class=\"time\">(?:发布时间：)?(.*?)</span>.*?" +
                "<div class=\"public-p.*?>(.*?)" +
                "</div>\\s*<p";

        pattern = Pattern.compile(pstr, Pattern.DOTALL);
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new Exception("无法用正则解析,cn1");
        }

        item.setTitle(removeTag(matcher.group(1)));
        item.setDate(matcher.group(2));
        item.setDescrip(removeTag(matcher.group(3)));

        // 忽略“编辑之选”
        if (item.getTitle().startsWith("编辑之选")) {
            throw new Exception("编辑之选");
        }

        //Log.i(TAG, item.getTitle()+item.getDate()+item.getDescrip());

        return item;
    }
}
