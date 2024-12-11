package ua.edu.sumdu.nefodov.shelteredmapp;

public enum ShelterCondition {
    WATER("Вода"),
    FOOD("Їжа"),
    ELECTRICITY("Електрика"),
    SEATS("Місця для сидіння"),
    WIFI("Вай-фай"),
    SOCKETS("Розетки"),
    RADIATION_PROTECTED("Протирадіаційне"),
    LIGHTING("Освітлення"),
    MEDICINES("Медикаменти");

    public final String label;

    ShelterCondition(String label) {
        this.label = label;
    }
}