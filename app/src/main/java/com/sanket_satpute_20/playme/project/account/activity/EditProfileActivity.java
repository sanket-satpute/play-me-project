package com.sanket_satpute_20.playme.project.account.activity;

import static com.sanket_satpute_20.playme.project.account.extra_stuffes.CommonMethodsUser.getFirebaseStoragePath;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;
import static com.sanket_satpute_20.playme.project.fragments.HomeFragment.REQUEST_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.model.MUser;
import com.sanket_satpute_20.playme.project.account.extra_stuffes.SaveUserImageCache;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

/*1. set qr code image
2. load default image if not image found -> 246*/

public class EditProfileActivity extends AppCompatActivity {

//    ad's
//    InterstitialAd interstitialAd;

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 110;
    private static final int BOX_ERROR_STROKE_WIDTH = 5;

    private static final String QR_DIALOG = "QR_DIALOG";
    public static final String USER_IMAGE_DIALOG = "USER_IMAGE_DIALOG";
    private static final String FOR_WHAT = "FOR_WHAT";

    String[] month_names = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    String[] year_names = {"1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999",
    "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011",
            "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2021", "2022", "2023"};
    String[] date_names = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    String upiIdRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z]{3,}$";
    String userNameRegex = "^[a-zA-Z0-9_-]{3,15}$";

    String required_str = "*Required";
    boolean isAllOk;

    ColorStateList is_all_ok_color = ColorStateList.valueOf(Color.parseColor("#ffa500"));

    FirebaseFirestore f_db;
    FirebaseAuth f_auth;

    MaterialAlertDialogBuilder material_user_fetch_dialog;
    AlertDialog alert_user_fetch_dialog;

    TextInputEditText first_name_txt, last_name_txt, phone_no_txt, username_txt, email_txt, upi_id_txt;
    TextInputLayout first_name_outer, last_name_outer, phone_outer, username_outer, email_outer, upi_id_outer, dob_date_outer, dob_month_outer,
    dob_year_outer;
    AutoCompleteTextView dob_date_txt, dob_month_txt, dob_year_txt;
    ImageView back_pressed, user_profile;
    TextView joined_date, user_email, user_full_name, email_textview, qr_code_helper_text,
            save_btn_txt, user_profile_picture_uploading_percent_txt;
    FloatingActionButton edit_user_profile, edit_email_id;
    MaterialButton set_qr_btn, save_btn, add_other_detail_btn;
    ProgressBar save_btn_progress;
    ConstraintLayout add_other_info_layout;
    MaterialCardView email_card_layout;
    ProgressBar userProfilePictureLoadingProgress;

    CircularProgressIndicator user_profile_picture_uploading_progress;

    String what_type_image = null;
    Bitmap userSelectedProfileImage, userSelectedQrCode;

    Uri profilePictureUri, qrCodeUri;

    MUser tempMUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        tempMUser = currentUser;
        initViews();
        starterMethod();
        onClick();
