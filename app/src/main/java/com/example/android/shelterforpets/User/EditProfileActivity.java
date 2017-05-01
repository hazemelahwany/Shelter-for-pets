package com.example.android.shelterforpets.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.shelterforpets.DatabaseObjects.User;
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent i = getIntent();
        final String userID = i.getStringExtra("userID");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final DatabaseReference userDatabase = firebaseDatabase.getReference().child("Users").child(userID);
        final StorageReference profilePics = storage.getReference().child("users");

        final ImageView profilePic = (ImageView) findViewById(R.id.edit_profile_pic);
        final EditText firstName = (EditText) findViewById(R.id.edit_profile_first_name);
        final EditText lastName = (EditText) findViewById(R.id.edit_profile_last_name);
        ImageButton save = (ImageButton) findViewById(R.id.edit_profile_save_button);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstName.setText(dataSnapshot.child("firstName").getValue(String.class));
                lastName.setText(dataSnapshot.child("lastName").getValue(String.class));
                Glide.with(getApplicationContext())
                        .load(dataSnapshot.child("photoUrl").getValue(String.class))
                        .into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("")) {
                    Toast.makeText(EditProfileActivity.this, R.string.check_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedImageUri != null) {
                        StorageReference photoRef = profilePics.child(userID);

                        UploadTask uploadTask = photoRef.putFile(selectedImageUri);
                        final ProgressDialog progress = ProgressDialog.show(EditProfileActivity.this, "Updating Profile",
                                "Updating", true);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                User user = new User(firstName.getText().toString(), lastName.getText().toString(), downloadUrl.toString());
                                userDatabase.setValue(user);
                                progress.dismiss();
                            }
                        });
                    } else {
                        User user = new User(firstName.getText().toString(), lastName.getText().toString(), "null");
                        userDatabase.setValue(user);
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
//            cursor.moveToFirst();
//            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos);
//            dataArray = baos.toByteArray();
//
//            // Do something with the bitmap
//
//            // At the end remember to close the cursor or you will end with the RuntimeException!
//            cursor.close();
        }
    }
}
