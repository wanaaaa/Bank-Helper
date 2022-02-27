package com.zybooks.thebanddatabase;

import android.content.Context;
import android.content.res.Resources;
import java.util.ArrayList;
import java.util.List;

public class BankRepository {

    private static BankRepository instance;
    private List<Bank> mBanks;

    public static BankRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BankRepository(context);
        }
        return instance;
    }

    private BankRepository(Context context) {
        mBanks = new ArrayList<>();
        Resources res = context.getResources();
        String[] bands = res.getStringArray(R.array.banks);
        String[] descriptions = res.getStringArray(R.array.descriptions);
        for (int i = 0; i < bands.length; i++) {
            mBanks.add(new Bank(i + 1, bands[i], descriptions[i]));
        }
    }

    public List<Bank> getBanks() {
        return mBanks;
    }

    public Bank getBand(int bandId) {
        for (Bank bank : mBanks) {
            if (bank.getId() == bandId) {
                return bank;
            }
        }
        return null;
    }
}

