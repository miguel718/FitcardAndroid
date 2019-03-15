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
import finternet.fitCard.model.ClassModel;
import finternet.fitCard.service.ServiceManager;

@SuppressWarnings("ALL")
public class AdapterBookClass extends BaseAdapter {

    private List<ClassModel> m_lstClasses;
    private final Context mContext;


    public AdapterBookClass(Context con) {
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
            localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_class_book, null);
        }
        else {
            localViewHolder = (ClassViewHolder) localView.getTag();
        }
        if (localViewHolder == null) {
            localViewHolder = new ClassViewHolder();
            localViewHolder.txtClassDuration = (TextView) localView.findViewById(R.id.txtClassBookDuration);
            localViewHolder.txtClassGym = (TextView) localView.findViewById(R.id.txtClassBookGym);
            localViewHolder.txtClassName = (TextView) localView.findViewById(R.id.txtClassBookName);
            localViewHolder.txtClassTime = (TextView) localView.findViewById(R.id.txtClassBookTime);
            localViewHolder.txtClassAvailable = (TextView) localView.findViewById(R.id.txtClassBookAvailable);
            localViewHolder.imgClassImage = (ImageView) localView.findViewById(R.id.imgClassBookImage);
            localViewHolder.btnCancel = (Button) localView.findViewById(R.id.btnClassBookCancel);
            localView.setTag(localViewHolder);
        }

        localViewHolder.txtClassName.setText(m_lstClasses.get(paramInt).mName);
        localViewHolder.txtClassGym.setText(m_lstClasses.get(paramInt).mGymName);
        localViewHolder.txtClassTime.setText(m_lstClasses.get(paramInt).mStartHour.substring(0, 5) + "-" + m_lstClasses.get(paramInt).mEndHour.substring(0, 5));
        localViewHolder.txtClassDuration.setText("(" + m_lstClasses.get(paramInt).mDuration + " Min)");
        localViewHolder.txtClassTime.setVisibility(View.GONE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM",Locale.US);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(m_lstClasses.get(paramInt).mDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        localViewHolder.txtClassAvailable.setText(mContext.getResources().getString(R.string.string_date) + sdf1.format(calendar.getTime()) + " " + m_lstClasses.get(paramInt).mStartHour.substring(0,5) + "-" + m_lstClasses.get(paramInt).mEndHour.substring(0,5));
        Picasso.with(mContext)
                .load(Constants.IMAGEBASEURL + m_lstClasses.get(paramInt).mImage)
                .into(localViewHolder.imgClassImage);
        final int index = paramInt;
        localViewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog mWelcome = new MaterialDialog.Builder(mContext)
                        .title(mContext.getResources().getString(R.string.string_cancelbook))
                        .positiveText(mContext.getResources().getString(R.string.string_yes))
                        .negativeText(mContext.getResources().getString(R.string.string_no))
                        .show();
                mWelcome.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        ServiceManager.serviceCancelClass(m_lstClasses.get(index).mBookId);
                        Globals.lstClasses.remove(index);
                        Globals.mAccount.mCredit = String.valueOf(Integer.parseInt(Globals.mAccount.mCredit) + 1);
                        saveUser();
                        update(Globals.lstClasses);
                    }
                });
            }
        });
        localView.setTag(localViewHolder);
        return localView;

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

    }

    public void update(List<ClassModel> tickets) {
        this.m_lstClasses = tickets;
        notifyDataSetChanged();
    }

    public class ClassViewHolder {
        public TextView txtClassTime;
        public TextView txtClassDuration;
        public TextView txtClassName;
        public TextView txtClassGym;
        public TextView txtClassAvailable;
        public ImageView imgClassImage;
        public Button btnCancel;

    }
}

