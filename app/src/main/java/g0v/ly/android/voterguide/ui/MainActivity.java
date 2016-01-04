package g0v.ly.android.voterguide.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import g0v.ly.android.voterguide.R;
import g0v.ly.android.voterguide.ui.guide.GuideFragment;
import g0v.ly.android.voterguide.ui.info.CandidateInfoFragment;
import g0v.ly.android.voterguide.ui.info.SelectCandidateFragment;
import g0v.ly.android.voterguide.ui.info.SelectCountyFragment;

public class MainActivity extends FragmentActivity {
    public static String KEY_FRAGMENT_BUNDLE_CANDIDATES_LIST = "key.fragment.bundle.candidates.list";
    public static String KEY_FRAGMENT_BUNDLE_CANDIDATE_INFO = "key.fragment.bundle.candidate.info";

    public enum State {
        STATE_MAIN("state.main"),
        STATE_GUIDE("state.guide"),
        STATE_INFO_COUNTIES_LIST("state.info.counties"),
        STATE_INFO_CANDIDATES_LIST("state.info.candidates"),
        STATE_INFO_CANDIDATE("state.info");

        private final String id;
        State(String id) {
            this.id = id;
        }
    }

    private State state = State.STATE_MAIN;
    private String bundleMessages = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        launch(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launch(State state) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(state.id);

        boolean stacked = false;
        Bundle args = null;

        switch (state) {
            case STATE_MAIN:
                fragment = MainFragment.newFragment(mainFragmentCallback);
                break;
            case STATE_GUIDE:
                fragment = GuideFragment.newFragment();
                stacked = true;
                break;
            case STATE_INFO_COUNTIES_LIST:
                fragment = SelectCountyFragment.newFragment();
                stacked = true;
                break;
            case STATE_INFO_CANDIDATES_LIST:
                fragment = SelectCandidateFragment.newFragment();
                stacked = true;

                if (bundleMessages.length() > 0) {
                    args = new Bundle();
                    args.putString(KEY_FRAGMENT_BUNDLE_CANDIDATES_LIST, bundleMessages);
                    fragment.setArguments(args);

                    bundleMessages = "";
                }
                break;
            case STATE_INFO_CANDIDATE:
                fragment = CandidateInfoFragment.newFragment();
                stacked = true;

                if (bundleMessages.length() > 0) {
                    args = new Bundle();
                    args.putString(KEY_FRAGMENT_BUNDLE_CANDIDATE_INFO, bundleMessages);
                    fragment.setArguments(args);

                    bundleMessages = "";
                }
                break;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, fragment, state.id);
        if (stacked) {
            fragmentTransaction.addToBackStack(null);
        }

        if (args != null) {
            fragment.setArguments(args);
            bundleMessages = "";
        }

        fragmentTransaction.commit();
    }

    private MainFragment.Callback mainFragmentCallback = new MainFragment.Callback() {

        @Override
        public void gotoGuide() {
            state = State.STATE_GUIDE;
            launch(state);
        }

        @Override
        public void gotoInfo() {
            state = State.STATE_INFO_COUNTIES_LIST;
            launch(state);

        }
    };

    public void gotoFragmentWithState(State state, String message) {
        this.state = state;
        bundleMessages = message;
        launch(this.state);
    }
}
