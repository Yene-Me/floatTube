package tube;

/**
 * Created by yene on 14/04/2017.
 */

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tube.floatView.*;
import tube.util.helper;

/**
 * A fragment that shows a static list of videos.
 */
public class VideoListView {

    public static final class VideoListFragment extends ListFragment {


        private static final int ANIMATION_DURATION_MILLIS = 300;
        private List<VideoEntryClass.VideoEntry> VIDEO_LIST = new ArrayList<>();
        private PageAdapterClass.PageAdapter adapter;
        public ReadPlayList readPlayList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            VIDEO_LIST.add(new VideoEntryClass.VideoEntry("YouTube Collection", "H4uWOZL4IwQ"));
            adapter = new PageAdapterClass.PageAdapter(getActivity(), VIDEO_LIST);


            Bundle bundle = getArguments();
            if (bundle != null) {
                Log.e(bundle.getString(helper.VIDEO_ID), "ID: " + bundle.getString(helper.VIDEO_ID));
                readPlayList = new ReadPlayList(bundle.getString(helper.VIDEO_ID));
                new DownloadFilesTask().execute();
            } else {
                Log.e("NOT YET HERE", "is NUll");
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String videoId = VIDEO_LIST.get(position).videoId;

            Log.e("videoId", videoId);
            Intent mIntent = new Intent(getActivity().getApplicationContext(), FloatingWindow.class);
            mIntent.putExtra(helper.VIDEO_ID, videoId);
            getActivity().startService(mIntent);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

            adapter.releaseLoaders();
        }

        public void setLabelVisibility(boolean visible) {
            adapter.setLabelVisibility(visible);
        }

        private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
            // Do the long-running work in here
            protected Long doInBackground(URL... urls) {
                long totalSize = 0;
                VIDEO_LIST = readPlayList.loadList();
                return totalSize;
            }

            // This is called each time you call publishProgress()
            protected void onProgressUpdate(Integer... progress) {

            }

            // This is called when doInBackground() is finished
            protected void onPostExecute(Long result) {
                adapter = new PageAdapterClass.PageAdapter(getActivity(), VIDEO_LIST);
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                setListAdapter(adapter);
            }
        }
    }

}
