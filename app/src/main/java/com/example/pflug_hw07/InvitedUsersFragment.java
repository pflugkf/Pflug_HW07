package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 InvitedUsersFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InvitedUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvitedUsersFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST_ID = "ARG_LIST_ID";

    // TODO: Rename and change types of parameters
    private String listID;

    public InvitedUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param list Parameter 1.
     * @return A new instance of fragment InvitedUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvitedUsersFragment newInstance(String list) {
        InvitedUsersFragment fragment = new InvitedUsersFragment();
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
        View view = inflater.inflate(R.layout.fragment_invited_users, container, false);

        return view;
    }

    RecyclerView invitedUsersRecyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<User> invitedUsers;
    InvitedUsersRecyclerViewAdapter invAdapter;
    Button inviteUsersButton;
    boolean calledOnce = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.invited_users_fragment_title);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        invitedUsersRecyclerView = view.findViewById(R.id.invited_recyclerView);
        invitedUsersRecyclerView.setHasFixedSize(true);
        invitedUsers = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        invitedUsersRecyclerView.setLayoutManager(layoutManager);
        invAdapter = new InvitedUsersRecyclerViewAdapter(invitedUsers);
        invitedUsersRecyclerView.setAdapter(invAdapter);

        if(calledOnce == false){
            Log.d("q/test", "calling getUsers method in onviewcreated");
            getInvitedUsers();
            Log.d("q/test", "setting switch to off, shouldn't call again");
            calledOnce = true;
        }

        inviteUsersButton = view.findViewById(R.id.invited_inviteUserButton);
        inviteUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToInviteUsers(listID);
            }
        });
    }

    void getInvitedUsers() {
        database.collection("lists").document(listID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        invitedUsers.clear();

                        ShoppingList list = value.toObject(ShoppingList.class);

                        if (list.getInvitedUsers().size() != 0) {
                            database.collection("users")
                                    .whereIn("userID", list.getInvitedUsers())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                User user = doc.toObject(User.class);

                                                invitedUsers.add(user);
                                            }
                                            invAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }

    class InvitedUsersRecyclerViewAdapter extends RecyclerView.Adapter<InvitedUsersRecyclerViewAdapter.InvitedUsersViewHolder> {
        ArrayList<User> userArrayList;

        public InvitedUsersRecyclerViewAdapter(ArrayList<User> invitedUsers) {
            this.userArrayList = invitedUsers;
        }

        @NonNull
        @Override
        public InvitedUsersRecyclerViewAdapter.InvitedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_user_list_item, parent, false);
            InvitedUsersViewHolder invitedUsersViewHolder = new InvitedUsersRecyclerViewAdapter.InvitedUsersViewHolder(view);

            return invitedUsersViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull InvitedUsersRecyclerViewAdapter.InvitedUsersViewHolder holder, int position) {
            if(userArrayList.size() != 0) {
                User user = userArrayList.get(position);
                holder.invUserName.setText(user.getUserName());
                holder.userID = user.getUserID();
            }

        }

        @Override
        public int getItemCount() {
            return userArrayList.size();
        }

        class InvitedUsersViewHolder extends RecyclerView.ViewHolder {
            TextView invUserName;
            ImageView deleteButton;
            String userID;

            public InvitedUsersViewHolder(@NonNull View itemView) {
                super(itemView);

                invUserName = itemView.findViewById(R.id.invUser_userName);
                deleteButton = itemView.findViewById(R.id.invUser_deleteButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.collection("lists").document(listID)
                                .update("invitedUsers", FieldValue.arrayRemove(userID));
                    }
                });
            }
        }
    }

    InvitedUsersFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (InvitedUsersFragmentListener) context;
    }

    interface InvitedUsersFragmentListener {
        void goToInviteUsers(String listID);
    }
}