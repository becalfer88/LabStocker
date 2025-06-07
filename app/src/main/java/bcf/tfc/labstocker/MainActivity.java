package bcf.tfc.labstocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;

import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.fragments.FormFragment;
import bcf.tfc.labstocker.fragments.HomeFragment;
import bcf.tfc.labstocker.fragments.OptionsFragment;
import bcf.tfc.labstocker.fragments.ProfileFragment;
import bcf.tfc.labstocker.fragments.SearchFragment;
import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.user.Account;
import bcf.tfc.labstocker.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private static final String ACCOUNT_TEXT = "account";
    private static final String LOGGED_TEXT = "logged";


    private MaterialToolbar userTopBar;
    private BottomNavigationView bottomNav;

    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent receivedIntent = getIntent();
        account = receivedIntent.getStringExtra(ACCOUNT_TEXT);

        // Top navigation
        userTopBar = findViewById(R.id.user_top_menu);
        /*userTopBar.setLogo(R.drawable.atomic); // Logo
        userTopBar.setTitle(""); // Title*/
        setSupportActionBar(userTopBar);

        // Bottom navigation
        bottomNav = findViewById(R.id.user_bottom_menu);
        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home_btn) {
                // Go to home
                Fragment fragment = HomeFragment.newInstance(account);
                loadFragment(fragment);
            } else if (item.getItemId() == R.id.subjects_btn) {
                // Go to subjects/practices options
                Fragment fragment = OptionsFragment.newInstance("subjects");
                loadFragment(fragment);
            } else if (item.getItemId() == R.id.rss_btn) {
                // Go to search
                Fragment fragment = SearchFragment.newInstance(false);
                this.loadFragment(fragment);
            } else if (item.getItemId() == R.id.places_btn) {
                // Go to Warehouses/Labs options
                Fragment fragment = OptionsFragment.newInstance("");
                loadFragment(fragment);
            } else if (item.getItemId() == R.id.calendar_btn) {
                Intent intent = new Intent(getApplicationContext(), UnderConstructionActivity.class);
                startActivity(intent);
            }
            return true;
        });

        if (savedInstanceState == null) {
            Fragment fragment = HomeFragment.newInstance(account);
            loadFragment(fragment);
            bottomNav.setSelectedItemId(R.id.home_btn);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Account oAccount = DataModel.getAccount(account);
        if(oAccount != null)
            DBManager.upsertAccount(oAccount, new DBCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
    }


    /**
     * Create the menu for the top bar.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_top_menu, menu);
        return true;
    }

    /**
     * Listener for the top enu items.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_btn) {
            loggedCheck(false, "");
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.settings_btn) {
            Fragment fragment = ProfileFragment.newInstance(account);
            loadFragment(fragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load the given fragment.
     *
     * @param fragment
     */
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void loggedCheck(Boolean logged, String account){
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED_TEXT, logged);
        if (logged){
            editor.putString(ACCOUNT_TEXT, account);
        } else {
            editor.putString(ACCOUNT_TEXT, "");
        }
        editor.apply();
    }

    public String getAccount() {
        return account;
    }

    public void configToggleButtons(Fragment fragment, ToggleButton btn1, ToggleButton btn2) {

        btn1.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                changemScreen(fragment);
                btn2.setChecked(false);
                Utils.changeColors(btn1);
                Utils.changeColors(btn2);
            }
        });

        btn2.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                changemScreen(fragment);
                btn1.setChecked(false);
                Utils.changeColors(btn1);
                Utils.changeColors(btn2);
            }
        });

        Utils.changeColors(btn1);
        Utils.changeColors(btn2);

    }

    private static void changemScreen(Fragment fragment) {
        if (fragment instanceof OptionsFragment) {
            String screen = ((OptionsFragment) fragment).getmScreen();
            if (screen != null) {
                screen = Utils.getNewScreen(screen);
                ((OptionsFragment) fragment).setmScreen(screen);
            }
        } else if (fragment instanceof FormFragment) {
            String screen = ((FormFragment) fragment).getmScreen();
            if (screen != null) {
                screen = Utils.getNewScreen(screen);
                ((FormFragment) fragment).setmScreen(screen);
            }
        }
    }


}


