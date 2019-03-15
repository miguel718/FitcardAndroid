package finternet.fitCard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.GymModel;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class AdapterBookGym extends BaseAdapter {

    private List<GymModel> m_lstClasses;
    private final Context mContext;
    private final int currentDay;


    public AdapterBookGym(Context con) {
        super();
        this.mContext = con;
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public int getCount() {
        if (this.m_lstClasses == null)
            return 0;
        return this.m_lstClasses.size();

    }

    public GymModel getItem(int paramInt) {
        return this.m_lstClasses.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        View localView = paramView;
        GymViewHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_gym_book, null);

        } else {
            localViewHolder = (GymViewHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new GymViewHolder();
            localViewHolder.txtGymHour = (TextView) localView.findViewById(R.id.txtGymBookHour);
            localViewHolder.txtGymName = (TextView) localView.findViewById(R.id.txtGymBookName);
            localViewHolder.txtGymDate = (TextView) localView.findViewById(R.id.txtGymBookTime);
            localViewHolder.imgGymImage = (ImageView) localView.findViewById(R.id.imgGymBookImage);
            localViewHolder.btnCancel = (Button) localView.findViewById(R.id.btnGymBookCancel);
            localView.setTag(localViewHolder);
        }

        localViewHolder.txtGymName.setText(m_lstClasses.get(paramInt).mName);
        String ss = Constants.DAYS[currentDay] + " " + m_lstClasses.get(paramInt).mStartHours.get(currentDay).substring(0, 5) + "-" + m_lstClasses.get(paramInt).mEndHours.get(currentDay).substring(0, 5);

        localViewHolder.txtGymHour.setText(ss);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM",Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm",Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(m_lstClasses.get(paramInt).mBookDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = calendar.getTimeInMillis() + 3 * 60 * 60 * 1000;
        Calendar limitCalendar = Calendar.getInstance();
        limitCalendar.setTimeInMillis(time);
        String timeString = sdf2.format(calendar.getTime()) + "-" + sdf2.format(limitCalendar.getTime());
        localViewHolder.txtGymDate.setText(sdf1.format(calendar.getTime()) + " " + mContext.getString(R.string.string_at)+" " + timeString);
        final int index = paramInt;
        localViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog mWelcome = new MaterialDialog.Builder(mContext)
                        .title("Are you sure to cancel book?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .show();
                mWelcome.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        ServiceManager.serviceCancelGym(m_lstClasses.get(index).mBookId);
                        Globals.lstGyms.remove(index);
                        Globals.mAccount.mCredit = String.valueOf(Integer.parseInt(Globals.mAccount.mCredit) + 1);
                        saveUser();
                        update(Globals.lstGyms);
                    }
                });
            }
        });
        Picasso.with(mContext)
                .load(Constants.IMAGEBASEURL + m_lstClasses.get(paramInt).mImage)
                .into(localViewHolder.imgGymImage);
        localView.setTag(localViewHolder);
        return localView;

    }

    public void update(List<GymModel> tickets) {
        this.m_lstClasses = tickets;
        notifyDataSetChanged();
    }

    private void saveUser() {
        SharedPreferences sp = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().putString("id", Globals.mAccount.mId).apply();
        sp.edit().putString("name", Globals.mAccount.mName).apply();
        sp.edit().putString("image", Globals.mAccount.mImage).apply();
        sp.edit().putString("address", Globals.mAccount.mAddress).apply();
        sp.edit().putString("phone", Globals.mAccount.mPhone).apply();
        sp.edit().putString("city", Globals.mAccount.mCity).apply();
        sp.edit().putString("credit", Globals.mAccount.mCredit).apply();
        sp.edit().putString("plan", Globals.mAccount.mPlan).apply();
        sp.edit().putString("email", Globals.mAccount.mEmail).apply();
        sp.edit().putString("invoiceend",Globals.mAccount.mInvoiceEnd).apply();
        sp.edit().putString("invoicestart",Globals.mAccount.mInvoiceStart).apply();
        sp.edit().putString("token", Globals.mAccount.mToken).apply();
        sp.edit().putString("hasCard", Globals.mAccount.mHasCard).apply();

    }

    public class GymViewHolder {
        public TextView txtGymHour;
        public TextView txtGymName;
        public TextView txtGymDate;
        public ImageView imgGymImage;
        public Button btnCancel;

    }
}

