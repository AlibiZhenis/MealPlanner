package mealplanner;

import mealplanner.Day;
import mealplanner.Meal;
import mealplanner.category;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

class DB {
    private String DB_URL = "jdbc:postgresql:meals_db";
    private String USER = "postgres";
    private String PASS = "1111";
    private Connection connection;

    public DB() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();

        statement.executeUpdate("create table if not exists meals (" +
                "category VARCHAR," +
                "meal VARCHAR," +
                "meal_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY)");

        statement.executeUpdate("create table if not exists ingredients (" +
                "ingredient VARCHAR," +
                "ingredient_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "meal_id INTEGER," +
                "FOREIGN KEY (meal_id) REFERENCES meals (meal_id))");

        statement.executeUpdate("create table if not exists plan (" +
                "category VARCHAR," +
                "meal VARCHAR," +
                "meal_id INTEGER," +
                "weekDay VARCHAR," +
                "FOREIGN KEY (meal_id) REFERENCES meals (meal_id)," +
                "PRIMARY KEY(category, weekDay))");
        statement.close();
    }

    public Boolean isPlanned() throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("select count(*) from plan");
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        int num = rs.getInt("COUNT");
        statement.close();
        if(num==21){
            return true;
        }
        return false;
    }

    public void saveIngredients(String filename) throws SQLException, FileNotFoundException {
        Hashtable<String, Integer> hashtable = new Hashtable<>();
        Statement statement = connection.createStatement();
        String query = String.format("select * from plan");
        ResultSet rs = statement.executeQuery(query);

        while(rs.next()){
            int meal_id = rs.getInt("meal_id");
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery("select * from ingredients WHERE meal_id=" + meal_id);
            while (rs2.next()) {
                String ingr = rs2.getString("ingredient");
                if(hashtable.containsKey(ingr)){
                    int num = hashtable.get(ingr);
                    hashtable.put(ingr, num+1);
                } else{
                    hashtable.put(ingr, 1);
                }
            }
            statement2.close();
        }
        statement.close();


        PrintWriter writer = new PrintWriter(filename);

        for(String ingr: hashtable.keySet()){
            int num = hashtable.get(ingr);
            if(num == 1){
                writer.println(ingr);
            } else{
                writer.println(ingr+" x"+num);
            }
        }

        writer.flush();
        writer.close();
    }

    public void addMeal(Meal meal) throws SQLException {
        Statement statement = connection.createStatement();
        int meal_id = -1;

        String query = String.format("insert into meals (category, meal) " +
                "values ('%s', '%s')", meal.cat, meal.name);
        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            meal_id = rs.getInt("meal_id");
        }

        for (String ingr : meal.ingredients) {
            query = String.format("insert into ingredients (ingredient, meal_id) " +
                    "values ('%s', %d)", ingr, meal_id);
            statement.executeUpdate(query);
        }
        statement.close();
    }

    public void printNames(category filter) throws SQLException {
        Statement statement = connection.createStatement();

        String query = String.format("select * from meals where category='%s'", filter);
        ResultSet rs = statement.executeQuery(query);

        if (!rs.next()) {
            System.out.println("\nNo meals found.");
            return;
        }

        String temp;
        ArrayList<String> res = new ArrayList<>();
        do {
            temp = rs.getString("meal");
            res.add(temp);
        } while (rs.next());

        Collections.sort(res);

        for(String meal: res){
            System.out.println(meal);
        }

        statement.close();
    }

    public void printMenu(category filter) throws SQLException {
        Statement statement = connection.createStatement();

        String query = String.format("select * from meals where category='%s'", filter);
        ResultSet rs = statement.executeQuery(query);

        if (!rs.next()) {
            System.out.println("\nNo meals found.");
            return;
        }

        Meal temp;
        ArrayList<Meal> res = new ArrayList<>();
        do {
            temp = new Meal();
            temp.setCategory(rs.getString("category"));
            temp.setName(rs.getString("meal"));
            int meal_id = rs.getInt("meal_id");

            ArrayList<String> ingrs = new ArrayList<>();
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery("select * from ingredients WHERE meal_id=" + meal_id);
            while (rs2.next()) {
                ingrs.add(rs2.getString("ingredient"));
            }

            temp.setIngredients(ingrs.toArray(new String[ingrs.size()]));
            res.add(temp);
            statement2.close();
        } while (rs.next());

        System.out.print("Category: " + filter);
        for(Meal meal: res){
            System.out.print(meal);
        }

        statement.close();
    }
    public void close() throws SQLException {
        connection.close();
    }
    public void choose(Day day, category cat, String name) throws SQLException {
        int meal_id = getMealID(name, cat);
        if(meal_id == -1){
            throw new SQLException();
        }
        Statement statement = connection.createStatement();
        String command1 = String.format("update plan set meal='%s' where category='%s' and ", name, cat);

        try{
            String command = String.format("insert into plan (category, meal, meal_id, weekDay)" +
                    "values ('%s', '%s', %d, '%s')", cat, name, meal_id, day);
            statement.executeUpdate(command);
        } catch (Exception e){
            String command = String.format("update plan set meal='%s' where category='%s' and " +
                    "weekDay='%s'", name, cat, day);
            statement.executeUpdate(command);
        }
    }
    public int getMealID(String name, category cat) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("select meal_id from meals where meal='%s' and category='%s'", name, cat);
        ResultSet rs = statement.executeQuery(query);

        if(!rs.next()){
            return -1;
        }
        int ans = rs.getInt("meal_id");
        statement.close();
        return ans;
    }
    public String getMealNamePlan(category cat, Day day) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("select meal from plan where weekDay='%s' and category='%s'", day, cat);
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        String ans = rs.getString("meal");
        statement.close();
        return ans;
    }
    public void printPlan() throws SQLException {
        for(Day day: Day.values()){
            System.out.println("\n"+day);
            for(category cat: category.getValues()){
                String res = getMealNamePlan(cat, day);
                System.out.println(cat+": "+res);
            }
        }
    }
}