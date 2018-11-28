package com.example.ahlbe.android_project_4;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.ahlbe.android_project_4.InputValidator.isEmpty;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddOwnerDialog extends DialogFragment
{
    private static final String TAG = "AddOwnerDialog";

    private Button mButtonConfirm, mButtonCancel;
    private EditText mEditTextEmail;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int petPosition = PetsInformationActivity.getPetPosition();
    private ArrayList<Pet> mPetArrayList = PetsActivity.getPets();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_add_owner, container, false);
        mContext = getActivity();

        mEditTextEmail = view.findViewById(R.id.edit_owner_email);
        mButtonConfirm = view.findViewById(R.id.button_confirm_add_owner);
        mButtonCancel = view.findViewById(R.id.button_add_owner_cancel);

        mButtonConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "inside the onclick for adding owners");
                if(!isEmpty(mEditTextEmail.getText().toString()))
                {
                    db.collection(mContext.getResources()
                            .getString(R.string.user_collection))
                            .whereEqualTo("email", mEditTextEmail.getText().toString())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            Log.d(TAG, "got the user document");
                            for(QueryDocumentSnapshot i: queryDocumentSnapshots)
                            {
                                Log.d(TAG, i.getId());
                                db.collection(mContext.getResources().getString(R.string.pet_collection))
                                        .document(mPetArrayList.get(petPosition).getDocumentID())
                                        .update("owners", FieldValue.arrayUnion(i.getId()));
                            }
                        }
                    });
                }
                else{Toast.makeText(mContext, "Please fill out the email field", Toast.LENGTH_SHORT).show();}



            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDialog().dismiss();
            }
        });

        return view;

    }
}
