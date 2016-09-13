package test;

import java.util.UUID;

/**
 *
 * @author RAFIS-FRED
 */
public class RandomString {

    public static void main(String[] args) {
        
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        System.out.println(id1.toString());
        System.out.println(id2.toString());
        System.out.println(id3.toString());
        
    }
    
}
