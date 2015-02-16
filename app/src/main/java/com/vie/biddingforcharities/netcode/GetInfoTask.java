package com.vie.biddingforcharities.netcode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.os.AsyncTask;

import com.vie.biddingforcharities.AccountActivity;
import com.vie.biddingforcharities.AuctionFormActivity;
import com.vie.biddingforcharities.AuctionSearchActivity;
import com.vie.biddingforcharities.BidListActivity;
import com.vie.biddingforcharities.CharityRequestActivity;
import com.vie.biddingforcharities.LoginActivity;
import com.vie.biddingforcharities.RegisterActivity;
import com.vie.biddingforcharities.WatchListActivity;

public class GetInfoTask extends AsyncTask<String, Void, String>{
    public enum SourceType {
        checkLogin,
        registerUser,
        getUserBids,
        getUserWatchList,
        getUserInfo,
        updateAuction,
        searchAuctions,
        requestCharity
    }
    SourceType type;
    Activity parent;

    public GetInfoTask(Activity parent) {
        this.parent = parent;
    }

    @Override
    protected String doInBackground(String... params) {
        type = SourceType.valueOf(params[0]);
        String queryStr = params[1];
        URI serviceLoc = getServiceLoc(type, queryStr);

        if(this.isCancelled())
            return "";
        else {
            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpGet httpget = new HttpGet(serviceLoc);
                HttpResponse response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                return sb.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    @Override
    protected void onPostExecute(String data) {
        if(!(this.isCancelled())){
            //Tell parent
            switch(type){
                case checkLogin:
                    ((LoginActivity) parent).onTaskFinish(this, data);
                    break;
                case registerUser:
                    ((RegisterActivity) parent).onTaskFinish(this, data);
                    break;
                case getUserBids:
                    ((BidListActivity) parent).onTaskFinish(this, data);
                    break;
                case getUserWatchList:
                    ((WatchListActivity) parent).onTaskFinish(this, data);
                    break;
                case getUserInfo:
                    ((AccountActivity) parent).onTaskFinish(this, data);
                    break;
                case updateAuction:
                    ((AuctionFormActivity) parent).onTaskFinish(this, data);
                    break;
                case searchAuctions:
                    ((AuctionSearchActivity) parent).onTaskFinish(this, data);
                    break;
                case requestCharity:
                    ((CharityRequestActivity) parent).onTaskFinish(this, data);
                    break;
                default:
                    break;
            }
        }
    }

    private URI getServiceLoc(SourceType type, String queryStr) {
        String str;

        switch(type) {
            case checkLogin:
                str = "http://www.biddingforcharities.com/mobile/mlogin.php" + queryStr;
                break;
            case registerUser:
                str = "http://www.biddingforcharities.com/mobile/mregister.php" + queryStr;
                break;
            case getUserBids:
                str = "http://www.biddingforcharities.com/mobile/mmybids.php" + queryStr;
                break;
            case getUserWatchList:
                str = "http://www.biddingforcharities.com/mobile/mwatchlist.php" + queryStr;
                break;
            case getUserInfo:
                str = "http://www.biddingforcharities.com/mobile/mmyinfo.php" + queryStr;
                break;
            case updateAuction:
                str = "http://www.biddingforcharities.com/mobile/madd_update_item.php" + queryStr;
                break;
            case searchAuctions:
                str = "http://www.biddingforcharities.com/mobile/msearch.php" + queryStr;
                break;
            case requestCharity:
                str = "http://www.biddingforcharities.com/mobile/mnew_vendor.php" + queryStr;
                break;
            default:
                str = "";
                break;
        }

        try {
            URL url = new URL(str);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}