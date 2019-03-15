package finternet.fitCard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import finternet.fitCard.R;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.ClassModel;

@SuppressWarnings("ALL")
public class AdapterClass extends BaseAdapter {

    private List<ClassModel> m_lstClasses;
    private final Context mContext;


    public AdapterClass(Context con) {
        super();
        this.mContext = con;
    }

    public int getCount() {
        if (this.m_lstClasses == null)
            return 0;
        return this.m_lstClasses.size();

    }

    public ClassModel getItem(int paramInt) {
        return this.m_lstClasses.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        View localView = paramView;
        ClassViewHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_class, null);

        } else {
            localViewHolder = (ClassViewHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new ClassViewHolder();
            localViewHolder.txtClassDuration = (TextView) localView.findViewById(R.id.txtClassDuration);
            localViewHolder.txtClassGym = (TextView) localView.findViewById(R.id.txtClassGym);
            localViewHolder.txtClassName = (TextView) localView.findViewById(R.id.txtClassName);
            localViewHolder.txtClassTime = (TextView) localView.findViewById(R.id.txtClassTime);
            localViewHolder.txtClassAvailable = (TextView) localView.findViewById(R.id.txtClassAvailable);
            localViewHolder.txtClassCategory = (TextView) localView.findViewById(R.id.txtClassItemCategory);
            localViewHolder.txtClassCity = (TextView) localView.findViewById(R.id.txtClassItemCity);
            localViewHolder.imgClassImage = (ImageView) localView.findViewById(R.id.imgClassImage);
            localView.setTag(localViewHolder);
        }

        localViewHolder.txtClassName.setText(m_lstClasses.get(paramInt).mName);
        localViewHolder.txtClassGym.setText(m_lstClasses.get(paramInt).mGymName);
        localViewHolder.txtClassTime.setText(m_lstClasses.get(paramInt).mStartHour.substring(0, 5) + "-" + m_lstClasses.get(paramInt).mEndHour.substring(0, 5));
        localViewHolder.txtClassDuration.setText("(" + m_lstClasses.get(paramInt).mDuration + " Min)");
        localViewHolder.txtClassCategory.setText(m_lstClasses.get(paramInt).mCategory);
        if (m_lstClasses.get(paramInt).mCity != "")
            localViewHolder.txtClassCity.setText(m_lstClasses.get(paramInt).mCity + ",");
        if (m_lstClasses.get(paramInt).mAvailable.equals("0"))
            localViewHolder.txtClassAvailable.setText(mContext.getString(R.string.string_full));
        else
            localViewHolder.txtClassAvailable.setText(mContext.getResources().getString(R.string.string_available_spots) + ":" + m_lstClasses.get(paramInt).mAvailable);
        if (Globals.isLogin == 1)
        {
            if (m_lstClasses.get(paramInt).mIsBook == null || m_lstClasses.get(paramInt).mIsBook.equals("0"))
            {
                if (m_lstClasses.get(paramInt).mAvailable.equals("0"))
                    localViewHolder.txtClassAvailable.setText(mContext.getString(R.string.string_full));
                else
                    localViewHolder.txtClassAvailable.setText(mContext.getResources().getString(R.string.string_available_spots) + ":" + m_lstClasses.get(paramInt).mAvailable);
            }
            else
            {
                localViewHolder.txtClassAvailable.setText(mContext.getString(R.string.string_booked));
            }
        }

        Picasso.with(mContext)
                .load(Constants.IMAGEBASEURL + m_lstClasses.get(paramInt).mImage)
                .into(localViewHolder.imgClassImage);
        localView.setTag(localViewHolder);
        return localView;

    }

    public void update(List<ClassModel> tickets) {
        this.m_lstClasses = tickets;
        notifyDataSetChanged();
    }

    public static class ClassViewHolder {
        public TextView txtClassTime;
        public TextView txtClassDuration;
        public TextView txtClassName;
        public TextView txtClassGym;
        public TextView txtClassAvailable;
        public TextView txtClassCategory;
        public TextView txtClassCity;
        public ImageView imgClassImage;

    }
}

