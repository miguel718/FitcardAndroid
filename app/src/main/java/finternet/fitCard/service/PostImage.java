package finternet.fitCard.service;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import finternet.fitCard.doc.Constants;
import finternet.fitCard.doc.Globals;
import finternet.fitCard.fragment.ProfileFragment;
import finternet.fitCard.model.UserModel;

@SuppressWarnings("ALL")
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class PostImage {  //Image Post Class for Attachment
    private boolean isRunning = false;
    private int l_m;
    private TimerTask task;
    private Timer mTimer;
    private PostClass posting;
    private ProfileFragment fragment;
    private String file;
    private UserModel data1;
    private String ff;
    private String url;
    private String name;
    private int mode; //0:Personal,1:Group

    public PostImage(UserModel profile, final ProfileFragment activity1) {
        this.data1 = profile;
        this.fragment = activity1;
        mode = 0;
        url = profile.mImage;
        name = profile.mName;
    }

    public PostImage(String path) {
        url = path;
        name = Globals.mAccount.mName;

    }

    public void postHttp() {
        isRunning = true;

        posting = new PostClass();
        posting.execute("");
    }


    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public class PostClass extends AsyncTask<String, Void, Boolean> {
        String responseString = "";
        boolean recording = false;

        public int uploadFile(String sourceFileUri) {
            int serverResponseCode = 0;
            String serverUrl = Constants.UPLOADLOGO;
            String fileName = name;

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                if (fragment.getActivity() != null)
                    Toast.makeText(fragment.getActivity(), "File does not exist", Toast.LENGTH_SHORT).show();
                return 0;
            } else {
                try {
                    String filenameArray[] = sourceFileUri.split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    fileName = fileName + "." + extension;
                    ff = fileName;
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(serverUrl);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    ex.printStackTrace();
                } catch (Exception ignored) {


                }
                return serverResponseCode;

            } // End else block
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            uploadFile(url);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                if (data1 != null) {
                    data1.mImage = "/uploads/consumer/" + ff;
                    if (fragment != null)
                        ServiceManager.serviceUpdateProfile(data1,fragment);
                }
            }
        }
    }
}
