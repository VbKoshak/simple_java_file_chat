package automation.io.impl.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileReader;
import java.io.IOException;

public class XMLUnmarshaller {
    public XMLMessage unmarshallBook(String pathTo) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(XMLMessage.class);
        return (XMLMessage) context.createUnmarshaller()
                .unmarshal(new FileReader(pathTo));
    }
}
