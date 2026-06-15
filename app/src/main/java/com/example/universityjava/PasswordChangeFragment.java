package com.example.universityjava;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordChangeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordChangeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordChangeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordChangeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordChangeFragment newInstance(String param1, String param2) {
        PasswordChangeFragment fragment = new PasswordChangeFragment();
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
        View view = inflater.inflate(R.layout.fragment_password_change, container, false);
        EditText editTextOldPass = view.findViewById(R.id.editTextOldPass);
        EditText editTextNewPass = view.findViewById(R.id.editTextNewPass);
        EditText editTextNewPass2 = view.findViewById(R.id.editTextNewPassRepeat);



        AppDatabase db = AppActivity.getDatabase();
        SharedPreferences prefs = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        long userID = prefs.getLong("user_id", -1);
        User user = db.userDAO().getUserByID(userID);

        Animator anim1 = AnimatorInflater.loadAnimator(getActivity(), R.animator.button_click_failed);
        Animator anim2 = AnimatorInflater.loadAnimator(getActivity(), R.animator.text_field_jump);
        Animator anim3 = AnimatorInflater.loadAnimator(getActivity(), R.animator.text_field_jump);
        anim1.setTarget(editTextOldPass);
        anim2.setTarget(editTextNewPass);
        anim3.setTarget(editTextNewPass2);

        Button buttonConfirm = view.findViewById(R.id.buttonChangePassConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = editTextOldPass.getText().toString().trim();
                String newPass = editTextNewPass.getText().toString().trim();
                String newPass2 = editTextNewPass2.getText().toString().trim();
                String message;
                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(newPass2)) {
                    message = "All fields must be filled";
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                }
                else if(!TextUtils.equals(oldPass, user.getPassword())) {
                    message = "Incorrect password";
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                    editTextOldPass.setError(message);
                    anim1.start();
                }
                else if(newPass.length() < 4) {
                    message = "Password must be at least 4 characters";
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                    editTextNewPass.setError(message);
                    anim2.start();
                }
                else if (!TextUtils.equals(newPass, newPass2)) {
                    message = "Repeated password does not match";
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                    editTextNewPass2.setError(message);
                    anim2.start();
                }
                else {
                    String hash = "";
                    try {
                        hash = PasswordHashing.hashPassword(newPass);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    db.userDAO().updatePassword(userID, newPass);
//                    Toast.makeText(getApplicationContext(), R.string.toast_registration_successful, Toast.LENGTH_SHORT).show();

                    ((MainActivity)getActivity()).replaceFragment(new ProfileFragment());
                }
            }
        });
        return view;
    }
}