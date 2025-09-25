package com.example.evenmate.clients;

import com.example.evenmate.models.chat.Chat;
import com.example.evenmate.models.chat.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {
    @GET("chat")
    Call<List<Chat>> getChats();

    @GET("chat/{chatId}/messages")
    Call<List<Message>> getMessages(@Path("chatId") Long chatId);
}
