package mealplanner;

import mealplanner.category;

public class Meal implements Comparable<Meal>{
    String name;
    category cat;
    String[] ingredients;

    public void setCategory(String cat) {
        this.cat = category.valueOf(cat);
    }

    public void setName(String name) {
        if (!isAlpha(name)) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public void setIngredients(String[] ingrs) {
        for (int i = 0; i < ingrs.length; i++) {
            ingrs[i] = ingrs[i].trim();
            if (!isAlpha(ingrs[i])) {
                throw new IllegalArgumentException();
            }
        }
        this.ingredients = ingrs;
    }

    public String toString() {
        String ans = String.format("\n\nName: %s\nIngredients:\n", name);
        for (String ingr : ingredients) {
            ans += ingr + "\n";
        }
        return ans;
    }

    public boolean isAlpha(String str) {
        if (str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char temp = str.charAt(i);
            if (temp != ' ' && !Character.isLetter(temp)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Meal meal) {
        return name.compareTo(meal.name);
    }
}
