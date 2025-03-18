package com.example.evenmate.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InvitationsFragment extends Fragment {

    private InvitationsAdapter adapter;
    private List<String> fullContactList = new ArrayList<>();
    private List<String> currentContacts = new ArrayList<>();
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private Button nextPageButton, prevPageButton;
    private TextView pageNumber;

    public InvitationsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitations, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        nextPageButton = rootView.findViewById(R.id.next_page_button);
        prevPageButton = rootView.findViewById(R.id.prev_page_button);
        pageNumber = rootView.findViewById(R.id.page_number);

        fullContactList = getAllContactList();
        adapter = new InvitationsAdapter(currentContacts, this::removeContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateContacts(); // Load first page

        nextPageButton.setOnClickListener(v -> {
            if (currentPage < Math.ceil((double) fullContactList.size() / PAGE_SIZE)) {
                currentPage++;
                updateContacts();
            }
        });

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateContacts();
            }
        });

        return rootView;
    }

    private void updateContacts() {
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, fullContactList.size());

        currentContacts.clear();
        currentContacts.addAll(fullContactList.subList(start, end));

        adapter.notifyDataSetChanged();

        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(end < fullContactList.size());
        pageNumber.setText(currentPage + "/" + getTotalPages());
    }
    private int getTotalPages() {
        return (int) Math.ceil((double) fullContactList.size() / PAGE_SIZE);
    }
    public void removeContact(String contact) {
        fullContactList.remove(contact);
        updateContacts();
    }

    private List<String> getAllContactList() {
        List<String> contacts = new ArrayList<>();
        for (int i = 1; i <= 250; i++) {
            contacts.add("Contact " + i);
        }
        return contacts;
    }
}
