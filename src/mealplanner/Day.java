package mealplanner;

public enum Day {
    Mon("Monday"), Tue("Tuesday"), Wed("Wednesday"),
    Thu("Thursday"), Fri("Friday"), Sat("Saturday"),
    Sun("Sunday");
    String name;
    Day(String name){this.name = name;}
    public String toString(){return name;}
}