package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import finternet.fitCard.R;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class ChargeFragment extends Fragment implements View.OnClickListener, ILoadService {

    private View mRootView;
    private Spinner spMonth;
    private Spinner spYear;
    private EditText editCardNumber;
    private EditText editCVV;
    private Button btnCharge;
    private MaterialDialog mDialog;
    private MaterialDialog mDialogSuccess;
    public MaterialDialog mDialogError;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_charge, null);
        }
        initView();
        return mRootView;
    }

    private void initView() {
        spMonth = (Spinner) mRootView.findViewById(R.id.spMonth);
        spYear = (Spinner) mRootView.findViewById(R.id.spYear);
        editCardNumber = (EditText) mRootView.findViewById(R.id.editChargeCard);
        editCVV = (EditText) mRootView.findViewById(R.id.editChargeCVV);
        TextView txtAmount = (TextView) mRootView.findViewById(R.id.txtChargeAmount);
        btnCharge = (Button) mRootView.findViewById(R.id.btnCharge);
        txtAmount.setText("1");
        if (Globals.currentPlan < 5)
            txtAmount.setText(Globals.lstPlans.get(Globals.currentPlan).mPrice);

        btnCharge.setOnClickListener(this);

        mDialog = new MaterialDialog.Builder(this.getContext())
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
        mDialogError = new MaterialDialog.Builder(this.getContext())
                .title("Virhe")
                .content("Maksun kanssa ilmeni ongelmia, yritä uudelleen")
                .positiveText("OK")
                .build();
        mDialog.setCancelable(false);

        /*SharedPreferences sp = getActivity().getSharedPreferences("credit", Context.MODE_PRIVATE);
        editCardNumber.setText(sp.getString("card", ""));
        spMonth.setSelection(sp.getInt("month",0));
        spYear.setSelection(sp.getInt("year",0));*/

    }

    @Override
    public void onSuccessLoad(int type) {
        hideDialog();
        if (Globals.currentPlan < 5) {
            Globals.mAccount.mPlan = Globals.lstPlans.get(Globals.currentPlan).mId;
            Globals.mAccount.mCredit = Globals.lstPlans.get(Globals.currentPlan).mCredit;
            Globals.mAccount.mHasCard = "true";
        }
        saveUser();
        if (Globals.currentPlan < 5)
            mDialogSuccess.show();
        if (Globals.currentPlan == 5)
        {
            Globals.e_mode = Enums.MODE.PROFILE;
            ((HomeActivity) ChargeFragment.this.getActivity()).setFragment();
        }
        mDialogSuccess.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Globals.e_mode = Enums.MODE.PROFILE;
                ((HomeActivity) ChargeFragment.this.getActivity()).setFragment();
            }
        });

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
        sp.edit().putString("invoiceend",Globals.mAccount.mInvoiceEnd).apply();
        sp.edit().putString("invoicestart",Globals.mAccount.mInvoiceStart).apply();
        sp.edit().putString("token", Globals.mAccount.mToken).apply();
        sp.edit().putString("hasCard", Globals.mAccount.mHasCard).apply();

    }

    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCharge:

                String cardNumber = editCardNumber.getText().toString();
                String cvv = editCVV.getText().toString();
                String month = (String) spMonth.getItemAtPosition(spMonth.getSelectedItemPosition());
                String year = (String) spYear.getItemAtPosition(spYear.getSelectedItemPosition());

                if (cardNumber.equals("")) {
                    Toast.makeText(this.getContext(), "Pankki / Luottokortin numero", Toast.LENGTH_SHORT).show();
                    return;
                } else if (cvv.equals("")) {
                    Toast.makeText(this.getContext(), "Please input CVV", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog();
                //cardNumber = "4012888888881881";
                //month = "02";
                //year = "2020";
                //cvv = "1234";
                /*SharedPreferences sp = getActivity().getSharedPreferences("credit", Context.MODE_PRIVATE);
                sp.edit().putString("card", cardNumber).apply();
                sp.edit().putInt("month", spMonth.getSelectedItemPosition()).apply();
                sp.edit().putInt("year", spYear.getSelectedItemPosition()).apply();*/
                String amount = "10";
                int aCents = 0;
                if (Globals.currentPlan < 5) {

                    amount = Globals.lstPlans.get(Globals.currentPlan).mPrice;
                    aCents = Integer.parseInt(amount) * 100;
                }
                ServiceManager.serviceChargeFund(cardNumber, cvv, month, year, String.valueOf(aCents), this);
                break;
        }
    }
}
