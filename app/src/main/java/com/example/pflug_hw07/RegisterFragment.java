package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Pflug_HW07 RegisterFragment.java
 * Full Name: Kristin Pflug
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore database;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    EditText newUserNameText, newUserEmailText, newUserPasswordText;
    Button registerSubmit, registerCancel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.register_fragment_title);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        newUserNameText = view.findViewById(R.id.register_nameTextbox);
        newUserEmailText = view.findViewById(R.id.register_emailTextbox);
        newUserPasswordText = view.findViewById(R.id.register_passwordTextbox);

        registerSubmit = view.findViewById(R.id.register_submitButton);
        registerCancel = view.findViewById(R.id.register_cancelButton);

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUserName = newUserNameText.getText().toString();
                String newUserEmail = newUserEmailText.getText().toString();
                String newUserPassword = newUserPasswordText.getText().toString();

                if(newUserName.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid username")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else if (newUserEmail.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid email address")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else if (newUserPassword.isEmpty()) {
                    //alert dialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid password")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    mAuth.createUserWithEmailAndPassword(newUserEmail, newUserPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(newUserName)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "New profile created!", Toast.LENGTH_SHORT).show();
                                                            User newUser = new User(newUserName, user.getUid());
                                                            database.collection("users")
                                                                    .add(newUser)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Log.d("q/test", "adding user: " + newUser.getUserName() + ", " +
                                                                                    newUser.getUserID());
                                                                        }
                                                                    });
                                                            mListener.goToProfileFragment();
                                                        } else {
                                                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                                            b.setTitle("Error")
                                                                    .setMessage(task.getException().getMessage())
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                        }
                                                                    });
                                                            b.create().show();
                                                            task.getException().printStackTrace();
                                                        }
                                                    }
                                                });
                                    } else {
                                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                        b.setTitle("Error")
                                                .setMessage(task.getException().getMessage())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                        b.create().show();
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
                }
            }
        });

        registerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelToLogin();
            }
        });
    }

    RegisterFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (RegisterFragmentListener) context;
    }

    interface RegisterFragmentListener {
        void goToProfileFragment();
        void cancelToLogin();
    }
}