package bcf.tfc.labstocker.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.user.Account;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple Fragment subclass for the signup page
 *
 * @author Beatriz Calzo
 */
public class SignupFragment extends Fragment {

    private final String ACCOUNT_TEXT = "account";
    private static final String EMAIL = "email";

    private String mEmail;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email email.
     * @return A new instance of fragment SignupFragment.
     */
    public static SignupFragment newInstance(String email) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        EditText rPassword = view.findViewById(R.id.repeatPassword);

        email.setText(mEmail);

        // Button action
        view.findViewById(R.id.action_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBManager.findAccount(email.getEditableText().toString(), new DBCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean exists) {
                        if (!exists) {
                            if (validateForm(email, password, rPassword)) {
                                saveAccount(email, password);
                            }
                        } else {
                            AlertDialog dialog = Utils.getErrorDialog(getActivity(), getString(R.string.account_exists));
                            dialog.show();
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Utils.getErrorDialog(getActivity(), e.getMessage());
                    }
                });
            }
        });

        return view;
    }

    /**
     * Save the account on DataModel and DB. Then start the main activity if success.
     * @param email Email field
     * @param password Password field
     */
    private void saveAccount(EditText email, EditText password) {
        Account account = DataModel.addAccount(email.getEditableText().toString(), password.getEditableText().toString(), null);
        DBManager.upsertAccount(account, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(ACCOUNT_TEXT, email.getEditableText().toString());

                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {
                Utils.getErrorDialog(getActivity(), e.getMessage() + "\n" + getString(R.string.account_save_error));
                DataModel.removeAccount(account);
            }
        });


    }


    /**
     * Validate the form data. If the fields are not empty and data is valid, return true and false otherwise
     *
     * @param email email field
     * @param password password field
     * @param rPassword repeat password field
     * @return
     */
    public boolean validateForm(EditText email, EditText password, EditText rPassword) {
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
        } else if (rPassword.getEditableText().toString().isEmpty()) {
            rPassword.setError(getString(R.string.r_password_empty));
            rPassword.requestFocus();
        } else if (!rPassword.getEditableText().toString().equals(password.getEditableText().toString())) {
            rPassword.setError(getString(R.string.passwords_match));
            rPassword.requestFocus();
        } else {
            return true;
        }
        return false;
    }


}