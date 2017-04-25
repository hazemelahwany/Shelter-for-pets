package com.example.android.shelterforpets.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.shelterforpets.DatabaseObjects.Event;
import com.example.android.shelterforpets.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddEvents extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;
    Button eventPhoto;
    Uri selectedImageUri;
    byte[] dataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final DatabaseReference eventsDatabase = mFirebaseDatabase.getReference().child("Events");
        final StorageReference eventsPics = storage.getReference().child("events");

        final EditText eventName = (EditText) findViewById(R.id.event_name);
        final EditText eventDescription = (EditText) findViewById(R.id.event_description);
        final EditText eventDate = (EditText) findViewById(R.id.event_date);
        final EditText eventTime = (EditText) findViewById(R.id.event_time);

        eventPhoto = (Button) findViewById(R.id.event_choose_photo);
        Button addEventButton = (Button) findViewById (R.id.add_event);

        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventName.getText().toString().equals("") ||
                        eventDescription.getText().toString().equals("") ||
                        eventDate.getText().toString().equals("") ||
                        eventTime.getText().toString().equals("")) {
                    Toast.makeText(AddEvents.this, R.string.check_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedImageUri != null) {
                        StorageReference photoRef = eventsPics.child(selectedImageUri.getLastPathSegment());

                        UploadTask uploadTask = photoRef.putBytes(dataArray);
                        final ProgressDialog progress = ProgressDialog.show(AddEvents.this, "Adding Event",
                                "Uploading Photo and Adding Event", true);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddEvents.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Event event = new Event(eventName.getText().toString(),
                                        eventDescription.getText().toString(),
                                        eventDate.getText().toString(),
                                        eventTime.getText().toString(),
                                        downloadUrl.toString());
                                eventsDatabase.child(UUID.randomUUID().toString()).setValue(event);
                                progress.dismiss();
                            }
                        });
                    } else {
                        Event event = new Event(eventName.getText().toString(),
                                eventDescription.getText().toString(),
                                eventDate.getText().toString(),
                                eventTime.getText().toString(),
                                "null");
                        eventsDatabase.child(UUID.randomUUID().toString()).setValue(event);
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImageUri, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            dataArray = baos.toByteArray();

            // Do something with the bitmap


            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }
}
