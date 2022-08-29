package com.example.pflug_hw07;

/**
 * Assignment #: HW07
 * File Name: Group25_HW07 MainActivity.java
 * Full Name: Kristin Pflug
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener, ProfileFragment.ProfileFragmentListener, MyListsFragment.MyListsFragmentListener, CreateListFragment.CreateListFragmentListener, SharedListFragment.SharedListsFragmentListener, ListDetailsFragment.ListDetailsFragmentListener, AddItemFragment.AddItemFragmentListener, InviteNewUserFragment.InviteNewUserFragmentListener, InvitedUsersFragment.InvitedUsersFragmentListener {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            getSupportFragmentManager().beginTransaction().add(R.id.rootView, new LoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ProfileFragment()).commit();
        }
    }

    @Override
    public void goToProfileFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ProfileFragment()).commit();
    }

    @Override
    public void cancelToLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void goToCreateAccountFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new RegisterFragment()).commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void goToMyLists() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new MyListsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void goToSharedLists() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new SharedListFragment()).addToBackStack(null).commit();
    }

    @Override
    public void goToFriendsList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new FriendsListFragment()).addToBackStack(null).commit();
    }

    @Override
    public void goToCreateList() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateListFragment()).addToBackStack(null).commit();
    }

    @Override
    public void goToListDetails(String listID) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, ListDetailsFragment.newInstance(listID)).addToBackStack(null).commit();
    }

    @Override
    public void submitNewList() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void submitNewItem() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void cancelToMyLists() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToManageUsers(String listID) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, InvitedUsersFragment.newInstance(listID)).addToBackStack(null).commit();
    }

    @Override
    public void goToAddItems(String listID) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, AddItemFragment.newInstance(listID)).addToBackStack(null).commit();
    }

    @Override
    public void addNewInvitedUser() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToInviteUsers(String listID) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, InviteNewUserFragment.newInstance(listID)).addToBackStack(null).commit();
    }
}