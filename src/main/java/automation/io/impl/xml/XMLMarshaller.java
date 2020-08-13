package automation.io.impl.xml;

import automation.io.exception.UnableToWriteException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

public class XMLMarshaller {
    public void marshall(Object obj, String pathTo) throws UnableToWriteException, JAXBException {
        if (obj.getClass().getAnnotation(XmlRootElement.class) == null) {
            throw new UnableToWriteException("Cannot write object!");
        }
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(obj, new File(pathTo).getAbsoluteFile());
    }
}
