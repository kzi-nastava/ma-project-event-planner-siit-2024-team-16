package com.example.evenmate.fragments.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.auth.AuthManager;
import com.example.evenmate.clients.ClientUtils;
import com.example.evenmate.models.chat.Message;
import com.example.evenmate.models.chat.SendMessageRequest;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class ChatFragment extends Fragment {
    private StompClient stompClient;
    private WebSocket webSocket;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private EditText editMessage;
    private Button buttonSend;
    private Long chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_messages);
        editMessage = view.findViewById(R.id.edit_message);
        buttonSend = view.findViewById(R.id.button_send);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatId = getArguments() != null ? getArguments().getLong("chatId") : 0;
        fetchMessages();
        connectWebSocket();
        buttonSend.setOnClickListener(v -> sendMessage());
        return view;
    }

    private void fetchMessages() {
        ClientUtils.chatService.getMessages(chatId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter = new MessageAdapter(response.body());
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("CHAT_FETCH_ERROR", "Error fetching messages: " + t.getMessage());
            }
        });
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (!text.isEmpty() && stompClient != null && stompClient.isConnected()) {
            SendMessageRequest messageRequest = new SendMessageRequest(AuthManager.loggedInUser.getId(), chatId, text);
            stompClient.send("/app/chat.sendMessage", new Gson().toJson(messageRequest)).subscribe();
            editMessage.setText("");
        }
    }

    private void connectWebSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + ClientUtils.BASE_URL + "/ws");
        stompClient.connect();

//        Disposable d = stompClient.lifecycle()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(lifecycleEvent -> {
//                    Log.d("STOMP_LIFECYCLE", "Event: " + lifecycleEvent.getType());
//                    if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED) {
//                        Log.d("STOMP_LIFECYCLE", "Connected!");
////                        buttonSend.setEnabled(true); // Optional: enable send button here
//                    }
//                    if (lifecycleEvent.getType() == LifecycleEvent.Type.ERROR) {
//                        Log.e("STOMP_LIFECYCLE", "Connection error", lifecycleEvent.getException());
//                    }
//                });

        Disposable disposable = stompClient.lifecycle()
                .filter(lifecycleEvent -> lifecycleEvent.getType() == LifecycleEvent.Type.OPENED)
                .firstElement()
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    Disposable disposable2 = stompClient.topic("/topic/chat/" + chatId + "/messages").subscribe(topicMessage -> {
                        Message message = new Gson().fromJson(topicMessage.getPayload(), Message.class);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (messageAdapter != null) {
                                    messageAdapter.addMessage(message);
                                    recyclerView.scrollToPosition(messageAdapter.getItemCount());
                                }
                            });
                        }
                    },throwable -> {;
                        Log.e("STOMP_ERROR", "Error on subscribe topic: " + throwable.getMessage());
                    });
                });
    }
}
