package tube;

/**
 * Created by yene on 14/04/2017.
 */

import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;




import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tube.floatView.*;
import tube.util.helper;

/**
 * A fragment that shows a static list of videos.
 */
public class VideoListView {
    public static final class VideoListFragment extends ListFragment {

        private static final List<VideoEntryClass.VideoEntry> VIDEO_LIST;
        private static final int ANIMATION_DURATION_MILLIS = 300;

        static {
            List<VideoEntryClass.VideoEntry> list = new ArrayList<VideoEntryClass.VideoEntry>();
            list.add(new VideoEntryClass.VideoEntry("YouTube Collection", "H4uWOZL4IwQ"));
            list.add(new VideoEntryClass.VideoEntry("GMail Tap", "1KhZKNZO8mQ"));
            list.add(new VideoEntryClass.VideoEntry("Chrome Multitask", "UiLSiqyDf4Y"));
            list.add(new VideoEntryClass.VideoEntry("Google Fiber", "re0VRK6ouwI"));
            list.add(new VideoEntryClass.VideoEntry("Autocompleter", "blB_X38YSxQ"));
            list.add(new VideoEntryClass.VideoEntry("GMail Motion", "Bu927_ul_X0"));
            list.add(new VideoEntryClass.VideoEntry("Translate for Animals", "3I24bSteJpw"));
            VIDEO_LIST = Collections.unmodifiableList(list);
        }

        private PageAdapterClass.PageAdapter adapter;


        @Override
        public  void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            adapter = new PageAdapterClass.PageAdapter(getActivity(), VIDEO_LIST);

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

            Log.e("videoId" ,videoId);
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
    }
}
