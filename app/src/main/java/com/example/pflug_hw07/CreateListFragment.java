package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 CreateListFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateListFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateListFragment newInstance(String param1, String param2) {
        CreateListFragment fragment = new CreateListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_list, container, false);
    }

    EditText listNameTextbox;
    Button submitButton, cancelButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.create_list_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        listNameTextbox = view.findViewById(R.id.createList_nameTextBox);
        submitButton = view.findViewById(R.id.createList_submitButton);
        cancelButton = view.findViewById(R.id.createList_cancelButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newListName = listNameTextbox.getText().toString();

                if(newListName.isEmpty()){
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid list name")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();
                    ShoppingList newList = new ShoppingList();
                    newList.setListName(newListName);
                    newList.setListOwner(user.getUid());

                    database = FirebaseFirestore.getInstance();

                    database.collection("lists")
                            .add(newList)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "New list created!", Toast.LENGTH_SHORT).show();
                                    mListener.submitNewList();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                    b.setTitle("Error")
                                            .setMessage(e.getMessage())
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                    b.create().show();
                                }
                            });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelToMyLists();
            }
        });
    }

    CreateListFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (CreateListFragmentListener) context;
    }

    interface CreateListFragmentListener {
        void submitNewList();
        void cancelToMyLists();
    }
}