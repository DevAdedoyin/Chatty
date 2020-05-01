package com.example.chatty;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class contact_fragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;


    public contact_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contact_fragment, container, false);
        recyclerView = view.findViewById(R.id.contactRecyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerViewAdapter();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ContactInfo contactInfo1 = new ContactInfo("Adedoyin", "Brother", "Coding is Life!!!");
        ContactInfo contactInfo2 = new ContactInfo("Sileola", "Mother", "Jesus is lord");
        ContactInfo contactInfo3 = new ContactInfo("Paul", "GrandFather", "Gid is the greatest");
        ContactInfo contactInfo4 = new ContactInfo("Veronica", "GrandMother", "Jesu kristi oluwa wa");

        adapter.addContactInfo(contactInfo1);
        adapter.addContactInfo(contactInfo2);
        adapter.addContactInfo(contactInfo3);
        adapter.addContactInfo(contactInfo4);


        return view;
    }

}
