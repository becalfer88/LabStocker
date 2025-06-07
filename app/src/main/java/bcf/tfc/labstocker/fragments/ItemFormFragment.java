package bcf.tfc.labstocker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bcf.tfc.labstocker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFormFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCREEN = "screen";
    private static final String ARG_ITEM = "item";

    // TODO: Rename and change types of parameters
    private String mScreen;
    private String mItem;

    public ItemFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param screen Parameter 1.
     * @param item Parameter 2.
     * @return A new instance of fragment ItemFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFormFragment newInstance(String screen, String item) {
        ItemFormFragment fragment = new ItemFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCREEN, screen);
        args.putString(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreen= getArguments().getString(ARG_SCREEN);
            mItem = getArguments().getString(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_form, container, false);



        return view;
    }
}