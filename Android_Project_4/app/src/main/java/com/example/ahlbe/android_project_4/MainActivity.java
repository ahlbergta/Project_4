package com.example.ahlbe.android_project_4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    public static final String FIRST = "first";
    public static final String LAST = "last";

    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("Users/johnconnor");

    TextView mNameTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNameTextview = findViewById(R.id.name_first);
    }
    public void fetchData(View view)

    {
        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    String first = documentSnapshot.getString(FIRST);
                    String last = documentSnapshot.getString(LAST);
                    mNameTextview.setText(first);

                }
            }
        });

    }

}
