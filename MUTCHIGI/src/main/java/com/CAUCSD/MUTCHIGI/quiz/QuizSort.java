package com.CAUCSD.MUTCHIGI.quiz;

public enum QuizSort {
    DATEAS(1, "dateAscending"),
    DATEDS(2, "dateDescending"),
    NAMEAS(3, "nameAscending"),
    NAMEDS(4, "nameDescending"),
    VIEWAS(5, "viewAscending"),
    VIEWDS(6, "viewDescending");

    private final int id;
    private final String name;

    QuizSort(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
