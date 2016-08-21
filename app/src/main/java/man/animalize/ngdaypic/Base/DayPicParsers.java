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

        String url = "http://photography.nationalgeographic.com/photography/photo-of-the-day/";
        String html = f.getString(url, "UTF-8");
        if (html == null)
            throw new Exception("无法下载英文版html");

        //再改版别忘了这个!
        //Log.i(TAG, html);

        String pstr = "<meta name=\"twitter:title\" content=\"([^\"]+)\">.*?"
                + "<meta name=\"twitter:description\" content=\"([^\"]+)\">.*?"
                + "<meta property=\"og:image\" content=\"([^\"]+)\"/>.*?"
                + "<meta property=\"gsa_publish_date\" content=\"([^\"]+)\"/>";

        Pattern pattern = Pattern.compile(pstr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            item.setPicurl(matcher.group(3));
            item.setTitle(matcher.group(1));
            item.setDate(matcher.group(4));
            item.setDescrip(matcher.group(2));
        } else {
            throw new Exception("无法用正则解析英文版页面");
        }
/*        Log.i(TAG, item.getTitle());
        Log.i(TAG, item.getPicurl());
        Log.i(TAG, item.getDate());
        Log.i(TAG, item.getDescrip());*/

        return item;
    }

    // 中文网站
    public static DayPicItem CNNGDayPicParser(Fetcher f) throws Exception {
        Log.i(TAG, "开始解析");
        DayPicItem item = new DayPicItem();

        String url = "http://www.nationalgeographic.com.cn/photography/photo_of_the_day/";
        String html = f.getString(url, "UTF-8");
        if (html == null)
            throw new Exception("无法下载中文版html, 1");

        String pstr = "<section class=\"show-imgs\">.*?"
                + "<a href=\"([^\"]+)\"><img";

        Pattern pattern = Pattern.compile(pstr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new Exception("无法用正则解析, cn1");
        }

        String url2 = "http://www.nationalgeographic.com.cn" +
                matcher.group(1);

        html = f.getString(url2, "UTF-8");
        if (html == null)
            throw new Exception("无法下载中文版html, 2");


        pstr = "<ul class=\"cont picbig\".*?" +
                "src=\"([^\"]+)\".*?" +
                "<div class=\"title\">(?:每日一图：)?(.*?)</div>\\s*" +
                "<span class=\"time\">(?:发布时间：)?(.*?)</span>.*?" +
                "<div class=\"public-p.*?>(.*?)" +
                "</div>\\s*<p";

        pattern = Pattern.compile(pstr, Pattern.DOTALL);
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new Exception("无法用正则解析, cn2");
        }

        item.setPicurl(matcher.group(1));
        item.setTitle(removeTag(matcher.group(2)));
        item.setDate(matcher.group(3));
        item.setDescrip(removeTag(matcher.group(4)));

        // 忽略“编辑之选”
        if (item.getTitle().startsWith("编辑之选")) {
            throw new Exception("编辑之选");
        }

        //Log.i(TAG, item.getTitle()+item.getDate()+item.getDescrip());

        return item;
    }
}
