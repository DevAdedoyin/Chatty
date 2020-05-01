package com.example.chatty;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ContactInfo> contactInfos = new ArrayList<>();

    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_CHAT = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_CONTACT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_rv_item, parent, false);
            return new ContactViewHolder(view);
        }else if (viewType == TYPE_CHAT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_rv_item, parent, false);
            return new ChatViewHolder(view);
        } else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CONTACT){
            ((ContactViewHolder) holder).setContactDetails(contactInfos.get(position));
        } else if (getItemViewType(position) == TYPE_CHAT){
            ((ChatViewHolder) holder).setChatDetails(contactInfos.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return contactInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(contactInfos.get(position).getLm())){
         return TYPE_CONTACT;
        }else{
            return TYPE_CHAT;
        }
    }

    public void addContactInfo(ContactInfo contactInfo){
        contactInfos.add(contactInfo);
        notifyItemInserted(contactInfos.size() - 1);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView contactName, contactPersonalMessage, contactRelationship;

        public void setContactName(String contactName) {
            this.contactName.setText(contactName);
        }

        public void setContactPersonalMessage(String contactPersonalMessage) {
            this.contactPersonalMessage.setText(contactPersonalMessage);
        }

        public void setContactRelationship(String contactRelationship) {
            this.contactRelationship.setText(contactRelationship);
        }

        private void setContactDetails(ContactInfo contactInfo){
//            contactName.setText(contactInfo.getName());
//            contactRelationship.setText(contactInfo.getRelationship());
//            contactPersonalMessage.setText(contactInfo.getPm());

            this.setContactName(contactInfo.getName());
            this.setContactRelationship(contactInfo.getRelationship());
            this.setContactPersonalMessage(contactInfo.getPm());
        }

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.txtContactName);
            contactPersonalMessage = itemView.findViewById(R.id.personalMessage);
            contactRelationship = itemView.findViewById(R.id.relationshipStatusText);

        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView contactName, contactRelationship, contactLastMessage;

        public void setContactName(String contactName) {
            this.contactName.setText(contactName);
        }

        public void setContactRelationship(String contactRelationship) {
            this.contactRelationship.setText(contactRelationship);
        }

        public void setContactLastMessage(String contactLastMessage) {
            this.contactLastMessage.setText(contactLastMessage);
        }

        private void setChatDetails(ContactInfo chatContactInfo){
//            chatContactInfo.setName(chatContactInfo.getName());
//            chatContactInfo.setRelationship(chatContactInfo.getRelationship());
//            chatContactInfo.setLm(chatContactInfo.getLm());

            this.setContactName(chatContactInfo.getName());
            this.setContactRelationship(chatContactInfo.getRelationship());
            this.setContactLastMessage(chatContactInfo.getLm());

        }

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.txtContactNameChat);
            contactLastMessage = itemView.findViewById(R.id.lastMessageTextChat);
            contactRelationship = itemView.findViewById(R.id.relationshipStatusTextChat);

        }
    }
}