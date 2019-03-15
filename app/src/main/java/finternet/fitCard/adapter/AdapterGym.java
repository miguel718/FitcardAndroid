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
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.GymModel;


@SuppressWarnings("ALL")
public class AdapterGym extends BaseAdapter {

    private List<GymModel> m_lstGym;
    private final Context mContext;
    private final int currentDay;


    public AdapterGym(Context con) {
        super();
        this.mContext = con;
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;

    }

    public int getCount() {
        if (this.m_lstGym == null)
            return 0;
        return this.m_lstGym.size();

    }

    public GymModel getItem(int paramInt) {
        return this.m_lstGym.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        View localView = paramView;
        GymViewHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_gym, null);

        } else {
            localViewHolder = (GymViewHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new GymViewHolder();
            localViewHolder.imgGymPicture = (ImageView) localView.findViewById(R.id.imgGymPicture);
            localViewHolder.txtGymLocation = (TextView) localView.findViewById(R.id.txtGymLocation);
            localViewHolder.txtGymName = (TextView) localView.findViewById(R.id.txtGymName);
            localViewHolder.txtGymReview = (TextView) localView.findViewById(R.id.txtGymReview);
            localViewHolder.ratGym = (RatingBar) localView.findViewById(R.id.ratingGym);
            localViewHolder.txtHours = (TextView) localView.findViewById(R.id.txtGymHours);
            localViewHolder.txtGymStatus = (TextView) localView.findViewById(R.id.txtGymListOpen);
            localViewHolder.txtDistance = (TextView) localView.findViewById(R.id.txtGymDistance);
            localView.setTag(localViewHolder);
        }

        localViewHolder.txtGymName.setText(m_lstGym.get(paramInt).mName);
        localViewHolder.txtGymLocation.setText(m_lstGym.get(paramInt).mAddress + "," + m_lstGym.get(paramInt).mCity);
        localViewHolder.txtGymReview.setText("(" + m_lstGym.get(paramInt).mReview + " " + mContext.getResources().getString(R.string.string_review));
        if (m_lstGym.get(paramInt).mRating == null || m_lstGym.get(paramInt).mRating.equals(""))
            localViewHolder.ratGym.setRating(0);
        else
            localViewHolder.ratGym.setRating(Float.parseFloat(m_lstGym.get(paramInt).mRating));
        Picasso.with(mContext)
                .load(Constants.IMAGEBASEURL + m_lstGym.get(paramInt).mImage)
                .into(localViewHolder.imgGymPicture);
        String mDistance = "";
        if (m_lstGym.get(paramInt).mDistance > 1000)
        {
            mDistance = String.valueOf((int)(m_lstGym.get(paramInt).mDistance / 1000)) + "K";
        }
        else mDistance = String.valueOf((int)(m_lstGym.get(paramInt).mDistance));
        localViewHolder.txtDistance.setText(mDistance +  "m");
        //String ss = Constants.DAYS[currentDay] + " " + m_lstGym.get(paramInt).mStartHours.get(currentDay).substring(0, 5) + "-" + m_lstGym.get(paramInt).mEndHours.get(currentDay).substring(0, 5);
        String ss = m_lstGym.get(paramInt).mStartHours.get(currentDay).substring(0, 5) + "-" + m_lstGym.get(paramInt).mEndHours.get(currentDay).substring(0, 5);
        if (m_lstGym.get(paramInt).mClose.size() > currentDay && m_lstGym.get(paramInt).mClose.get(currentDay).equals("0"))
        {
            localViewHolder.txtHours.setText(ss);
            localViewHolder.txtHours.setVisibility(View.VISIBLE);
            localViewHolder.txtGymStatus.setText("Open Today");
        }
        else
        {
            localViewHolder.txtGymStatus.setText(mContext.getResources().getText(R.string.string_close));
            localViewHolder.txtHours.setVisibility(View.GONE);
        }

        localView.setTag(localViewHolder);
        return localView;

    }

    public void update(List<GymModel> tickets) {
        this.m_lstGym = tickets;
        notifyDataSetChanged();
    }

    public class GymViewHolder {
        public ImageView imgGymPicture;
        public TextView txtGymName;
        public TextView txtGymLocation;
        public RatingBar ratGym;
        public TextView txtGymReview;
        public TextView txtHours;
        public TextView txtGymStatus;
        public TextView txtDistance;

    }
}

