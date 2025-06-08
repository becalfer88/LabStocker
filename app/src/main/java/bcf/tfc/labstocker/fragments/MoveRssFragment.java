package bcf.tfc.labstocker.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.adapters.SimpleAdapter;
import bcf.tfc.labstocker.adapters.SimpleItem;
import bcf.tfc.labstocker.model.DBManager;
import bcf.tfc.labstocker.adapters.FeedAdapter;
import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.DBCallback;
import bcf.tfc.labstocker.model.data.LabInstrument;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Quantity;
import bcf.tfc.labstocker.model.data.Reagent;
import bcf.tfc.labstocker.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MoveRssFragment extends Fragment {

    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText quantityInput;
    private EditText searchInput;
    private RecyclerView resultRecycler;
    private Button transferButton;
    private TextView selectedItemText;

    private Location fromLocation;
    private Location toLocation;
    private Object selectedItem; // Reagent o LabInstrument

    private List<Location> allLocations;
    private List<SimpleItem> itemList = new ArrayList<>();
    private SimpleAdapter adapter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public MoveRssFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_move_rss, container, false);

        spinnerFrom = view.findViewById(R.id.spinner_from);
        spinnerTo = view.findViewById(R.id.spinner_to);
        quantityInput = view.findViewById(R.id.quantity_input);
        searchInput = view.findViewById(R.id.search_input);
        resultRecycler = view.findViewById(R.id.result_recycler);
        transferButton = view.findViewById(R.id.transfer_button);
        selectedItemText = view.findViewById(R.id.selected_item_text);

        loadLocations();
        setupSpinners();

        adapter = new SimpleAdapter(itemList);
        resultRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        resultRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            if (item.isReagent()){
                selectedItem = DataModel.getReagent(item.getId());
            } else {
                selectedItem = DataModel.getLabInstrument(item.getId());
            }
            selectedItemText.setText(item.getLabel());
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        transferButton.setOnClickListener(v -> {
            if (fromLocation == null || toLocation == null || selectedItem == null || quantityInput.getText().toString().isEmpty()) {
                AlertDialog dialog = Utils.getErrorDialog(getContext(), getString(R.string.all_fields_are_required));
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                return;
            }

            double qty;
            try {
                qty = Double.parseDouble(quantityInput.getText().toString());
            } catch (NumberFormatException e) {
                AlertDialog dialog = Utils.getErrorDialog(getContext(), getString(R.string.invalid_quantity));
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                return;
            }

            Quantity originalFrom = selectedItem instanceof Reagent ? fromLocation.getReagentQuantity((Reagent) selectedItem) : fromLocation.getLabInstrumentQuantity((LabInstrument) selectedItem);
            Quantity originalTo = selectedItem instanceof Reagent ? toLocation.getReagentQuantity((Reagent) selectedItem) : toLocation.getLabInstrumentQuantity((LabInstrument) selectedItem);

            if (selectedItem instanceof Reagent) {
                fromLocation.updateReagent((Reagent) selectedItem, (float) originalFrom.getValue() - (float) qty, originalFrom.getUnit());

                if (originalTo == null) {
                    toLocation.addReagent((Reagent) selectedItem, (float) qty, originalFrom.getUnit());
                } else {
                    toLocation.addReagent((Reagent) selectedItem, (float) originalTo.getValue() + (float) qty, originalFrom.getUnit());
                }
            } else {
                fromLocation.updateLabInstrument((LabInstrument) selectedItem, (float) originalFrom.getValue() - (float) qty, originalFrom.getUnit());

                if (originalTo == null) {
                    toLocation.addLabInstrument((LabInstrument) selectedItem, (float) qty, originalFrom.getUnit());
                } else {
                    toLocation.addLabInstrument((LabInstrument) selectedItem, (float) originalTo.getValue() + (float) qty, originalFrom.getUnit());
                }
            }

            DBManager.transferResources(fromLocation, toLocation, new DBCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    selectedItem = null;
                    selectedItemText.setText("");
                    quantityInput.setText("");
                    Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                   AlertDialog dialog = Utils.getErrorDialog(getContext(), getString(R.string.error));
                   dialog.show();
                   dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryDark));
                }
            });
        });

        return view;
    }

    private void performSearch(String query) {
        executor.execute(() -> {
            List<SimpleItem> filtered = new ArrayList<>();
            if (fromLocation != null && query != null) {
                filtered.addAll(fromLocation.search(query));
            }
            mainHandler.post(() -> {
                itemList.clear();
                itemList.addAll(filtered);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
    }

    private void loadLocations() {
        allLocations = DataModel.getLocations("warehouses");
        allLocations.addAll(DataModel.getLocations("laboratories"));
    }

    private void setupSpinners() {
        List<String> labels = new ArrayList<>();
        for (Location loc : allLocations) {
            String label = (loc instanceof Laboratory) ? getString(R.string.laboratory)+ " - " : getString(R.string.warehouse) +" - ";
            label += loc.getAddress();
            labels.add(label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLocation = allLocations.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLocation = allLocations.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}