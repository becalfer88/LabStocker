package bcf.tfc.labstocker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bcf.tfc.labstocker.MainActivity;
import bcf.tfc.labstocker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ACCOUNT_TEXT = "account";

    private String mAccount;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param account Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */

    public static HomeFragment newInstance(String account) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ACCOUNT_TEXT, account);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccount = getArguments().getString(ACCOUNT_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button searchBtn = view.findViewById(R.id.search);
        Button moveItemBtn = view.findViewById(R.id.move_rss);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment fragment = SearchFragment.newInstance(true);
                ((MainActivity)getActivity()).loadFragment(fragment);
            }
        });

        return view;
    }
}