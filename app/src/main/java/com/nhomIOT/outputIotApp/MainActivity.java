package com.nhomIOT.outputIotApp;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhomIOT.outputIotApp.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getMessage().observe(this, message -> {
            if (message.equals("0")) {
                binding.tvMessage.setText("Giới hạn tốc độ 30Km/h");
            } else if (message.equals("1")) {
                binding.tvMessage.setText("Giao nhau với đường ưu tiên");
            } else if (message.equals("2")) {
                binding.tvMessage.setText("Công trình đang thi công");
            } else {
                binding.tvMessage.setText(message);
            }
            playMp3FromAssets(message);
            loadImage(message);
        });

        viewModel.getStatus().observe(this, status -> {
            binding.tvStatus.setText(status);
        });

        viewModel.isConnected().observe(this,isConnected->{
            if(isConnected) {
                binding.btnReconnect.setVisibility(View.GONE);
            }
            else {
                binding.btnReconnect.setVisibility(View.VISIBLE);
            }
        });

        binding.btnReconnect.setOnClickListener(v->{
            viewModel.reconnect();
        });


    }


    private void playMp3FromAssets(String soundName) {
        String fileName = "sound/" + soundName + ".mp3";

        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImage(String imgName) {
        String fileName = "img/" + imgName + ".jpg";
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(fileName));
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            binding.ivTrafficSign.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.closeSocket();
    }
}

