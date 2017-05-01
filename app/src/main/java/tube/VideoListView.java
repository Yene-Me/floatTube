package tube;

/**
 * Created by yene on 14/04/2017.
 */

import android.app.ListFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


import com.tube.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        private View videoBox;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            adapter = new PageAdapterClass.PageAdapter(getActivity(), VIDEO_LIST);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            videoBox = getActivity().findViewById(R.id.video_box);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String videoId = VIDEO_LIST.get(position).videoId;

            VideoPlayerActivity.VideoFragment videoFragment =
                    (VideoPlayerActivity.VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(videoId);

            // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
            if (videoBox.getVisibility() != View.VISIBLE) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Initially translate off the screen so that it can be animated in from below.
                    videoBox.setTranslationY(videoBox.getHeight());
                }
                videoBox.setVisibility(View.VISIBLE);
            }

            // If the fragment is off the screen, we animate it in.
            if (videoBox.getTranslationY() > 0) {
                videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
            }
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
