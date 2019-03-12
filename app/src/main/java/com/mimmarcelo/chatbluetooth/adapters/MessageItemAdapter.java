package com.mimmarcelo.chatbluetooth.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mimmarcelo.chatbluetooth.R;

import java.util.List;

public class MessageItemAdapter extends RecyclerView.Adapter<MessageItemHolder> {

    List<String> messages;

    public MessageItemAdapter(List<String> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageItemHolder onCreateViewHolder(@NonNull ViewGroup view, int i) {
        return new MessageItemHolder(LayoutInflater.from(view.getContext()).inflate(R.layout.item_message, view, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItemHolder holder, int i) {
        holder.setFields(messages.get(i));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
