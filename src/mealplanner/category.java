package mealplanner;

import java.util.ArrayList;
import java.util.List;

public enum category {
    lunch("lunch"), breakfast("breakfast"), dinner("dinner");
    String name;

    category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static ArrayList<category> getValues(){
        return new ArrayList<>(List.of(
                category.breakfast,
                category.lunch,
                category.dinner
        ));
    }
}