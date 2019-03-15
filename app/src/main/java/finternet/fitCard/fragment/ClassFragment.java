package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.activity.DetailClassActivity;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.adapter.AdapterClass;
import finternet.fitCard.adapter.AdapterSearchCity;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.CategoryModel;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class ClassFragment extends Fragment implements View.OnClickListener, ILoadService {

    private ListView mLstClasses;
    private AdapterClass adapterClass;
    private int mode = 0;
    private DialogPlus mDlgPost;
    private View mDialogPostView;
    private View mRootView = null;
    public LinearLayout layoutSearch;

    private final TextView[] txtDay = new TextView[7];
    private final TextView[] txtDate = new TextView[7];

    private TextView txtBack;
    private TextView txtNext;

    private String currentDate;
    private String searchDate;

    private final String gymId = "-1";

    private MaterialDialog mDialog;
    private LinearLayout layoutUpcomingSchedule;
    private TextView txtUpcoming;

    private Spinner spCity;
    private Spinner spCategory;
    private EditText editKeyword;
    private Button btnSearchRun;
    private AdapterSearchCity adapterSearch;
    private AdapterSearchCity adapterCategory;
    public TextView txtUpcoming1;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_class, null);
        }
        initView();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = curFormater.format(c.getTime());
        searchDate = currentDate;
        updateCalendar();
        initSearchDialog();
        loadClass();
        return mRootView;
    }

    private void initView() {

        mLstClasses = (ListView) mRootView.findViewById(R.id.lstClasses);
        layoutUpcomingSchedule = (LinearLayout) mRootView.findViewById(R.id.layoutNextUpcomingClass);
        txtUpcoming = (TextView) mRootView.findViewById(R.id.txtNextUpcomingDate);
        txtUpcoming1 = (TextView) mRootView.findViewById(R.id.txtNextUpcomingDate1);
        txtDay[0] = (TextView) mRootView.findViewById(R.id.txtClassDay1);
        txtDay[1] = (TextView) mRootView.findViewById(R.id.txtClassDay2);
        txtDay[2] = (TextView) mRootView.findViewById(R.id.txtClassDay3);
        txtDay[3] = (TextView) mRootView.findViewById(R.id.txtClassDay4);
        txtDay[4] = (TextView) mRootView.findViewById(R.id.txtClassDay5);
        txtDay[5] = (TextView) mRootView.findViewById(R.id.txtClassDay6);
        txtDay[6] = (TextView) mRootView.findViewById(R.id.txtClassDay7);

        txtDate[0] = (TextView) mRootView.findViewById(R.id.txtClassDate1);
        txtDate[1] = (TextView) mRootView.findViewById(R.id.txtClassDate2);
        txtDate[2] = (TextView) mRootView.findViewById(R.id.txtClassDate3);
        txtDate[3] = (TextView) mRootView.findViewById(R.id.txtClassDate4);
        txtDate[4] = (TextView) mRootView.findViewById(R.id.txtClassDate5);
        txtDate[5] = (TextView) mRootView.findViewById(R.id.txtClassDate6);
        txtDate[6] = (TextView) mRootView.findViewById(R.id.txtClassDate7);

        txtBack = (TextView) mRootView.findViewById(R.id.txtClassDateBack);
        txtNext = (TextView) mRootView.findViewById(R.id.txtClassDateNext);
        layoutUpcomingSchedule.setVisibility(View.GONE);

        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Tunteja ladataan, pieni hetki")
                .progress(true, 0)
                .build();


        adapterClass = new AdapterClass(this.getActivity());
        mLstClasses.setAdapter(adapterClass);
        ((HomeActivity) getActivity()).imgSearch.setOnClickListener(this);
        adapterClass.update(Globals.lstClasses);
        mLstClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Globals.currentClass = Globals.lstClasses.get(i);
                Intent m = new Intent(ClassFragment.this.getContext(), DetailClassActivity.class);
                ClassFragment.this.startActivityForResult(m, 1);
            }
        });
        txtBack.setOnClickListener(this);
        txtNext.setOnClickListener(this);
    }

    public void loadClass() {
        showDialog();
        String keyword = editKeyword.getText().toString();
        String request = searchDate + "/" + gymId;
        if (spCity.getSelectedItemPosition() == 0)
            request = request + "/" + String.valueOf(-1);
        else
            request = request + "/" + Globals.lstCitys.get(spCity.getSelectedItemPosition() - 1).mName;
        if (spCategory.getSelectedItemPosition() == 0)
            request = request + "/" + String.valueOf(-1);
        else
            request = request + "/" + ((CategoryModel) Globals.lstCategory.get(spCategory.getSelectedItemPosition() - 1)).mId;
        if (keyword.equals(""))
            request = request + "/" + String.valueOf(-1);
        else
            request = request + "/" + keyword;
        ServiceManager.serviceSearchClass(request, this);
    }

    private void updateCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        try {
            calendar.setTime(sdf.parse(currentDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = currentDay; i < currentDay + 7; i++) {
            txtDay[i - currentDay].setText(Constants.DAYS[i % 7]);
        }
        for (int i = 0; i < 7; i++) {
            Calendar calendar1 = Calendar.getInstance();
            try {
                calendar1.setTime(sdf.parse(currentDate));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar1.add(Calendar.DATE, i);
            txtDate[i].setText(String.valueOf(calendar1.get(Calendar.DAY_OF_MONTH)));
            final int p = i;
            txtDate[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < 7; j++) {
                        txtDate[j].setBackgroundDrawable(null);
                    }
                    txtDate[p].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(curFormater.parse(currentDate));// all done
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.DATE, p);
                    searchDate = curFormater.format(c.getTime());
                    //updateCalendar();
                    loadClass();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutSearchClass:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.imgSearchTop:
                mDlgPost.show();
                break;
            case R.id.txtNextUpcomingDate:
                currentDate = Globals.mUpcomingDate;
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.txtClassDateBack:
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                try {
                    calendar.setTime(sdf.parse(currentDate));// all done
                    calendar.add(Calendar.DATE, -7);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate = sdf.format(calendar.getTime());
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.txtClassDateNext:
                calendar = Calendar.getInstance();
                sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                try {
                    calendar.setTime(sdf.parse(currentDate));// all done
                    calendar.add(Calendar.DATE, +7);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentDate = sdf.format(calendar.getTime());
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.btnClassSearchRun:
                mDlgPost.dismiss();
                loadClass();
                break;
        }
    }

    private void initSearchDialog() {
        mDlgPost = DialogPlus.newDialog(this.getContext())
                .setContentHolder(new ViewHolder(R.layout.layout_dialog_search_class))
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, Globals.contentHeight / 2)
                .setContentHeight(Globals.contentHeight / 2)
                .setBackgroundColorResourceId(R.color.color_white)
                .create();
        mDialogPostView = mDlgPost.getHolderView();
        inflateSearchDialog();
    }

    private void inflateSearchDialog() {
        layoutSearch = (LinearLayout) mDialogPostView.findViewById(R.id.layoutSearchClass);
        spCity = (Spinner) mDialogPostView.findViewById(R.id.spSearchClassCity);
        spCategory = (Spinner) mDialogPostView.findViewById(R.id.spSearchClassCategory);
        btnSearchRun = (Button) mDialogPostView.findViewById(R.id.btnClassSearchRun);
        editKeyword = (EditText) mDialogPostView.findViewById(R.id.editSearchClassKeyword);

        btnSearchRun.setOnClickListener(this);
        adapterSearch = new AdapterSearchCity(this.getActivity());
        adapterCategory = new AdapterSearchCity(this.getActivity());
        spCity.setAdapter(adapterSearch);
        spCategory.setAdapter(adapterCategory);
        adapterSearch.update(Globals.lstCitys);
        adapterCategory.update(Globals.lstCategory);
        btnSearchRun.setOnClickListener(this);
        layoutSearch.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessLoad(int type) {
        if (Globals.lstClasses.size() > 0) {
            mLstClasses.setVisibility(View.VISIBLE);
            layoutUpcomingSchedule.setVisibility(View.GONE);
            Collections.sort(Globals.lstClasses);
            adapterClass.update(Globals.lstClasses);
        } else {
            layoutUpcomingSchedule.setVisibility(View.VISIBLE);
            mLstClasses.setVisibility(View.GONE);
            if (Globals.mUpcomingDate.equals("")) {
                txtUpcoming.setText(getResources().getString(R.string.string_noupcoming));
                txtUpcoming.setOnClickListener(null);
                txtUpcoming.setTextColor(0xff000000);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM",Locale.US);
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(Globals.mUpcomingDate));// all done
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtUpcoming.setText(getResources().getString(R.string.string_upcoming_class) + sdf1.format(calendar.getTime()));
                txtUpcoming.setOnClickListener(this);
                txtUpcoming.setTextColor(0xff2798C6);
            }
        }
        /*mLstClasses.setVisibility(View.VISIBLE);
        layoutUpcomingSchedule.setVisibility(View.VISIBLE);



        if (Globals.mUpcomingDate.equals("")) {
            txtUpcoming.setText(getResources().getString(R.string.string_noupcoming));
            txtUpcoming.setOnClickListener(null);
            txtUpcoming.setTextColor(0xff000000);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM",Locale.US);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(Globals.mUpcomingDate));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtUpcoming.setText(getResources().getString(R.string.string_upcoming_class) + sdf1.format(calendar.getTime()));
            txtUpcoming.setOnClickListener(this);
            txtUpcoming.setTextColor(0xff2798C6);
        }*/

        inflateSearchDialog();
    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.dismiss();
    }
}
