package com.example.fashionstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
    private final Context context;
    private List<User> users;

    public AdminUserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(context.getString(R.string.username_format, user.getUserName()));
        holder.userEmail.setText(user.getEmail());
        holder.manageButton.setOnClickListener(v -> showManageUserDialog(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> userList) {
        users.addAll(userList);
        notifyDataSetChanged();
    }

    private void showManageUserDialog(User user) {
        // Implementation for managing user
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        Button manageButton;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userNameText);
            userEmail = itemView.findViewById(R.id.userEmailText);
            manageButton = itemView.findViewById(R.id.manageUserButton);
        }
    }
}
