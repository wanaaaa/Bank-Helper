package com.zybooks.thebanddatabase;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    public static final String ARG_BAND_ID = "band_id";
    private Bank mBank;
    public DetailFragment() {
        // Required empty public constructor
    }
    ///////////////////////
    public String urlStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int bandId = 1;

        // Get the band ID from the fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            bandId = args.getInt(ARG_BAND_ID);
        }

        // Get the selected band
        mBank = BankRepository.getInstance(requireContext()).getBand(bandId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (mBank != null) {
            TextView nameTextView = rootView.findViewById(R.id.bank_name);
            nameTextView.setText(mBank.getName());

            TextView descriptionTextView = rootView.findViewById(R.id.band_description);
            descriptionTextView.setText(mBank.getDescription());

            String nameStr = mBank.getName();

            //String nameStr = mBand.getName();
            ImageView imgV = rootView.findViewById(R.id.imageViewID);
            if (nameStr.contains("Capital")) {
                WanVariables.getInstance().urlStr = "https://en.wikipedia.org/wiki/Capital_One";
                imgV.setImageResource(R.drawable.capital_big);
            }
            else if (nameStr.contains("Morgan"))
            {
                WanVariables.getInstance().urlStr = "https://en.wikipedia.org/wiki/Chase_Bank";
                imgV.setImageResource(R.drawable.morgan_big);
            }
            else if (nameStr.contains("America"))
            {
                WanVariables.getInstance().urlStr = "https://en.wikipedia.org/wiki/Bank_of_America";
                imgV.setImageResource(R.drawable.america_big);
            }
            else if (nameStr.contains("Wells"))
            {
                WanVariables.getInstance().urlStr = "https://en.wikipedia.org/wiki/Wells_Fargo";
                imgV.setImageResource(R.drawable.wells_big);
            }
        }
        return rootView;
    }

}