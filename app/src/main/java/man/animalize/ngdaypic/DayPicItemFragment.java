package man.animalize.ngdaypic;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Utility.FileReadWrite;

import static android.speech.tts.TextToSpeech.OnInitListener;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class DayPicItemFragment
        extends Fragment
        implements OnInitListener {

    private static final String TAG = "DayPicItemFragment";
    private static final int TTSCHECKSUM = 1234;

    private TextView mTextView;
    private DayPicItem mItem;
    private byte[] mJpg;
    private TextToSpeech mTts;

    // tts的事件处理
    private UtteranceProgressListener ttslistener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
        }

        @Override
        public void onDone(String utteranceId) {
            getActivity().invalidateOptionsMenu();
        }

        @Override
        public void onError(String utteranceId) {
        }
    };

    public DayPicItemFragment() {
        // Required empty public constructor
    }

    // 供DayPicItemActivity调用
    public static DayPicItemFragment newInstance(DayPicItem item) {
        Bundle arg = new Bundle();
        arg.putSerializable("item", item);

        DayPicItemFragment f = new DayPicItemFragment();
        f.setArguments(arg);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // 得到参数item
        Bundle arg = getArguments();
        mItem = (DayPicItem) arg.getSerializable("item");

        // 图片
        if (mItem.getIcon() != null)
            mJpg = FileReadWrite.readFile(mItem.get_id() + ".jpg");

        // tts
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTSCHECKSUM);
    }

    // 创建tts实例
    protected void instanceTTS() {
        mTts = new TextToSpeech(getActivity(), this);
        mTts.setOnUtteranceProgressListener(ttslistener);
    }

    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == TTSCHECKSUM) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                instanceTTS();
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    // TextToSpeech.OnInitListener的接口
    @Override
    public void onStop() {
        if (mTts != null && mTts.isSpeaking())
            mTts.stop();

        super.onStop();
    }

    // TextToSpeech.OnInitListener的接口
    @Override
    public void onDestroy() {
        if (mTts != null)
            mTts.shutdown();

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Inflate the main_menu for this fragment
        View v = inflater.inflate(R.layout.fragment_day_pic_item, container, false);
        ImageView mImageView = (ImageView) v.findViewById(R.id.image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mJpg == null)
                    return;

                Intent i = new Intent(getActivity(), TouchImageActivity.class);
                i.putExtra("jpgfilename", mItem.get_id() + ".jpg");
                startActivity(i);
            }
        });

        mTextView = (TextView) v.findViewById(R.id.text_view);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        // 图片
        if (mItem.getIcon() != null && mJpg != null) {
            mImageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(mJpg, 0, mJpg.length)
            );
        } else {
            mImageView.setVisibility(View.GONE);
        }

        // 文字
        mTextView.setText("标题：" + mItem.getTitle() +
                "\n介绍：" + mItem.getDescrip() +
                "\n日期：" + mItem.getDate());

        // 刷新菜单
        getActivity().invalidateOptionsMenu();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_daypic_fragement, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mTts == null)
            return;

        MenuItem mi = menu.findItem(R.id.ttsid);
        if (mTts.isSpeaking()) {
            mi.setTitle("停止");
        } else {
            mi.setTitle("朗读");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                getActivity().finish();
                return true;

            case R.id.ttsid:
                if (mTts == null)
                    return true;

                if (mTts.isSpeaking()) {
                    mTts.stop();
                } else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

                    mTts.speak(mItem.getTitle() + ".\n" + mItem.getDescrip(),
                            QUEUE_FLUSH,
                            map);
                }

                getActivity().invalidateOptionsMenu();

                return true;

            case R.id.copytext:
                Context cont = getActivity();
                ClipboardManager clipboard = (ClipboardManager) cont.getSystemService(cont.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("", mTextView.getText().toString());
                clipboard.setPrimaryClip(clip);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // tts引擎初始化之后调用此函数
    @Override
    public void onInit(int status) {
        mTts.setLanguage(Locale.ENGLISH);
        mTts.setSpeechRate((float) 0.78);
    }
}
