package my.code.a001.adapter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Serialize (marshall) batch of {@link BinaryResource} to XML
 * 
 * @author vkanopelko
 */
public class BinaryResourceBatchSerializer {

	/**
	 * Serialize (marshall) batch of {@link BinaryResource} to XML string.
	 * 
	 * @return XML as String
	 */
	public String serialize(List<BinaryResource> batch) throws JAXBException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JAXBContext jaxbContext = JAXBContext.newInstance(BinaryResourceBatchJaxbAdapter.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // set pretty print
		BinaryResourceBatchJaxbAdapter batchAdapter = new BinaryResourceBatchJaxbAdapter(batch);
		jaxbMarshaller.marshal(batchAdapter, outputStream);
		return new String(outputStream.toByteArray());
	}
}
