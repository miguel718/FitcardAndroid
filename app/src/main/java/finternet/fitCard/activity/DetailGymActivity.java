package finternet.fitCard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.adapter.AdapterClass;
import finternet.fitCard.adapter.AdapterOpenHours;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.ReviewModel;
import finternet.fitCard.receiver.ReviewReceiver;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;
import finternet.fitCard.view.OvalImageView;


@SuppressWarnings("ALL")
public class DetailGymActivity extends Activity implements View.OnClickListener, ILoadService {

    private ImageView imgBack;
    private ImageView imgPhoto;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtReviews;
    private TextView txtHour;
    private TextView txtAddress;
    private RatingBar ratingGym;
    private Button btnVisit;
    private MaterialDialog mDialog;
    private MaterialDialog mDialogSuccess;
    private final ReviewReceiver broadcastReceiver = new ReviewReceiver();
    private GoogleMap googleMap;
    private int firstLoad = 0;
    private int hasClass = 0;

    //Schedule

    private MaterialDialog mDialogSchedule;
    private int mode = 0;
    private DialogPlus mDlgPost;
    private View mDialogPostView;

    private final TextView[] txtDay = new TextView[7];
    private final TextView[] txtDate = new TextView[7];

    private String currentDate;
    private String searchDate;
    private LinearLayout layoutUpcomingSchedule;
    private LinearLayout layoutSchedule;
    private TextView txtUpcoming;

