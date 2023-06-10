package mealplanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class Main {
  public static void main(String[] args) throws SQLException, FileNotFoundException {
    Scanner in = new Scanner(System.in);
    Meal inp;
    DB db = new DB();
    String command, input;
    while(true){
      System.out.println("What would you like to do (add, show, plan, save, exit)?");
      command = in.nextLine();
      switch (command){
        case "save":{
          if(!db.isPlanned()){
            System.out.println("Unable to save. Plan your meals first.");
          } else {
            System.out.println("Input a filename:");
            input = in.nextLine();

            db.saveIngredients(input);
            System.out.println("Saved!");
          }
          break;
        }
        case "plan":{
          for(Day day: Day.values()){
            System.out.println(day);
            for(category cat: category.getValues()){
              db.printNames(cat);
              System.out.printf("Choose the %s for %s from the list above:\n", cat, day);
              while(true){
                try{
                  input = in.nextLine();
                  db.choose(day, cat, input);
                  break;
                } catch (Exception e){
                  System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                }
              }
            }
          System.out.printf("Yeah! We planned the meals for %s.\n", day);
          }
          db.printPlan();
          break;
        }
        case "add":{
          inp = new Meal();
          System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
          while(true){
            try{
              inp.setCategory(in.nextLine());
              break;
            } catch (Exception e){
              System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }
          }

          System.out.println("Input the meal's name:");
          while(true){
            try{
              inp.setName(in.nextLine().trim());
              break;
            } catch (Exception e){
              System.out.println("Wrong format. Use letters only!");
            }
          }

          System.out.println("Input the ingredients:");
          while(true){
            try{
              String line = in.nextLine();
              String[] ingrs = line.split(",");
              inp.setIngredients(ingrs);
              break;
            } catch (Exception e){
              System.out.println("Wrong format. Use letters only!");
            }
          }

          db.addMeal(inp);
          System.out.println("The meal has been added!");
          break;
        }
        case "show":{
          System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
          category filter = null;
          while(true){
            try{
              filter = category.valueOf(in.nextLine());
              break;
            } catch(Exception e){
              System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }
          }
          db.printMenu(filter);
          break;
        }
        case "exit":{
          System.out.println("Bye!");
          db.close();
          return;
        }
        default:{
          continue;
        }
      }
    }
  }
}


