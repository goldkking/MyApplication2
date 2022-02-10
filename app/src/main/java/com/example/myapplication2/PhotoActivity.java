package com.example.myapplication2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    //카메라
    final String TAG = getClass().getSimpleName();
    ImageView imageView;
    Button cameraBtnFir;
    TextView precaution;

    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    //   하단 네비게이션
    LinearLayout home_ly;
    BottomNavigationView bottomNavigationView;
    private long lastTimeBackPressed;

    //뒤로 가기를 누르면 어플을 종료할 수 있도록 함
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        //두 번 클릭시 어플 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

//        bottomNavigationView.setSelectedItemId(R.id.); //시작 초기화면
        // 레이아웃과 변수 연결
        imageView = findViewById(R.id.imageview);
        cameraBtnFir = findViewById(R.id.camera_button_first);
        precaution = findViewById(R.id.precautions);

        // 카메라 버튼에 listener 추가
        cameraBtnFir.setOnClickListener(this);

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    // 버튼 onClick 리스너 처리부분
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_button_first:

                //                 카메라 앱을 여는 소스
//                dispatchTakePictureIntent();
//                break;
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent, TAKE_PICTURE);
                //사진 촬영하면 주의사항이랑 사진 촬영 버튼 사라지게 함

                precaution.setVisibility(View.GONE);
                cameraBtnFir.setVisibility(View.GONE);
                break;
            //사진 촬영하면 주의사항이랑 사진 촬영 버튼 사라지게 함
//            case R.id.camera_button_second:
//                //                 카메라 앱을 여는 소스
////                dispatchTakePictureIntent();
////                break;
//                Intent Intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(Intent, TAKE_PICTURE);
//                //사진 촬영하면 주의사항이랑 사진 촬영 버튼 사라지게 함
//
//                precaution.setVisibility(View.GONE);
//                cameraBtnFir.setVisibility(View.GONE);
//                break;
        }

    }
    //
    // 카메라로 촬영한 영상을 가져오는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && intent.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }

                }
                break;
        }
    }

    //카메라 인텐트를 실행하는 함수
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.cameratest.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }


    //카메라로 촬영한 사진을 파일로 저장해주는 함수
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}