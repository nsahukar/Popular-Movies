package android.nsahukar.com.popularmovies.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";
    private static final String REQUEST_URL_QUEUE_KEY = "requestUrlQueue";

    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    private ArrayList<String> mRequestUrls;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager) {
        NetworkFragment networkFragment = new NetworkFragment();
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    public void addRequestUrl(String url) {
        Log.d(TAG, "adding request url: " + url);
        if (mRequestUrls != null) {
            if (!mRequestUrls.contains(url)) {
                mRequestUrls.add(url);
            }
        } else {
            mRequestUrls = new ArrayList<>();
            mRequestUrls.add(url);
        }

        Log.d(TAG, "no. of requests: " + mRequestUrls.size());

        if (mRequestUrls.size() == 1) {
            startDownloadForRequestUrl(mRequestUrls.get(0));
        }
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Retain this Fragment across configuration changes in the host Activity.
//        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null && args.containsKey(REQUEST_URL_QUEUE_KEY)) {
            ArrayList<String> requestUrls = args.getStringArrayList(REQUEST_URL_QUEUE_KEY);
            if (requestUrls != null && requestUrls.size() > 0) {
                startDownloadForRequestUrl(requestUrls.get(0));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    private void startDownloadForRequestUrl(String url) {
        cancelDownload();
        mDownloadTask = new DownloadTask(url, mCallback);
        Log.d(TAG, "startDownload for url: " + url);
        mDownloadTask.execute();
    }

    private void downloadingCancelledForRequestUrl(String url) {
        if (mRequestUrls != null && mRequestUrls.size() > 0) {
            mRequestUrls.remove(url);
            if (mRequestUrls.size() > 0) {
                startDownloadForRequestUrl(mRequestUrls.get(0));
            }
        }
    }

    private void downloadingFinishedForRequestUrl(String url) {
        if (mRequestUrls != null && mRequestUrls.size() > 0) {
            mRequestUrls.remove(url);
            if (mRequestUrls.size() > 0) {
                startDownloadForRequestUrl(mRequestUrls.get(0));
            }
        }
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private class DownloadTask extends AsyncTask<Void, Integer, DownloadTask.Result> {

        private String mUrl;
        private DownloadCallback<String> mCallback;

        DownloadTask(String url, DownloadCallback<String> callback) {
            setUrl(url);
            setCallback(callback);
        }

        void setUrl(String url) {
            mUrl = url;
        }

        void setCallback(DownloadCallback<String> callback) {
            mCallback = callback;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        class Result {
            public String mResultValue;
            public Exception mException;
            public Result(String resultValue) {
                mResultValue = resultValue;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.updateFromDownload(null, mUrl);
                    cancel(true);
                    downloadingCancelledForRequestUrl(mUrl);
                } else {
                    mCallback.startDownloading(mUrl);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(Void... params) {
            Result result = null;
            if (!isCancelled() && mUrl != null) {
                try {
                    URL url = new URL(mUrl);
                    String resultString = getResponseFromHttpUrl(url);
                    if (resultString != null) {
                        result = new Result(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }
                } catch(Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
            if (result != null && mCallback != null) {
                if (result.mException != null) {
                    mCallback.updateFromDownload(result.mException.getMessage(), mUrl);
                } else if (result.mResultValue != null) {
                    mCallback.updateFromDownload(result.mResultValue, mUrl);
                }
                mCallback.finishDownloading(mUrl);
                downloadingFinishedForRequestUrl(mUrl);
            }
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private String getResponseFromHttpUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpsURLConnection connection = null;
            String result = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                // Open communications link (network traffic occurs here).
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
                if (stream != null) {
                    Scanner scanner = new Scanner(stream);
                    scanner.useDelimiter("\\A");
                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        result = scanner.next();
                    }
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

    }

}
