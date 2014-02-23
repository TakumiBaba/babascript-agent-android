package org.babascript.android.agent.views;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.babascript.android.agent.OnFragmentInteractionListener;
import org.babascript.android.agent.R;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link IntFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class IntFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText mInputView;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IntFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IntFragment newInstance(JSONObject object) {
        IntFragment fragment = new IntFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, object.toString());
        fragment.setArguments(args);
        return fragment;
    }
    public IntFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JSONObject tuple = null;
        String methodname = "";
        String description = "";
        try {
            tuple = new JSONObject(mParam1);
            methodname = tuple.getString("key");
            description = tuple.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View v = inflater.inflate(R.layout.fragment_int, container, false);
        Button sendButton = (Button) v.findViewById(R.id.return_value_int_button);
        sendButton.setOnClickListener(this);
        mInputView = (EditText) v.findViewById(R.id.return_value_int_input);
        TextView methodNameView = (TextView) v.findViewById(R.id.method_name);
        methodNameView.setText(methodname);
        TextView descriptionTextView = (TextView) v.findViewById(R.id.description);
        descriptionTextView.setText(description);
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
    public void onClick(View v) {
        if(v.getId() == R.id.return_value_int_button){
            String value = mInputView.getText().toString();
            mListener.onFragmentInteraction(value);
        }
    }

}
