package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Pflug_HW07 LoginFragment.java
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    EditText emailTextbox, passwordTextbox;
    Button loginButton, createAccountButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.login_fragment_title);

        mAuth = FirebaseAuth.getInstance();

        emailTextbox = view.findViewById(R.id.addItem_itemNameTextbox);
        passwordTextbox = view.findViewById(R.id.addItem_itemPriceTextbox);
        loginButton = view.findViewById(R.id.addItem_submitButton);
        createAccountButton = view.findViewById(R.id.addItem_cancelButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailTextbox.getText().toString().equals("")) {
                    //alertdialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid email to log in")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    if(passwordTextbox.getText().toString().equals("")) {
                        //alertdialog
                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                        b.setTitle("Error")
                                .setMessage("Please enter a valid password to log in")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        b.create().show();
                    } else {
                        String userEmail = emailTextbox.getText().toString();
                        String userPassword = passwordTextbox.getText().toString();
                        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            mListener.goToProfileFragment();
                                        } else {
                                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                            b.setTitle("Error During Login")
                                                    .setMessage(task.getException().getMessage())
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                        }
                                                    });
                                            b.create().show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToCreateAccountFragment();
            }
        });
    }

    LoginFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginFragmentListener) context;
    }

    interface LoginFragmentListener {
        void goToProfileFragment();
        void goToCreateAccountFragment();
    }
}