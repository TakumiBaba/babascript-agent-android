package org.babascript.android.agent.views;



import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.babascript.android.agent.OnFragmentInteractionListener;
import org.babascript.android.agent.R;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link LoginView#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginView extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;
    private EditText loginIdField;
    private EditText loginPassField;
    private Button loginButton;
    private Application app;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginView.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginView newInstance() {
        LoginView fragment = new LoginView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public LoginView() {
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
        View v = inflater.inflate(R.layout.fragment_login_view, container, false);
        loginIdField =   (EditText) v.findViewById(R.id.loginIdField);
        loginPassField = (EditText) v.findViewById(R.id.loginPassField);
        loginButton = (Button) v.findViewById(R.id.loginSubmitButton);
        loginButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginSubmitButton){
            String id = loginIdField.getText().toString();
            String pass = loginPassField.getText().toString();
            mListener.onLoginFragmentInteraction(id, pass);
        }
    }
}
