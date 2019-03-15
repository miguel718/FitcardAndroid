package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import finternet.fitCard.R;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class RegisterFragment extends Fragment implements View.OnClickListener, ILoadService {

    private View mRootView;
    private EditText editUser;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnRegister;
    private MaterialDialog mDialog;
    private MaterialDialog mDialogSuccess;
    public LinearLayout layoutRegister;


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_register, null);
        }
        initView();
        return mRootView;
    }

    private void initView() {
        editUser = (EditText) mRootView.findViewById(R.id.editRegisterUser);
        editEmail = (EditText) mRootView.findViewById(R.id.editRegisterEmail);
        editPassword = (EditText) mRootView.findViewById(R.id.editRegisterPassword);
        btnRegister = (Button) mRootView.findViewById(R.id.btnRegisterRegister);
        layoutRegister = (LinearLayout) mRootView.findViewById(R.id.layoutRegister);

        btnRegister.setOnClickListener(this);
        layoutRegister.setOnClickListener(this);
        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Luodaan tiliä")
                .progress(true, 0)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutRegister:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.btnRegisterRegister:
                String user = editUser.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if (user.equals("")) {
                    Toast.makeText(this.getContext(), "Käyttäjänimi puuttuu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.equals("")) {
                    Toast.makeText(this.getContext(), "Kirjoita sähköpostiosoite", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.equals("")) {
                    Toast.makeText(this.getContext(), "Salasana puuttuu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(email)) {
                    Toast.makeText(this.getContext(), "Syötä sähköpostiosoite oikein ja yritä uudelleen", Toast.LENGTH_SHORT).show();
                    return;
                }
                ServiceManager.serviceRegisterUser(user, password, email, this);
                break;
        }
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onSuccessLoad(int type) {
        if (type == 1) {
            Toast.makeText(this.getContext(), "Sähköpostiosoite löytyy jo tietokannastamme", Toast.LENGTH_SHORT).show();
        } else {
            Globals.isLogin = 1;
            Globals.e_mode = Enums.MODE.PRICE;
            saveUser();
            ((HomeActivity) getActivity()).loginMode();
            ((HomeActivity) getActivity()).setFragment();
            new MaterialDialog.Builder(this.getContext())
                    .title("Kiitos rekisteröitymisestä")
                    .content("Valitse jäsenyys")
                    .show();


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
        mDialog.dismiss();
    }
}
