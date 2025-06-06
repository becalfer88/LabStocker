package bcf.tfc.labstocker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCREEN = "screen";
    private static final String ARG_OPTION = "option";


    private String mScreen;
    private String mOption;

    private static ArrayList<View> formComponents = new ArrayList<>();
    private boolean editable = true;

    public FormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param screen Parameter 1.
     * @param option Parameter 2.
     * @return A new instance of fragment FormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormFragment newInstance(String screen, String option) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCREEN, screen);
        args.putString(ARG_OPTION, option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreen = getArguments().getString(ARG_SCREEN);
            mOption = getArguments().getString(ARG_OPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_form, container, false);

        Button editBtn = view.findViewById(R.id.edit_btn);
        Button deleteBtn = view.findViewById(R.id.delete_btn);
        TextView title = view.findViewById(R.id.title);
        TextView stringText = view.findViewById(R.id.string_content);
        EditText stringField = view.findViewById(R.id.string_field);
        TextView spinnerText = view.findViewById(R.id.spinner_content);
        Spinner spinnerField = view.findViewById(R.id.spinner_field);
        TextView yearText = view.findViewById(R.id.year_text);
        EditText yearField = view.findViewById(R.id.year_field);
        TextView semesterText = view.findViewById(R.id.semester_text);
        EditText semesterField = view.findViewById(R.id.semester_field);
        RecyclerView table = view.findViewById(R.id.table_list);
        Button actionBtn = view.findViewById(R.id.action_btn);
        TextView practicesText = view.findViewById(R.id.practices_text);
        ToggleButton instrumentsBtn = view.findViewById(R.id.instruments_btn);
        ToggleButton reagentsBtn = view.findViewById(R.id.reagents_btn);
        Button addBtn = view.findViewById(R.id.add_btn);

        switch (mScreen) {
            case "subjects": {
                title.setText(R.string.subjects);
                stringText.setText(R.string.subject);
                formComponents = configSubjects(spinnerText, spinnerField, yearText, yearField, semesterText, semesterField,practicesText);
                Utils.setVisibility(formComponents);
                String[] careers = DataModel.getAllCareers().toArray(new String[0]);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, careers);
                spinnerField.setAdapter(spinnerAdapter);

                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getSubject(mOption).getName());
                    int position = spinnerAdapter.getPosition(DataModel.getSubject(mOption).getCareer());
                    spinnerField.setSelection(position);
                    yearField.setText(String.valueOf(DataModel.getSubject(mOption).getYear()));
                    semesterField.setText(String.valueOf(DataModel.getSubject(mOption).getSemester()));
                    Utils.setEditable(formComponents, editable);
                }
                break;
            }
            case "laboratories": {
                title.setText(R.string.laboratories);
                stringText.setText(R.string.address);
                formComponents = configLabs(spinnerText,spinnerField, instrumentsBtn,reagentsBtn);
                Utils.setVisibility(formComponents);
                ArrayList<Location> warehousesList = DataModel.getLocations("warehouses");
                String[] warehouses = new String[warehousesList.size()];
                for (int i = 0; i < warehousesList.size(); i++) {
                    warehouses[i] = warehousesList.get(i).getAddress();
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, warehouses);
                spinnerField.setAdapter(spinnerAdapter);

                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getLocation(mOption).getAddress());
                    int position = spinnerAdapter.getPosition(DataModel.getLocation(mOption).getAddress());
                    spinnerField.setSelection(position);
                    Utils.setEditable(formComponents, editable);
                }

                break;
            }
            case "warehouses": {
                title.setText(R.string.warehouses);
                stringText.setText(R.string.address);
                ((MainActivity) getActivity()).configToggleButtons(this, instrumentsBtn, reagentsBtn);

                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getLocation(mOption).getAddress());
                }

                break;
            }
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configStringField(stringField, !editable, stringField.getText().toString());
                Utils.setEditable(formComponents, editable);


            }
        });

        return view;
    }

    private void configStringField(EditText stringField, Boolean editable, String text) {
        this.editable = editable;
        stringField.setText(text);
        stringField.setEnabled(editable);
    }


    private ArrayList<View> configSubjects(TextView spinnerText, Spinner spinnerField, TextView yearText, EditText yearField, TextView semesterText, EditText semesterField, TextView practicesText) {
        ArrayList<View> formComponents = new ArrayList<>();
        formComponents.add(spinnerText);
        spinnerText.setText(R.string.career);
        formComponents.add(spinnerField);
        formComponents.add(yearText);
        formComponents.add(yearField);
        formComponents.add(semesterText);
        formComponents.add(semesterField);
        formComponents.add(practicesText);
        return formComponents;
    }

    private ArrayList<View> configLabs(TextView spinnerText, Spinner spinnerField, ToggleButton instrumentsBtn, ToggleButton reagentsBtn) {
        ArrayList<View> formComponents = new ArrayList<>();
        formComponents.add(spinnerText);
        spinnerText.setText(R.string.warehouse);
        formComponents.add(spinnerField);
        formComponents.add(instrumentsBtn);
        formComponents.add(reagentsBtn);
        ((MainActivity) getActivity()).configToggleButtons(this, instrumentsBtn, reagentsBtn);
        return formComponents;
    }

    public void setmScreen(String mScreen) {
        this.mScreen = mScreen;
    }

    public String getmScreen() {
        return mScreen;
    }
}