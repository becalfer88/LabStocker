package bcf.tfc.labstocker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import bcf.tfc.labstocker.fragments.LoginFragment;
import bcf.tfc.labstocker.fragments.SignupFragment;

public class LogActivity extends AppCompatActivity {

    MaterialToolbar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Material toolbar setup
        topBar = findViewById(R.id.topBar);
        topBar.setLogo(R.drawable.nombre_edit);
        topBar.setTitle("");
        setSupportActionBar(topBar);

        // Load the first fragment by default
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    /**
     * Create the menu for the top bar.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * Listener for the menu items.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_login) {
            loadFragment(new LoginFragment());
            return true;
        } else if (id == R.id.action_signup) {
            loadFragment(new SignupFragment());
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
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}