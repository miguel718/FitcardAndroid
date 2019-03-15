package finternet.fitCard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import finternet.fitCard.R;
import finternet.fitCard.model.Model;


@SuppressWarnings("unused")
public class AdapterSearchCity extends BaseAdapter {

    private List<Model> mlstModels;
    private final Context mContext;


    public AdapterSearchCity(Context con) {
        super();
        this.mContext = con;
    }

    public int getCount() {
        if (this.mlstModels == null)
            return 1;
        return this.mlstModels.size() + 1;

    }

    public Model getItem(int paramInt) {
        return this.mlstModels.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        View localView = paramView;
        ClassViewHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_search_city, null);

        } else {
            localViewHolder = (ClassViewHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new ClassViewHolder();
            localViewHolder.txtSearchCity = (TextView) localView.findViewById(R.id.txtSearchCityItem);
            localView.setTag(localViewHolder);
        }
        if (paramInt == 0) {
            localViewHolder.txtSearchCity.setText(mContext.getResources().getString(R.string.string_all));
        } else localViewHolder.txtSearchCity.setText(mlstModels.get(paramInt - 1).mName);
        localView.setTag(localViewHolder);
        return localView;

    }

    public void update(List<Model> tickets) {
        this.mlstModels = tickets;
        notifyDataSetChanged();
    }

    public class ClassViewHolder {
        public TextView txtSearchCity;

    }
}

