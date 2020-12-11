package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signUp extends AppCompatActivity {

    private ProgressBar loading;
    private de.hdodenhof.circleimageview.CircleImageView profilePicture;
    private Button buttonRegister, gotLocation;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private TextInputLayout textInputPhone;
    private Spinner spinnerAreas;

    private RadioGroup groupCustomerWorker;

    private RadioButton customerRadioButton;
    private RadioButton workerRadioButton;
    private Spinner spinnerProfessions;

    public static LatLng homeAddress;

    private Uri imageUri;

    private String usernameInput, emailInput, passwordInput, confirmPasswordInput, phoneInput;

    private User user;
    private miniUser miniUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private StorageReference storageReference;

    private static final int PERMISSION_CODE = 1000, REQUEST_CAMERA = 1, SELECT_FILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        updateUI();

        user = new User();
        miniUser = new miniUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        ArrayAdapter<CharSequence> areasAdapter = ArrayAdapter.createFromResource(this, R.array.areas, android.R.layout.simple_spinner_item);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreas.setAdapter(areasAdapter);
        spinnerAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setArea(parent.getItemAtPosition(position).toString());
                miniUser.setArea(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> professionsAdapter = ArrayAdapter.createFromResource(this, R.array.professions, android.R.layout.simple_spinner_item);
        professionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfessions.setAdapter(professionsAdapter);

        textInputUsername.getEditText().addTextChangedListener(signUpWatcher);
        textInputEmail.getEditText().addTextChangedListener(signUpWatcher);
        textInputPassword.getEditText().addTextChangedListener(signUpWatcher);
        textInputConfirmPassword.getEditText().addTextChangedListener(signUpWatcher);
        textInputPhone.getEditText().addTextChangedListener(signUpWatcher);

    }

    private void updateUI() {
        loading = findViewById(R.id.loading);
        profilePicture = findViewById(R.id.profile_picture);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirmpassword);
        textInputPhone = findViewById(R.id.text_input_phone_number);

        spinnerAreas = findViewById(R.id.spinner_areas);
        gotLocation = findViewById(R.id.got_location);
        groupCustomerWorker = findViewById(R.id.group_customer_worker);
        customerRadioButton = findViewById(R.id.customer);
        workerRadioButton = findViewById(R.id.worker);
        spinnerProfessions = findViewById(R.id.spinner_professions);
        buttonRegister = findViewById(R.id.btn_register);

        homeAddress = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (homeAddress == null) {
            gotLocation.setVisibility(View.GONE);
        } else {
            gotLocation.setVisibility(View.VISIBLE);
        }
    }

    public void role(View v) {
        int radioId = groupCustomerWorker.getCheckedRadioButtonId();
        if (radioId == workerRadioButton.getId()) {
            spinnerProfessions.setVisibility(View.VISIBLE);
            user.setRole(1);
            miniUser.setRole(1);
            spinnerProfessions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    user.setProfession(parent.getItemAtPosition(position).toString());
                    miniUser.setProfession(parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            spinnerProfessions.setVisibility(View.GONE);
            user.setRole(0);
            user.setProfession("none");
            miniUser.setRole(0);
            miniUser.setProfession("none");
        }
    }

    private TextWatcher signUpWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            usernameInput = textInputUsername.getEditText().getText().toString().trim();
            emailInput = textInputEmail.getEditText().getText().toString().trim();
            passwordInput = textInputPassword.getEditText().getText().toString().trim();
            confirmPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();
            phoneInput = textInputPhone.getEditText().getText().toString().trim();

            buttonRegister.setEnabled(!usernameInput.isEmpty()
                    && !emailInput.isEmpty() && !passwordInput.isEmpty()
                    && !confirmPasswordInput.isEmpty() && !phoneInput.isEmpty());
        }
    };

    private boolean validateProfilePicture() {
        if (imageUri.toString().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean validateUsername() {
        if (usernameInput.length() > 20) {
            textInputUsername.setError("Username too long");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        if (!passwordInput.equals(confirmPasswordInput)) {
            textInputConfirmPassword.setError("Field input must match the previous password");
            return false;
        } else {
            textInputConfirmPassword.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        if (phoneInput.length() != 11) {
            textInputPhone.setError("Field input must contain 11 digits");
            return false;
        } else {
            textInputPhone.setError(null);
            return true;
        }
    }

    public void getLocation(View v) {
        startActivity(new Intent(signUp.this, MapsActivity.class));
    }

    public void register(View v) {
        loading.setVisibility(View.VISIBLE);
        if (!validateProfilePicture() || !validateUsername() || !validateConfirmPassword() || !validatePhone()) {
            return;
        } else {
            //registeration
            user.setUserName(usernameInput);
            user.setEmailAddress(emailInput);
            user.setPassword(passwordInput);
            user.setPhoneNumber(phoneInput);
            user.setLatitude(homeAddress.latitude);
            user.setLongitude(homeAddress.longitude);
            user.setRate(2);

            creatAccount(emailInput, passwordInput);
        }
    }

    public void creatAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user.setId(mAuth.getCurrentUser().getUid());
                            miniUser.setId(mAuth.getCurrentUser().getUid());
                            uploadImage();
                        } else {
                            Toast.makeText(signUp.this, "Authentication failed.\ntask.getException().toString()",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setMiniUser() {
        database.getReference("All Persons")
                .child(user.getId())
                .setValue(miniUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loading.setVisibility(View.GONE);
                    startActivity(new Intent(signUp.this, login.class));
                    finish();
                } else {
                    Toast.makeText(signUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUser() {
        if (user.getRole() == 0) {
            database.getReference("Users").child(user.getArea())
                    .child(user.getId()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setMiniUser();
                            } else {
                                Toast.makeText(signUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            database.getReference("Workers").child(user.getArea()).child(user.getProfession())
                    .child(user.getId()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setMiniUser();
                            } else {
                                Toast.makeText(signUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void uploadImage() {
        Toast.makeText(this, R.string.photo_is_being_uploaded, Toast.LENGTH_LONG).show();
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user.setImageUrl(uri.toString());
                            setUser();
                        }
                    });
                } else {
                    Toast.makeText(signUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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

    public void chooseImage(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                openDialog();
            }
        } else {
            openDialog();
        }
    }

    public void openDialog() {
        final CharSequence[] options = {"Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(signUp.this);
        builder.setTitle("Choose image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Gallery")) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                profilePicture.setImageURI(imageUri);
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

    public void login(View v) {
        startActivity(new Intent(signUp.this, login.class));
        finish();
    }

}