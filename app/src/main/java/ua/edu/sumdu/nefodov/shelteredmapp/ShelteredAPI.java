package ua.edu.sumdu.nefodov.shelteredmapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShelteredAPI {

    private static ShelteredAPI instance;
    private static final String SHELTERED_API_BASE_URL = "http://10.0.2.2:8080/api/shelters/";
    private final OkHttpClient client = new OkHttpClient();

    private ShelteredAPI() {
    }

    public static ShelteredAPI getInstance() {
        if (instance == null) {
            instance = new ShelteredAPI();
        }
        return instance;
    }

    public void requestShelters(ShelteredDataListener listener) {
        Request request = new Request.Builder()
                .url(SHELTERED_API_BASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray sheltersJson = new JSONArray(response.body().string());
                        List<Shelter> shelters = new ArrayList<>(sheltersJson.length());
                        for (int i = 0; i < sheltersJson.length(); i++) {
                            JSONObject shelterJson = sheltersJson.getJSONObject(i);
                            JSONObject coordinatesJson = shelterJson.getJSONObject("coordinates");
                            Shelter shelter = new Shelter();
                            shelter.setLatitude(coordinatesJson.getDouble("latitude"));
                            shelter.setLongitude(coordinatesJson.getDouble("longitude"));
                            shelter.setStatus(ShelterStatus.valueOf(shelterJson.getString("status")));
                            JSONArray conditionsJson = shelterJson.getJSONArray("conditions");
                            List<ShelterCondition> conditions = new ArrayList<>(conditionsJson.length());
                            for (int j = 0; j < conditionsJson.length(); j++) {
                                ShelterCondition sc = ShelterCondition.valueOf(conditionsJson.getString(j));
                                conditions.add(sc);
                            }
                            shelter.setConditions(conditions);
                            shelter.setCapacity(shelterJson.getInt("capacity"));
                            shelter.setArea(shelterJson.getDouble("area"));
                            shelter.setAdditional(shelterJson.getString("additional"));
                            shelters.add(shelter);
                        }
                        listener.onSheltersReceived(shelters);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
