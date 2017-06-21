package my.code.a001.adapter;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Test {@link BinaryResourceBatchSerializer} and also indirectly {@link BinaryResourceBatchJaxbAdapter}.
 * @author vkanopelko
 */
public class BinaryResourceBatchSerializerTest {

	private BinaryResourceBatchSerializer binaryResourceBatchSerializer = new BinaryResourceBatchSerializer();

	@Test
	public void testSerializationToXml_twoOkElements() throws Exception {
		// prepare data
		List<BinaryResource> batch = new LinkedList<>();
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png",
			"596175812392525317", "image/png", 10_000L));
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg",
			"140575992557512440", "image/jpeg", 14_000L));
		
		// tested method
		String result = binaryResourceBatchSerializer.serialize(batch);
		
		// assert results
		assertEquals("" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<batch>\n" +
			"    <batch-item action=\"PUT\" id=\"596175812392525317\">\n" +
			"        <metadata>\n" +
			"            <conten-length>10000</conten-length>\n" +
			"            <conten-type>image/png</conten-type>\n" +
			"            <origin>http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png</origin>\n" +
			"        </metadata>\n" +
			"        <batch-file local-name=\"596175812392525317.png\"/>\n" +
			"    </batch-item>\n" +
			"    <batch-item action=\"PUT\" id=\"140575992557512440\">\n" +
			"        <metadata>\n" +
			"            <conten-length>14000</conten-length>\n" +
			"            <conten-type>image/jpeg</conten-type>\n" +
			"            <origin>http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg</origin>\n" +			
			"        </metadata>\n" +
			"        <batch-file local-name=\"140575992557512440.jpg\"/>\n" +
			"    </batch-item>\n" +
			"</batch>\n" +
			"",
			result);
	}
	
	@Test
	public void testSerializationToXml_twoOkElementsOneError() throws Exception {
		// prepare data
		// 2 batch items OK, 1 batch item incomplete, see the nulls in constructor (assume invalid link)
		List<BinaryResource> batch = new LinkedList<>();
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png",
			"596175812392525317", "image/png", 10_000L));
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg",
			"140575992557512440", "image/jpeg", 14_000L));
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/foofoofoo.jpg",
			"486965078511476995", null, null));
		
		// tested method
		String result = binaryResourceBatchSerializer.serialize(batch);
		
		// assert results
		// incomplete record should be serialized but with ERROR action and only URL in metadata and no local-file 
		assertEquals("" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<batch>\n" +
			"    <batch-item action=\"PUT\" id=\"596175812392525317\">\n" +
			"        <metadata>\n" +
			"            <conten-length>10000</conten-length>\n" +
			"            <conten-type>image/png</conten-type>\n" +
			"            <origin>http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png</origin>\n" +
			"        </metadata>\n" +
			"        <batch-file local-name=\"596175812392525317.png\"/>\n" +
			"    </batch-item>\n" +
			"    <batch-item action=\"PUT\" id=\"140575992557512440\">\n" +
			"        <metadata>\n" +
			"            <conten-length>14000</conten-length>\n" +
			"            <conten-type>image/jpeg</conten-type>\n" +
			"            <origin>http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg</origin>\n" +			
			"        </metadata>\n" +
			"        <batch-file local-name=\"140575992557512440.jpg\"/>\n" +
			"    </batch-item>\n" +
			"    <batch-item action=\"ERROR\" id=\"486965078511476995\">\n" +
			"        <metadata>\n" +
			"            <origin>http://upload.wikimedia.org/wikipedia/commons/foofoofoo.jpg</origin>\n" +			
			"        </metadata>\n" +
			"    </batch-item>\n" +
			"</batch>\n" +
			"",
			result);
	}
}
