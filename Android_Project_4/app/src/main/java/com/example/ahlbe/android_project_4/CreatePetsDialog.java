package com.example.ahlbe.android_project_4;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CreatePetsDialog extends DialogFragment
{
    private EditText pName, pNotes;
    private Context mContext;
    private Button mCancel, mConfirm;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_create_pet_profile, container, false);
        pName = view.findViewById(R.id.dialog_pet_name);
        pNotes = view.findViewById(R.id.dialog_pet_notes);
        mContext = getActivity();
        mConfirm = view.findViewById(R.id.dialog_pet_confirm);
        View view1 = inflater.inflate(R.layout.activity_pets, null);
        final LinearLayout linearLayout = view1.findViewById(R.id.layout_activity_pets);
        final Button mButton = new Button(getActivity());
        mConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_HORIZONTAL));
                mButton.setText(pName.getText().toString());
                linearLayout.addView(mButton);
                getDialog().dismiss();
            }
        });

    return view;
    }
}
