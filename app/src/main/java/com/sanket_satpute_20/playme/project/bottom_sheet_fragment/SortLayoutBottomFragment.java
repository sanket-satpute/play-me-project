package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTED;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.SORTINGORDER;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sanket_satpute_20.playme.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class SortLayoutBottomFragment extends BottomSheetDialogFragment {

    public static final String ORDER = "ORDER";
    String asc = "ASC";
    String desc = "DESC";

    RadioGroup radioGroup, order_by;
    RadioButton byname, bydate, bysize, by_asc, by_desc;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String sortOrder, byOrder;

    MaterialCardView divider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.sort_elements, container, false);
        initViews(view);
        doExtra();
        onClick();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    private void initViews(View view) {
        radioGroup = view.findViewById(R.id.radio_group);
        byname = view.findViewById(R.id.by_name);
        bydate = view.findViewById(R.id.by_date);
        bysize = view.findViewById(R.id.by_size);

        divider = view.findViewById(R.id.divider);

        order_by = view.findViewById(R.id.order_by);
        by_asc = view.findViewById(R.id.asc);
        by_desc = view.findViewById(R.id.desc);
    }

    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));
        preferences = requireActivity().getSharedPreferences(SORTINGORDER, MODE_PRIVATE);
        sortOrder = preferences.getString(SORTED, "BYNAME");
        byOrder = preferences.getString(ORDER, asc);

        switch (sortOrder) {
            case "BYSIZE":
                bysize.setChecked(true);
                break;
            case "BYDATE":
                bydate.setChecked(true);
                break;
            default:
                byname.setChecked(true);
                break;
        }

        if ("DESC".equals(byOrder)) {
            by_desc.setChecked(true);
        } else {
            by_asc.setChecked(true);
        }
    }


    @SuppressLint("NonConstantResourceId")
    private void onClick() {
        final int b_name = radioGroup.getChildAt(0).getId();
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (preferences == null) {
                editor = requireActivity().getSharedPreferences(SORTINGORDER, MODE_PRIVATE).edit();
            }
            editor = preferences.edit();

            if (i == R.id.by_name) {
                editor.putString(SORTED, "BYNAME");
            } else if (i == R.id.by_date) {
                editor.putString(SORTED, "BYDATE");
            } else if (i == R.id.by_size) {
                editor.putString(SORTED, "BYSIZE");
            }

            requireActivity().recreate();
            SortLayoutBottomFragment.this.dismiss();
        });

        order_by.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (preferences == null) {
                editor = requireActivity().getSharedPreferences(SORTINGORDER, MODE_PRIVATE).edit();
            }
            editor = preferences.edit();
            if (i == R.id.desc) {
                editor.putString(ORDER, desc);
            } else {
                editor.putString(ORDER, asc);
            }
            editor.apply();
            requireActivity().recreate();
            SortLayoutBottomFragment.this.dismiss();
        });
    }
}