//        boolean canICallAd = (new Random().nextInt(100) % 3 == 0);
//        if (canICallAd) {
//            loadInterstitialAd();
//        }
    }

    @Override
    public void onBackPressed() {
//        if (interstitialAd != null)
//            showInterstitialAd();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if((grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                launchCameraImageChooserActivity.launch(intent);
            }
            else
            {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{
                                Manifest.permission.CAMERA}, REQUEST_CODE);
            }
        }
    }

    private void initViews() {
        first_name_txt = findViewById(R.id.first_name_edit);
        last_name_txt = findViewById(R.id.last_name_edit);
        phone_no_txt = findViewById(R.id.phone_edit);
        username_txt = findViewById(R.id.username_edit);
        email_txt = findViewById(R.id.email_edit);
        upi_id_txt = findViewById(R.id.upi_id_edit);
        dob_date_txt = findViewById(R.id.birth_date_date_edit);
        dob_month_txt = findViewById(R.id.birth_date_month_edit);
        dob_year_txt = findViewById(R.id.birth_date_year_edit);
        first_name_outer = findViewById(R.id.first_name_edit_outer);
        last_name_outer = findViewById(R.id.last_name_edit_outer);
        phone_outer = findViewById(R.id.phone_edit_outer);
        username_outer = findViewById(R.id.username_edit_outer);
        email_outer = findViewById(R.id.email_edit_outer);
        upi_id_outer = findViewById(R.id.upi_id_edit_outer);
        dob_date_outer = findViewById(R.id.birth_date_date_edit_outer);
        dob_month_outer = findViewById(R.id.birth_date_month_edit_outer);
        dob_year_outer = findViewById(R.id.birth_date_year_edit_outer);
        back_pressed = findViewById(R.id.back_pressed);
        user_profile = findViewById(R.id.user_profile_picture);
        joined_date = findViewById(R.id.joined_date_txt);
        edit_user_profile = findViewById(R.id.edit_img_fab);
        edit_email_id = findViewById(R.id.edit_email_fab);
        set_qr_btn = findViewById(R.id.set_qr_code_btn);
        save_btn = findViewById(R.id.save_edited_info_btn);
        add_other_detail_btn = findViewById(R.id.add_other_detail_btn);
        add_other_info_layout = findViewById(R.id.add_other_detail_layout);
        user_email = findViewById(R.id.user_email_id);
        user_full_name = findViewById(R.id.user_full_name);
        email_textview = findViewById(R.id.email_txt);
        save_btn_progress = findViewById(R.id.save_btn_progress);
        email_card_layout = findViewById(R.id.email_card_layout);
        qr_code_helper_text = findViewById(R.id.set_qr_code_txt);
        user_profile_picture_uploading_progress = findViewById(R.id.is_profile_picture_uploaded_progress);
        user_profile_picture_uploading_percent_txt = findViewById(R.id.image_uploaded_percent_txt);
        save_btn_txt = findViewById(R.id.save_btn_txt);
        userProfilePictureLoadingProgress = findViewById(R.id.user_profile_picture_loading_progress_bar);
    }

    private void starterMethod() {
        add_other_info_layout.setVisibility(View.GONE);
        add_other_detail_btn.setVisibility(View.VISIBLE);
        email_txt.setVisibility(View.GONE);

        if (currentUser == null)
            fetchUser();
        user_full_name.setText(currentUser.getFull_name());
        user_email.setText(currentUser.getEmail_id());
        first_name_txt.setText(currentUser.getFirst_name());
        last_name_txt.setText(currentUser.getLast_name());
        phone_no_txt.setText(String.valueOf(currentUser.getPhone_number()));
        username_txt.setText(currentUser.getUser_name());
        email_txt.setText(currentUser.getEmail_id());
        upi_id_txt.setText(currentUser.getUpiID());

        /* setting add other detail btn background color */
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES: // night
                add_other_detail_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1bBB86FC")));
                break;
            case Configuration.UI_MODE_NIGHT_NO: // night no
                add_other_detail_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1b0000ff")));
                break;
        }

        Bitmap user_image = SaveUserImageCache.getImage(EditProfileActivity.this, "user_profile_image");
        if (user_image != null) {
            Glide.with(this)
                    .asBitmap()
                    .error(R.drawable.orange_man_user_profile_picture)
                    .load(user_image)
                    .into(user_profile);
        } else if (currentUser.getProfilePicturePath() != null) {
            user_profile.setAlpha(0.6f);
            userProfilePictureLoadingProgress.setVisibility(View.VISIBLE);
            StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                    getFirebaseStoragePath() +
                    currentUser.getProfilePicturePath());
            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Glide.with(EditProfileActivity.this)
                        .load(bytes)
                            .error(R.drawable.orange_man_user_profile_picture)
                        .into(user_profile);
                user_profile.setAlpha(1f);
                userProfilePictureLoadingProgress.setVisibility(View.GONE);
                Bitmap userBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                SaveUserImageCache.putImage(EditProfileActivity.this, "user_profile_image", userBitmap);
            }).addOnFailureListener(exception -> {
                user_profile.setAlpha(1f);
                userProfilePictureLoadingProgress.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Failed to load Image", Toast.LENGTH_SHORT).show();
            });
        } else {
            currentUser.setProfilePicturePath(null);
            user_profile.setImageResource(R.drawable.orange_man_user_profile_picture);
//            set default user image
        }

        String date_str;
        try { // birth date time
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                joined date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate d = LocalDate.parse(currentUser.getAccountCreationDate(), formatter);

                int doj_date = d.getDayOfMonth();
                Month doj_month = d.getMonth();
                int doj_year = d.getYear();
                date_str = doj_date + " " + month_names[doj_month.getValue() - 1] + " " + doj_year;
                joined_date.setText(date_str);
            }
            else {
                joined_date.setText(currentUser.getAccountCreationDate().replaceAll("-", " "));
            }
        } catch (Exception e) {
            e.printStackTrace();
            joined_date.setText(currentUser.getAccountCreationDate().replaceAll("-", " "));
        }

        try {
            String birthDate = currentUser.getBirthDate();
            if (birthDate != null) {
                DateTimeFormatter formatterBirthDate;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatterBirthDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate birth_date = LocalDate.parse(currentUser.getBirthDate(), formatterBirthDate);

                    int dob_date = birth_date.getDayOfMonth();
                    Month dob_month = birth_date.getMonth();
                    int dob_year = birth_date.getYear();
                    dob_date_txt.setText(String.valueOf(dob_date));
                    dob_month_txt.setText(String.valueOf(month_names[dob_month.getValue() - 1]));
                    dob_year_txt.setText(String.valueOf(dob_year));
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date dobDate = format.parse(birthDate);
                    if (dobDate != null) {
                        dob_date_txt.setText(String.valueOf(dobDate.getDate()));
                        dob_month_txt.setText(String.valueOf(month_names[dobDate.getMonth() - 1]));
                        dob_year_txt.setText(String.valueOf(dobDate.getYear()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ArrayAdapter<String> adapter_dates = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, date_names);
            dob_date_txt.setAdapter(adapter_dates);

            ArrayAdapter<String> adapter_month = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, month_names);
            dob_month_txt.setAdapter(adapter_month);

            ArrayAdapter<String> adapter_year = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, year_names);
            dob_year_txt.setAdapter(adapter_year);
        }

        isSaveBtnIsAvailable();
    }

    private void onClick() {
        back_pressed.setOnClickListener(view -> onBackPressed());

        edit_user_profile.setOnClickListener(view -> chooseProfilePictureDialog(USER_IMAGE_DIALOG));

        set_qr_btn.setOnClickListener(view -> chooseProfilePictureDialog(QR_DIALOG));

        add_other_detail_btn.setOnClickListener(view -> {
            add_other_detail_btn.setVisibility(View.GONE);
            add_other_info_layout.setVisibility(View.VISIBLE);
        });

        email_card_layout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
            finish();
        });

        edit_email_id.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
            finish();
        });

        save_btn.setOnClickListener(view -> {
            save_btn_progress.setVisibility(View.VISIBLE);
            saveUserBtn();
        });

        first_name_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (first_name_outer.getError() != null) {
                    if (s.toString().trim().length() > 0) {
                        if (s.toString().contains(" ")) {
                            first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                            first_name_outer.setError("first name contains spaces");
                        } else if (s.toString().trim().length() > 10) {
                            first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                            first_name_outer.setError("first name is too long");
                        } else {
                            first_name_outer.setBoxStrokeWidth(0);
                            first_name_outer.setError(null);
                        }
                    } else {
                        first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                        first_name_outer.setError(required_str);
                    }
                    isSaveBtnIsAvailable();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        last_name_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (last_name_outer.getError() != null) {
                    if (s.toString().trim().length() > 0) {
                        if (s.toString().trim().contains(" ")) {
                            last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                            last_name_outer.setError("last name contains spaces.");
                        } else if (s.toString().trim().length() > 10) {
                            last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                            last_name_outer.setError("last name is too long.");
                        } else {
                            last_name_outer.setBoxStrokeWidth(0);
                            last_name_outer.setError(null);
                        }
                    } else {
                        last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                        last_name_outer.setError(required_str);
                    }
                    isSaveBtnIsAvailable();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        phone_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone_outer.getError() != null) {
                    if (s.toString().trim().length() > 0) {
                        if (s.toString().trim().length() == 10 && isValidMobile(s)) {
                            phone_outer.setError(null);
                            phone_outer.setBoxStrokeWidth(0);
                        } else {
                            phone_outer.setError("phone number is invalid.");
                            phone_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                        }
                    } else {
                        phone_outer.setError(required_str);
                        phone_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    }
                    isSaveBtnIsAvailable();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        email_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("") || s.toString().trim().length() == 0) {
                    email_txt.setError(required_str);
                } else if (isValidEmail(s) /*&& focus_change_email > 2*/) {
                    String email_is_not_valid_str = "email is not valid";
                    email_txt.setError(email_is_not_valid_str);
                } else {
                    email_txt.setError(null);
                }
                isSaveBtnIsAvailable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        username_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username_outer.getError() != null) {
                    if (!isValidUsername(s) && s.toString().length() > 0) {
                        Log.d("isvaliduser", String.valueOf(isValidUsername(s)));
                        username_outer.setError("username is not valid eg. username@123");
                        username_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    } else {
                        username_outer.setBoxStrokeWidth(0);
                        username_outer.setError(null);
                    }
                    isSaveBtnIsAvailable();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        upi_id_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (upi_id_outer.getError() != null) {
                    if (!isValidUpi(s) && s.toString().length() > 0) {
                        upi_id_outer.setError("upi id is not valid eg. 1234567890@upi");
                    } else {
                        upi_id_outer.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    int setMaxImageUploadedProgress = 0;

    private void saveUserBtn() {
        if (currentUser != null) {
            if (f_auth == null || f_db == null)
                connectToFirebase();

            if (first_name_txt.getText() != null) {
                if (first_name_txt.getText().length() > 0) {
                    if (first_name_txt.getText().toString().contains(" ")) {
                        first_name_outer.setError("first name contains spaces.");
                        first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    } else {
                        first_name_outer.setError(null);
                        first_name_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    first_name_outer.setError("first name can't be empty.");
                    first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
            } else {
                first_name_outer.setError("first name can't be empty.");
                first_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
            }

            if (last_name_txt.getText() != null) {
                if (last_name_txt.getText().length() > 0) {
                    if (last_name_txt.getText().toString().contains(" ")) {
                        last_name_outer.setError("last name contains spaces.");
                        last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    } else {
                        last_name_outer.setError(null);
                        last_name_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    last_name_outer.setError("last name can't be empty.");
                    last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
            } else {
                last_name_outer.setError("last name can't be empty.");
                last_name_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
            }

            if (phone_no_txt.getText() != null) {
                if (phone_no_txt.getText().length() > 0) {
                    if (isValidMobile(phone_no_txt.getText().toString()) && phone_no_txt.getText().toString().length() == 10) {
                        phone_outer.setError(null);
                        phone_outer.setBoxStrokeWidth(0);
                    } else {
                        phone_outer.setError("phone number is not valid.");
                        phone_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    }
                } else {
                    phone_outer.setError("phone number can't be empty.");
                    phone_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
            } else {
                phone_outer.setError("phone number can't be empty.");
                phone_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
            }

            if (username_txt.getText() != null) {
                if (username_txt.getText().length() > 0) {
                    if (isValidUsername(username_txt.getText().toString())) {
                        username_outer.setError(null);
                        username_outer.setBoxStrokeWidth(0);
                    } else {
                        username_outer.setError("username is not valid. eq. your_name@123");
                        username_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    }
                } else {
                    username_outer.setError(null);
                    username_outer.setBoxStrokeWidth(0);
                }
            } else {
                username_outer.setError(null);
                username_outer.setBoxStrokeWidth(0);
            }

            if (upi_id_txt.getText() != null) {
                if (upi_id_txt.getText().length() > 0) {
                    if (isValidUpi(upi_id_txt.getText().toString())) {
                        upi_id_outer.setError("username is not valid. eq. your_name@123");
                        upi_id_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    } else {
                        upi_id_outer.setError(null);
                        upi_id_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    upi_id_outer.setError(null);
                    upi_id_outer.setBoxStrokeWidth(0);
                }
            } else {
                upi_id_outer.setError(null);
                upi_id_outer.setBoxStrokeWidth(0);
            }

            if (upi_id_txt.getText() != null) {
                if (upi_id_txt.getText().length() > 0) {
                    if (isValidUpi(upi_id_txt.getText().toString())) {
                        upi_id_outer.setError("upi id is not valid. eq. your_id@back_name");
                        upi_id_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                    } else {
                        upi_id_outer.setError(null);
                        upi_id_outer.setBoxStrokeWidth(0);
                    }
                } else {
                    upi_id_outer.setError(null);
                    upi_id_outer.setBoxStrokeWidth(0);
                }
            } else {
                upi_id_outer.setError(null);
                upi_id_outer.setBoxStrokeWidth(0);
            }

//            birth date errors check
            if (String.valueOf(dob_date_txt.getText()).trim().length() > 0 &&
                    String.valueOf(dob_month_txt.getText()).trim().length() > 0 &&
                    String.valueOf(dob_year_txt.getText()).trim().length() > 0) {
                dob_date_outer.setError(null);
                dob_date_outer.setBoxStrokeWidth(0);
                dob_month_outer.setError(null);
                dob_month_outer.setBoxStrokeWidth(0);
                dob_year_outer.setError(null);
                dob_year_outer.setBoxStrokeWidth(0);
            } else if (String.valueOf(dob_date_txt.getText()).trim().length() == 0 &&
                    String.valueOf(dob_month_txt.getText()).trim().length() == 0 &&
                    String.valueOf(dob_year_txt.getText()).trim().length() == 0) {
                dob_date_outer.setError(null);
                dob_date_outer.setBoxStrokeWidth(0);
                dob_month_outer.setError(null);
                dob_month_outer.setBoxStrokeWidth(0);
                dob_year_outer.setError(null);
                dob_year_outer.setBoxStrokeWidth(0);
            } else {
                if (!(add_other_info_layout.getVisibility() == View.VISIBLE)) {
                    add_other_info_layout.setVisibility(View.VISIBLE);
                }
                if (String.valueOf(dob_date_txt.getText()).trim().length() == 0) {
                    dob_date_outer.setError("select date.");
                    dob_date_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
                if (String.valueOf(dob_month_txt.getText()).trim().length() == 0) {
                    dob_month_outer.setError("select month.");
                    dob_month_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
                if (String.valueOf(dob_year_txt.getText()).trim().length() == 0) {
                    dob_year_outer.setError("select year.");
                    dob_year_outer.setBoxStrokeWidth(BOX_ERROR_STROKE_WIDTH);
                }
            }

            if (first_name_outer.getError() == null && last_name_outer.getError() == null && phone_outer.getError() == null
            && username_outer.getError() == null && upi_id_outer.getError() == null && dob_date_outer.getError() == null
            && dob_month_outer.getError() == null && dob_year_outer.getError() == null) {
                String saving_str = "Saving...";
                save_btn_txt.setText(saving_str);

                currentUser.setFirst_name(String.valueOf(first_name_txt.getText()).trim());
                currentUser.setLast_name(String.valueOf(last_name_txt.getText()).trim());
                currentUser.setFull_name(currentUser.getFirst_name() + " " + currentUser.getLast_name());
                String phoneNoStr = String.valueOf(phone_no_txt.getText()).trim();
                currentUser.setPhone_number(Long.parseLong((phoneNoStr.length() > 0) ? phoneNoStr : String.valueOf(0L)));
                currentUser.setUser_name(String.valueOf(username_txt.getText()).trim());
                currentUser.setUpiID(String.valueOf(upi_id_txt.getText()).trim());
                if (userSelectedProfileImage != null && f_auth.getCurrentUser() != null)
                    currentUser.setProfilePicturePath("image/" + f_auth.getCurrentUser().getUid() + "/profilePicture/image.png");

                if (dob_date_txt.getText().toString().length() > 0 && dob_month_txt.getText().toString().length() > 0
                        && dob_year_txt.getText().toString().length() > 0) {

                    save_btn.setClickable(false);
                    set_qr_btn.setClickable(false);
                    add_other_detail_btn.setClickable(false);

                    String birthDateStr = dob_year_txt.getText().toString().trim() + "-" +
                            getObjectIndex(dob_month_txt.getText().toString().trim()) + "-" + dob_date_txt.getText().toString().trim();
                    currentUser.setBirthDate(birthDateStr);
                }

                if (f_auth.getCurrentUser() != null) {
                    f_db.collection("users").document(f_auth.getCurrentUser().getUid())
                            .set(currentUser)
                            .addOnSuccessListener(unused -> userSaveDataTaskCompleted())
                            .addOnFailureListener(e -> {
                                if (tempMUser != null)
                                    currentUser = tempMUser;
                                save_btn_progress.setVisibility(View.GONE);
                                set_qr_btn.setClickable(true);
                                add_other_detail_btn.setClickable(true);
                                save_btn.setClickable(true);
                            })
                            .addOnCanceledListener(() -> {
                                if (tempMUser != null)
                                    currentUser = tempMUser;
                                save_btn_progress.setVisibility(View.GONE);
                                set_qr_btn.setClickable(true);
                                add_other_detail_btn.setClickable(true);
                                save_btn.setClickable(true);
                            });
                } else {
                    if (tempMUser != null)
                        currentUser = tempMUser;
                    save_btn_progress.setVisibility(View.GONE);
                    set_qr_btn.setClickable(true);
                    add_other_detail_btn.setClickable(true);
                    save_btn.setClickable(true);
                }
            } else {
                if (tempMUser != null)
                    currentUser = tempMUser;
                save_btn_progress.setVisibility(View.GONE);
                Toast.makeText(this, "Please correct the detail's first", Toast.LENGTH_SHORT).show();
            }
        } else {
            fetchUser();
            saveUserBtn();
        }
    }

    private void userSaveDataTaskCompleted() {
//                check the user image is changed and the qr code
        if (f_auth == null)
            connectToFirebase();
        if (userSelectedProfileImage != null) {
            user_profile.setAlpha(0.6f);
            edit_user_profile.setVisibility(View.GONE);
            user_profile_picture_uploading_percent_txt.setText("0%");
            user_profile_picture_uploading_progress.setVisibility(View.VISIBLE);
            user_profile_picture_uploading_percent_txt.setVisibility(View.VISIBLE);
            setMaxImageUploadedProgress = 0;

            Uri savedImageUri = SaveUserImageCache.putImage(EditProfileActivity.this, "user_profile_image", userSelectedProfileImage);
            if (savedImageUri != null && f_auth.getCurrentUser() != null) {
                StorageReference f_storage = FirebaseStorage.getInstance().getReference();
                f_storage.child("image/" + f_auth.getCurrentUser().getUid() + "/profilePicture/image.png")
                        .putFile(savedImageUri)
                        .addOnProgressListener(snapshot -> {
                            String uploadedImagePercent = "0%";
                            if (snapshot.getBytesTransferred() > 0) {
                                uploadedImagePercent = (int)(((float)snapshot.getBytesTransferred() / (float) snapshot.getTotalByteCount()) * 100.0f) + "%";
                            }
                            Log.d("data_uploaded", "Max : " + snapshot.getTotalByteCount() + " Uploaded : " + snapshot.getBytesTransferred() + "  % : " + uploadedImagePercent);
                            save_btn_txt.setText(uploadedImagePercent);
                            user_profile_picture_uploading_percent_txt.setText(uploadedImagePercent);
                            user_profile_picture_uploading_progress.setProgress((int) snapshot.getBytesTransferred());
                            if (setMaxImageUploadedProgress == 0) {
                                user_profile_picture_uploading_progress.setMax((int) snapshot.getTotalByteCount());
                                setMaxImageUploadedProgress++;
                            }
                            if (snapshot.getBytesTransferred() >= snapshot.getTotalByteCount()) {
                                set_qr_btn.setClickable(true);
                                add_other_detail_btn.setClickable(true);
                                save_btn.setClickable(true);
                                user_profile.setAlpha(1f);
                                user_profile_picture_uploading_progress.setVisibility(View.GONE);
                                user_profile_picture_uploading_percent_txt.setVisibility(View.GONE);
                                edit_user_profile.setVisibility(View.VISIBLE);
                            }
                        })
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                finish();
                            }
                            return f_storage.getDownloadUrl();
                        })
                        .addOnSuccessListener(taskSnapshot -> {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            f_storage.getDownloadUrl()
                                    .addOnSuccessListener(uri ->
                                            database.getReference().child("user_profile_picture.png")
                                                    .setValue(uri.toString()).addOnSuccessListener(unused -> {
                                                        save_btn_progress.setVisibility(View.GONE);
                                                        set_qr_btn.setClickable(true);
                                                        add_other_detail_btn.setClickable(true);
                                                        save_btn.setClickable(true);
                                                        String saved_str = "Saved Successfully.";
                                                        save_btn_txt.setText(saved_str);
                                                        Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }).addOnFailureListener(e -> {
                                                        e.printStackTrace();
                                                        save_btn.setClickable(true);
                                                        user_profile.setAlpha(1f);
                                                        user_profile_picture_uploading_progress.setVisibility(View.GONE);
                                                        user_profile_picture_uploading_percent_txt.setVisibility(View.GONE);
                                                        edit_user_profile.setVisibility(View.VISIBLE);
                                                        finish();
                                                    }))
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        set_qr_btn.setClickable(true);
                                        add_other_detail_btn.setClickable(true);
                                        save_btn.setClickable(true);
                                        user_profile.setAlpha(1f);
                                        user_profile_picture_uploading_progress.setVisibility(View.GONE);
                                        user_profile_picture_uploading_percent_txt.setVisibility(View.GONE);
                                        edit_user_profile.setVisibility(View.VISIBLE);
                                        finish();
                                    });
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                            set_qr_btn.setClickable(true);
                            add_other_detail_btn.setClickable(true);
                            save_btn.setClickable(true);
                            user_profile.setAlpha(1f);
                            user_profile_picture_uploading_progress.setVisibility(View.GONE);
                            user_profile_picture_uploading_percent_txt.setVisibility(View.GONE);
                            edit_user_profile.setVisibility(View.VISIBLE);
                            finish();
                        });
            }
            else {
                finish();
            }
        } else {
            finish();
        }
    }

    private String getObjectIndex(String monthName) {
        String index = "0";
        for (int i = 0; i < month_names.length; i++) {
            if (month_names[i].equalsIgnoreCase(monthName)) {
                index = String.valueOf(i + 1);
                break;
            }
        }
        if (Integer.parseInt(index) < 10)
            index = "0" + index;
        return index;
    }

    private void fetchUser() {
        View fetching_view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.user_fetch_layout, null);
        material_user_fetch_dialog = new MaterialAlertDialogBuilder(EditProfileActivity.this)
                .setView(fetching_view);
        alert_user_fetch_dialog = material_user_fetch_dialog.create();
        alert_user_fetch_dialog.show();
        Toast.makeText(this, "Wait Until User is Fetched", Toast.LENGTH_SHORT).show();

        if (f_auth == null || f_db == null)
            connectToFirebase();
        if (f_auth.getCurrentUser() != null) {
            f_db.collection("users").document(f_auth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        currentUser = documentSnapshot.toObject(MUser.class);
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (alert_user_fetch_dialog != null)
                            alert_user_fetch_dialog.dismiss();
                        finish();
                    });
        } else {
            Toast.makeText(this, "Please SignIn or Create Account", Toast.LENGTH_SHORT).show();
            if (alert_user_fetch_dialog != null)
                alert_user_fetch_dialog.dismiss();
            finish();
        }
    }

    private void chooseProfilePictureDialog(String which) {
        what_type_image = which;
        MaterialButton camera_btn, gallery_btn;
        View view = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout.edit_or_choose_user_profile_picture_dialog_layout, null);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(EditProfileActivity.this, R.style.RoundedCornerAlertDialog);
        alert.setView(view);

        camera_btn = view.findViewById(R.id.open_camera_btn);
        gallery_btn = view.findViewById(R.id.open_gallery_btn);

        AlertDialog dialog = alert.create();
        dialog.show();

        camera_btn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{
                                Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                Intent intent = new Intent();
                intent.putExtra(FOR_WHAT, which);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                launchCameraImageChooserActivity.launch(intent);
                dialog.dismiss();
            }
        });

        gallery_btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(FOR_WHAT, which);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launchGalleryImageChooserActivity.launch(intent);
            dialog.dismiss();
        });
    }

    ActivityResultLauncher<Intent> launchCameraImageChooserActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if (what_type_image.equalsIgnoreCase(QR_DIALOG)) {
                            userSelectedQrCode = (Bitmap) data.getExtras().get("data");
                            qrCodeUri = result.getData().getData();
                        } else {
                            profilePictureUri = result.getData().getData();
                            userSelectedProfileImage = (Bitmap) data.getExtras().get("data");
                            user_profile.setImageBitmap(userSelectedProfileImage);
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> launchGalleryImageChooserActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            if (what_type_image.equalsIgnoreCase(QR_DIALOG)) {
                                qrCodeUri = selectedImageUri;
                                File file = new File(selectedImageUri.toString());
                                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                                userSelectedQrCode.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                os.close();
                                qr_code_helper_text.setText(file.getName());
                            } else {
                                profilePictureUri = selectedImageUri;
                                userSelectedProfileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                                user_profile.setImageBitmap(userSelectedProfileImage);
                            }
                        } catch (IOException f) {
                            f.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Bitmap photo = bundle.getParcelable("data");
                if (photo != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    user_profile.setImageBitmap(photo);
                } else {
                    user_profile.setImageResource(R.drawable.orange_man_user_profile_picture);
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else {
                user_profile.setImageResource(R.drawable.orange_man_user_profile_picture);
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            user_profile.setImageResource(R.drawable.orange_man_user_profile_picture);
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    ActivityResultLauncher<Intent> result = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Bitmap selectedImageBitmap;
                        selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                        user_profile.setImageBitmap(selectedImageBitmap);
                    }
                }
            });

    private void connectToFirebase() {
        if (f_auth == null)
            f_auth = FirebaseAuth.getInstance();
        if (f_db == null)
            f_db = FirebaseFirestore.getInstance();
    }

    public boolean isValidEmail(CharSequence target) {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidMobile(CharSequence phone) {
        Log.d("amg", "Mobile is Valid : "+android.util.Patterns.PHONE.matcher(phone).matches());
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public boolean isValidUpi(CharSequence upi_Id)
    {
        Pattern pattern = Pattern.compile(upiIdRegex);
        if (upi_Id == null)
            return true;
        Log.d("amg", "Upi is Valid : "+!pattern.matcher(upi_Id).matches());
        return !pattern.matcher(upi_Id).matches();
    }

    private boolean isValidUsername(CharSequence username) {
        Pattern pattern = Pattern.compile(userNameRegex);
        if (username == null)
            return true;
        Log.d("amg", "Username is Valid : "+!pattern.matcher(username).matches());
        return !pattern.matcher(username).matches();
    }

    private void isSaveBtnIsAvailable() {
        isAllOk = first_name_outer.getError() == null && last_name_outer.getError() == null && phone_outer.getError() == null
                && username_outer.getError() == null && upi_id_outer.getError() == null && dob_date_outer.getError() == null
                && dob_month_outer.getError() == null && dob_year_outer.getError() == null;
        if (isAllOk) {
            save_btn.setBackgroundTintList(is_all_ok_color);
            save_btn.setClickable(true);
        } else {
            save_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9ad9d9d9")));
            save_btn.setClickable(false);
        }
    }

    private void sendSignInLink(String email) {
        connectToFirebase();
        ActionCodeSettings settings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.sanket_satpute_20.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://playmeappauth.page.link/")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.sanket_satpute_20.ios")
                        .setAndroidPackageName(
                                "com.sanket_satpute_20.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        f_auth.sendSignInLinkToEmail(email, settings)
                .addOnSuccessListener(unused -> Toast.makeText(EditProfileActivity.this, "Send", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.d("mmky", e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Not Send", Toast.LENGTH_SHORT).show();
                });
       /* f_auth.signInWithEmailLink(email, "https://www.sanket_satpute_20.com/finishSignUp?cartId=1234")
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(EditProfileActivity.this, "Send", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Not Send", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

//    ad's
//    private void loadInterstitialAd() {
//        String TAG = "mTagL";
//        MobileAds.initialize(this, initializationStatus -> {});
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        String ad_interstitial_str = ad_values_map.get("ad_edit_user_detail_interstitial_ad");
//        if (ad_interstitial_str != null) {
//            InterstitialAd.load(this, ad_interstitial_str, adRequest,
//                    new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAdM) {
//                            // The mInterstitialAd reference will be null until
//                            // an ad is loaded.
//                            interstitialAd = interstitialAdM;
//                            Log.i(TAG, "onAdLoaded");
//
//                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                                @Override
//                                public void onAdClicked() {
//                                    // Called when a click is recorded for an ad.
//                                    Log.d(TAG, "Ad was clicked.");
//                                }
//
//                                @Override
//                                public void onAdDismissedFullScreenContent() {
//                                    // Called when ad is dismissed.
//                                    // Set the ad reference to null so you don't show the ad a second time.
//                                    Log.d(TAG, "Ad dismissed fullscreen content.");
//                                    interstitialAd = null;
//                                }
//
//                                @Override
//                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//                                    // Called when ad fails to show.
//                                    Log.e(TAG, "Ad failed to show fullscreen content.");
//                                    interstitialAd = null;
//                                }
//
//                                @Override
//                                public void onAdImpression() {
//                                    // Called when an impression is recorded for an ad.
//                                    Log.d(TAG, "Ad recorded an impression.");
//                                }
//
//                                @Override
//                                public void onAdShowedFullScreenContent() {
//                                    // Called when ad is shown.
//                                    Log.d(TAG, "Ad showed fullscreen content.");
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            // Handle the error
//                            Log.d(TAG, loadAdError.toString());
//                            interstitialAd = null;
//                        }
//                    });
//        }
//    }
//
//    private void showInterstitialAd() {
//        interstitialAd.show(EditProfileActivity.this);
//        interstitialAd = null;
//        Log.d("mTagL", "Showed Ad");
//    }
}