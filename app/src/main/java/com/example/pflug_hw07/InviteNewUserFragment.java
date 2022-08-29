package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 InviteNewUserFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InviteNewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteNewUserFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST_ID = "ARG_LIST_ID";

    private String listID;

    public InviteNewUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param list Parameter 1.
     * @return A new instance of fragment InviteNewUserFragment.
     */
    public static InviteNewUserFragment newInstance(String list) {
        InviteNewUserFragment fragment = new InviteNewUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ID, list);
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
        return inflater.inflate(R.layout.fragment_invite_new_user, container, false);
    }

    ListView usersListView;
    ArrayList<String> users;
    ArrayAdapter<String> adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.add_user_fragment_title);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        usersListView = view.findViewById(R.id.listView);
        users = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, users);
        usersListView.setAdapter(adapter);
        getUsers();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                database.collection("users")
                        .whereEqualTo("userName", users.get(i))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String chosenUserID;
                                for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    chosenUserID = doc.getString("userID");
                                    database.collection("lists").document(listID)
                                            .update("invitedUsers", FieldValue.arrayUnion(chosenUserID));
                                }
                                mListener.addNewInvitedUser();
                            }
                        });
            }
        });
    }

    void getUsers() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        database.collection("users")
                .whereNotEqualTo("userID", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            users.add(doc.getString("userName"));
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    InviteNewUserFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (InviteNewUserFragmentListener) context;
    }

    interface InviteNewUserFragmentListener {
        void addNewInvitedUser();
    }
}