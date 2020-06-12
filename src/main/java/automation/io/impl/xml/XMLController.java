package automation.io.impl.xml;

import automation.io.exception.UnableToWriteException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLController {
    public static void sendMessage(XMLMessage msg, String localPath) {
        try {
            new XMLMarshaller().marshall(msg, System.getProperty("user.dir") + localPath);
        } catch (UnableToWriteException uwe) {
            uwe.printStackTrace();
            throw new RuntimeException("Bad object type!");
        } catch (JAXBException jaxe) {
            jaxe.printStackTrace();
            throw new RuntimeException("Something went wrong while marshalling!");
        }
    }

    public static XMLMessage readMessage(String localPath) {
        try {
            return new XMLUnmarshaller().unmarshallBook(System.getProperty("user.dir") + localPath);
        } catch (IOException | JAXBException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Something went wrong while unmarshalling!");
        }
    }

    public static void main(String[] args) {
        List<String> str = new ArrayList<String>();
        str.add("hi");
        str.add("there");
        System.out.println(str.toString());
    }
}
