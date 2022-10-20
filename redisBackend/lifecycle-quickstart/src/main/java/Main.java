import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

// Graceful Shutdown is off in current build

@QuarkusMain
public class Main {
    public static void main(String ... args) {
        System.out.println("Running main method!!");
        Quarkus.run(args);
    }

    // Hur kan jag nå min fina shutdown metod? Vet inte om Quarkus kör på någon port ifall jag har en main metod(?)
    public static void shutdown(){
        System.exit(0);
    }





}