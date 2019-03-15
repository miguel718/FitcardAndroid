package finternet.fitCard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import finternet.fitCard.R;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.ReviewModel;
import finternet.fitCard.receiver.ReviewReceiver;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;
import finternet.fitCard.view.OvalImageView;


@SuppressWarnings("ALL")
public class DetailClassActivity extends Activity implements View.OnClickListener, ILoadService {


    private ImageView imgBack;
    private ImageView imgPhoto;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtReviews;
    private TextView txtAddress;
    private TextView txtHour;
    private TextView txtCategory;
    private TextView txtGymName;
    private TextView txtDetailAddress;
    private TextView txtDetailClassDate;
    private LinearLayout layoutReviews;
    private Button btnBook;
    private MaterialDialog mDialog;
    private MaterialDialog mDialogSuccess;
    private RatingBar ratingBar;
    private TextView txtReviewLabel;
    private final ReviewReceiver broadcastReceiver = new ReviewReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailclass);
        registerReceiver(broadcastReceiver, new IntentFilter("finternet.fitcard.CUSTOM_INTENT"));
        initView();
        setData();
        showDialog();
        ServiceManager.serviceLoadReviewForClass(Globals.currentClass.mId, this);
    }

    private void initView() {
        imgBack = (ImageView) this.findViewById(R.id.btnDetailClassBack);
        imgPhoto = (ImageView) this.findViewById(R.id.imgDetailClassPicture);
        txtName = (TextView) this.findViewById(R.id.txtDetailClassName);
        txtDescription = (TextView) this.findViewById(R.id.txtDetailClassDescription);
        txtReviews = (TextView) this.findViewById(R.id.txtDetailClassReview);
        layoutReviews = (LinearLayout) this.findViewById(R.id.layoutDetailClassReview);
        btnBook = (Button) this.findViewById(R.id.btnDetailClassBook);
        txtHour = (TextView) this.findViewById(R.id.txtDetailClassHour);
        ratingBar = (RatingBar) this.findViewById(R.id.ratingDetailClass);
        txtAddress = (TextView) this.findViewById(R.id.txtDetailClassAddress);
        txtCategory = (TextView) this.findViewById(R.id.txtClassCategory);
        txtGymName = (TextView) this.findViewById(R.id.txtClassGymName);
        txtDetailAddress = (TextView) this.findViewById(R.id.txtClassAddress);
        txtReviewLabel = (TextView) this.findViewById(R.id.txtDetailClassReviewLabel);
        txtDetailClassDate = (TextView) this.findViewById(R.id.txtDetailClassDate);

        imgBack.setOnClickListener(this);
        btnBook.setOnClickListener(this);

        mDialog = new MaterialDialog.Builder(this)
                .title("Pieni hetki...")
                .content("Loading Review Infos")
                .progress(true, 0)
                .build();
        mDialogSuccess = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.string_successClass))
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

                        ShareDialog shareDialog = new ShareDialog(DetailClassActivity.this);
                        shareDialog.registerCallback(Globals.g_callbackManager, new FacebookCallback<Sharer.Result>() {
                            @Override
                            public void onSuccess(Sharer.Result result) {
                                new MaterialDialog.Builder(DetailClassActivity.this)
                                        .title("Osake")
                                        .content("Postattu Facebookissa").show();
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
                        ShareDialog.show(DetailClassActivity.this, linkContent2);
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
    private void setData(){
        txtName.setText(Globals.currentClass.mName);
        //txtReviews.setText("(" + Globals.currentClass.m + ") Reviews");
        txtDetailAddress.setText(Globals.currentClass.mCity + " " + Globals.currentClass.mAddress);
        txtGymName.setText(Globals.currentClass.mGymName);
        txtCategory.setText(Globals.currentClass.mCategory);
        txtDescription.setText(getResources().getString(R.string.string_description) + Globals.currentClass.mDescription);
        Picasso.with(this)
                .load(Constants.IMAGEBASEURL + Globals.currentClass.mImage)
                .into(imgPhoto);
        Calendar calendar = Calendar.getInstance();
        String ss = Globals.currentClass.mStartHour.substring(0, 5) + "-" + Globals.currentClass.mEndHour.substring(0, 5);
        txtHour.setText(ss);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM", Locale.US);
        Date dt = null;
        try {
            dt = sdf.parse(Globals.currentClass.mDate);
            txtDetailClassDate.setText(sdf1.format(dt) + " "+ ss);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtAddress.setText(Globals.currentClass.mCity);

        if (Globals.isLogin == 0)
            btnBook.setVisibility(View.GONE);
        else
            btnBook.setVisibility(View.VISIBLE);
        /*if (Globals.currentGym.mRating == null || Globals.currentGym.mRating.equals(""))
            ratingGym.setRating(0);
        else
            ratingGym.setRating(Float.parseFloat(Globals.currentGym.mRating));*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDetailClassBack:
                finish();
                break;
            case R.id.btnDetailClassBook:
                if (Integer.parseInt(Globals.mAccount.mCredit) > 0)
                    ServiceManager.serviceBookClass(this);
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
                                new MaterialDialog.Builder(DetailClassActivity.this)
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
        }
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessLoad(int type) {

        if (type == 2) {
            btnBook.setText(getResources().getString(R.string.string_booked));
            Globals.currentClass.mAvailable = String.valueOf(Integer.parseInt(Globals.currentClass.mAvailable) - 1);
            btnBook.setOnClickListener(null);
            Globals.mAccount.mCredit = String.valueOf(Integer.parseInt(Globals.mAccount.mCredit) - 1);
            saveUser();
            mDialogSuccess.show();
            return;
        } else if (type == 3) {
            if (Globals.mClassBook == 1) {
                btnBook.setText(getResources().getString(R.string.string_booked));
                btnBook.setOnClickListener(null);
            } else {
                if (Integer.parseInt(Globals.currentClass.mAvailable) > 0) {
                    btnBook.setText(getResources().getString(R.string.string_book));
                    btnBook.setOnClickListener(this);
                }
                else
                {
                    btnBook.setText(getResources().getString(R.string.string_full));
                    btnBook.setOnClickListener(null);
                }
            }
            return;
        }
        layoutReviews.removeAllViews();
        float rate = 0;
        if (Globals.lstReviews.size() > 0)
            txtReviewLabel.setText(getResources().getString(R.string.string_review));
        else
            txtReviewLabel.setText(getResources().getString(R.string.string_no_review));

        /*for (int i = 0; i < Globals.lstReviews.size(); i++) {
            addReview(Globals.lstReviews.get(i));
            rate += Integer.parseInt(Globals.lstReviews.get(i).mRating);
        }*/
        if (Globals.lstReviews.size() == 0) rate = 0;
        else rate /= Globals.lstReviews.size();
        txtReviews.setText("(" + String.valueOf(Globals.lstReviews.size()) + ") " + getResources().getString(R.string.string_review));
        ratingBar.setRating(rate);
        if (Globals.isLogin == 1) {
            ServiceManager.serviceLastVisitClass(this);
        }
        if (Integer.parseInt(Globals.currentClass.mAvailable) > 0) {
            btnBook.setText("Book");
            btnBook.setOnClickListener(this);
        }
        else
        {
            btnBook.setText("Full");
            btnBook.setOnClickListener(null);
        }
    }

    @SuppressLint("InflateParams")
    private void addReview(ReviewModel reviewModel) {
        View localView;
        ReviewHolder localViewHolder;
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
        localViewHolder.txtDate.setText(reviewModel.mDate);
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
        layoutReviews.addView(localView);
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
