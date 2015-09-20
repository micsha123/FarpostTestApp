package com.gmail.micsha123.farposttestapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.micsha123.farposttestapp.R;
import com.gmail.micsha123.farposttestapp.activity.MainActivity;
import com.gmail.micsha123.farposttestapp.adapter.RecyclerItemClickListener;
import com.gmail.micsha123.farposttestapp.adapter.RecyclerViewAdapter;
import com.gmail.micsha123.farposttestapp.data.Links;
import com.gmail.micsha123.farposttestapp.service.LoadResultReceiver;
import com.gmail.micsha123.farposttestapp.service.LoadService;

import java.util.ArrayList;

public class MainFragment extends Fragment implements LoadResultReceiver.Receiver {

    /** EditText for entering link*/
    private EditText linkEditText;
    /** Button for extracting links */
    private Button getLinksButton;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    /** Requests array for showing */
    private ArrayList<String> links = new ArrayList<String>();
    /** receiver for LoadService */
    private LoadResultReceiver resultReceiver;

    /** keys fr EditText, Receiver and Progressbar for saving and restoring states */
    private final String RECEIVER_KEY = "receiver";
    private final String EDITTEXT_KEY = "edittext";
    private final String PROGRESSBAR_KEY = "progressbar";

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        links = Links.getInstance(getActivity()).getLinks();

        /** loading last inserted text in edittext*/
        linkEditText = (EditText) rootView.findViewById(R.id.link_edit_text);
        String savedLink = preferences.getString(EDITTEXT_KEY, null);
        linkEditText.setText(savedLink);

        getLinksButton = (Button) rootView.findViewById(R.id.get_links_button);
        if (Patterns.WEB_URL.matcher(linkEditText.getText().toString().toLowerCase()).matches()) {
            getLinksButton.setEnabled(true);
        } else {
            getLinksButton.setEnabled(false);
        }

        /** loading instancestate after rotation, configuration changes and so on */
        if (savedInstanceState != null) {
            resultReceiver = savedInstanceState.getParcelable(RECEIVER_KEY);
            linkEditText.setText(savedInstanceState.getString(EDITTEXT_KEY));
            ((MainActivity)getActivity()).showProgressBar(savedInstanceState.getBoolean(PROGRESSBAR_KEY));
            getLinksButton.setEnabled(!savedInstanceState.getBoolean(PROGRESSBAR_KEY));
        } else {
            resultReceiver = new LoadResultReceiver(new Handler());
        }
        resultReceiver.setReceiver(this);

        /** Here checking links in edit text and enabling button */
        linkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!((MainActivity) getActivity()).isProgressbarVisible() &&
                        Patterns.WEB_URL.matcher(linkEditText.getText().toString().toLowerCase()).matches()) {
                    getLinksButton.setEnabled(true);
                } else {
                    getLinksButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getLinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showProgressBar(true);
                getLinksButton.setEnabled(false);
                loadDataFromServer(String.valueOf(linkEditText.getText()).toLowerCase());
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        linkEditText.setText(links.get(position));
                    }
                })
        );
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RECEIVER_KEY, resultReceiver);
        outState.putBoolean(PROGRESSBAR_KEY, ((MainActivity) getActivity()).isProgressbarVisible());
        outState.putString(EDITTEXT_KEY, linkEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void loadDataToRecyclerView(){
        links = Links.getInstance(getActivity()).getLinks();
        adapter.notifyDataSetChanged();
    }

    /** Method create intent for LoadService and start it*/
    private void loadDataFromServer(String url){
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), LoadService.class);

        intent.putExtra("url", url);
        intent.putExtra("receiver", resultReceiver);
        intent.putExtra("requestId", 101);

        getActivity().startService(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EDITTEXT_KEY, linkEditText.getText().toString());
        editor.apply();
    }
    /** Method provides loading data from DB to requests array */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case LoadService.STATUS_RUNNING:
                break;
            case LoadService.STATUS_FINISHED:
                ((MainActivity) getActivity()).showProgressBar(false);
                getLinksButton.setEnabled(true);
                loadDataToRecyclerView();
                break;
            case LoadService.STATUS_ERROR:
                ((MainActivity)getActivity()).showProgressBar(false);
                getLinksButton.setEnabled(true);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
