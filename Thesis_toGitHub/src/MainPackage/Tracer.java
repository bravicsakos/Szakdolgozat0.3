package MainPackage;

/**
 * Utility class for debugging
 *
 * Contains methods for adding debug 'tracers' easily.
 * @variable basicId : The index of the last added tracer.
 * @variable basicName : Name of the tracer shown in console.
 */
public class Tracer {

    private static int basicId = 1;
    private static final String basicName = "Tracer_";

    /**
     * Add a tracer with base settings.
     */
    public static void addTracer(){
        System.out.println(basicName + basicId);
        basicId++;
    }

    /**
     * Add tracer with specific name.
     *
     * @param name : Name of the tracer shown in console.
     */
    public static void addTracer(String name){
        System.out.println(name + "_" + basicId);
        basicId++;
    }

    /**
     * Add tracer with specific id
     *
     * @param id : Index of the tracer.
     */
    public static void addTracer(int id){
        System.out.println(basicName + "_" + id);
    }
}
