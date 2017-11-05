package com.example.conornaylor.fyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewEventFragment extends Fragment {

    private Event event;
    private TextView text;

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
        text = getActivity().findViewById(R.id.eventNameView);
        text.setText(event.toString());
    }
}
