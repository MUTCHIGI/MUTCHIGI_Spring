package com.CAUCSD.MUTCHIGI.quiz;

public enum Instrument {
    VOCAL(1, "Vocal"),
    BASS(2, "Bass"),
    DRUM(3, "Drum"),
    OTHER(4, "Other"),
    NONE(5, "NONE");

    private final int id;
    private final String name;

    Instrument(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
