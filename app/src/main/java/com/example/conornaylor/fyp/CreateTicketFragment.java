package com.example.conornaylor.fyp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTicketFragment extends Fragment {

    private Event event;
    private Button button;
    private TextView eventName;
    private TextView eventPrice;

    public CreateTicketFragment() {
        // Required empty public constructor
    }

    public static CreateTicketFragment newInstance(Event e) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", e);
        CreateTicketFragment fragment = new CreateTicketFragment();
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
        return inflater.inflate(R.layout.fragment_create_ticket, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        button = getActivity().findViewById(R.id.confirm_purchase);
        eventName = getActivity().findViewById(R.id.confirmEventName);
        eventPrice = getActivity().findViewById(R.id.confirmPurchasePrice);

        eventName.setText(event.getTitle());
        eventPrice.setText(event.getPrice().toString());
    }
}
