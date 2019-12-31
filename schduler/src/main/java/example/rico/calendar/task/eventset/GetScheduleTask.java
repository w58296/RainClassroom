package example.rico.calendar.task.eventset;

import android.content.Context;

import yang.rico.common.bean.Schedule;
import yang.rico.common.data.ScheduleDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

import java.util.List;

/**
 * Created by Jimmy on 2016/10/13 0013.
 */
public class GetScheduleTask extends BaseAsyncTask<List<Schedule>> {

    private int mId;

    public GetScheduleTask(Context context, OnTaskFinishedListener<List<Schedule>> onTaskFinishedListener, int id) {
        super(context, onTaskFinishedListener);
        mId = id;
    }

    @Override
    protected List<Schedule> doInBackground(Void... params) {
        ScheduleDao dao = ScheduleDao.getInstance(mContext);
        return dao.getScheduleByEventSetId(mId);
    }

}
