package man.animalize.ngdaypic.Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class MyDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDBHelper";
    private static final int COUNT_LIMIT = 42;
    private static final String DB_NAME = "daypic.sqlite";
    private static final int VERSION = 1;
    private static final String TABLE_DAYPIC = "daypic_tbl";
    private static MyDBHelper singleton;

    private static ItemCursor currentCursor;
    private MyDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static ItemCursor getCurrentCursor() {
        return currentCursor;
    }

    public static MyDBHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new MyDBHelper(context.getApplicationContext());
        }
        return singleton;
    }

    // 首次运行，创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE daypic_tbl (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "date TEXT, " +
                "descrip TEXT, " +
                "icon BLOB);";
        db.execSQL(sql);

        sql = "CREATE INDEX title_idx ON daypic_tbl(title);";
        db.execSQL(sql);

        sql = "CREATE INDEX date_idx ON daypic_tbl(date);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 是否已存在 标题 及 日期
    public boolean isTitleDateExist(DayPicItem item) {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT * FROM daypic_tbl WHERE title=? AND date=?";
        Cursor c = db.rawQuery(sql, new String[]{item.getTitle(), item.getDate()});
        if (c.moveToFirst()) {
            c.close();
            db.close();
            return true;
        } else {
            c.close();
            db.close();
            return false;
        }
    }

    // 写入数据，返回数据id
    public long insertItem(DayPicItem item) {
        // 写入
        ContentValues cv = new ContentValues();
        cv.put("title", item.getTitle());
        cv.put("date", item.getDate());
        cv.put("descrip", item.getDescrip());

        if (item.getIcon() == null)
            cv.putNull("icon");
        else
            cv.put("icon", item.getIcon());

        long id = getWritableDatabase().insert(TABLE_DAYPIC, null, cv);
        Log.i(TAG, "数据库id：" + id);

        return id;
    }

    // 删除旧数据，返回图片id的List
    public ArrayList<Integer> delOlds() {
        SQLiteDatabase db = getWritableDatabase();

        // 得到总行数
        String sql = "SELECT COUNT(*) FROM daypic_tbl";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count <= COUNT_LIMIT)
            return null;

        // 得到要删除的_id
        ArrayList<Integer> list = new ArrayList<Integer>();

        int delcount = count - COUNT_LIMIT;
        sql = "SELECT _id, icon FROM daypic_tbl ORDER BY _id LIMIT ?";
        cursor = db.rawQuery(sql, new String[]{String.valueOf(delcount)});

        cursor.moveToFirst();
        while (true) {
            if (cursor.isAfterLast())
                break;

            int tempid = cursor.getInt(0);
            byte[] tempicon = cursor.getBlob(1);

            Log.i(TAG, "要删除的id:" + tempid);

            if (tempicon != null)
                list.add(tempid);

            cursor.moveToNext();
        }
        cursor.close();

        // 删除数据
        sql = "DELETE FROM daypic_tbl " +
                "WHERE _id IN(SELECT _id FROM daypic_tbl ORDER BY _id LIMIT ?);";
        db.execSQL(sql, new String[]{String.valueOf(delcount)});

        return list;
    }

    // 用于显示列表
    public ItemCursor queryItems() {
        Cursor c = getReadableDatabase().query(
                TABLE_DAYPIC, null, null, null, null, null,
                "_id desc");

        //Log.i(TAG, "数量:" + c.getCount());
        currentCursor = new ItemCursor(c);
        return currentCursor;
    }

    // 专为DayPicItem的CursorWarpper
    public static class ItemCursor extends CursorWrapper {
        public ItemCursor(Cursor cursor) {
            super(cursor);
        }

        public DayPicItem getItem() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            DayPicItem item = new DayPicItem();

            item.set_id(getLong(getColumnIndex("_id")));
            item.setTitle(getString(getColumnIndex("title")));
            item.setDate(getString(getColumnIndex("date")));
            item.setDescrip(getString(getColumnIndex("descrip")));
            item.setIcon(getBlob(getColumnIndex("icon")));

            //Log.i(TAG, "getItem " + getPosition());
            return item;
        }
    }
}
