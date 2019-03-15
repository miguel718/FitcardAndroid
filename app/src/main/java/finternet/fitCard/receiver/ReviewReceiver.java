package finternet.fitCard.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import finternet.fitCard.R;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.service.ServiceManager;

@SuppressWarnings("ALL")
public class ReviewReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        for (int i = 0; i < Globals.lstReviewGyms.size(); i++) {
            showDialog(context, "", Globals.lstReviewGyms.get(i).mName, Globals.lstReviewGyms.get(i).mId, 0, Globals.lstReviewGyms.get(i).mBookId);
        }
        for (int i = 0; i < Globals.lstReviewClasses.size(); i++) {
            showDialog(context, Globals.lstReviewClasses.get(i).mGymId, Globals.lstReviewClasses.get(i).mName, Globals.lstReviewClasses.get(i).mId, 1, Globals.lstReviewClasses.get(i).mBookId);
        }
    }

    private void showDialog(final Context con, final String gym, String name, final String mId, final int type, final String mBookId) {
        boolean wrapInScrollView = true;
        MaterialDialog mFeedback = new MaterialDialog.Builder(con)
                .title(con.getResources().getString(R.string.string_pleasereview) +" " +  name)
                .customView(R.layout.layout_dialog_feedback, true)
                .positiveText(con.getResources().getString(R.string.string_done))
                .autoDismiss(false)
                .show();
        mFeedback.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                final EditText editTitle = (EditText) materialDialog.getCustomView().findViewById(R.id.editDialogReviewTitle);
                EditText editContent = (EditText) materialDialog.getCustomView().findViewById(R.id.editDialogReviewContent);
                RatingBar rateBar = (RatingBar) materialDialog.getCustomView().findViewById(R.id.rateFeedback);
                editTitle.setText("Review");
                if (type == 0)
                    ServiceManager.serviceReviewGym(mId, editTitle.getText().toString(), editContent.getText().toString(), rateBar.getRating(), mBookId);
                else
                    ServiceManager.serviceReviewClass(gym, mId, editTitle.getText().toString(), editContent.getText().toString(), rateBar.getRating(), mBookId);

                materialDialog.dismiss();
            }
        });
        mFeedback.setCancelable(false);
    }
}
