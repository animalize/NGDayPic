package man.animalize.ngdaypic;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.MyDBHelper;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MainListFragment extends ListFragment {

    private static final String TAG = "MainListFragment";
    // 用于判断是否在运行
    private static int objectCount = 0;
    private MyDBHelper.ItemCursor mCursor;
    private ItemCursorAdapter mAdapter;
    private LoaderManager.LoaderCallbacks callbacks =
            new LoaderManager.LoaderCallbacks<MyDBHelper.ItemCursor>() {
                // 创建Loader后，开始Load
                @Override
                public Loader<MyDBHelper.ItemCursor> onCreateLoader(int id, Bundle args) {

                    return (Loader) new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            MyDBHelper db = MyDBHelper.getInstance(getActivity());
                            synchronized (db) {
                                return db.queryItems();
                            }
                        }
                    };
                }


                @Override
                public void onLoaderReset(android.support.v4.content.Loader<MyDBHelper.ItemCursor> loader) {

                }

                // Load结束
                @Override
                public void onLoadFinished(android.support.v4.content.Loader<MyDBHelper.ItemCursor> loader, MyDBHelper.ItemCursor data) {
                    mCursor = data;

                    // 为adapter设置cursor
                    if (mAdapter == null) {
                        mAdapter = new ItemCursorAdapter(getActivity(), mCursor);
                        setListAdapter(mAdapter);
                    } else
                        mAdapter.changeCursor(mCursor);

                    Log.i(TAG, "onLoadFinished，当前数量" + mCursor.getCount());
                }

            };

    // 广播接收器。 当数据库改变时，用doQuery()刷新显示列表
    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "BroadcastReceiver,onReceive");
            doQuery();
        }
    };

    public MainListFragment() {
    }

    public static boolean isRunning() {
        return objectCount != 0;
    }

    @Override
    public void onStart() {
        objectCount += 1;
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        objectCount -= 1;
    }

    // 刷新显示列表
    // LoaderManager会使用LoaderManager.LoaderCallbacks完成具体任务
    private void doQuery() {
        LoaderManager lm = getLoaderManager();
        lm.restartLoader(1, null, callbacks);
        Log.i(TAG, "启动loader");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("条目列表");

        // 注册广播接收器
        // 接到此广播时，会刷新显示列表
        IntentFilter itf = new IntentFilter(BackService.FILTER);
        getActivity().registerReceiver(mReciver, itf);

        // 刷新显示列表
        doQuery();

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        // 注销广播接收器
        getActivity().unregisterReceiver(mReciver);

        // 关闭cursor
        if (mCursor != null) {
            mCursor.close();
        }

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 取消通知栏
        NotificationManager nm =
                (NotificationManager) inflater.getContext().getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // 从cursor得到item
        // int temp_posi = mCursor.getPosition();
        mCursor.moveToPosition(position);
        DayPicItem item = mCursor.getItem();
        //mCursor.moveToPosition(temp_posi);

        // start activity
        Intent i = new Intent(getActivity(), ItemPagerActivity.class);
        i.putExtra("item", item);
        startActivity(i);
    }

    // 创建菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main_list, menu);
    }

    // 准备菜单内容
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // 刷新间隔
        MenuItem mi = menu.findItem(R.id.interval);
        mi.setTitle("刷新间隔" + BackService.getIntervalHour() + "小时");

        // 服务状态
        mi = menu.findItem(R.id.toggleid);
        if (BackService.isServiceAlarmOn(getActivity()))
            mi.setTitle("停止后台服务");
        else
            mi.setTitle("启动后台服务");
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refreshid:
                Intent i = new Intent(getActivity(), BackService.class);
                i.putExtra("allow3g", true);
                getActivity().startService(i);

                return true;

            case R.id.toggleid:
                boolean is_on = BackService.isServiceAlarmOn(getActivity());
                BackService.setServiceAlarm(getActivity(), !is_on);
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putBoolean("should_on", !is_on).apply();

                getActivity().invalidateOptionsMenu();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView date;
    }

    private class ItemCursorAdapter extends CursorAdapter {
        // 小图标的点击处理
        private MyClickListener myListener = new MyClickListener();

        public ItemCursorAdapter(Context context, MyDBHelper.ItemCursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = inflater.inflate(R.layout.list_item_1,
                    parent, false);

            // 点击图片的事件
            ImageView icon = (ImageView) v.findViewById(R.id.imgviewid);
            icon.setOnClickListener(myListener);

            // holder
            ViewHolder holder = new ViewHolder();
            holder.icon = icon;
            holder.title = (TextView) v.findViewById(R.id.texttitleid);
            holder.date = (TextView) v.findViewById(R.id.textdateid);
            v.setTag(holder);

            return v;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            // 从cursor得到item对象
            final DayPicItem item = ((MyDBHelper.ItemCursor) cursor).getItem();
            if (item == null)
                return;

            ViewHolder holder = (ViewHolder) view.getTag();

            // 显示小图标
            byte[] icon = item.getIcon();
            if (icon != null) {
                holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(icon, 0, icon.length));
                holder.icon.setTag((int) item.get_id());
            } else {
                holder.icon.setImageResource(android.R.color.transparent);
                holder.icon.setTag(-1);
            }

            // 标题、日期
            TextView title = holder.title;
            holder.title.setText(" " + item.getTitle());

            TextView date = holder.date;
            holder.date.setText("  " + item.getDate());
        }

        // 图片点击监听器
        private class MyClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                int id = (Integer) v.getTag();
                if (id != -1) {
                    Intent i = new Intent(MainListFragment.this.getActivity(),
                            TouchImageActivity.class);
                    i.putExtra("jpgfilename", id + ".jpg");
                    MainListFragment.this.startActivity(i);
                }
            }
        }
    }

}
