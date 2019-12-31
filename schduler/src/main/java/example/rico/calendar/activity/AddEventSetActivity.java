package example.rico.calendar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jeek.calendar.R;
import yang.rico.common.bean.EventSet;
import example.rico.calendar.dialog.SelectColorDialog;
import example.rico.calendar.task.eventset.AddEventSetTask;
import example.rico.calendar.utils.JeekUtils;
import yang.rico.common.base.app.BaseActivity;
import yang.rico.common.listener.OnTaskFinishedListener;
import yang.rico.common.util.ToastUtils;


public class AddEventSetActivity extends BaseActivity implements View.OnClickListener, OnTaskFinishedListener<EventSet>, SelectColorDialog.OnSelectColorListener {

    public static int ADD_EVENT_SET_CANCEL = 1;
    public static int ADD_EVENT_SET_FINISH = 2;
    public static String EVENT_SET_OBJ = "event.set.obj";

    private EditText etEventSetName;
    private View vEventSetColor;
    private SelectColorDialog mSelectColorDialog;

    private int mColor = 0;

    @Override
    protected void bindView() {
        setContentView(R.layout.activity_add_event_set);
        TextView tvTitle = searchViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.menu_add_event_set));
        etEventSetName = searchViewById(R.id.etEventSetName);
        vEventSetColor = searchViewById(R.id.vEventSetColor);
        searchViewById(R.id.tvCancel).setOnClickListener(this);
        searchViewById(R.id.tvFinish).setOnClickListener(this);
        searchViewById(R.id.rlEventSetColor).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvCancel) {
            setResult(ADD_EVENT_SET_CANCEL);
            finish();
        } else if (id == R.id.tvFinish) {
            addEventSet();
        } else if (id == R.id.rlEventSetColor) {
            showSelectColorDialog();
        }
    }

    private void showSelectColorDialog() {
        if (mSelectColorDialog == null)
            mSelectColorDialog = new SelectColorDialog(this, this);
        mSelectColorDialog.show();
    }

    private void addEventSet() {
        String name = etEventSetName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortToast(this, R.string.event_set_name_is_not_null);
        } else {
            EventSet eventSet = new EventSet();
            eventSet.setName(name);
            eventSet.setColor(mColor);
            new AddEventSetTask(this, this, eventSet).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onTaskFinished(EventSet data) {
        setResult(ADD_EVENT_SET_FINISH, new Intent().putExtra(EVENT_SET_OBJ, data));
        finish();
    }

    @Override
    public void onSelectColor(int color) {
        mColor = color;
        vEventSetColor.setBackgroundResource(JeekUtils.getEventSetCircle(color));
    }
}
