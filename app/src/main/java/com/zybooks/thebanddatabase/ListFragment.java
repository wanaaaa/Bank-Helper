package com.zybooks.thebanddatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Click listener for the RecyclerView
        View.OnClickListener onClickListener = itemView -> {

            // Create fragment arguments containing the selected band ID
            int selectedBandId = (int) itemView.getTag();
            Bundle args = new Bundle();
            args.putInt(DetailFragment.ARG_BAND_ID, selectedBandId);

            View detailFragmentContainer = rootView.findViewById(R.id.detail_frag_container);
            if (detailFragmentContainer == null) {
                // Replace list with details
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, args);
            } else {
                // Show details on the right
                Navigation.findNavController(detailFragmentContainer)
                        .navigate(R.id.fragment_detail, args);
            }

            // Replace list with details
            //Navigation.findNavController(itemView).navigate(R.id.show_item_detail, args);
        };

        // Send bands to RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.band_list);
        List<Bank> banks = BankRepository.getInstance(requireContext()).getBanks();
        recyclerView.setAdapter(new BandAdapter(banks, onClickListener));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        return rootView;
    }

    private class BandAdapter extends RecyclerView.Adapter<BandHolder> {

        private final List<Bank> mBanks;
        private final View.OnClickListener mOnClickListener;

        public BandAdapter(List<Bank> banks, View.OnClickListener onClickListener) {
            mBanks = banks;
            mOnClickListener = onClickListener;
        }

        @NonNull
        @Override
        public BandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BandHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BandHolder holder, int position) {
            Bank bank = mBanks.get(position);
            holder.bind(bank);
            holder.itemView.setTag(bank.getId());
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mBanks.size();
        }
    }

    private static class BandHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;
        private TextView mImgTView;
        private ImageView smView;

        public BandHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_band, parent, false));
            mNameTextView = itemView.findViewById(R.id.band_name);
            smView = itemView.findViewById(R.id.smImaViewID);
        }

        public void bind(Bank bank) {
            mNameTextView.setText(bank.getName());
            String nameStr = bank.getName();
            if (nameStr.contains("Capital") ) {
                smView.setImageResource(R.drawable.capital);
            }
            else if (nameStr.contains("Morgan")) {
                smView.setImageResource(R.drawable.morgan);
            }
            else if (nameStr.contains("America")) {
                smView.setImageResource(R.drawable.america);
            }
            else if (nameStr.contains("Wells")) {
                smView.setImageResource(R.drawable.wells);
            }
        }
    }
}