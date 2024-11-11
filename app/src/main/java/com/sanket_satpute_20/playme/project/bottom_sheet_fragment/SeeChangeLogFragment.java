package com.sanket_satpute_20.playme.project.bottom_sheet_fragment;

import static com.sanket_satpute_20.playme.project.service.AppStarterIntentService.ACCENT_COLOR;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.change_log_data;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.recycler_views.ChangeLogParentRecycle;
import com.sanket_satpute_20.playme.project.service.ChangeLogDataAdderIntentService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

public class SeeChangeLogFragment extends BottomSheetDialogFragment {


    public static final String FROM_WHERE = "FROM_WHERE";
    MaterialCardView divider;

    RecyclerView recycler_view;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("initialised_change_log")) {
                setRecycler();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_see_change_log, container, false);
        initViews(view);
        doExtra();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("initialised_change_log");
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver);
    }

    private void initViews(View view) {
        divider = view.findViewById(R.id.divider);
        recycler_view = view.findViewById(R.id.recycler_view);
    }

    private void doExtra() {
        divider.setBackgroundTintList(ColorStateList.valueOf(ACCENT_COLOR));

        if (change_log_data.size() <= 0) {
            Intent intent = new Intent(requireActivity(), ChangeLogDataAdderIntentService.class);
            intent.putExtra(FROM_WHERE, "ChangeLogFragment");
            requireActivity().startService(intent);
        } else {
            setRecycler();
        }
    }

    private void setRecycler() {
        recycler_view.setHasFixedSize(true);
        ChangeLogParentRecycle adapter = new ChangeLogParentRecycle(requireActivity(), change_log_data);
        LinearLayoutManager layout = new LinearLayoutManager(requireActivity());
        recycler_view.setLayoutManager(layout);
        recycler_view.setAdapter(adapter);
    }

}