package com.example.chatty;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class chat_fragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;
    private Context context;

    public chat_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        recyclerView = view.findViewById(R.id.chatRecyclerView);

        recyclerViewAdapter = new RecyclerViewAdapter();
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        ContactInfo contactInfo1 = new ContactInfo("Emma", "Uncle", "init?", true);
        ContactInfo contactInfo2 = new ContactInfo("Rebecca", "Aunt", "I love my Kids", false);
        ContactInfo contactInfo3 = new ContactInfo("Martha", "Aunt", "God bless this family", true);
        ContactInfo contactInfo4 = new ContactInfo("Mary", "Aunt", "God is good", false);

        recyclerViewAdapter.addContactInfo(contactInfo1);
        recyclerViewAdapter.addContactInfo(contactInfo2);
        recyclerViewAdapter.addContactInfo(contactInfo3);
        recyclerViewAdapter.addContactInfo(contactInfo4);

        return view;
    }
}
