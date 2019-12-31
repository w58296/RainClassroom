package example.rico.calendar.task.eventset;

import android.content.Context;

import yang.rico.common.bean.EventSet;
import yang.rico.common.data.EventSetDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

/**
 * Created by Jimmy on 2016/10/11 0011.
 */
public class AddEventSetTask extends BaseAsyncTask<EventSet> {

    private EventSet mEventSet;

    public AddEventSetTask(Context context, OnTaskFinishedListener<EventSet> onTaskFinishedListener, EventSet eventSet) {
        super(context, onTaskFinishedListener);
        mEventSet = eventSet;
    }

    @Override
    protected EventSet doInBackground(Void... params) {
        if (mEventSet != null) {
            EventSetDao dao = EventSetDao.getInstance(mContext);
            int id = dao.addEventSet(mEventSet);
            if (id != 0) {
                mEventSet.setId(id);
                return mEventSet;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
