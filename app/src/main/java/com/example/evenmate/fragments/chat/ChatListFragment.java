package com.example.evenmate.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.clients.ChatService;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.chat.Chat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchChats();
        return view;
    }

    private void fetchChats() {
        ClientUtils.chatService.getChats().enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatListAdapter = new ChatListAdapter(response.body(), chat -> {
                        Bundle args = new Bundle();
                        args.putLong("chatId", chat.getId());
                        NavHostFragment.findNavController(ChatListFragment.this)
                                .navigate(R.id.action_chatListFragment_to_chatFragment, args);
                    });
                    recyclerView.setAdapter(chatListAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable throwable) { }
        });
    }
}
