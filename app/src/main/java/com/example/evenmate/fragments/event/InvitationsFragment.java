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
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.event.InvitationRequest;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationsFragment extends Fragment {

    private InvitationsAdapter adapter;
    private final List<String> currentContacts = new ArrayList<>();
    private int currentPage = 1;
    private Button nextPageButton,prevPageButton;
    private TextView pageNumber;
    private Event event;
    private Integer allContactsSize;
    public InvitationsFragment(Event event) {
        this.event = event;
        this.allContactsSize=event.getInvited()!=null?event.getInvited().size():0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_invitations, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        nextPageButton = rootView.findViewById(R.id.next_page_button);
        prevPageButton = rootView.findViewById(R.id.prev_page_button);
        pageNumber = rootView.findViewById(R.id.page_number);
        Button addContactButton = rootView.findViewById(R.id.add_contact_button);

        adapter = new InvitationsAdapter(this.event, currentContacts,contact -> {
            ClientUtils.eventService.uninviteUser(event.getId(), new InvitationRequest(contact))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            event.getInvited().remove(contact);
                            allContactsSize--;
                            updateContacts();
                            Toast.makeText(getContext(), "Removed: " + contact, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to remove: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        } );
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
        int end = Math.min(start + 10, allContactsSize);

        currentContacts.clear();
        if (event.getInvited()!=null){
            currentContacts.addAll(event.getInvited().subList(start, end));
        }

        adapter.notifyDataSetChanged();

        prevPageButton.setEnabled(currentPage > 1);
        nextPageButton.setEnabled(end < allContactsSize);
        pageNumber.setText(String.format("%s/%s", currentPage, getTotalPages()));
    }

    private int getTotalPages() {return (int) Math.ceil((double)  allContactsSize / 10);}

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Contact");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_invite, null);
        EditText input = viewInflated.findViewById(R.id.new_contact_input);
        builder.setView(viewInflated);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newContact = input.getText().toString().trim();
            if (!newContact.isEmpty()) {
                ClientUtils.eventService.inviteUser(event.getId(), new InvitationRequest(newContact))
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                response.body();
                                if (response.isSuccessful() || response.code() == 206 || response.code() == 409) { // if he was already invited to another event (qr)(206)
                                    event.getInvited().add(newContact);
                                    allContactsSize++;
                                    updateContacts();
                                } else {
                                    Toast.makeText(getContext(), "Failed to add invitation", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
