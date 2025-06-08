package bcf.tfc.labstocker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.UnderConstructionActivity;
import bcf.tfc.labstocker.adapters.FeedAdapter;
import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormFragment extends Fragment {

    private static final String ARG_SCREEN = "screen";
    private static final String ARG_OPTION = "option";
    private static final String ARG_SUBJECT = "subject";


    private String mScreen;
    private String mOption;
    private String mSubject;

    private static ArrayList<View> formComponents = new ArrayList<>();
    private boolean editable = true;
    private ArrayList<ItemFeed> itemFeed = new ArrayList<>();

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
    public static FormFragment newInstance(String screen, String option, String subject) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCREEN, screen);
        args.putString(ARG_OPTION, option);
        args.putString(ARG_SUBJECT, subject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreen = getArguments().getString(ARG_SCREEN);
            mOption = getArguments().getString(ARG_OPTION);
            mSubject = getArguments().getString(ARG_SUBJECT);
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
        Button saveBtn = view.findViewById(R.id.save_btn);
        TextView practicesText = view.findViewById(R.id.practices_text);
        Button addBtn = view.findViewById(R.id.add_btn);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UnderConstructionActivity.class);
                startActivity(intent);
            }
        });

        switch (mScreen) {
            case "subjects": {
                title.setText(R.string.subjects);
                stringText.setText(R.string.subject);
                formComponents = configSubjects(spinnerText, spinnerField, yearText, yearField, semesterText, semesterField, practicesText);
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
                    itemFeed = DataModel.getSubject(mOption).getPracticesFeed();
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveSubject(stringField, spinnerField, yearField, semesterField);
                    }
                });
                break;
            }
            case "laboratories": {
                title.setText(R.string.laboratories);
                stringText.setText(R.string.address);
                formComponents = configLabs(spinnerText, spinnerField);
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
                    itemFeed = DataModel.getLocation(mOption).getInstrumentFeed();
                    itemFeed.addAll(DataModel.getLocation(mOption).getReagentFeed());
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveLaboratory(stringField, spinnerField);
                    }
                });
                break;
            }
            case "warehouses": {
                title.setText(R.string.warehouses);
                stringText.setText(R.string.address);

                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getLocation(mOption).getAddress());
                    itemFeed = DataModel.getLocation(mOption).getInstrumentFeed();
                    itemFeed.addAll(DataModel.getLocation(mOption).getReagentFeed());
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveWarehouse(stringField);
                    }
                });
                break;
            }
            case "practices": {
                title.setText(R.string.practices);
                stringText.setText(R.string.practice);
                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getPractice(mOption).getName());
                    itemFeed = DataModel.getPractice(mOption).getInstrumentFeed(DataModel.getSubject(mSubject));
                    itemFeed.addAll(DataModel.getPractice(mOption).getReagentFeed(DataModel.getSubject(mSubject)));
                    chargeRecycler(getActivity(), table, itemFeed);
                }
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        savePractice(stringField);
                    }
                });
                break;
            }
        }

        if (mOption.equals("new")) {
            editBtn.setVisibility(View.INVISIBLE);
        } else{
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    configStringField(stringField, !editable, stringField.getText().toString());
                    Utils.setEditable(formComponents, editable);
                    if (editable) {
                        editBtn.setBackgroundResource(R.drawable.create_8860653);
                    } else {
                        editBtn.setBackgroundResource( R.drawable.edit);
                    }

                }
            });
        }

        if (!DataModel.isAdmin(((MainActivity) getActivity()).getAccount())
        || (!mScreen.equals("subjects") && !mScreen.equals("practices"))){
            deleteBtn.setVisibility(View.INVISIBLE);
        } else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mScreen.equals("practices")) {
                        Intent intent = new Intent(getContext(), UnderConstructionActivity.class);
                        (getContext()).startActivity(intent);
                    } else {
                        Subject subj = DataModel.getSubject(mOption);

                        AlertDialog dialog = Utils.getDeleteDialog(getContext(), subj);
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(v.getContext(), R.color.secondaryDark));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(v.getContext(), R.color.primaryDark));
                    }
                }
            });
        }

        return view;
    }



    // GETTERS AND SETTERS
    public void setmScreen(String mScreen) {
        this.mScreen = mScreen;
    }

    public String getmScreen() {
        return mScreen;
    }

    // SAVES
    private void saveSubject(EditText stringField, Spinner spinnerField, EditText yearField, EditText semesterField) {
        String name = stringField.getText().toString();
        String career = spinnerField.getSelectedItem().toString();
        int year = Integer.parseInt(yearField.getText().toString());
        int semester = Integer.parseInt(semesterField.getText().toString());

        Subject subject;
        if (mOption.equals("new")) {
            DataModel.addSubject(null, name, career, year, semester);
            subject = DataModel.getSubject(name,career);
        } else {
            subject = DataModel.getSubject(mOption);
            DataModel.updateSubject(subject, name, career, year, semester);
        }

        DBManager.upsertSubject(subject, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                Toast.makeText(getContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showError();
            }
        });
    }

    private void saveLaboratory(EditText stringField, Spinner spinnerField) {
        String address = stringField.getText().toString();
        String warehouse = spinnerField.getSelectedItem().toString();

        Location location;
        if (mOption.equals("new")) {
            DataModel.addLaboratory(address, DataModel.getWarehouse(warehouse), false);
            location = DataModel.getLaboratory(address);
        } else {
            location = DataModel.getLocation(mOption);
            DataModel.updateLaboratory((Laboratory) location, address, warehouse);
        }
        DBManager.upsertLocation(location, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                Toast.makeText(getContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showError();
            }
        });
    }

    private void saveWarehouse(EditText stringField) {
        String address = stringField.getText().toString();
        Location location;
        if (mOption.equals("new")) {
            DataModel.addWarehouse(address, false);
            location = DataModel.getWarehouse(address);
        } else {
            location = (Warehouse) DataModel.getLocation(mOption);
            DataModel.updateWarehouse((Warehouse) location, address);
        }
        DBManager.upsertLocation(location, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                Toast.makeText(getContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showError();
            }
        });
    }

    private void savePractice(EditText stringField) {
        String name = stringField.getText().toString();

        Subject subject = DataModel.getSubject(mSubject);
        if (mOption.equals("new")) {
            DataModel.addPractice(name, subject);
        } else {
            DataModel.updatePractice(mOption, name, subject);
        }
        DBManager.upsertSubject(subject, new DBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                Toast.makeText(getContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showError();
            }
        });
    }

    // OTHER METHODS
    private void showError() {
        AlertDialog dialog = Utils.getErrorDialog(getActivity(),  getString(R.string.error));
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
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

    private ArrayList<View> configLabs(TextView spinnerText, Spinner spinnerField) {
        ArrayList<View> formComponents = new ArrayList<>();
        formComponents.add(spinnerText);
        spinnerText.setText(R.string.warehouse);
        formComponents.add(spinnerField);
        return formComponents;
    }


    private void chargeRecycler(Context context, RecyclerView rV, ArrayList<ItemFeed> list) {

        rV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        FeedAdapter rVAdapter = new FeedAdapter(list);
        rV.setAdapter(rVAdapter);

        rVAdapter.notifyDataSetChanged();
    }
}