package bcf.tfc.labstocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.DBCallback;

public class SplashScreen extends AppCompatActivity {

    private final String ACCOUNT_TEXT = "account";
    private final String LOGGED_TEXT = "logged";

    private String account;
    private boolean logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);

        DataModel.dataInit(new DBCallback<Boolean>() {
                               @Override
                               public void onSuccess(Boolean result) {
                                   splashScreenStart();
                               }

                               @Override
                               public void onFailure(Exception e) {

                               }
                           }
        );
        //splashScreenStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        account = sharedPreferences.getString(ACCOUNT_TEXT, "");
        logged = sharedPreferences.getBoolean(LOGGED_TEXT, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void splashScreenStart() {
        Context context = getApplicationContext();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (logged) {
                    Intent i;
                        i = new Intent(SplashScreen.this, MainActivity.class);
                        i.putExtra(ACCOUNT_TEXT, account);
                    startActivity(i);
                } else {
                    startActivity(new Intent(SplashScreen.this, LogActivity.class));
                }
                finish();
            }
        }, 4000);
    }
}