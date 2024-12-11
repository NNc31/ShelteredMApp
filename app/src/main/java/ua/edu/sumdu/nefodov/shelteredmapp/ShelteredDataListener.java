package ua.edu.sumdu.nefodov.shelteredmapp;

import java.util.List;

public interface ShelteredDataListener {
    void onSheltersReceived(List<Shelter> shelters);
}