    private TextView txtBack;
    private TextView txtNext;
    private LinearLayout layoutClasses;
    private Spinner spOpenHours;
    private AdapterOpenHours adapterOpenHours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailgym);
        registerReceiver(broadcastReceiver, new IntentFilter("finternet.fitcard.CUSTOM_INTENT"));
        initView();
        initScheduleView();
        setData();
        showDialog();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        currentDate = curFormater.format(c.getTime());
        searchDate = currentDate;
        //ServiceManager.serviceLoadReviewForGym(Globals.currentGym.mId, this);
        loadClass();
        updateCalendar();
    }

    private void initView() {
        imgBack = (ImageView) this.findViewById(R.id.btnDetailGymBack);
        imgPhoto = (ImageView) this.findViewById(R.id.imgDetailgymPicture);
        txtName = (TextView) this.findViewById(R.id.txtDetailGymName);
        txtDescription = (TextView) this.findViewById(R.id.txtDetailGymDescription);
        txtReviews = (TextView) this.findViewById(R.id.txtDetailGymReview);
        txtHour = (TextView) this.findViewById(R.id.txtDetailGymHour);
        txtAddress = (TextView) this.findViewById(R.id.txtDetailGymCity);
        btnVisit = (Button) this.findViewById(R.id.btnDetailGymVisit);
        ratingGym = (RatingBar) this.findViewById(R.id.ratingDetailGym);
        layoutClasses = (LinearLayout) this.findViewById(R.id.layoutGymClasses);
        googleMap = ((MapFragment) this.getFragmentManager().findFragmentById(
                R.id.gymMap)).getMap();
        spOpenHours = (Spinner) this.findViewById(R.id.spGymOpenHours);
        adapterOpenHours = new AdapterOpenHours(this);
        spOpenHours.setAdapter(adapterOpenHours);
        adapterOpenHours.update(Globals.currentGym);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        spOpenHours.setSelection(currentDay);
        layoutClasses.setVisibility(View.GONE);
        imgBack.setOnClickListener(this);
        btnVisit.setOnClickListener(this);

        mDialog = new MaterialDialog.Builder(this)
                .title("Please wait")
                .content("Loading Review Infos")
                .progress(true, 0)
                .build();
        mDialogSuccess = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.string_success))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.string_sharetext), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mDialogSuccess.dismiss();
                        ShareLinkContent linkContent2 = new ShareLinkContent.Builder()
                                .setContentTitle("FitCard")
                                .setContentDescription(input.toString())
                                .setImageUrl(Uri.parse("http://s150717x97.imwork.net:8088/images/banner/fitcardbg3.jpg"))
                                .setContentUrl(Uri.parse("http://s150717x97.imwork.net:8088"))
                                .build();

                        ShareDialog shareDialog = new ShareDialog(DetailGymActivity.this);
                        shareDialog.registerCallback(Globals.g_callbackManager, new FacebookCallback<Sharer.Result>() {
                            @Override
                            public void onSuccess(Sharer.Result result) {
                                new MaterialDialog.Builder(DetailGymActivity.this)
                                        .title("Share")
                                        .content("Posted on Facebook").show();
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onError(FacebookException e) {
                                e.printStackTrace();

                            }
                        });
                        //shareDialog.show(this, composeContent(title, description, link));
                        ShareDialog.show(DetailGymActivity.this, linkContent2);
                    }
                })
                .positiveText(getResources().getString(R.string.string_sharefacebook))
                .negativeText(getResources().getString(R.string.string_closess))
                .build();
        /*mDialogSuccess.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
               @Override
               public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

               }
           });*/

        mDialogSuccess.setCancelable(false);
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        txtName.setText(Globals.currentGym.mName);
        txtReviews.setText("(" + Globals.currentGym.mReview + ") " + getResources().getString(R.string.string_review));
        txtDescription.setText(Globals.currentGym.mDescription);
        Picasso.with(this)
                .load(Constants.IMAGEBASEURL + Globals.currentGym.mImage)
                .into(imgPhoto);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String ss = Constants.DAYS[currentDay] + " " + Globals.currentGym.mStartHours.get(currentDay).substring(0, 5) + "-" + Globals.currentGym.mEndHours.get(currentDay).substring(0, 5);
        txtHour.setText(ss);
        txtAddress.setText(Globals.currentGym.mAddress + "," + Globals.currentGym.mCity);

        if (Globals.isLogin == 0)
            btnVisit.setVisibility(View.GONE);
        else
            btnVisit.setVisibility(View.VISIBLE);
        if (Globals.currentGym.mRating == null || Globals.currentGym.mRating.equals(""))
            ratingGym.setRating(0);
        else
            ratingGym.setRating(Float.parseFloat(Globals.currentGym.mRating));

        if (googleMap != null) {
            LatLng latLng = new LatLng(Double.parseDouble(Globals.currentGym.mLat), Double.parseDouble(Globals.currentGym.mLon));
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDetailGymBack:
                finish();
                break;
            /*case R.id.btnDetailGymSchedule:
                Intent m = new Intent(this, ScheduleClassActivity.class);
                this.startActivity(m);
                break;*/
            case R.id.btnDetailGymVisit:
                if (Integer.parseInt(Globals.mAccount.mCredit) > 0)
                    ServiceManager.serviceBookGym(this);
                else {
                    mDialogSuccess = new MaterialDialog.Builder(this)
                            .title(getResources().getString(R.string.string_notice))
                            .content(getResources().getString(R.string.string_notice1))
                            .positiveText(getResources().getString(R.string.string_notice2))
                            .negativeText(getResources().getString(R.string.string_notice3))
                            .show();
                    mDialogSuccess.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            if (!Globals.isUserOverInvoice())
                            {
                                new MaterialDialog.Builder(DetailGymActivity.this)
                                        .title("Notice")
                                        .content("You can't access during Invoice Period")
                                        .show();
                                return;
                            }
                            Globals.e_mode = Enums.MODE.PRICE;
                            finish();
                        }
                    });
                }
                break;
            case R.id.txtScheduleBack:
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
            case R.id.txtScheduleUpcoming:
                currentDate = Globals.mUpcomingDate;
                searchDate = currentDate;
                updateCalendar();
                for (int j = 0; j < 7; j++) {
                    txtDate[j].setBackgroundDrawable(null);
                }
                txtDate[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_circle_calendar));
                loadClass();
                break;
            case R.id.txtScheduleNext:
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
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessLoad(int type) {
        if (type == 2) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM HH:mm", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 3);
            Globals.mLastVisitGym = sdf1.format(calendar.getTime());
            btnVisit.setText(getResources().getString(R.string.string_activeUntil) + " " + Globals.mLastVisitGym  + getResources().getString(R.string.string_activeUntil1));
            btnVisit.setOnClickListener(null);
            Globals.mAccount.mCredit = String.valueOf(Integer.parseInt(Globals.mAccount.mCredit) - 1);
            saveUser();
            mDialogSuccess.setContent("");
            if (Globals.visitCode > 0)
            {
                mDialogSuccess.setContent(getResources().getString(R.string.string_visitcode) + String.valueOf(Globals.visitCode));
            }
            mDialogSuccess.show();
            return;
        } else if (type == 3) {
            if (!Globals.mLastVisitGym.equals("")) {
                btnVisit.setText(getResources().getString(R.string.string_activeUntil) + " " + Globals.mLastVisitGym + getResources().getString(R.string.string_activeUntil1));
                btnVisit.setOnClickListener(null);
            } else {
                if (!isCloseGym())
                {
                    btnVisit.setText(getResources().getText(R.string.string_close));
                    btnVisit.setOnClickListener(null);

                }
                else {
                    if (Globals.currentGym.mUsability > Globals.mClassBook) {
                        btnVisit.setText(getResources().getText(R.string.string_visit));
                        btnVisit.setOnClickListener(this);
                    }
                    else {
                        btnVisit.setText(getResources().getText(R.string.string_full));
                        btnVisit.setOnClickListener(null);
                    }
                }
            }
            return;
        }
        btnVisit.setOnClickListener(this);
        if (!isCloseGym())
        {
            btnVisit.setText(getResources().getText(R.string.string_close));
            btnVisit.setOnClickListener(null);
        }
        else {
            btnVisit.setText(getResources().getText(R.string.string_visit));
            btnVisit.setOnClickListener(this);
        }
        /*layoutReviews.removeAllViews();
        if (Globals.lstReviews.size() > 0)
            txtReviewLabel.setText(getResources().getString(R.string.string_review));
        else
            txtReviewLabel.setText(getResources().getString(R.string.string_no_review));
        for (int i = 0; i < Globals.lstReviews.size(); i++) {
            addReview(Globals.lstReviews.get(i));
        }*/
        addClasses();
        if (Globals.isLogin == 1) {
            ServiceManager.serviceLastVisitGym(this);
        }
    }
    public void addClassView(final int paramInt)
    {
        View localView = null;
        AdapterClass.ClassViewHolder localViewHolder = null;

        if (localView == null) {
            localView = LayoutInflater.from(this).inflate(R.layout.item_class, null);

        }
        if (localViewHolder == null) {
            localViewHolder = new AdapterClass.ClassViewHolder();
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

        localViewHolder.txtClassName.setText(Globals.lstClasses.get(paramInt).mName);
        localViewHolder.txtClassGym.setText(Globals.lstClasses.get(paramInt).mGymName);
        localViewHolder.txtClassTime.setText(Globals.lstClasses.get(paramInt).mStartHour.substring(0, 5) + "-" + Globals.lstClasses.get(paramInt).mEndHour.substring(0, 5));
        localViewHolder.txtClassDuration.setText("(" + Globals.lstClasses.get(paramInt).mDuration + " Min)");
        localViewHolder.txtClassCategory.setText(Globals.lstClasses.get(paramInt).mCategory);
        if (Globals.lstClasses.get(paramInt).mCity != "")
            localViewHolder.txtClassCity.setText(Globals.lstClasses.get(paramInt).mCity + ",");
        if (Globals.lstClasses.get(paramInt).mAvailable.equals("0"))
            localViewHolder.txtClassAvailable.setText(getString(R.string.string_full));
        else
            localViewHolder.txtClassAvailable.setText(getResources().getString(R.string.string_available_spots) + ":" + Globals.lstClasses.get(paramInt).mAvailable);
        if (Globals.isLogin == 1)
        {
            if (Globals.lstClasses.get(paramInt).mIsBook == null || Globals.lstClasses.get(paramInt).mIsBook.equals("0"))
            {
                if (Globals.lstClasses.get(paramInt).mAvailable.equals("0"))
                    localViewHolder.txtClassAvailable.setText(getString(R.string.string_full));
                else
                    localViewHolder.txtClassAvailable.setText(getResources().getString(R.string.string_available_spots) + ":" + Globals.lstClasses.get(paramInt).mAvailable);
            }
            else
            {
                localViewHolder.txtClassAvailable.setText(getString(R.string.string_booked));
            }
        }

        Picasso.with(this)
                .load(Constants.IMAGEBASEURL + Globals.lstClasses.get(paramInt).mImage)
                .into(localViewHolder.imgClassImage);
        localView.setTag(localViewHolder);
        localView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.currentClass = Globals.lstClasses.get(paramInt);
                Intent m = new Intent(DetailGymActivity.this, DetailClassActivity.class);
                DetailGymActivity.this.startActivity(m);
            }
        });
        layoutSchedule.addView(localView);
    }
    public void addClasses()
    {
        if (Globals.lstClasses.size() > 0) {
            layoutClasses.setVisibility(View.VISIBLE);
            layoutSchedule.setVisibility(View.VISIBLE);
            layoutUpcomingSchedule.setVisibility(View.GONE);
            layoutSchedule.removeAllViews();
            for (int i = 0;i < Globals.lstClasses.size();i++)
            {
                addClassView(i);
            }
            if (firstLoad == 0) {
                firstLoad = 1;
                hasClass  = 1;
            }
        } else {
            layoutUpcomingSchedule.setVisibility(View.VISIBLE);
            layoutSchedule.setVisibility(View.GONE);
            if (Globals.mUpcomingDate.equals("")) {
                txtUpcoming.setTextColor(0xff000000);
                txtUpcoming.setText(getResources().getString(R.string.string_noupcoming));
                txtUpcoming.setOnClickListener(null);
                layoutClasses.setVisibility(View.GONE);
                if (firstLoad == 0) {
                    firstLoad = 1;
                    hasClass  = 0;
                }
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
                if (firstLoad == 0) {
                    firstLoad = 1;
                    hasClass  = 1;
                }
            }

        }
        if (hasClass == 0)
            layoutClasses.setVisibility(View.GONE);
        else
            layoutClasses.setVisibility(View.VISIBLE);
    }
    public void initScheduleView() {

        layoutUpcomingSchedule = (LinearLayout) this.findViewById(R.id.layoutScheduleUpcoming);
        txtUpcoming = (TextView) this.findViewById(R.id.txtScheduleUpcoming);
        layoutSchedule = (LinearLayout) this.findViewById(R.id.layoutDetailGymSchedule);
        txtDay[0] = (TextView) this.findViewById(R.id.txtScheduleDay1);
        txtDay[1] = (TextView) this.findViewById(R.id.txtScheduleDay2);
        txtDay[2] = (TextView) this.findViewById(R.id.txtScheduleDay3);
        txtDay[3] = (TextView) this.findViewById(R.id.txtScheduleDay4);
        txtDay[4] = (TextView) this.findViewById(R.id.txtScheduleDay5);
        txtDay[5] = (TextView) this.findViewById(R.id.txtScheduleDay6);
        txtDay[6] = (TextView) this.findViewById(R.id.txtScheduleDay7);

        txtDate[0] = (TextView) this.findViewById(R.id.txtScheduleDate1);
        txtDate[1] = (TextView) this.findViewById(R.id.txtScheduleDate2);
        txtDate[2] = (TextView) this.findViewById(R.id.txtScheduleDate3);
        txtDate[3] = (TextView) this.findViewById(R.id.txtScheduleDate4);
        txtDate[4] = (TextView) this.findViewById(R.id.txtScheduleDate5);
        txtDate[5] = (TextView) this.findViewById(R.id.txtScheduleDate6);
        txtDate[6] = (TextView) this.findViewById(R.id.txtScheduleDate7);

        txtBack = (TextView) this.findViewById(R.id.txtScheduleBack);
        txtNext = (TextView) this.findViewById(R.id.txtScheduleNext);
        layoutUpcomingSchedule.setVisibility(View.GONE);

        txtBack.setOnClickListener(this);
        txtNext.setOnClickListener(this);

    }
    public void loadClass() {
        String request = searchDate + "/" + Globals.currentGym.mId + "/-1/-1/-1";
        ServiceManager.serviceSearchClass(request, this);
    }
    public void updateCalendar() {
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
    public boolean isCloseGym()
    {
        Date currentDate = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String currentDateString = sdf1.format(currentDate);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String startDateString = currentDateString + " " + Globals.currentGym.mStartHours.get(currentDay).substring(0, 5);
        String endDateString = currentDateString + " " + Globals.currentGym.mEndHours.get(currentDay).substring(0, 5);
        try {
            long startTimeStamp = format1.parse(startDateString).getTime();
            long endTimeStamp = format1.parse(endDateString).getTime();

            if (currentDate.getTime() > startTimeStamp && currentDate.getTime() < endTimeStamp)
            {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void saveUser() {
        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
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

    @SuppressLint("InflateParams")
    private void addReview(ReviewModel reviewModel) {
        /*View localView;
        ReviewHolder localViewHolder = null;
        localView = LayoutInflater.from(this).inflate(R.layout.item_review, null);

        localViewHolder = new ReviewHolder();
        localViewHolder.imgReviewPicture = (OvalImageView) localView.findViewById(R.id.imgReview);
        localViewHolder.txtName = (TextView) localView.findViewById(R.id.txtReviewName);
        localViewHolder.txtContent = (TextView) localView.findViewById(R.id.txtReviewContent);
        localViewHolder.txtDate = (TextView) localView.findViewById(R.id.txtReviewDate);
        localViewHolder.ratingBar = (RatingBar) localView.findViewById(R.id.ratingReview);
        localView.setTag(localViewHolder);

        localViewHolder.txtName.setText(reviewModel.mName);
        localViewHolder.txtContent.setText(reviewModel.mContent);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date date = null;
        try {
            date = sdf1.parse(reviewModel.mDate);
            localViewHolder.txtDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        localViewHolder.ratingBar.setRating(Float.parseFloat(reviewModel.mRating));
        localViewHolder.imgReviewPicture.setImageDrawable(getResources().getDrawable(R.drawable.banner3));
        Picasso.with(this)
                .load(Constants.IMAGEBASEURL + reviewModel.mImage)
                .into(localViewHolder.imgReviewPicture);
        localView.setTag(localViewHolder);
        //mFeatureGyms.add(localView);
        layoutReviews.addView(localView);*/
    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.dismiss();
    }

    public class ReviewHolder {
        public OvalImageView imgReviewPicture;
        public TextView txtName;
        public TextView txtContent;
        public TextView txtDate;
        public RatingBar ratingBar;
    }
}
