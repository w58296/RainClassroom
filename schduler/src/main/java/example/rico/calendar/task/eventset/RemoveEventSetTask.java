package example.rico.calendar.task.eventset;

import android.content.Context;

import yang.rico.common.data.EventSetDao;
import yang.rico.common.data.ScheduleDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

/**
 * Created by Jimmy on 2016/10/11 0011.
 */
public class RemoveEventSetTask extends BaseAsyncTask<Boolean> {

    private int mId;

    public RemoveEventSetTask(Context context, OnTaskFinishedListener<Boolean> onTaskFinishedListener, int id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        ScheduleDao scheduleDao = ScheduleDao.getInstance(mContext);
        scheduleDao.removeScheduleByEventSetId(mId);
        EventSetDao dao = EventSetDao.getInstance(mContext);
        return dao.removeEventSet(mId);
    }
}
