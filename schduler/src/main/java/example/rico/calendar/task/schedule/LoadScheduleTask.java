package example.rico.calendar.task.schedule;

import android.content.Context;

import yang.rico.common.bean.Schedule;
import yang.rico.common.data.ScheduleDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

import java.util.List;

public class LoadScheduleTask extends BaseAsyncTask<List<Schedule>> {

    private int mYear;
    private int mMonth;
    private int mDay;

    public LoadScheduleTask(Context context, OnTaskFinishedListener<List<Schedule>> onTaskFinishedListener, int year, int month, int day) {
        super(context, onTaskFinishedListener);
        mYear = year;
        mMonth = month;
        mDay = day;
    }

    @Override
    protected List<Schedule> doInBackground(Void... params) {
        ScheduleDao dao = ScheduleDao.getInstance(mContext);
        return dao.getScheduleByDate(mYear, mMonth,mDay);
    }
}
