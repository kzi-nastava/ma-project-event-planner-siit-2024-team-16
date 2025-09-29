package com.example.evenmate.clients;

import com.example.evenmate.models.chat.Chat;
import com.example.evenmate.models.chat.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {
    @GET("chat")
    Call<List<Chat>> getChats();

    @GET("chat/{chatId}/messages")
    Call<List<Message>> getMessages(@Path("chatId") Long chatId);

    @POST("chat/initiate")
    Call<Chat> initiateChat(@Query("userId") Long userId);
}
