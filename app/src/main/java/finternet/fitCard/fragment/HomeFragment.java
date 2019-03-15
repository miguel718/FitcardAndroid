package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import finternet.fitCard.R;
import finternet.fitCard.activity.DetailGymActivity;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.adapter.AdapterHomePage;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.GymModel;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;
import finternet.fitCard.util.HttpUtil;


@SuppressWarnings("ALL")
public class HomeFragment extends Fragment implements View.OnClickListener, ILoadService {
    private View mRootView = null;
    private LinearLayout layoutGyms;
    private RelativeLayout layoutFindClass;
    private RelativeLayout layoutFindGym;
    private ViewPager viewPager;
    private ArrayList<View> mFeatureGyms;
    private AdapterHomePage adapterHomePage;
    private MaterialDialog mDialog;
    private int firstTime = 0;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_home, null);
        }

        initView();
        showDialog();
        ServiceManager.serviceLoadOverReview(this.getActivity().getApplicationContext());
        //ServiceManager.serviceLoadLocation(this);
        ServiceManager.serviceFeaturedGym(this);

        //viewPager();
        getSavedUser();
        if (Globals.mAccount.mId.equals(""))
            Globals.isLogin = 0;
        else Globals.isLogin = 1;
        ((HomeActivity) getActivity()).loginMode();
        return mRootView;
    }
    private void getSavedUser() {
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        Globals.mAccount.mId = sp.getString("id", "");
        Globals.mAccount.mName = sp.getString("name", "");
        Globals.mAccount.mImage = sp.getString("image", "");
        Globals.mAccount.mAddress = sp.getString("address", "");
        Globals.mAccount.mPhone = sp.getString("phone", "");
        Globals.mAccount.mCity = sp.getString("city", "");
        Globals.mAccount.mCredit = sp.getString("credit", "");
        Globals.mAccount.mPlan = sp.getString("plan", "");
        Globals.mAccount.mEmail = sp.getString("email", "");
        Globals.mAccount.mInvoiceStart = sp.getString("invoicestart","");
        Globals.mAccount.mInvoiceEnd = sp.getString("invoiceend","");
        Globals.mAccount.mToken = sp.getString("token","");
        Globals.mAccount.mHasCard = sp.getString("hasCard","");

        firstTime = sp.getInt("first", 0);
        if (firstTime == 0) {
            sp.edit().putInt("first", 1).apply();
            MaterialDialog mWelcome = new MaterialDialog.Builder(this.getContext())
                    .title(getResources().getString(R.string.string_welcomefitcard))
                    .positiveText(getResources().getString(R.string.string_login))
                    .negativeText(getResources().getString(R.string.string_signup))
                    .show();
            mWelcome.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    Globals.e_mode = Enums.MODE.LOGIN;
                    ((HomeActivity) HomeFragment.this.getActivity()).setFragment();
                }
            });
            mWelcome.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    Globals.e_mode = Enums.MODE.REGISTER;
                    ((HomeActivity) HomeFragment.this.getActivity()).setFragment();
                }
            });
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }

    private void initView() {
        layoutGyms = (LinearLayout) mRootView.findViewById(R.id.layoutFeaturedGym);
        layoutFindClass = (RelativeLayout) mRootView.findViewById(R.id.relFindClasses);
        layoutFindGym = (RelativeLayout) mRootView.findViewById(R.id.relFindGym);

        layoutFindClass.setOnClickListener(this);
        layoutFindGym.setOnClickListener(this);

        mDialog = new MaterialDialog.Builder(this.getContext())
                .title(getResources().getString(R.string.string_please_wait))
                .content(getResources().getString(R.string.string_loading_feature))
                .progress(true, 0)
                .build();

        addViews();
    }
    private void addViews() {
        layoutGyms.removeAllViews();
        for (int i = 0; i < Globals.lstFeaturedGym.size(); i++) {
            addGymView(Globals.lstFeaturedGym.get(i));
        }
    }

    @SuppressLint("InflateParams")
    private void addGymView(final GymModel gymModel) {
        View localView;
        GymViewHolder localViewHolder = null;
        localView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_home_feature, null);

        localViewHolder = new GymViewHolder();
        localViewHolder.imgGymPicture = (ImageView) localView.findViewById(R.id.imgFeatureGym);
        localViewHolder.txtGymName = (TextView) localView.findViewById(R.id.txtFeatureGym);
        localView.setTag(localViewHolder);

        localViewHolder.txtGymName.setText(gymModel.mName);
        //localViewHolder.ratGym.setRating(3.8f);
        localViewHolder.imgGymPicture.setImageDrawable(getResources().getDrawable(R.drawable.banner3));
        Picasso.with(this.getContext())
                .load(Constants.IMAGEBASEURL + gymModel.mImage)
                .into(localViewHolder.imgGymPicture);
        localView.setTag(localViewHolder);
        localView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.currentGym = gymModel;
                Intent m = new Intent(HomeFragment.this.getContext(), DetailGymActivity.class);
                startActivityForResult(m, 1);
            }
        });
        //mFeatureGyms.add(localView);
        layoutGyms.addView(localView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relFindClasses:
                Globals.e_mode = Enums.MODE.CLASSES;
                break;
            case R.id.relFindGym:
                Globals.e_mode = Enums.MODE.GYM;
                break;
        }
        ((HomeActivity) getActivity()).setFragment();
    }

    @Override
    public void onSuccessLoad(int type) {
        addViews();
    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        try {
            mDialog.dismiss();
        } catch (Exception ignored) {

        }
    }

    public class GymViewHolder {
        public ImageView imgGymPicture;
        public TextView txtGymName;
        // public RatingBar ratGym;
    }

}