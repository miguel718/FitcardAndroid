package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.Collection;

import finternet.fitCard.R;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


@SuppressWarnings("ALL")
public class LoginFragment extends Fragment implements View.OnClickListener, ILoadService {

    private View mRootView;
    private EditText editUser;
    private EditText editPassword;
    private TextView txtForgetPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnFacebook;
    private MaterialDialog mDialog;
    public LinearLayout layoutLogin;








    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_login, null);
        }
        initView();
        return mRootView;
    }

    public void initView() {
        editUser = (EditText) mRootView.findViewById(R.id.editLoginUser);
        editPassword = (EditText) mRootView.findViewById(R.id.editLoginPassword);
        txtForgetPassword = (TextView) mRootView.findViewById(R.id.editLoginForget);
        btnLogin = (Button) mRootView.findViewById(R.id.btnLoginLogin);
        btnRegister = (Button) mRootView.findViewById(R.id.btnLoginSignUp);
        btnFacebook = (Button) mRootView.findViewById(R.id.btnLoginFaceBook);
        layoutLogin = (LinearLayout) mRootView.findViewById(R.id.layoutLogin);
        layoutLogin.setOnClickListener(this);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        txtForgetPassword.setOnClickListener(this);
        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Tarkistetaan tilin asetuksia")
                .progress(true, 0)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoginLogin:
                String user = editUser.getText().toString();
                String password = editPassword.getText().toString();
                if (user.equals("")) {
                    Toast.makeText(this.getContext(), "Käyttäjänimi puuttuu", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.equals("")) {
                    Toast.makeText(this.getContext(), "Salasana puuttuu", Toast.LENGTH_SHORT).show();
                    return;
                }
                ServiceManager.serviceLoginUser(user, password, this);
                break;
            case R.id.layoutLogin:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.editLoginForget:
                break;
            case R.id.btnLoginSignUp:
                //m = new Intent(this,RegisterActivity.class);
                //this.startActivity(m);
                Globals.e_mode = Enums.MODE.REGISTER;
                ((HomeActivity) getActivity()).setFragment();
                break;
            case R.id.btnLoginFaceBook:
                //((HomeActivity)getActivity()).loginManager.logInWithReadPermissions(this, ((HomeActivity)getActivity()).permissions);
                ((HomeActivity)getActivity()).startFacebookLogin();
                break;
        }
    }

    @Override
    public void onSuccessLoad(int type) {
        if (type == 1) {
            new MaterialDialog.Builder(this.getContext())
                    .title("Error")
                    .content("Kirjautumisen kanssa ongelmia, yritä uudelleen ")
                    .show();
        }
        else {
            Globals.isLogin = 1;
            Globals.e_mode = Enums.MODE.HOME;
            if (Globals.mAccount.mPlan.equals("4"))
                Globals.e_mode = Enums.MODE.PRICE;
            saveUser();
            ((HomeActivity) getActivity()).loginMode();
            ((HomeActivity) getActivity()).setFragment();
        }
    }

    public void saveUser() {
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
