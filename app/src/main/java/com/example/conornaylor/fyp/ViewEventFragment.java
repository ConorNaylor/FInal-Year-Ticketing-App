package com.example.conornaylor.fyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventFragment extends Fragment {

    private Event event;
    private EditText nameText;
    private EditText descText;
    private EditText dateText;
    private EditText priceText;
    private EditText numTicksText;
    private EditText locText;
    private CheckBox cb;
    private boolean show;


    public ViewEventFragment() {
    }

    public static ViewEventFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        ViewEventFragment fragment = new ViewEventFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            event = (Event)bundle.getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());
        cb = getActivity().findViewById(R.id.vieweditFields);
        show = cb.isChecked();
        nameText = getActivity().findViewById(R.id.viewEventName);
        descText = getActivity().findViewById(R.id.viewEventDescription);
        dateText = getActivity().findViewById(R.id.viewEventDate);
        priceText = getActivity().findViewById(R.id.viewEventPrice);
        numTicksText = getActivity().findViewById(R.id.viewnumTickets);
        locText = getActivity().findViewById(R.id.viewEventLocation);

        nameText.setEnabled(show);
        descText.setEnabled(show);
        dateText.setEnabled(show);
        locText.setEnabled(show);
        priceText.setEnabled(show);
        numTicksText.setEnabled(show);

        nameText.setText(event.getTitle());
        descText.setText(event.getDescription());
        dateText.setText(event.getDate());
        locText.setText(event.getAddress());

        cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                show = cb.isChecked();
                nameText.setEnabled(show);
                descText.setEnabled(show);
                dateText.setEnabled(show);
                locText.setEnabled(show);
                priceText.setEnabled(show);
                numTicksText.setEnabled(show);
            }
        });
    }
}
