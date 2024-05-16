package com.example.NicoPart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ladyapp.R;

import java.util.List;

public class SelectedContactsAdapter extends RecyclerView.Adapter<SelectedContactsAdapter.ViewHolder> {
    private List<Contact> selectedContacts;

    public SelectedContactsAdapter(List<Contact> contacts) {
        this.selectedContacts = contacts;
    }

    private ContactChangeCallback callback;

    public interface ContactChangeCallback {
        void onContactListChanged(List<Contact> updatedList);
    }

    public void setContactChangeCallback(ContactChangeCallback callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chosen_contacts_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = selectedContacts.get(position);
        holder.contactName.setText(contact.getName());

        holder.iconRemoveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    selectedContacts.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, selectedContacts.size());

                    if (callback != null) {
                        callback.onContactListChanged(selectedContacts);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public ImageView iconRemoveContact;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.chosenContactName);
            iconRemoveContact = itemView.findViewById(R.id.iconRemoveContact);
        }
    }

    public void updateContacts(List<Contact> newContacts) {
        selectedContacts.clear();
        selectedContacts.addAll(newContacts);
        notifyDataSetChanged();
    }
}