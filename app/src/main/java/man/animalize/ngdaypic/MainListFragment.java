package man.animalize.ngdaypic;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.MyDBHelper;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MainListFragment extends ListFragment {

    private static final String TAG = "MainListFragment";
    // 用于判断是否在运行
    private static int objectCount = 0;
    private MyArrayListAdapter adapter;

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

    private void doQuery() {
        AsyncTask<Void, Integer, ArrayList<DayPicItem>> at = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                return MyDBHelper.getInstance(MainListFragment.this.getActivity())
                        .getArrayList();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<DayPicItem> al = (ArrayList<DayPicItem>) o;
                MainListFragment.this.adapter.setArrayList(al);
            }
        };
        at.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("条目列表");

        // 注册adapter
        adapter = new MyArrayListAdapter(getActivity());
        setListAdapter(adapter);

        // 注册广播接收器
        // 接到此广播时，会刷新显示列表
        IntentFilter itf = new IntentFilter(BackService.FILTER);
        getActivity().registerReceiver(mReciver, itf);

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDestroy() {
        // 注销广播接收器
        getActivity().unregisterReceiver(mReciver);

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 取消通知栏
        NotificationManager nm =
                (NotificationManager) inflater.getContext().getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);

        // 刷新显示列表
        doQuery();

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ViewHolder holder = (ViewHolder) v.getTag();
        CharSequence title = holder.title.getText();

        // start activity
        Intent i = new Intent(getActivity(), ItemPagerActivity.class);
        i.putExtra("posi", position);
        i.putExtra("title", title);
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
                i.putExtra("once", true);
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

    private static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView date;
    }

    private class MyArrayListAdapter extends ArrayAdapter<DayPicItem> {
        private ArrayList<DayPicItem> mList;
        private Context context;
        // 小图标的点击处理
        private MyClickListener myListener = new MyClickListener();


        public MyArrayListAdapter(Context context) {
            super(context, R.layout.list_item_1);
            this.context = context;
        }

        public void setArrayList(ArrayList<DayPicItem> al) {
            mList = al;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            } else {
                return mList.size();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            DayPicItem item = mList.get(position);
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.list_item_1,
                        parent, false);

                // 点击图片的事件
                ImageView icon = (ImageView) view.findViewById(R.id.imgviewid);
                icon.setOnClickListener(myListener);

                // holder
                holder = new ViewHolder();
                holder.icon = icon;
                holder.title = (TextView) view.findViewById(R.id.texttitleid);
                holder.date = (TextView) view.findViewById(R.id.textdateid);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            // 显示 小图标
            byte[] icon = item.getIcon();
            if (icon != null) {
                Glide.with(context).load(icon).dontAnimate().into(holder.icon);
                holder.icon.setTag((int) item.get_id());
            } else {
                holder.icon.setImageResource(android.R.color.transparent);
                holder.icon.setTag(-1);
            }

            // 显示 标题、日期
            holder.title.setText(" " + item.getTitle());
            holder.date.setText("  " + item.getDate());

            return view;
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
