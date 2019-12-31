package example.rico.calendar.task.eventset;

import android.content.Context;

import yang.rico.common.bean.EventSet;
import yang.rico.common.data.EventSetDao;
import yang.rico.common.base.task.BaseAsyncTask;
import yang.rico.common.listener.OnTaskFinishedListener;

import java.util.Map;

/**
 * Created by Jimmy on 2016/10/11 0011.
 */
public class LoadEventSetMapTask extends BaseAsyncTask<Map<Integer, EventSet>> {

    private Context mContext;

    public LoadEventSetMapTask(Context context, OnTaskFinishedListener<Map<Integer, EventSet>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        mContext = context;
    }

    @Override
    protected Map<Integer, EventSet> doInBackground(Void... params) {
        EventSetDao dao = EventSetDao.getInstance(mContext);
        return dao.getAllEventSetMap();
    }

}
