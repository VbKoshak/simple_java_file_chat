package automation;
import automation.classes.c10.Client;
import org.testng.annotations.*;
import static org.testng.AssertJUnit.*;

public class AppTest 
{
    @Test(threadPoolSize = 4, invocationCount = 3, timeOut = 10_000)
    public void MultiClients() {
        Client cl = new Client("127.0.0.1",8000,"user");
        boolean ans = cl.registerXML();
        assertEquals(true, ans);
    }
}
