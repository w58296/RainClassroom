package example.rico.calendar.task.schedule;

import android.content.Context;

import yang.rico.common.bean.Schedule;
import yang.rico.common.data.ScheduleDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

/**
 * Created by Jimmy on 2016/10/11 0011.
 */
public class UpdateScheduleTask extends BaseAsyncTask<Boolean> {

    private Schedule mSchedule;

    public UpdateScheduleTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, Schedule schedule) {
        super(context, onTaskFinishedListener);
        mSchedule = schedule;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (mSchedule != null) {
            ScheduleDao dao = ScheduleDao.getInstance(mContext);
            return dao.updateSchedule(mSchedule);
        } else {
            return false;
        }
    }
}
