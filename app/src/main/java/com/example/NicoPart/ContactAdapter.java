package com.example.NicoPart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ladyapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final List<Contact> contacts;
    private final List<Contact> allContacts;
    private int selectedCount = 0;

    // Constructor
    public ContactAdapter(List<Contact> allContacts, List<Contact> savedSelectedContacts) {
        this.allContacts = new ArrayList<>(allContacts);
        this.contacts = new ArrayList<>(allContacts);

        for (Contact contact : this.contacts) {
            if (savedSelectedContacts.contains(contact)) {
                contact.setSelected(true);
                selectedCount++;
            }
        }
    }

    private OnContactSelectionChangedListener selectionListener;

    public void setOnContactSelectionChangedListener(OnContactSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    public interface OnContactSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.contactNumber.setText(contact.getPhoneNumber());
        holder.radioButton.setChecked(contact.isSelected());

        holder.radioButton.setOnCheckedChangeListener(null);

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedCount < 10) {
                    contact.setSelected(true);
                    selectedCount++;
                } else {
                    // Prevent selection if limit is reached and inform the user
                    holder.radioButton.setChecked(false);
                    Toast.makeText(holder.itemView.getContext(), "Maximum of 10 contacts can be selected", Toast.LENGTH_SHORT).show();
                    return; // Early return to skip the rest of the listener
                }
            } else {
                contact.setSelected(false);
                selectedCount--;
            }

            // Notify the selection change
            if (selectionListener != null) {
                selectionListener.onSelectionChanged(selectedCount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView contactNumber;
        public RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            radioButton = itemView.findViewById(R.id.radioButtonContacts);
        }
    }

    // Add method to get selected contacts
    public List<Contact> getSelectedContacts() {
        List<Contact> selectedContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.isSelected()) {
                selectedContacts.add(contact);
            }
        }
        return selectedContacts;
    }

    // Method to filter contacts based on search text
    public void filterContacts(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        List<Contact> filteredList = new ArrayList<>();

        if (searchText.isEmpty()) {
            filteredList.addAll(allContacts);
        } else {
            for (Contact contact : allContacts) {
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    filteredList.add(contact);
                }
            }
        }

        applyAndAnimateRemovals(filteredList);
        applyAndAnimateAdditions(filteredList);
        applyAndAnimateMovedItems(filteredList);
    }

    // Additional methods for animations
    private void applyAndAnimateRemovals(List<Contact> newContacts) {
        for (int i = contacts.size() - 1; i >= 0; i--) {
            final Contact contact = contacts.get(i);
            if (!newContacts.contains(contact)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Contact> newContacts) {
        for (int i = 0, count = newContacts.size(); i < count; i++) {
            final Contact contact = newContacts.get(i);
            if (!contacts.contains(contact)) {
                addItem(i, contact);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Contact> newContacts) {
        for (int toPosition = newContacts.size() - 1; toPosition >= 0; toPosition--) {
            final Contact contact = newContacts.get(toPosition);
            final int fromPosition = contacts.indexOf(contact);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private Contact removeItem(int position) {
        final Contact contact = contacts.remove(position);
        notifyItemRemoved(position);
        return contact;
    }

    private void addItem(int position, Contact contact) {
        contacts.add(position, contact);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Contact contact = contacts.remove(fromPosition);
        contacts.add(toPosition, contact);
        notifyItemMoved(fromPosition, toPosition);
    }
}

