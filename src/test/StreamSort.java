package test;

import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author RAFIS-FRED
 */
public class StreamSort {

    public static void main(String[] args) {
        ObservableList<Person> persons = FXCollections.observableArrayList(
                new Person("Anna", 35),
                new Person("Letty", 27),
                new Person("Ronny", 45),
                new Person("Jannah", 20)
        );
        
        persons.stream().sorted(Comparator.comparing(p -> p.age)).forEach(System.out::println);
    }
    
    static class Person {
        String name;
        int age;
        
        Person(String n, int a){
            name = n;
            age = a;
        }
        
        @Override
        public String toString(){
            return String.format("Person[%s, %d]", name, age);
        }
    }
}
