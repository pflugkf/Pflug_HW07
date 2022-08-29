package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 MyListsFragment.java
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyListsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListsFragment newInstance(String param1, String param2) {
        MyListsFragment fragment = new MyListsFragment();
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
        return inflater.inflate(R.layout.fragment_my_lists, container, false);
    }

    RecyclerView myListsRecyclerView;
    LinearLayoutManager layoutManager;
    MyListsRecyclerViewAdapter adapter;
    Button createListButton;
    ArrayList<ShoppingList> shoppingLists;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.myLists_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        shoppingLists = new ArrayList<>();

        myListsRecyclerView = view.findViewById(R.id.list_recyclerView);
        myListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        myListsRecyclerView.setLayoutManager(layoutManager);
        adapter = new MyListsRecyclerViewAdapter(shoppingLists);
        myListsRecyclerView.setAdapter(adapter);
        getMyLists();

        createListButton = view.findViewById(R.id.list_addItemButton);

        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateList();
            }
        });
    }

    void getMyLists() {
        database = FirebaseFirestore.getInstance();

        database.collection("lists")
                .whereEqualTo("listOwner", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        shoppingLists.clear();

                        for(QueryDocumentSnapshot doc : value) {
                            ShoppingList list = doc.toObject(ShoppingList.class);
                            list.setListID(doc.getId());

                            database.collection("lists").document(doc.getId())
                                    .collection("items")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            double totalCost = 0.0;
                                            for(QueryDocumentSnapshot doc : value) {
                                                double cost = doc.getDouble("itemPrice");
                                                totalCost += cost;
                                            }
                                            list.setTotalCost(totalCost);
                                        }
                                    });

                            shoppingLists.add(list);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    class MyListsRecyclerViewAdapter extends RecyclerView.Adapter<MyListsRecyclerViewAdapter.MyListsViewHolder> {
        ArrayList<ShoppingList> myListsArray;

        public MyListsRecyclerViewAdapter (ArrayList<ShoppingList> shoppingLists) {
            this.myListsArray = shoppingLists;
        }

        @NonNull
        @Override
        public MyListsRecyclerViewAdapter.MyListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
            MyListsViewHolder listsViewHolder = new MyListsRecyclerViewAdapter.MyListsViewHolder(view);

            return listsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyListsRecyclerViewAdapter.MyListsViewHolder holder, int position) {
            if(myListsArray.size() != 0){
                ShoppingList list = myListsArray.get(position);
                holder.listNameLabel.setText(list.getListName());
                holder.listCostLabel.setText(String.format("Total Cost: $%.2f", list.getTotalCost()));
                holder.deleteButton.setClickable(true);
                holder.listID = list.getListID();
            }
        }

        @Override
        public int getItemCount() {
            return myListsArray.size();
        }

        class MyListsViewHolder extends RecyclerView.ViewHolder {
            TextView listNameLabel, listCostLabel;
            ImageView deleteButton;
            String listID;

            public MyListsViewHolder(@NonNull View itemView) {
                super(itemView);
                listNameLabel = itemView.findViewById(R.id.shoppingList_name);
                listCostLabel = itemView.findViewById(R.id.shoppingList_totalCost);
                deleteButton = itemView.findViewById(R.id.shoppingList_deleteButton);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("q/test", "deleting: " + listID);
                        database.collection("lists").document(listID)
                                .collection("items")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            String docToDelete = doc.getId();
                                            //delete subcollection docs here
                                            database.collection("lists").document(listID)
                                                    .collection("items").document(docToDelete).delete();
                                        }

                                        database.collection("lists").document(listID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "List successfully deleted!", Toast.LENGTH_SHORT).show();
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
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.goToListDetails(listID);
                    }
                });
            }
        }
    }

    MyListsFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (MyListsFragmentListener) context;
    }

    interface MyListsFragmentListener {
        void goToCreateList();
        void goToListDetails(String listID);
    }
}