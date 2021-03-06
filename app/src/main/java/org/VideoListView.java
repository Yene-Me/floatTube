package org;

/**
 * Created by yene on 14/04/2017.
 */

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.player.VideoPlayer;
import org.util.helper;

/**
 * A fragment that shows a static list of videos.
 */
public class VideoListView {

    public static final class VideoListFragment extends ListFragment {

        private static final int ANIMATION_DURATION_MILLIS = 300;
        private List<VideoEntryClass.VideoEntry> VIDEO_LIST = new ArrayList<>();
        private PageAdapterClass.PageAdapter adapter = null;
        public ReadPlayList readPlayList = null;
        public SearchList searchList = null;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (bundle != null) {

                if(bundle.getString(helper.VIDEO_ID) != null)
                {
                    readPlayList = new ReadPlayList(bundle.getString(helper.VIDEO_ID));
                }
                else
                    {
                        searchList = new SearchList(bundle.getString(helper.VIDEO_SEARCH_TERM));
                    }

                new DownloadFilesTask().execute();
            } else {
                Log.e("NOT YET HERE", "is NUll");
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            if (adapter != null) {
                setListAdapter(adapter);
            }

        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id)
        {

            Log.e("onListItemClick", "onListItemClick");
            String videoId = VIDEO_LIST.get(position).videoId;

            Intent mIntent = new Intent(getActivity().getApplicationContext(), VideoPlayer.class);
            mIntent.putExtra(helper.VIDEO_ID, videoId);
            getActivity().startActivity(mIntent);

        }

        @Override
        public void onDestroyView()
        {
            super.onDestroyView();
            if (adapter != null) {
                adapter.releaseLoaders();
            }

        }

        public void setLabelVisibility(boolean visible)
        {
            if (adapter != null) {
                adapter.setLabelVisibility(visible);
            }

        }

        private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
            // Do the long-running work in here
            protected Long doInBackground(URL... urls)
            {
                long totalSize = 0;
                if(readPlayList !=null)
                {
                    VIDEO_LIST = readPlayList.loadList();
                }
                else if( searchList != null)
                {
                    VIDEO_LIST = searchList.loadList();
                }

                return totalSize;
            }

            // This is called each time you call publishProgress()
            protected void onProgressUpdate(Integer... progress)
            {

            }

            // This is called when doInBackground() is finished
            protected void onPostExecute(Long result)
            {
                adapter = new PageAdapterClass.PageAdapter(getActivity(), VIDEO_LIST);
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                setListAdapter(adapter);
            }
        }
    }

}
