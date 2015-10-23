package man.animalize.ngdaypic.Base;


import java.io.Serializable;

public class DayPicItem implements Serializable {
    private long _id = -1;
    private String picurl;
    private byte[] icon;

    private String title;
    private String date;
    private String descrip;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "标题：" + title +
                "\n日期：" + date +
                "\n描述：" + descrip;
    }
}
