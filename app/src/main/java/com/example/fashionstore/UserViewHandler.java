package com.example.fashionstore;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;

public class UserViewHandler {

    private static final int PICK_IMAGE_REQUEST = 1;

    public static void setup(View userView, String loggedInUserName, Activity activity) {
        loadUserData(userView, loggedInUserName);
        setupButtonListeners(userView, loggedInUserName, activity);
        setupBirthdayPicker(userView, activity);
        hideEditCard(userView); // Hide edit card initially
    }

    private static void loadUserData(View userView, String loggedInUserName) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(loggedInUserName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            displayUserData(userView, userSnapshot);
                            break;
                        }
                    } else {
                        Toast.makeText(userView.getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(userView.getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private static void displayUserData(View userView, DataSnapshot userSnapshot) {
        setText(userView, R.id.fullNameText, userSnapshot.child("fullName").getValue(String.class));
        setText(userView, R.id.genderText, userSnapshot.child("gender").getValue(String.class));
        setText(userView, R.id.birthdayText, userSnapshot.child("birthday").getValue(String.class));
        setText(userView, R.id.phoneText, userSnapshot.child("phone").getValue(String.class));
        setText(userView, R.id.emailText, userSnapshot.child("email").getValue(String.class));
        setText(userView, R.id.locationText, userSnapshot.child("location").getValue(String.class));
        setText(userView, R.id.usernameText, userSnapshot.child("userName").getValue(String.class));

        setEdit(userView, R.id.usernameEditText, userSnapshot.child("fullName").getValue(String.class));
        setEdit(userView, R.id.editEmail, userSnapshot.child("email").getValue(String.class));
        setEdit(userView, R.id.editBirthday, userSnapshot.child("birthday").getValue(String.class));
        setEdit(userView, R.id.editPhone, userSnapshot.child("phone").getValue(String.class));
        setEdit(userView, R.id.editLocation, userSnapshot.child("location").getValue(String.class));

        String gender = userSnapshot.child("gender").getValue(String.class);
        RadioGroup genderGroup = userView.findViewById(R.id.genderRadioGroup);
        if (gender != null && genderGroup != null) {
            if (gender.equalsIgnoreCase("Male")) genderGroup.check(R.id.maleRadio);
            else if (gender.equalsIgnoreCase("Female")) genderGroup.check(R.id.femaleRadio);
            else genderGroup.check(R.id.otherRadio);
        }

        String imageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);
        ImageView userImage = userView.findViewById(R.id.userImage);
        if (imageUrl != null && !imageUrl.isEmpty() && userImage != null) {
            Glide.with(userView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_profile_foreground)
                .into(userImage);
        }
    }

    private static void setupButtonListeners(View userView, String loggedInUserName, Activity activity) {
        // Upload image
        ImageButton uploadBtn = userView.findViewById(R.id.uploadUserImageBtn); // CORRECT TYPE
        if (uploadBtn != null) {
            uploadBtn.setOnClickListener(v -> openImagePicker(activity));
        }

        // Edit info button (show edit card)
        ImageButton editBtn = userView.findViewById(R.id.editInfoBtn); // CORRECT TYPE
        CardView editCard = findEditCard(userView);
        if (editBtn != null && editCard != null) {
            editBtn.setOnClickListener(v -> {
                editCard.setVisibility(View.VISIBLE);
                // Scroll to edit card
                View parent = (View) editCard.getParent();
                while (parent != null && !(parent instanceof ScrollView)) {
                    parent = (View) parent.getParent();
                }
                if (parent instanceof ScrollView) {
                    final ScrollView scrollView = (ScrollView) parent;
                    scrollView.post(() -> scrollView.smoothScrollTo(0, editCard.getTop()));
                }
            });
        }

        // Save changes
        Button updateProfileBtn = userView.findViewById(R.id.updateProfileBtn);
        if (updateProfileBtn != null) {
            updateProfileBtn.setOnClickListener(v -> {
                updateUserProfile(loggedInUserName, userView);
                hideEditCard(userView);
            });
        }

        // Change password
        Button changePasswordBtn = userView.findViewById(R.id.changePasswordBtn);
        if (changePasswordBtn != null) {
            changePasswordBtn.setOnClickListener(v -> changePassword(loggedInUserName, userView));
        }

        // Logout
        ImageView logoutIcon = userView.findViewById(R.id.logoutIcon);
        if (logoutIcon != null) {
            logoutIcon.setOnClickListener(v -> {
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            });
        }
    }

    private static void setupBirthdayPicker(View userView, Activity activity) {
        EditText birthdayEdit = userView.findViewById(R.id.editBirthday);
        if (birthdayEdit != null) {
            birthdayEdit.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(activity,
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                        birthdayEdit.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            });
        }
    }

    private static void openImagePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public static void handleImageResult(Activity activity, int requestCode, int resultCode,
                                         Intent data, String loggedInUserName, View userView) {
        if (activity == null || userView == null) return;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImageToCloudinary(activity, imageUri, loggedInUserName, userView);
            }
        }
    }

    private static void uploadImageToCloudinary(Activity activity, Uri imageUri,
                                                String loggedInUserName, View userView) {
        ImageView userImage = userView.findViewById(R.id.userImage);
        MediaManager.get().upload(imageUri)
            .callback(new UploadCallback() {
                @Override public void onStart(String requestId) {}
                @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                @Override
                public void onSuccess(String requestId, Map resultData) {
                    Object urlObj = resultData.get("secure_url");
                    if (urlObj != null) {
                        String imageUrl = urlObj.toString();
                        updateUserProfileImage(loggedInUserName, imageUrl, activity, userImage);
                    } else {
                        showToast(activity, "Image upload failed: No URL");
                    }
                }
                @Override
                public void onError(String requestId, ErrorInfo error) {
                    showToast(activity, "Upload error: " + error.getDescription());
                }
                @Override public void onReschedule(String requestId, ErrorInfo error) {}
            })
            .dispatch();
    }

    private static void updateUserProfileImage(String loggedInUserName, String imageUrl,
                                               Activity activity, ImageView userImage) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(loggedInUserName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userSnapshot.getRef().child("profileImageUrl").setValue(imageUrl)
                            .addOnSuccessListener(aVoid -> updateUIWithNewImage(activity, userImage, imageUrl))
                            .addOnFailureListener(e -> showToast(activity, "Failed to update profile image"));
                        break;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast(activity, "Database error: " + databaseError.getMessage());
                }
            });
    }

    private static void updateUIWithNewImage(Activity activity, ImageView userImage, String imageUrl) {
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            activity.runOnUiThread(() -> {
                Glide.with(activity).load(imageUrl).into(userImage);
                showToast(activity, "Profile image updated successfully");
            });
        }
    }

    private static void updateUserProfile(String loggedInUserName, View userView) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(loggedInUserName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("fullName", getEdit(userView, R.id.usernameEditText));
                            updates.put("email", getEdit(userView, R.id.editEmail));
                            updates.put("birthday", getEdit(userView, R.id.editBirthday));
                            updates.put("phone", getEdit(userView, R.id.editPhone));
                            updates.put("location", getEdit(userView, R.id.editLocation));
                            updates.put("userName", getText(userView, R.id.usernameText));
                            RadioGroup genderGroup = userView.findViewById(R.id.genderRadioGroup);
                            int checkedId = genderGroup != null ? genderGroup.getCheckedRadioButtonId() : -1;
                            String gender = "";
                            if (checkedId == R.id.maleRadio) gender = "Male";
                            else if (checkedId == R.id.femaleRadio) gender = "Female";
                            else if (checkedId == R.id.otherRadio) gender = "Other";
                            updates.put("gender", gender);

                            userSnapshot.getRef().updateChildren(updates)
                                .addOnSuccessListener(aVoid -> {
                                    showToast(userView.getContext(), "Profile updated successfully");
                                    loadUserData(userView, getText(userView, R.id.usernameText));
                                })
                                .addOnFailureListener(e -> showToast(userView.getContext(), "Update failed: " + e.getMessage()));
                            break;
                        }
                    } else {
                        showToast(userView.getContext(), "User not found");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast(userView.getContext(), "Database error: " + databaseError.getMessage());
                }
            });
    }

    private static void changePassword(String loggedInUserName, View userView) {
        EditText currentPassword = userView.findViewById(R.id.currentPassword);
        EditText newPassword = userView.findViewById(R.id.passwordEditText);
        EditText confirmPassword = userView.findViewById(R.id.confirmPassword);

        String current = currentPassword != null ? currentPassword.getText().toString().trim() : "";
        String newPass = newPassword != null ? newPassword.getText().toString().trim() : "";
        String confirm = confirmPassword != null ? confirmPassword.getText().toString().trim() : "";

        if (newPass.isEmpty() || confirm.isEmpty()) {
            showToast(userView.getContext(), "Enter new password and confirm");
            return;
        }
        if (!newPass.equals(confirm)) {
            showToast(userView.getContext(), "Passwords do not match");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("userName").equalTo(loggedInUserName)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);
                        if (dbPassword != null && dbPassword.equals(current)) {
                            userSnapshot.getRef().child("password").setValue(newPass)
                                .addOnSuccessListener(aVoid -> showToast(userView.getContext(), "Password changed"))
                                .addOnFailureListener(e -> showToast(userView.getContext(), "Failed: " + e.getMessage()));
                        } else {
                            showToast(userView.getContext(), "Current password incorrect");
                        }
                        break;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast(userView.getContext(), "Database error: " + databaseError.getMessage());
                }
            });
    }

    // Utility methods
    private static void setText(View v, int id, String value) {
        TextView tv = v.findViewById(id);
        if (tv != null && value != null) tv.setText(value);
    }
    private static void setEdit(View v, int id, String value) {
        EditText et = v.findViewById(id);
        if (et != null && value != null) et.setText(value);
    }
    private static String getEdit(View v, int id) {
        EditText et = v.findViewById(id);
        return et != null ? et.getText().toString().trim() : "";
    }
    private static String getText(View v, int id) {
        TextView tv = v.findViewById(id);
        return tv != null ? tv.getText().toString().trim() : "";
    }
    private static void showToast(android.content.Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Find the edit card containing updateProfileBtn
    private static CardView findEditCard(View userView) {
        Button updateProfileBtn = userView.findViewById(R.id.updateProfileBtn);
        if (updateProfileBtn != null) {
            View parent = (View) updateProfileBtn.getParent();
            while (parent != null && !(parent instanceof CardView)) {
                parent = (View) parent.getParent();
            }
            return (CardView) parent;
        }
        return null;
    }

    // Hide the edit card initially
    private static void hideEditCard(View userView) {
        CardView editCard = findEditCard(userView);
        if (editCard != null) {
            editCard.setVisibility(View.GONE);
        }
    }
}