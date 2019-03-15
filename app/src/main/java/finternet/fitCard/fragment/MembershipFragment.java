package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import finternet.fitCard.R;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class MembershipFragment extends Fragment implements View.OnClickListener, ILoadService {
    private View mRootView;
    private Button btnPlan1;
    private Button btnPlan2;
    private Button btnPlan3;
    private TextView txtTitle1;
    private TextView txtTitle2;
    private TextView txtTitle3;

    private TextView txtPrice1;
    private TextView txtPrice2;
    private TextView txtPrice3;

    private TextView txtCredit1;
    private TextView txtCredit2;
    private TextView txtCredit3;

    private EditText editCouponCode;
    private Button btnSubmitCoupon;


    private MaterialDialog mDialog;
    private MaterialDialog mDialog1;
    private MaterialDialog mDialogSuccess;


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_membership, null);
        }
        initView();
        showDialog();
        ServiceManager.serviceLoadPlan(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }

    public void initView() {

        txtTitle1 = (TextView) mRootView.findViewById(R.id.txtPlanTitle1);
        txtTitle2 = (TextView) mRootView.findViewById(R.id.txtPlanTitle2);
        txtTitle3 = (TextView) mRootView.findViewById(R.id.txtPlanTitle3);

        txtPrice1 = (TextView) mRootView.findViewById(R.id.txtPlanPrice1);
        txtPrice2 = (TextView) mRootView.findViewById(R.id.txtPlanPrice2);
        txtPrice3 = (TextView) mRootView.findViewById(R.id.txtPlanPrice3);


        txtCredit1 = (TextView) mRootView.findViewById(R.id.txtPlanCredit1);
        txtCredit2 = (TextView) mRootView.findViewById(R.id.txtPlanCredit2);
        txtCredit3 = (TextView) mRootView.findViewById(R.id.txtPlanCredit3);

        editCouponCode = (EditText) mRootView.findViewById(R.id.editMembershipCoupon);
        btnSubmitCoupon = (Button) mRootView.findViewById(R.id.btnSubmitCoupon);

        btnPlan1 = (Button) mRootView.findViewById(R.id.btnPlan1);
        btnPlan2 = (Button) mRootView.findViewById(R.id.btnPlan2);
        btnPlan3 = (Button) mRootView.findViewById(R.id.btnPlan3);

        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Ladataan jäsenyytesi tietoja")
                .progress(true, 0)
                .build();
        mDialog1 = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Käsitellään maksua")
                .progress(true, 0)
                .build();
        mDialogSuccess = new MaterialDialog.Builder(this.getContext())
                .title("Onnistunut päivitys")
                .content("Maksu suoritettu")
                .positiveText("OK")
                .build();
        mDialogSuccess.setCancelable(false);
        btnSubmitCoupon.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlan1:
                Globals.currentPlan = 0;
                break;
            case R.id.btnPlan2:
                Globals.currentPlan = 1;
                break;
            case R.id.btnPlan3:
                Globals.currentPlan = 2;
                break;
            case R.id.btnSubmitCoupon:
                String strCode = editCouponCode.getText().toString();
                if (strCode.equals(""))
                {
                    Toast.makeText(this.getContext(),"Empty Code",Toast.LENGTH_SHORT).show();
                    return;
                }
                ServiceManager.serviceCoupon(strCode,this);
                return;
        }
        if (Globals.mAccount.mHasCard == null || Globals.mAccount.mHasCard.equals("false") || Globals.mAccount.mHasCard.equals("")) {
            Globals.e_mode = Enums.MODE.CHARGE;
            ((HomeActivity) getActivity()).setFragment();
        }
        else
        {
            MaterialDialog mWelcome = new MaterialDialog.Builder(this.getContext())
                    .title(this.getContext().getResources().getString(R.string.string_changemember))
                    .positiveText(this.getContext().getResources().getString(R.string.string_yes))
                    .negativeText(this.getContext().getResources().getString(R.string.string_no))
                    .show();
            mWelcome.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    mDialog1.show();
                    ServiceManager.serviceChargeMemberWithToken(Globals.mAccount.mId,Globals.lstPlans.get(Globals.currentPlan).mId,MembershipFragment.this);
                }
            });



        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessLoad(int type) {
        if (type == 1)
        {
            new MaterialDialog.Builder(this.getContext())
                    .title("Notice")
                    .content("Your credits charged with Coupon Code")
                    .show();
            return;
        }
        else if (type == 2)
        {
            new MaterialDialog.Builder(this.getContext())
                    .title("Warning")
                    .content("You input Wrong Coupon code")
                    .show();
            return;
        }
        else if (type == 4)
        {
            mDialog1.hide();
            Globals.mAccount.mPlan = Globals.lstPlans.get(Globals.currentPlan).mId;
            Globals.mAccount.mCredit = Globals.lstPlans.get(Globals.currentPlan).mCredit;
            Globals.mAccount.mHasCard = "true";
            //saveUser
            saveUser();
            mDialogSuccess.show();
            mDialogSuccess.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Globals.e_mode = Enums.MODE.PROFILE;
                    ((HomeActivity) MembershipFragment.this.getActivity()).setFragment();
                }
            });
            return;
        }
        if (Globals.lstPlans.size() > 2) {
            txtTitle1.setText(Globals.lstPlans.get(0).mName);
            txtTitle2.setText(Globals.lstPlans.get(1).mName);
            txtTitle3.setText(Globals.lstPlans.get(2).mName);

            txtCredit1.setText(Globals.lstPlans.get(0).mCredit + " " + getResources().getString(R.string.string_crditeachmonth));
            txtCredit2.setText(Globals.lstPlans.get(1).mCredit + " " + getResources().getString(R.string.string_crditeachmonth));
            txtCredit3.setText(Globals.lstPlans.get(2).mCredit + " " + getResources().getString(R.string.string_crditeachmonth));

            txtPrice1.setText("€" + Globals.lstPlans.get(0).mPrice + "/" + getResources().getString(R.string.string_each_month));
            txtPrice2.setText("€" + Globals.lstPlans.get(1).mPrice + "/" + getResources().getString(R.string.string_each_month));
            txtPrice3.setText("€" + Globals.lstPlans.get(2).mPrice + "/" + getResources().getString(R.string.string_each_month));


            selectPlan();
        }
    }
    private void saveUser() {
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().putString("id", Globals.mAccount.mId).apply();
        sp.edit().putString("name", Globals.mAccount.mName).apply();
        sp.edit().putString("image", Globals.mAccount.mImage).apply();
        sp.edit().putString("address", Globals.mAccount.mAddress).apply();
        sp.edit().putString("phone", Globals.mAccount.mPhone).apply();
        sp.edit().putString("city", Globals.mAccount.mCity).apply();
        sp.edit().putString("credit", Globals.mAccount.mCredit).apply();
        sp.edit().putString("plan", Globals.mAccount.mPlan).apply();
        sp.edit().putString("email", Globals.mAccount.mEmail).apply();
        sp.edit().putString("token", Globals.mAccount.mToken).apply();
        sp.edit().putString("hasCard", Globals.mAccount.mHasCard).apply();

    }
    @SuppressLint("SetTextI18n")
    public void selectPlan() {
        btnPlan1.setText(getResources().getString(R.string.string_select));
        btnPlan2.setText(getResources().getString(R.string.string_select));
        btnPlan3.setText(getResources().getString(R.string.string_select));
        btnPlan1.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_home_button));
        btnPlan2.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_home_button));
        btnPlan3.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_home_button));

        btnPlan1.setOnClickListener(this);
        btnPlan2.setOnClickListener(this);
        btnPlan3.setOnClickListener(this);


        if (Globals.lstPlans.get(0).mId.equals(Globals.mAccount.mPlan)) {
            btnPlan1.setText(getResources().getString(R.string.string_selected));
            btnPlan1.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_search_button));
            btnPlan1.setOnClickListener(null);
        } else if (Globals.lstPlans.get(1).mId.equals(Globals.mAccount.mPlan)) {
            btnPlan2.setText(getResources().getString(R.string.string_selected));
            btnPlan2.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_search_button));
            btnPlan2.setOnClickListener(null);
        } else if (Globals.lstPlans.get(2).mId.equals(Globals.mAccount.mPlan)) {
            btnPlan3.setText(getResources().getString(R.string.string_selected));
            btnPlan3.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_search_button));
            btnPlan3.setOnClickListener(null);
        }

    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.hide();
        mDialog1.hide();
    }
}
