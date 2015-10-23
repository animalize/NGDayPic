package man.animalize.ngdaypic;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import man.animalize.ngdaypic.ThirdPartLib.TouchImageView;
import man.animalize.ngdaypic.Utility.FileReadWrite;


public class TouchImageFragment extends Fragment {

    private static final String TAG = "TouchImageFragment";

    public static TouchImageFragment newInstance(String jpgfn) {
        Bundle b = new Bundle();
        b.putString("jpgfilename", jpgfn);

        TouchImageFragment f = new TouchImageFragment();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_touch_image, container, false);

        // 得到参数
        Bundle b = getArguments();
        String jpgfn = b.getString("jpgfilename");

        // 读取文件
        byte[] jpg = FileReadWrite.readFile(jpgfn);
        if (jpg == null)
            return v;

        // 标题，向上按钮
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("查看图片(" +
                String.format("%,d", jpg.length) +
                "字节)");

        // 显示
        TouchImageView touch = (TouchImageView) v.findViewById(R.id.touchid);
        touch.setImageBitmap(BitmapFactory.decodeByteArray(jpg, 0, jpg.length));

        return v;
    }
}
