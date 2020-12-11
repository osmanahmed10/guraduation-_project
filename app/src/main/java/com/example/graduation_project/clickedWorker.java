package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduation_project.SendNotificationPack.APIService;
import com.example.graduation_project.SendNotificationPack.Client;
import com.example.graduation_project.SendNotificationPack.Data;
import com.example.graduation_project.SendNotificationPack.MyResponse;
import com.example.graduation_project.SendNotificationPack.NotificationSender;
import com.example.graduation_project.SendNotificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class clickedWorker extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private Request request;
    private Uri imageUri;

    User chosenWorker;
    private de.hdodenhof.circleimageview.CircleImageView workerImage;
    private TextView workerName;
    private RatingBar workerRate;
    private TextInputLayout textInputRequest;
    private Button buttonRequest;
    private ImageButton chatPicture, dialogPicture;

    String currentPhotoPath;
    private static final int PERMISSION_CODE = 1000, REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private APIService apiService;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_worker);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        buildView();

        textInputRequest.getEditText().addTextChangedListener(requestWatcher);

        chatPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(clickedWorker.this,chatActivity.class);
                chatIntent.putExtra("workerInfo",chosenWorker);
                startActivity(chatIntent);
            }
        });

        dialogPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openDialog();
                    }
                } else {
                    openDialog();
                }
            }
        });
    }

    private void buildView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * .85), (int) (dm.heightPixels * .6));

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        request = new Request();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Images").child("Requests");
        chatPicture = findViewById(R.id.chat_button);
        workerImage = findViewById(R.id.worker_image);
        workerName = findViewById(R.id.worker_name);
        workerRate = findViewById(R.id.worker_rate);
        dialogPicture = findViewById(R.id.dialog_picture);

        Intent popUp = getIntent();
        chosenWorker = popUp.getParcelableExtra("workerInfo");

        Picasso.get().load(chosenWorker.getImageUrl()).placeholder(R.drawable.ic_person).fit().centerCrop().into(workerImage);
        workerName.setText(chosenWorker.getUserName());
        workerRate.setRating(chosenWorker.getRate());

        textInputRequest = findViewById(R.id.text_input_request);
        buttonRequest = findViewById(R.id.btn_request);
    }

    private TextWatcher requestWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String problemExplanation = textInputRequest.getEditText().getText().toString().trim();

            buttonRequest.setEnabled(!problemExplanation.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void openDialog() {
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(clickedWorker.this);
        builder.setTitle("How do you want to get Image?").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Camera")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = creatPhotoFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            imageUri = FileProvider.getUriForFile(clickedWorker.this,
                                    "com.example.android.fileprovider", photoFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(cameraIntent, REQUEST_CAMERA);
                        }
                    }
                } else if (options[which].equals("Gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gallery.setType("image/*");
                    startActivityForResult(gallery.createChooser(gallery, "Select File"), SELECT_FILE);
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File creatPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(name, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void request(View v) {
        notify = true;
        Toast.makeText(this, R.string.request_is_being_sent, Toast.LENGTH_LONG).show();
        request.setRequestId(FirebaseDatabase.getInstance().getReference().push().getKey());
        request.setUserId(mAuth.getCurrentUser().getUid());
        request.setWorkerId(chosenWorker.getId());
        request.setWorkerName(chosenWorker.getUserName());
        request.setProblemExplanation(textInputRequest.getEditText().getText().toString().trim());
        request.setField(chosenWorker.getProfession());
        request.setArea(chosenWorker.getArea());
        uploadImage();
    }

    private void uploadImage() {
        Toast.makeText(this, R.string.uploading_image_take_time, Toast.LENGTH_LONG).show();
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            request.setImageUrl(uri.toString());
                            database.getReference("Requests").child(chosenWorker.getArea())
                                    .child(request.getRequestId()).setValue(request)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //sendNotifications(chosenWorker.getId(), getString(R.string.new_request), request.getRequestId());
                                                finish();
                                            } else {
                                                Toast.makeText(clickedWorker.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                } else {
                    Toast.makeText(clickedWorker.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //get the extension of imageFile
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(currentPhotoPath);
                dialogPicture.setImageURI(Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                imageUri = Uri.fromFile(f);
                mediaScanIntent.setData(imageUri);
                this.sendBroadcast(mediaScanIntent);
            } else if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                dialogPicture.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openDialog();
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }

    /*public void sendNotifications(String userId, final String title, final String message) {
        FirebaseDatabase.getInstance().getReference("Tokens").child(chosenWorker.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Token token = dataSnapshot.getValue(Token.class);
                        Toast.makeText(clickedWorker.this, R.string.notify_worker, Toast.LENGTH_SHORT).show();
                        Data data = new Data(title,message);
                        NotificationSender sender = new NotificationSender(data,token.getToken());
                        apiService.sendNotifiction(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                Toast.makeText(clickedWorker.this, response.message()+"", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }*/

    private void updateToken(String token) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Token mToken = new Token(token);
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(mToken);
    }
}