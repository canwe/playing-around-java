package my.code.a001.adapter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Test jaxb mapping proxy of {@link BinaryResourceBatchJaxbAdapter} for list of
 * {@link BinaryResource}s. For perhaps better view of target structure, that is
 * of the batch, see test {@link BinaryResourceBatchSerializerTest}.
 */
public class BinaryResourceBatchAdapterTest {

	@Test
	public void testBinaryResourceBatchAdapterWithOneElement() throws Exception {

		// prepare data
		List<BinaryResource> batch = new LinkedList<BinaryResource>();
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png",
			"596175812392525317",
			"image/png", 11_152L));

		// create proxy
		BinaryResourceBatchJaxbAdapter batchAdapter = new BinaryResourceBatchJaxbAdapter(batch);

		// test mapping
		BatchItem batchItem0 = batchAdapter.getBatchItems().get(0);
		assertThat(batchAdapter.getBatchItems(), notNullValue());
		assertThat(batchAdapter.getBatchItems().size(), equalTo(1));
		assertThat(batchItem0, notNullValue());
		assertThat(batchItem0.getBatchAction(), equalTo("PUT"));
		assertThat(batchItem0.getId(), equalTo("596175812392525317"));
		assertThat(batchItem0.getMetadata(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType().getValue(), equalTo("image/png"));
		assertThat(batchItem0.getMetadata().getContentLength(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentLength().getValue(), equalTo(11_152L));
		assertThat(batchItem0.getMetadata().getOriginUrl(),
			equalTo("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png"));
		assertThat(batchItem0.getBatchFile().getLocalName(), equalTo("596175812392525317.png"));
	}

	@Test
	public void testBinaryResourceBatchAdapterWithTwoElements() throws Exception {
		// prepare data
		List<BinaryResource> batch = new LinkedList<BinaryResource>();
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png",
			"596175812392525317",
			"image/png", 11_152L));
		batch
			.add(new BinaryResource(
				"http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg",
				"140575992557512440",
				"image/jpeg", 10_994L));

		// create proxy
		BinaryResourceBatchJaxbAdapter batchAdapter = new BinaryResourceBatchJaxbAdapter(batch);

		// test mapping
		assertThat(batchAdapter.getBatchItems(), notNullValue());
		assertThat(batchAdapter.getBatchItems().size(), equalTo(2));

		BatchItem batchItem0 = batchAdapter.getBatchItems().get(0);
		assertThat(batchItem0, notNullValue());
		assertThat(batchItem0.getBatchAction(), equalTo("PUT"));
		assertThat(batchItem0.getId(), equalTo("596175812392525317"));
		assertThat(batchItem0.getMetadata(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType().getValue(), equalTo("image/png"));
		assertThat(batchItem0.getMetadata().getContentLength(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentLength().getValue(), equalTo(11_152L));
		assertThat(batchItem0.getMetadata().getOriginUrl(),
			equalTo("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png"));
		assertThat(batchItem0.getBatchFile().getLocalName(), equalTo("596175812392525317.png"));

		BatchItem batchItem1 = batchAdapter.getBatchItems().get(1);
		assertThat(batchItem1, notNullValue());
		assertThat(batchItem1.getBatchAction(), equalTo("PUT"));
		assertThat(batchItem1.getId(), equalTo("140575992557512440"));
		assertThat(batchItem1.getMetadata(), notNullValue());
		assertThat(batchItem1.getMetadata().getContentType(), notNullValue());
		assertThat(batchItem1.getMetadata().getContentType().getValue(), equalTo("image/jpeg"));
		assertThat(batchItem1.getMetadata().getContentLength(), notNullValue());
		assertThat(batchItem1.getMetadata().getContentLength().getValue(), equalTo(10_994L));
		assertThat(
			batchItem1.getMetadata().getOriginUrl(),
			equalTo("http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg"));
		assertThat(batchItem1.getBatchFile().getLocalName(), equalTo("140575992557512440.jpg"));
	}

	@Test
	public void testBinaryResourceBatchAdapterWithTwoElementsWhereOneIsBroken() throws Exception {
		// prepare data
		List<BinaryResource> batch = new LinkedList<BinaryResource>();
		batch.add(new BinaryResource("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png",
			"596175812392525317",
			"image/png", 11_152L));
		// assumed broken link - size and type is unknown 
		batch
			.add(new BinaryResource(
				"http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg",
				"140575992557512440",
				null, null));

		// create proxy
		BinaryResourceBatchJaxbAdapter batchAdapter = new BinaryResourceBatchJaxbAdapter(batch);

		// test mapping
		assertThat(batchAdapter.getBatchItems(), notNullValue());
		assertThat(batchAdapter.getBatchItems().size(), equalTo(2));

		BatchItem batchItem0 = batchAdapter.getBatchItems().get(0);
		assertThat(batchItem0, notNullValue());
		assertThat(batchItem0.getBatchAction(), equalTo("PUT"));
		assertThat(batchItem0.getId(), equalTo("596175812392525317"));
		assertThat(batchItem0.getMetadata(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentType().getValue(), equalTo("image/png"));
		assertThat(batchItem0.getMetadata().getContentLength(), notNullValue());
		assertThat(batchItem0.getMetadata().getContentLength().getValue(), equalTo(11_152L));
		assertThat(batchItem0.getMetadata().getOriginUrl(),
			equalTo("http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png"));
		assertThat(batchItem0.getBatchFile().getLocalName(), equalTo("596175812392525317.png"));

		BatchItem batchItem1 = batchAdapter.getBatchItems().get(1);
		assertThat(batchItem1, notNullValue());
		assertThat(batchItem1.getBatchAction(), equalTo("ERROR"));
		assertThat(batchItem1.getId(), equalTo("140575992557512440"));
		assertThat(batchItem1.getMetadata(), notNullValue());
		assertThat(batchItem1.getMetadata().getContentType(), nullValue());
		assertThat(batchItem1.getMetadata().getContentLength(), nullValue());
		assertThat(
			batchItem1.getMetadata().getOriginUrl(),
			equalTo("http://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/Adapter_2011-02-27.jpg/220px-Adapter_2011-02-27.jpg"));
		assertThat(batchItem1.getBatchFile(), nullValue());
	}

}
