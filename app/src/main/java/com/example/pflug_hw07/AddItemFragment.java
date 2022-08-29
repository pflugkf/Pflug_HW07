package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 AddItemFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST_ID = "ARG_LIST_ID";

    // TODO: Rename and change types of parameters
    private String listID;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sentListID Parameter 1.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String sentListID) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ID, sentListID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listID = getArguments().getString(ARG_LIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    EditText itemNameTextBox, itemPriceTextBox;
    Button submit, cancel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.add_item_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        itemNameTextBox = view.findViewById(R.id.addItem_itemNameTextbox);
        itemPriceTextBox = view.findViewById(R.id.addItem_itemPriceTextbox);

        submit = view.findViewById(R.id.addItem_submitButton);
        cancel = view.findViewById(R.id.addItem_cancelButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = itemNameTextBox.getText().toString();
                String itemPriceText = itemPriceTextBox.getText().toString();

                if(itemName.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid item name")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else if(itemPriceText.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid item price")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    ShoppingListItem newItem = new ShoppingListItem();
                    FirebaseUser user = mAuth.getCurrentUser();
                    newItem.setItemName(itemName);
                    newItem.setItemPrice(Double.parseDouble(itemPriceText));

                    database = FirebaseFirestore.getInstance();

                    database.collection("lists").document(listID)
                            .collection("items")
                            .add(newItem)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    newItem.setItemID(documentReference.getId());

                                    database.collection("lists").document(listID)
                                            .collection("items")
                                            .document(documentReference.getId())
                                            .update("itemID", newItem.getItemID())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("q/test", "itemID added to new item");
                                                }
                                            });
                                    Log.d("q/test", "new item added");
                                    mListener.submitNewItem();
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelToMyLists();
            }
        });
    }


    AddItemFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddItemFragmentListener) context;
    }

    interface AddItemFragmentListener {
        void submitNewItem();
        void cancelToMyLists();
    }
}