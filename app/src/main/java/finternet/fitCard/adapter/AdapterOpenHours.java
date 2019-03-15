package finternet.fitCard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import finternet.fitCard.R;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.model.GymModel;

/**
 * Created by Administrator on 10/25/2016.
 */
public class AdapterOpenHours extends BaseAdapter {

    private GymModel curGym;
    private final Context mContext;
    private final int currentDay;


    public AdapterOpenHours(Context con) {
        super();
        this.mContext = con;
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

    }

    public int getCount() {
        if (this.curGym == null || this.curGym.mStartHours == null)
            return 0;
        return this.curGym.mStartHours.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        View localView = paramView;
        OpenHourHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_openhour, null);

        } else {
            localViewHolder = (OpenHourHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new OpenHourHolder();
            localViewHolder.txtHours = (TextView) localView.findViewById(R.id.txtOpenHour);
            localView.setTag(localViewHolder);
        }
        String ss = Constants.DAYS[paramInt] + " " + curGym.mStartHours.get(paramInt).substring(0, 5) + "-" + curGym.mEndHours.get(paramInt).substring(0, 5);
        localViewHolder.txtHours.setText(ss);
        localView.setTag(localViewHolder);
        return localView;

    }

    public void update(GymModel gModel) {
        this.curGym = gModel;
        notifyDataSetChanged();
    }

    public class OpenHourHolder {
        public TextView txtHours;

    }
}

