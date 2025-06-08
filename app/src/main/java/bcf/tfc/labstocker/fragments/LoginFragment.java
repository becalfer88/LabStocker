package bcf.tfc.labstocker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import bcf.tfc.labstocker.LogActivity;
import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.UnderConstructionActivity;
import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.user.Account;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple Fragment subclass for login
 */
public class LoginFragment extends Fragment {

    private final String ACCOUNT_TEXT = "account";
    private final String LOGGED_TEXT = "logged";
    private final String ACCOUNT_TYPE_TEXT = "accountType";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        CheckBox logged = view.findViewById(R.id.logged);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        // Button action
        view.findViewById(R.id.action_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager.findAccount(email.getEditableText().toString(), new DBCallback<Boolean>(){
                    @Override
                    public void onSuccess(Boolean exists) {
                        if (exists) {
                            DBManager.getAccount(email.getEditableText().toString(), new DBCallback<Account>() {
                                @Override
                                public void onSuccess(Account account) {
                                    if (account.getPassword().equals(password.getEditableText().toString())) {
                                        // Save Preferences
                                        if (logged.isChecked()){
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(ACCOUNT_TEXT, email.getEditableText().toString());
                                            editor.putInt(ACCOUNT_TYPE_TEXT, account.getType().getId());
                                            editor.putBoolean(LOGGED_TEXT, true);
                                            editor.apply();
                                        } else {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(ACCOUNT_TEXT, "");
                                            editor.putInt(ACCOUNT_TYPE_TEXT, account.getType().getId());
                                            editor.putBoolean(LOGGED_TEXT, false);
                                            editor.apply();
                                        }
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra(ACCOUNT_TEXT, account.getEmail());
                                        startActivity(intent);
                                    } else {
                                        password.setError(getString(R.string.wrong_password));
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), getString(R.string.impossible_check));
                                }
                            });
                        } else {
                            AlertDialog dialog = getDialog(email);
                            dialog.show();
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryDark));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Utils.getErrorDialog(getActivity(), getString(R.string.impossible_check));
                    }
                        }
                );

            }
        });

        // Text link to the forgotten password fragment in the future
        view.findViewById(R.id.forgotten_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UnderConstructionActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Create a dialog builder that provides the interface to create and show a simple alert dialog.
     * Set both positive and negative buttons with their respective click listeners and the message.
     *
     * @param email
     * @return The AlertDialog created through the builder
     */
    private  @NonNull AlertDialog getDialog(EditText email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons.
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Fragment fragment = SignupFragment.newInstance(email.getEditableText().toString());
                ((LogActivity) getActivity()).loadFragment(fragment);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancels the dialog.
            }
        }).setMessage(R.string.dialog_message);
        return builder.create();
    }

    /**
     * Validate the form data. If the fields are not empty and data is valid, return true and false otherwise
     *
     * @param email
     * @param password
     * @return
     */
    public boolean validateForm(EditText email, EditText password) {
        if (email.getEditableText().toString().isEmpty()) {
            email.setError(getString(R.string.email_empty));
            email.requestFocus();
        } else if (!Utils.validateEmail(email.getEditableText().toString())) {
            email.setError(getString(R.string.email_not_valid));
            email.requestFocus();
        } else if (password.getEditableText().toString().isEmpty()) {
            password.setError(getString(R.string.password_empty));
            password.requestFocus();
        } else if (password.getEditableText().toString().length() < 4 ||
                password.getEditableText().toString().length() > 12) {
            password.setError(getString(R.string.password_size));
            password.requestFocus();
        } else {
            return true;
        }
        return false;
    }
}