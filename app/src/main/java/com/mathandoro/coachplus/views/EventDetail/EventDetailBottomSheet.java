package com.mathandoro.coachplus.views.EventDetail;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mathandoro.coachplus.R;

public class EventDetailBottomSheet extends BottomSheetDialogFragment {

    IEventDetailBottomSheetEvent listener;

    public interface IEventDetailBottomSheetEvent {
        void onchangeDidAttendState();
    }

    public void setListener(IEventDetailBottomSheetEvent listener){
        this.listener = listener;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.event_detail_bottom_sheet, null);
        dialog.setContentView(contentView);

        Button button = dialog.findViewById(R.id.changeDidAttendButton);
        button.setOnClickListener(view -> {
            // change state
            listener.onchangeDidAttendState();
        });
    }
}