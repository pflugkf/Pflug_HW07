package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 SharedListFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SharedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SharedListFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SharedListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SharedListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SharedListFragment newInstance(String param1, String param2) {
        SharedListFragment fragment = new SharedListFragment();
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
        return inflater.inflate(R.layout.fragment_shared_list, container, false);
    }

    RecyclerView sharedListsRecyclerView;
    LinearLayoutManager layoutManager;
    SharedListsRecyclerViewAdapter adapter;
    ArrayList<ShoppingList> sharedShoppingLists;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.invited_lists_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        sharedShoppingLists = new ArrayList<>();

        sharedListsRecyclerView = view.findViewById(R.id.shared_recyclerView);
        sharedListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedListsRecyclerView.setLayoutManager(layoutManager);
        adapter = new SharedListsRecyclerViewAdapter(sharedShoppingLists);
        sharedListsRecyclerView.setAdapter(adapter);
        getMySharedLists();

    }

    void getMySharedLists() {
        database = FirebaseFirestore.getInstance();

        database.collection("lists")
                .whereArrayContains("invitedUsers", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        sharedShoppingLists.clear();

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

                            sharedShoppingLists.add(list);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

    }

    class SharedListsRecyclerViewAdapter extends RecyclerView.Adapter<SharedListsRecyclerViewAdapter.SharedListsViewHolder> {

        ArrayList<ShoppingList> myListsArray;

        public SharedListsRecyclerViewAdapter (ArrayList<ShoppingList> sharedShoppingLists) {
            this.myListsArray = sharedShoppingLists;
        }

        @NonNull
        @Override
        public SharedListsRecyclerViewAdapter.SharedListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
            SharedListsViewHolder listsViewHolder = new SharedListsRecyclerViewAdapter.SharedListsViewHolder(view);

            return listsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SharedListsRecyclerViewAdapter.SharedListsViewHolder holder, int position) {
            if(myListsArray.size() != 0) {
                ShoppingList sharedList = myListsArray.get(position);
                holder.listNameLabel.setText(sharedList.getListName());
                holder.listCostLabel.setText(String.format("Total Cost: $%.2f", sharedList.getTotalCost()));
                holder.deleteButton.setClickable(false);
                holder.deleteButton.setVisibility(View.INVISIBLE);
                holder.listID = sharedList.getListID();
            }
        }

        @Override
        public int getItemCount() {
            return myListsArray.size();
        }

        class SharedListsViewHolder extends RecyclerView.ViewHolder {
            TextView listNameLabel, listCostLabel;
            ImageView deleteButton;
            String listID;

            public SharedListsViewHolder(@NonNull View itemView) {
                super(itemView);
                listNameLabel = itemView.findViewById(R.id.shoppingList_name);
                listCostLabel = itemView.findViewById(R.id.shoppingList_totalCost);
                deleteButton = itemView.findViewById(R.id.shoppingList_deleteButton);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("q/test", "getting details for: " + listID);
                        mListener.goToListDetails(listID);
                    }
                });
            }
        }
    }

    SharedListsFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (SharedListsFragmentListener) context;
    }

    interface SharedListsFragmentListener {
        void goToListDetails(String listID);
    }
}