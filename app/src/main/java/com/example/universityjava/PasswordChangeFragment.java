package com.example.universityjava;

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

        Button buttonConfirm = view.findViewById(R.id.buttonChangePassConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = editTextOldPass.getText().toString().trim();
                String newPass = editTextNewPass.getText().toString().trim();
                String newPass2 = editTextNewPass2.getText().toString().trim();
                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(newPass2)) {
                    Toast.makeText(view.getContext(), "Visi laukai turi buti uzpildyti", Toast.LENGTH_SHORT).show();
                }
                else if (!TextUtils.equals(newPass, newPass2)) {
//                    Toast.makeText(view.getContext(), "Pakartotas slaptazodis nesutampa", Toast.LENGTH_SHORT).show();
                    Toast.makeText(view.getContext(), newPass + " " + newPass2, Toast.LENGTH_SHORT).show();
                }
                else {
                    String hash = "";
                    try {
                        hash = PasswordHashing.hashPassword(newPass);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    db.userDAO().updatePassword(userID, hash);
//                    Toast.makeText(getApplicationContext(), R.string.toast_registration_successful, Toast.LENGTH_SHORT).show();

                    ((MainActivity)getActivity()).replaceFragment(new ProfileFragment());
                }
            }
        });
        return view;
    }
}