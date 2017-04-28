package com.balloonmail.app.balloonmailapp.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import com.balloonmail.app.balloonmailapp.utilities.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Reem Hamdy on 7/20/2016.
 */
public class ReusableAsync<T> {
    private String api_token;
    private String url;

    private enum HTTP_METHOD {POST, GET}

    ;
    private HTTP_METHOD method;
    private HashMap<String, String> query;
    private JSONObject data;
    private SuccessHandler<T> successHandler;
    private PostHandler<T> postHandler;
    private HttpURLConnection connection;
    private URL urlObj;
    private String dialog_msg = null;
    private boolean error_in_add_data = false;
    private Context context;
    private ProgressBar mProgressBar;
    private int count = 0;

    private static class AsyncResult<T> {
        T data;
        String error_msg;

        AsyncResult(T data, String error_msg) {
            this.data = data;
            this.error_msg = error_msg;
        }

        static <T> AsyncResult error(String message) {
            return new AsyncResult<T>(null, message);
        }
    }

    public ReusableAsync(Context context) {
        data = new JSONObject();
        query = new HashMap<>();
        this.context = context;
    }


    public ReusableAsync bearer(String api_token) {
        this.api_token = api_token;
        return this;
    }

    public ReusableAsync post(String url) {
        method = HTTP_METHOD.POST;
        this.url = url;
        return this;
    }

    public ReusableAsync get(String url) {
        method = HTTP_METHOD.GET;
        this.url = url;
        return this;
    }

    public ReusableAsync addData(String key, String value) {
        try {
            data.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            error_in_add_data = true;
        }
        return this;
    }

    public ReusableAsync addQuery(String key, String value) {
        query.put(key, value);
        return this;
    }

    public void send() {
        ParallelAsync task = new ParallelAsync();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    public ReusableAsync dialog(String message) {
        dialog_msg = message;
        return this;
    }


    public ReusableAsync progressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
        return this;
    }

    public ReusableAsync onSuccess(SuccessHandler<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public ReusableAsync onPost(PostHandler<T> postHandler) {
        this.postHandler = postHandler;
        return this;
    }

    private AsyncResult<T> handleBackground(Void... voids) {
        if (error_in_add_data) {
            return new AsyncResult<>(null, "JSON Exception adding send data.");
        }
        String error_msg = "";
        String method_string = "", query_part = "";
        if (method == HTTP_METHOD.POST) {
            method_string = "POST";
        } else if (method == HTTP_METHOD.GET) {
            method_string = "GET";
            if (query.size() > 0) {
                StringBuilder s = new StringBuilder("");
                s.append('?');
                for (String key : query.keySet()) {
                    //TODO: Escape html characters
                    s.append(key);
                    s.append("=");
                    s.append(query.get(key));
                    s.append("&");
                }
                s.deleteCharAt(s.length() - 1);
                query_part = s.toString();
            }
        }
        DataOutputStream outputStream = null;
        try {
            urlObj = new URL(Global.SERVER_URL + url + query_part);
            connection = (HttpURLConnection) urlObj.openConnection();

            // set the request method to POST
            connection.setRequestMethod(method_string);

            connection.setConnectTimeout(10000);

            // set content-type property
            connection.setRequestProperty("Content-Type", "application/json");

            // set charset property to utf-8
            connection.setRequestProperty("charset", "utf-8");

            if (api_token != null) {
                connection.setRequestProperty("authorization", "Bearer " + api_token);
            }

            // set accept property
            connection.setRequestProperty("Accept", "application/json");

            if (method == HTTP_METHOD.POST) {

                // set connection to allow output
                connection.setDoOutput(true);
            }

            // set connection to allow input
            connection.setDoInput(true);

            // connect to server
            connection.connect();

            if (method == HTTP_METHOD.POST) {
                outputStream = new DataOutputStream(connection.getOutputStream());

                // write JSON body to the output stream
                outputStream.write(data.toString().getBytes("utf-8"));

                // flush to ensure all data in the stream is sent
                outputStream.flush();
            }

            return getResponse();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            error_msg = "URL exception";
        } catch (IOException e) {
            e.printStackTrace();
            error_msg = "IO exception";
        } catch (JSONException e) {
            e.printStackTrace();
            error_msg = "JSON exception";
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return new AsyncResult<T>(null, error_msg);
    }

    private AsyncResult<T> getResponse() throws IOException, JSONException {
        BufferedReader reader = null;
        // create StringBuilder object to append the input stream in
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            // get input stream
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // append stream in a the StringBuilder object
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // convert StringBuilder object to string and store it in a variable
            String JSONResponse = sb.toString();
            // convert response to JSONObject
            JSONObject response = new JSONObject(JSONResponse);

            if (response != null) {
                if (response.has("error")) {
                    return new AsyncResult<T>(null, response.get("error").toString());
                } else {
                    return new AsyncResult<T>(successHandler.handle(response), null);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return new AsyncResult<T>(null, "Connection Timeout.");
    }

    private void handlePost(AsyncResult<T> result) {
        if (result.error_msg != null) {
            Global.showMessage(context, result.error_msg, Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
        } else {
            if (postHandler != null) {
                postHandler.handle(result.data);
            }
        }
    }


    private class ParallelAsync extends AsyncTask<Void, Void, AsyncResult<T>> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog_msg != null) {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage(dialog_msg);
                mProgressDialog.show();
            }

            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected AsyncResult<T> doInBackground(Void... voids) {
            return handleBackground(voids);
        }

        @Override
        protected void onPostExecute(AsyncResult<T> result) {
            super.onPostExecute(result);
            handlePost(result);
            if (dialog_msg != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

}