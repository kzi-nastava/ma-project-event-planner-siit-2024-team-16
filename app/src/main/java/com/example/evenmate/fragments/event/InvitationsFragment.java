package com.example.evenmate.fragments.event;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private final List<String> currentContacts = new ArrayList<>();
    private int currentPage = 1;
    private Button nextPageButton,prevPageButton;
    private TextView pageNumber;

    public InvitationsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitations, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        nextPageButton = rootView.findViewById(R.id.next_page_button);
        prevPageButton = rootView.findViewById(R.id.prev_page_button);
        pageNumber = rootView.findViewById(R.id.page_number);
        Button addContactButton = rootView.findViewById(R.id.add_contact_button);

        fullContactList = getAllContactList();// from BE
        adapter = new InvitationsAdapter(currentContacts, this::removeContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        updateContacts();// first page

        // buttons
        nextPageButton.setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
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
        addContactButton.setOnClickListener(v -> showAddContactDialog());

        return rootView;
    }

    private void updateContacts() {
        int start = (currentPage - 1) * 10;
        int end = Math.min(start + 10, fullContactList.size());

        currentContacts.clear();
        currentContacts.addAll(fullContactList.subList(start, end));

        adapter.notifyDataSetChanged();

        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(end < fullContactList.size());
        pageNumber.setText(String.format("%s/%s", currentPage, getTotalPages()));
    }

    private int getTotalPages() {return (int) Math.ceil((double) fullContactList.size() / 10);}

    public void removeContact(String contact) {
        fullContactList.remove(contact);
        updateContacts();
    }
    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Contact");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_invite, null);
        EditText input = viewInflated.findViewById(R.id.new_contact_input);
        builder.setView(viewInflated);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newContact = input.getText().toString().trim();
            if (!newContact.isEmpty()) {
                // talk to BE
                fullContactList.add(newContact);
                updateContacts();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    public static List<String> getAllContactList() {
        List<String> contacts = new ArrayList<>();
        for (int i = 1; i <= 250; i++) {
            contacts.add("Contact " + i);
        }
        return contacts;
    }

}
