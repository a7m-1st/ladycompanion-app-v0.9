package com.example.AmjadPart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MainActivity.recyclerViews.messageRecyclerAdapter;
import com.example.ladyapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragment_chatBot extends Fragment {
    RecyclerView recyclerView;
    TextView chatBotWelcomeText;
    EditText messageEditText;
    ImageButton sendButton;
    public static ArrayList<ChatBotMessages> chatBotMessagesList = new ArrayList<>();
    public static messageRecyclerAdapter messageRecyclerAdapter;
    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_bot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatBotMessagesList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.chatBotRecycler);
        chatBotWelcomeText = view.findViewById(R.id.chatBotWelcomeText);
        messageEditText = view.findViewById(R.id.Msg_CB_EditText);
        sendButton = view.findViewById(R.id.send_Msg_BTN);

        // Setup recycler view
        messageRecyclerAdapter = new messageRecyclerAdapter(getActivity(), chatBotMessagesList);
        recyclerView.setAdapter(messageRecyclerAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            if (messageEditText.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            String message = messageEditText.getText().toString().trim();
            getResponse(message, ChatBotMessages.SENT_BY_USER);
            messageEditText.setText("");
            String inquiry = mergeResponses(chatBotMessagesList);
            callAPI(inquiry);
            chatBotWelcomeText.setVisibility(View.GONE);
        });
    }

    private void getResponse(String message, String sentBy){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add the new message to the list
                chatBotMessagesList.add(new ChatBotMessages(message, sentBy));
                // Notify the adapter that a new item has been inserted at the last position
                messageRecyclerAdapter.notifyDataSetChanged();
                // Scroll to the bottom of the RecyclerView
                recyclerView.smoothScrollToPosition(messageRecyclerAdapter.getItemCount());
            }
        });

    }

    void addResponse(String response){
        getResponse(response, ChatBotMessages.SENT_BY_BOT);
    }

    void callAPI(String question){
        //okhttp
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 2048);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("OnFailure \nFailed to get response as: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    addResponse("Failed to get response as: " + response.body().string());
                }
            }
        });


    }

    public String mergeResponses(ArrayList<ChatBotMessages> messages){
        String text = "";
        int index = 0;
        for (int i = messages.size()-1; i >= 0; i--){
            ChatBotMessages chatBotMessages = messages.get(i);
            if (("Role: " + chatBotMessages.getSentBy() + "\n" + chatBotMessages.getMessage() + "\n" + text).split("\\s+").length < 2048) {
                text = "Role: " + chatBotMessages.getSentBy() + "\n" + chatBotMessages.getMessage() + "\n" + text;
                index += 1;
            }
        }
        if (index <= 1) {
            return text;
        }
        else return text + "Role: bot\n";
    }
}
