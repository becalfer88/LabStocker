package bcf.tfc.labstocker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.model.data.user.Account;
import bcf.tfc.labstocker.model.data.user.AccountType;
import bcf.tfc.labstocker.model.DataModel;

/**
 * A simple Fragment subclass.
 */
public class ProfileFragment extends Fragment {

    private final String ACCOUNT_TYPE_TEXT = "accountType";
    private final String LOGGED_TEXT = "logged";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCOUNT = "account";

    private String mAccount;

    //View elements
    private TextView name;
    private CheckBox logged;
    private Button changeName;
    private Spinner accountType;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param account email account
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String account) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccount = getArguments().getString(ARG_ACCOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //View elements
        name = view.findViewById(R.id.name);
        logged = view.findViewById(R.id.logged);
        changeName = view.findViewById(R.id.change_name_btn);
        accountType = view.findViewById(R.id.account_types_spinner);

        //Spinner
        if (DataModel.isAdmin(mAccount)) {
            AccountType[] accountTypes = AccountType.values();
            ArrayAdapter<AccountType> accountTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, accountTypes);
            accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accountType.setAdapter(accountTypeAdapter);
        } else {
            accountType.setVisibility(View.INVISIBLE);
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Account account = DataModel.getAccount(mAccount);
        name.setText(account.getName());

        // Read from SharedPreferences if the user is logged in order to check the checkbox
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean spLogged = sharedPreferences.getBoolean(LOGGED_TEXT, false);
        int spAccountType = sharedPreferences.getInt(ACCOUNT_TYPE_TEXT, 0);
        String spAccount = sharedPreferences.getString(ARG_ACCOUNT, "");

        if (spLogged && spAccount.equals(account.getEmail())) {
            logged.setChecked(true);
        }

       /* // Camera Laucher
        ActivityResultLauncher<Intent> camLauncher = createCamLauncher(image);

        // Camera action and permissions
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissoinCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

                if (permissoinCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 225);
                } else {
                    Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    camLauncher.launch(camIntent);
                }
            }
        });
*/
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show an Editable AlertDialog.
                getEditableDialog(activity, name).show();
            }
        });

        logged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).loggedCheck(logged.isChecked(), mAccount);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Create the AlertDialog to change the user's name.
     *
     * @param activity Activity where the dialog is going to be shown.
     * @param name TextView where the user's name is
     * @return Builder
     */
    private  @NonNull AlertDialog getEditableDialog(AppCompatActivity activity, TextView name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View view = getActivity().getLayoutInflater().inflate(R.layout.editable_alert, null);
        EditText newName = view.findViewById(R.id.editable_field);
        newName.setVisibility(View.VISIBLE);
        builder.setView(view);
        // Add the buttons and message
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Account account = DataModel.getAccount(mAccount);
                if(account != null && !newName.getText().toString().isEmpty()
                        && account.validateName(newName.getText().toString())) {
                    saveName(activity, account, newName, name);
                }
                newName.setVisibility(View.INVISIBLE);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
                newName.setVisibility(View.INVISIBLE);
            }
        }).setMessage(R.string.insert_your_new_name);
        return builder.create();
    }


    /**
     * Save the new name in Java memory and shows it on the UI.
     *
     * @param activity
     * @param account
     * @param newName
     * @param name
     */
    private void saveName(AppCompatActivity activity, Account account, EditText newName, TextView name) {
        account.setName(newName.getText().toString());
        name.setText(newName.getText().toString());

    }

}