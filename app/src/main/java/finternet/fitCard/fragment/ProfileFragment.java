package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import finternet.fitCard.R;
import finternet.fitCard.activity.HomeActivity;
import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Enums;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.model.UserModel;
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;
import finternet.fitCard.view.OvalImageView;


@SuppressWarnings("ALL")
public class ProfileFragment extends Fragment implements View.OnClickListener, ILoadService {

    private View mRootView;
    private OvalImageView imgProfile;
    private ImageView imgFacebook;
    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editAddress;
    private EditText editCity;
    private TextView txtCredit;
    private Button btnUpdateProfile;
    private Button btnReplace;
    private String imgPath;
    private MaterialDialog mDialog;
	private MaterialDialog mDialog1;
    private MaterialDialog mDialogSucess;
    public LinearLayout layoutProfile;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_profile, null);
        }
        initView();
        return mRootView;
    }

    @SuppressLint("SetTextI18n")
    public void initView() {
        imgProfile = (OvalImageView) mRootView.findViewById(R.id.imgProfile);
        imgFacebook = (ImageView) mRootView.findViewById(R.id.imgProfileFacebook);
        editName = (EditText) mRootView.findViewById(R.id.editProfileName);
        editEmail = (EditText) mRootView.findViewById(R.id.editProfileEmail);
        editPhone = (EditText) mRootView.findViewById(R.id.editProfilePhone);
        editAddress = (EditText) mRootView.findViewById(R.id.editProfileAddress);
        editCity = (EditText) mRootView.findViewById(R.id.editProfileCity);
        txtCredit = (TextView) mRootView.findViewById(R.id.txtProfileCredit);
        btnUpdateProfile = (Button) mRootView.findViewById(R.id.btnProfileUpdate);
        btnReplace = (Button) mRootView.findViewById(R.id.btnProfileReplaceCard);
        layoutProfile = (LinearLayout) mRootView.findViewById(R.id.layoutProfile);

        ServiceManager.serviceProfile(this);

        btnUpdateProfile.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnReplace.setOnClickListener(this);
        layoutProfile.setOnClickListener(this);
        mDialog = new MaterialDialog.Builder(this.getContext())
                .title("Pieni hetki...")
                .content("Update Profile")
                .progress(true, 0)
                .build();
		mDialog1 = new MaterialDialog.Builder(this.getContext())
                .content("Kortti on poistettu")
                .build();
        mDialogSucess = new MaterialDialog.Builder(this.getContext())
                .title("Onnistunut päivitys")
                .content("Profiilin päivitys onnistunut")
                .build();
    }
    public void setData()
    {
        Picasso.with(this.getContext())
                .load(Constants.IMAGEBASEURL + Globals.mAccount.mImage)
                .into(imgProfile);
        editName.setText(Globals.mAccount.mName);
        editEmail.setText(Globals.mAccount.mEmail);
        editPhone.setText(Globals.mAccount.mPhone);
        editAddress.setText(Globals.mAccount.mAddress);
        editCity.setText(Globals.mAccount.mCity);
        txtCredit.setText("Tämänhetkinen treenimäärä:" + Globals.mAccount.mCredit);// + " " + getResources().getString(R.string.string_credit));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfileReplaceCard:
                ServiceManager.serviceCleanCode(this);
                break;
            case R.id.layoutProfile:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.btnProfileUpdate:
                showDialog();
                UserModel uModel = new UserModel();
                uModel.mId = Globals.mAccount.mId;
                uModel.mName = editName.getText().toString();
                uModel.mCity = editCity.getText().toString();
                uModel.mPhone = editPhone.getText().toString();
                uModel.mAddress = editAddress.getText().toString();
                uModel.mImage = imgPath;
                if (uModel.mName.equals("")) {
                    Toast.makeText(this.getContext(), "Nimikenttä tyhjä", Toast.LENGTH_SHORT).show();
                    return;
                } else if (uModel.mCity.equals("")) {
                    Toast.makeText(this.getContext(), "Kaupunkikohta tyhjä", Toast.LENGTH_SHORT).show();
                    return;
                } else if (uModel.mPhone.equals("")) {
                    Toast.makeText(this.getContext(), "Puhelinnumero tyhjä", Toast.LENGTH_SHORT).show();
                    return;
                } else if (uModel.mAddress.equals("")) {
                    Toast.makeText(this.getContext(), "Osoite tyhjä", Toast.LENGTH_SHORT).show();
                    return;
                }
                ServiceManager.serviceProfileImageSave(uModel, this);
                break;
            case R.id.imgProfile:
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                this.startActivityForResult(
                        Intent.createChooser(intent, "Select File"), 13);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 13:
                if (data != null) {
                    Uri m_uri = data.getData();
                    if (m_uri != null) {

                        this.imgPath = Globals.getRealPathFromURI(this.getActivity(), m_uri);
                        imgProfile.setImageURI(m_uri);
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccessLoad(int type) {
		if (type == 1)
		{
            Globals.mAccount.mHasCard = "";
            saveUser();
			mDialog1.show();
            return;
		}
        else if (type == 3)
        {
            setData();
            return;
        }
        saveUser();
        initView();
        mDialogSucess.show();
        saveUser();
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
