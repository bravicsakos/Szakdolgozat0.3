package MainPackage;

public class Tracer {

    private static int basicId = 1;
    private static final String basicName = "Tracer_";

    public static void addTracer(){
        System.out.println(basicName + basicId);
        basicId++;
    }

    public static void addTracer(String name){
        System.out.println(name + "_" + basicId);
        basicId++;
    }

    public static void addTracer(int id){
        System.out.println(basicName + "_" + id);
        basicId++;
    }

    public static void countTracers(){
        System.out.println("Number of active Tracers: " + basicId);
    }
}
