package bcf.tfc.labstocker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.adapters.FeedAdapter;
import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.model.data.LabInstrument;
import bcf.tfc.labstocker.model.data.Reagent;
import bcf.tfc.labstocker.model.DataModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_IS_REAGENT = "isReagent";
    private boolean searchingReagents;

    private EditText searchInput;
    private RecyclerView resultRecycler;
    private FeedAdapter adapter;
    private List<ItemFeed> itemList = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(boolean isReagent) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_REAGENT, isReagent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchingReagents = getArguments().getBoolean(ARG_IS_REAGENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        resultRecycler = view.findViewById(R.id.search_recycler);

        resultRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new FeedAdapter(itemList);
        resultRecycler.setAdapter(adapter);

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

        return view;
    }

    private void performSearch(String query) {
        itemList.clear();
        query = query.toLowerCase();

        if (searchingReagents) {
            for (Reagent r : DataModel.reagents) {
                if (r.getFormula().toLowerCase().contains(query) ||
                        (r.getDescription() !=null && r.getDescription().toLowerCase().contains(query))) {
                    itemList.add(new ItemFeed(r.getId(), r.getFormula(), null, null, null, null));
                }
            }
        } else {
            for (LabInstrument i : DataModel.labInstruments) {
                if (i.getName().toLowerCase().contains(query) ||
                        (i.getObservations() != null && i.getObservations().toLowerCase().contains(query))) {
                    itemList.add(new ItemFeed(i.getId(), i.getName(), null, null, null, null));
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}