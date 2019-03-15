package finternet.fitCard.fragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import finternet.fitCard.service.ILoadService;
import finternet.fitCard.service.ServiceManager;


public class ContactUsFragment extends Fragment implements View.OnClickListener,ILoadService{

    private View mRootView;
    public Button btnSend;
    public EditText editName;
    public EditText editEmail;
    public EditText editMessage;
    public EditText editPhone;
    public EditText editAddress;
    public LinearLayout layoutContact;
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_contact, null);
        }
        initView();
        return mRootView;
    }
    public void initView()
    {
        editName = (EditText)mRootView.findViewById(R.id.editContactName);
        editEmail = (EditText)mRootView.findViewById(R.id.editContactEmail);
        editMessage = (EditText)mRootView.findViewById(R.id.editContactMessage);
        editPhone = (EditText)mRootView.findViewById(R.id.editContactPhone);
        editAddress = (EditText)mRootView.findViewById(R.id.editContactAddress);
        layoutContact = (LinearLayout) mRootView.findViewById(R.id.layoutContact);

        layoutContact.setOnClickListener(this);

        btnSend = (Button) mRootView.findViewById(R.id.btnContactSend);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.layoutContact:
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.btnContactSend:
                if (editMessage.getText().toString().equals(""))
                {
                    Toast.makeText(this.getContext(),getResources().getString(R.string.string_contact_fillmessage),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editEmail.getText().toString().equals(""))
                {
                    Toast.makeText(this.getContext(),getResources().getString(R.string.string_contact_fillemail),Toast.LENGTH_SHORT).show();
                    return;
                }
                String message = "Name:" + editName.getText().toString() + "<br>" + "Email:" + editEmail.getText().toString() + "<br>" +
                        "Phone:" + editPhone.getText().toString() + "<br>" + "Address:" + editAddress.getText().toString() + "<br><br>" +
                        editMessage.getText().toString();

                ServiceManager.serviceSendContact(message,this);
                new MaterialDialog.Builder(this.getContext())
                        .title(getResources().getString(R.string.string_contact_successSend))
                        .negativeText("Close")
                        .show();
                break;
        }
    }

    @Override
    public void onSuccessLoad(int type) {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {

    }
}
