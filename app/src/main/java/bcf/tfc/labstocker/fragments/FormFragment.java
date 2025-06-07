package bcf.tfc.labstocker.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
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
import java.util.LinkedList;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCREEN = "screen";
    private static final String ARG_OPTION = "option";


    private String mScreen;
    private String mOption;

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
        Button saveBtn = view.findViewById(R.id.save_btn);
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
                    itemFeed = DataModel.getSubject(mOption).getPracticesFeed();
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = stringField.getText().toString();
                        String career = spinnerField.getSelectedItem().toString();
                        int year = Integer.parseInt(yearField.getText().toString());
                        int semester = Integer.parseInt(semesterField.getText().toString());


                        if (mOption.equals("new")) {
                            DataModel.addSubject(null, name, career, year, semester);
                            DBManager.upsertSubject(DataModel.getSubject(name, career), new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        } else {
                            Subject subject = DataModel.getSubject(mOption);
                            DataModel.updateSubject(subject, name, career, year, semester);
                            DBManager.upsertSubject(subject, new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                    }
                });
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
                    itemFeed = DataModel.getLocation(mOption).getInstrumentFeed();
                    itemFeed.addAll(DataModel.getLocation(mOption).getReagentFeed());
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String address = stringField.getText().toString();
                        String warehouse = spinnerField.getSelectedItem().toString();

                        if (mOption.equals("new")) {
                            DataModel.addLaboratory( address, DataModel.getWarehouse(warehouse),false);
                            DBManager.upsertLocation(DataModel.getLaboratory(address), new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        } else {
                            Laboratory location = (Laboratory) DataModel.getLocation(mOption);
                            DataModel.updateLaboratory(location, address, warehouse);
                            DBManager.upsertLocation(location, new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                    }
                });

                break;
            }
            case "warehouses": {
                title.setText(R.string.warehouses);
                stringText.setText(R.string.address);
                ((MainActivity) getActivity()).configToggleButtons(this, instrumentsBtn, reagentsBtn);

                if (!mOption.equals("new")) {
                    configStringField(stringField, false, DataModel.getLocation(mOption).getAddress());
                    itemFeed = DataModel.getLocation(mOption).getInstrumentFeed();
                    itemFeed.addAll(DataModel.getLocation(mOption).getReagentFeed());
                    chargeRecycler(getActivity(), table, itemFeed);
                }

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String address = stringField.getText().toString();
                        if (mOption.equals("new")) {
                            DataModel.addWarehouse(address, false);
                            DBManager.upsertLocation(DataModel.getWarehouse(address), new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        } else {
                            Warehouse location = (Warehouse) DataModel.getLocation(mOption);
                            DataModel.updateWarehouse(location, address);
                            DBManager.upsertLocation(location, new DBCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean exists) {
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Utils.getErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                    }
                });
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

        if (!DataModel.isAdmin(((MainActivity) getActivity()).getAccount())){
            deleteBtn.setVisibility(View.INVISIBLE);
        } else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = null;
                    if (mScreen.equals("subjects")) {
                        obj = DataModel.getSubject(mOption);
                    } else if (mScreen.equals("laboratories")) {
                        obj = DataModel.getLocation(mOption);
                    } else if (mScreen.equals("warehouses")) {
                        obj = DataModel.getLocation(mOption);
                    }
                    Utils.getDeleteDialog(getContext(), obj);
                }
            });
        }



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

    private void chargeRecycler(Context context, RecyclerView rV, ArrayList<ItemFeed> list) {

        rV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        FeedAdapter rVAdapter = new FeedAdapter(list);
        rV.setAdapter(rVAdapter);

        rVAdapter.notifyDataSetChanged();
    }
}