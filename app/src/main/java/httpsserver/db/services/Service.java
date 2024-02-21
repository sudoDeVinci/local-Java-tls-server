package httpsserver.db.services;

/**
 * This is just to give the Services a unifying type so i can pass them into methods and call the same
 * methods on them. 
 * Services are made up of static methods, meaning their structure cannot be enforced with interfaces/abstract classes.
 */
public abstract class Service {

    /**
     * public static List<Entity> getAll();
     */

    /**
     * public static Entity get(String MAC or String timestamp);
     */ 

    /**
     * public static void add(...);
     */

    /**
     * public static void update(...); 
     */

    /**
     * public static void delete(String MAC or String timestamp);
     */

    /**
     * public static boolean exists(String MAC or String timestamp);
     */
}