package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 ListDetailsFragment.java
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST_ID = "ARG_LIST_ID";

    private String listID;

    public ListDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param chosenListID Parameter 1.
     * @return A new instance of fragment ListDetailsFragment.
     */
    public static ListDetailsFragment newInstance(String chosenListID) {
        ListDetailsFragment fragment = new ListDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_ID, chosenListID);
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
        return inflater.inflate(R.layout.fragment_list_details, container, false);
    }

    Button manageUsersButton, addItemButton;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ListDetailsRecyclerViewAdapter adapter;
    ArrayList<ShoppingListItem> shoppingListItems;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.list_details_fragment_title);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        manageUsersButton = view.findViewById(R.id.list_manageButton);
        manageUsersButton.setClickable(false);
        manageUsersButton.setVisibility(View.INVISIBLE);

        database.collection("lists").document(listID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String ownerID = documentSnapshot.getString("listOwner");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if(currentUser.getUid().equals(ownerID)){
                            manageUsersButton.setClickable(true);
                            manageUsersButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

        shoppingListItems = new ArrayList<>();
        recyclerView = view.findViewById(R.id.list_recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListDetailsRecyclerViewAdapter(shoppingListItems);
        recyclerView.setAdapter(adapter);
        getListItems();

        addItemButton = view.findViewById(R.id.list_addItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToAddItems(listID);
            }
        });

        manageUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToManageUsers(listID);
            }
        });
    }

    void getListItems() {
        database.collection("lists").document(listID)
                .collection("items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        shoppingListItems.clear();
                        double totalCost = 0.0;

                        for(QueryDocumentSnapshot doc : value) {
                            ShoppingListItem listItem = doc.toObject(ShoppingListItem.class);
                            listItem.setItemID(doc.getId());
                            totalCost += listItem.getItemPrice();

                            shoppingListItems.add(listItem);
                        }

                        adapter.notifyDataSetChanged();

                        database.collection("lists").document(listID)
                                .update("totalCost", totalCost)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                    }
                });
    }

    class ListDetailsRecyclerViewAdapter extends RecyclerView.Adapter<ListDetailsRecyclerViewAdapter.ListDetailsViewHolder> {

        ArrayList<ShoppingListItem> itemArrayList;

        public ListDetailsRecyclerViewAdapter(ArrayList<ShoppingListItem> arrayOfItems) {
            this.itemArrayList = arrayOfItems;
        }

        @NonNull
        @Override
        public ListDetailsRecyclerViewAdapter.ListDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item_list_item, parent, false);
            ListDetailsViewHolder listItemsViewHolder = new ListDetailsRecyclerViewAdapter.ListDetailsViewHolder(view);

            return listItemsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ListDetailsRecyclerViewAdapter.ListDetailsViewHolder holder, int position) {
            if(itemArrayList.size() != 0) {
                ShoppingListItem item = itemArrayList.get(position);
                holder.itemName.setText(item.getItemName());
                holder.itemPrice.setText(String.format("$%.2f", item.getItemPrice()));
                holder.itemID = item.getItemID();
                holder.itemAcquired = item.isItemAcquired();

                //set the button images here, checkbox
                if(item.itemAcquired == true) {
                    holder.checkBox.setImageResource(R.drawable.checked);
                } else {
                    holder.checkBox.setImageResource(R.drawable.unchecked);
                }
            }

        }

        @Override
        public int getItemCount() {
            return itemArrayList.size();
        }

        class ListDetailsViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, itemPrice;
            ImageView checkBox, deleteButton;
            String itemID;
            boolean itemAcquired;

            public ListDetailsViewHolder(@NonNull View itemView) {
                super(itemView);

                itemName = itemView.findViewById(R.id.item_itemName);
                itemPrice = itemView.findViewById(R.id.item_itemPrice);
                checkBox = itemView.findViewById(R.id.item_acquiredButton);
                deleteButton = itemView.findViewById(R.id.item_deleteButton);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(itemAcquired) {
                            database.collection("lists").document(listID)
                                    .collection("items").document(itemID)
                                    .update("itemAcquired", false);
                        } else {
                            database.collection("lists").document(listID)
                                    .collection("items").document(itemID)
                                    .update("itemAcquired", true);
                        }

                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.collection("lists").document(listID)
                                .collection("items").document(itemID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("q/test", "Item deleted" + itemID);
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
            }
        }
    }

    ListDetailsFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ListDetailsFragmentListener) context;
    }

    interface ListDetailsFragmentListener {
        void goToManageUsers(String listID);
        void goToAddItems(String listID);
    }
}