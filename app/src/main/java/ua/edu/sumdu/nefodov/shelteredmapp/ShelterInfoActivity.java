package ua.edu.sumdu.nefodov.shelteredmapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShelterInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_info);

        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvCapacity = findViewById(R.id.tvCapacity);
        TextView tvConditions = findViewById(R.id.tvConditions);
        TextView tvAdditional = findViewById(R.id.tvAdditional);

        Bundle shelterInfo = getIntent().getBundleExtra("shelter_info");
        if (shelterInfo != null) {

            tvStatus.setText(ShelterStatus.valueOf(shelterInfo.getString("status")).label);
            tvArea.setText(String.valueOf(shelterInfo.getDouble("area")));
            tvCapacity.setText(String.valueOf(shelterInfo.getInt("capacity")));
            tvAdditional.setText(shelterInfo.getString("additional"));

            List<String> conditionNames = shelterInfo.getStringArrayList("conditions");
            StringBuilder conditionsStr = new StringBuilder();
            if (conditionNames != null) {
                for (String name : conditionNames) {
                    conditionsStr.append(ShelterCondition.valueOf(name).label).append(", ");
                }
            }
            if (conditionsStr.length() > 0) {
                conditionsStr.setLength(conditionsStr.length() - 2);
            }
            tvConditions.setText(conditionsStr.toString());

            Bundle userLocationInfo = getIntent().getBundleExtra("user_location");
            Button routeBtn = findViewById(R.id.btnRoute);
            routeBtn.setOnClickListener(view -> {
                if (userLocationInfo != null) {
                    double startLatitude = userLocationInfo.getDouble("latitude");
                    double startLongitude = userLocationInfo.getDouble("longitude");
                    double endLatitude = shelterInfo.getDouble("latitude");
                    double endLongitude = shelterInfo.getDouble("longitude");
                    Log.i("SHELTER_INFO", startLatitude + " " + startLongitude);
                    Log.i("SHELTER_INFO", endLatitude + " " + endLongitude);
                    String uri = String.format("https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=driving",
                            startLatitude, startLongitude, endLatitude, endLongitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Google Maps не встановлено", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        Button backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }
}
