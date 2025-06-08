package bcf.tfc.labstocker.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 * It is used to choose between create or edit objects (laboratories, warehouses, subjects)
 *
 * @author Beatriz Calzo
 */
public class OptionsFragment extends Fragment {

    private static final String ARG_SCREEN = "screen";
    private static ArrayList<View> alertComponents = new ArrayList<>();

    private String mScreen;

    public OptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param screen Parameter 1.
     * @return A new instance of fragment OptionsFragment.
     */
    public static OptionsFragment newInstance(String screen) {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCREEN, screen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreen = getArguments().getString(ARG_SCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        ToggleButton warehouses = view.findViewById(R.id.warehouse_btn);
        ToggleButton labs =  view.findViewById(R.id.laboratory_btn);
        TextView title = view.findViewById(R.id.title);
        Button newButton = view.findViewById(R.id.new_btn);
        Button editButton = view.findViewById(R.id.edit_btn);

        switch (mScreen) {
            case "subjects": {
                title.setVisibility(View.VISIBLE);
                warehouses.setVisibility(View.INVISIBLE);
                labs.setVisibility(View.INVISIBLE);
                break;
            }

            default: {
                title.setVisibility(View.INVISIBLE);
                warehouses.setVisibility(View.VISIBLE);
                labs.setVisibility(View.VISIBLE);

                if (labs.isChecked()) {
                    mScreen = "laboratories";
                } else {
                    mScreen = "warehouses";
                }
                ((MainActivity) getActivity()).configToggleButtons(this, labs, warehouses);
            }
        }

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FormFragment.newInstance(mScreen, "new", null);
                ((MainActivity) getActivity()).loadFragment(fragment);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = getDialog();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utils.setVisibility(alertComponents);
                    }
                });
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(v.getContext(), R.color.primaryDark));
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


    /**
     * Create a dialog builder that provides the interface to create and show an alert dialog.
     * It contains spinners to choose the object to edit.
     * Set positive button with its click listener and the message.
     *
     * @return The AlertDialog created through the builder
     */
    private  @NonNull AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.editable_alert, null);
        TextView mainText = view.findViewById(R.id.main_text);
        TextView subText = view.findViewById(R.id.sub_text);
        Spinner mainSpinner = view.findViewById(R.id.main_spinner);
        Spinner secondarySpinner = view.findViewById(R.id.sub_spinner);


        switch (mScreen) {
            case "subjects": {
                alertComponents = configSubjects(mainSpinner, secondarySpinner, mainText, subText);
                Utils.setVisibility(alertComponents);
                String[] careers = DataModel.getAllCareers().toArray(new String[0]);

                fillSpinner(careers, mainSpinner);

                // Set an item selected listener for main spinner
                mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String career = careers[position];
                        ArrayList<Subject> subjects = DataModel.getSubjectsByCareer(career);
                        String[] subjectsArray = new String[subjects.size()];
                        for (int i = 0; i < subjects.size(); i++) {
                            subjectsArray[i] = subjects.get(i).getName();
                        }
                        fillSpinner(subjectsArray, secondarySpinner);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        fillSpinner(new String[0], secondarySpinner);
                    }
                });
                break;
            }
            case "warehouses":
            case "laboratories": {
                alertComponents = configLocations(mainSpinner, mainText);
                Utils.setVisibility(alertComponents);
                ArrayList<Location> locations = DataModel.getLocations(mScreen);
                String[] locationsArray = new String[locations.size()];
                for (int i = 0; i < locations.size(); i++) {
                    locationsArray[i] = locations.get(i).getAddress();
                }
                fillSpinner(locationsArray, mainSpinner);
                break;
            }
        }

        builder.setView(view);
        // Add the buttons.
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String option ="";
                switch (mScreen) {
                    case "subjects": {
                        option = DataModel.getSubject(secondarySpinner.getSelectedItem().toString(), mainSpinner.getSelectedItem().toString()).getId();
                        break;
                    }
                    case "laboratories":{
                        option = DataModel.getLaboratory(mainSpinner.getSelectedItem().toString()).getId();
                        break;
                    }
                    case "warehouses": {
                        option = DataModel.getWarehouse(mainSpinner.getSelectedItem().toString()).getId();
                        break;
                    }
                }
                Fragment fragment = FormFragment.newInstance(mScreen, option, null);
                ((MainActivity) getActivity()).loadFragment(fragment);
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            mainSpinner.post(() -> {
                mainSpinner.setSelection(0); // Esto sí dispara onItemSelected()
            });
        });

        dialog.setOnDismissListener(d -> {
            Log.d("Dialog", "Dialog dismissed");
        });

        return dialog;
    }

    private void fillSpinner(String[] careers, Spinner mainSpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, careers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);
    }

    private static ArrayList<View> configSubjects(Spinner mainSpinner, Spinner secondarySpinner, TextView mainText, TextView subText) {
        ArrayList<View> alertComponents = new ArrayList<>();
        alertComponents.add(mainSpinner);
        alertComponents.add(secondarySpinner);
        mainText.setText(R.string.career);
        subText.setText(R.string.subject);
        alertComponents.add(mainText);
        alertComponents.add(subText);
        return alertComponents;
    }

    private static ArrayList<View> configLocations(Spinner mainSpinner, TextView mainText) {
        ArrayList<View> alertComponents = new ArrayList<>();
        alertComponents.add(mainSpinner);
        mainText.setText(R.string.location);
        alertComponents.add(mainText);
        return alertComponents;
    }

    public void setmScreen(String mScreen) {
        this.mScreen = mScreen;
    }

    public String getmScreen() {
        return mScreen;
    }
}