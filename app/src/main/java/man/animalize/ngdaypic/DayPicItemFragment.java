package man.animalize.ngdaypic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DayPicItemFragment extends Fragment {
    private ImageView mImageView;
    private TextView mTextView;
    private int mTemp = 0;

    public DayPicItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_day_pic_item, container, false);
        mImageView = (ImageView)v.findViewById(R.id.image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("点击图片"+(mTemp++));
            }
        });

        mTextView = (TextView)v.findViewById(R.id.text_view);

        return v;
    }

}
