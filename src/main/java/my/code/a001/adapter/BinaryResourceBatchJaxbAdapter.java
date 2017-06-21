package my.code.a001.adapter;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import my.code.a001.listadapter.AbstractImmutableAdapterList;

/**
 * Root element of mapping Adapters for a batch of {@link BinaryResource}s to
 * structure required by some externally system. Adapt this batch of
 * {@link BinaryResource}s for serialization to XML by adding JAXB annotations.
 * <p>
 * Purpose of this adapter:
 * <ul>
 * <li>mapping of a simple flat class to more complex structure required
 * externally
 * <li>prevent "polluting" core class (adaptee) with excessive annotations.
 * There may be other adapters for different systems and formats (Jackson,
 * binary object stream ..)
 * </ul>
 * <p>
 * Let's assume that batch is part of wider batch processing XML standard and by
 * this adapter only minimal required relevant parts is implemented
 * <p>
 * Also note that you can have several classes in one Java file. Just that only
 * one of them can be public. Yes, you can do that i Java. Not widely known or
 * used java feature.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "batch")
public class BinaryResourceBatchJaxbAdapter {

	private List<BinaryResource> resourceList;

	public BinaryResourceBatchJaxbAdapter() {
		this.resourceList = null;
	}

	public BinaryResourceBatchJaxbAdapter(List<BinaryResource> resourceList) {
		this.resourceList = resourceList;
	}

	@XmlElement(name = "batch-item")
	public List<BatchItem> getBatchItems() {
		return new AbstractImmutableAdapterList<BinaryResource, BatchItem>(resourceList) {
			@Override
			protected BatchItem transformItem(BinaryResource item) {
				return new BatchItem(item);
			}
		};
	}
}

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "batch-item", propOrder = { "metadata", "batchFile" })
class BatchItem {

	private BinaryResource binaryResource;

	public BatchItem() {
	}

	protected BatchItem(BinaryResource binaryResource) {
		this.binaryResource = binaryResource;
	}

	@XmlAttribute(name = "action")
	public String getBatchAction() {
		if (BatchAdapterHelper.isCorrectItem(binaryResource)) {
			return "PUT";
		} else {
			return "ERROR";
		}
	}

	@XmlAttribute
	public String getId() {
		return binaryResource.getInternalId();
	}

	@XmlElement(name = "metadata")
	public Metadata getMetadata() {
		return new Metadata(binaryResource);
	}

	@XmlElement(name = "batch-file")
	public BatchFile getBatchFile() {
		if (BatchAdapterHelper.areFullMetadataKnown(binaryResource)) {
			return new BatchFile(binaryResource);
		} else {
			return null;
		}
	}
}

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metadata", propOrder = { "contentLength", "contentType", "originUrl" })
class Metadata {

	private BinaryResource binaryResource;

	public Metadata() {

	}

	protected Metadata(BinaryResource binaryResource) {
		this.binaryResource = binaryResource;
	}

	@XmlElement(name = "conten-length")
	public ContentLength getContentLength() {
		if (BatchAdapterHelper.areFullMetadataKnown(binaryResource)) {
			return new ContentLength(binaryResource);
		} else {
			return null;
		}
	}

	@XmlElement(name = "conten-type")
	public ContentType getContentType() {
		if (BatchAdapterHelper.areFullMetadataKnown(binaryResource)) {
			return new ContentType(binaryResource);
		} else {
			return null;
		}
	}

	/**
	 * @return {@link BinaryResource#getUrl()}
	 */
	@XmlElement(name = "origin")
	public String getOriginUrl() {
		return binaryResource.getUrl();
	}
}

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "batch-file")
class BatchFile {

	private BinaryResource binaryResource;

	public BatchFile() {

	}

	BatchFile(BinaryResource binaryResource) {
		this.binaryResource = binaryResource;
	}

	@XmlAttribute(name = "local-name")
	public String getLocalName() {
		return BatchAdapterHelper.composeLocalFileName(binaryResource);
	}
}

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "content-length")
class ContentLength {

	private BinaryResource binaryResource;

	public ContentLength() {

	}

	ContentLength(BinaryResource binaryResource) {
		this.binaryResource = binaryResource;
	}

	/**
	 * @return {@link BinaryResource#getFileSize()}
	 */
	@XmlValue
	public long getValue() {
		return binaryResource.getFileSize();
	}
}

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "content-type")
class ContentType {

	private BinaryResource binaryResource;

	public ContentType() {

	}

	ContentType(BinaryResource binaryResource) {
		this.binaryResource = binaryResource;
	}

	/**
	 * @return {@link BinaryResource#getContentType()}
	 */
	@XmlValue
	public String getValue() {
		return binaryResource.getContentType();
	}
}

/**
 * Helper class. Static class is not ideal form.
 */
class BatchAdapterHelper {

	/** simple error state detector */
	public static boolean isCorrectItem(BinaryResource binaryResource) {
		return (binaryResource.getFileSize() != null);
	}

	/**
	 * are file length and content type known? if not then do not serialize
	 * certain parts of structure
	 */
	public static boolean areFullMetadataKnown(BinaryResource binaryResource) {
		return (binaryResource.getFileSize() != null);
	}

	/** compose file name */
	public static String composeLocalFileName(BinaryResource binaryResource) {
		return binaryResource.getInternalId() + "." + getSuitableSuffix(binaryResource.getContentType());
	}

	/**
	 * Trivial implementation of MIME type to suitable file suffix Example:
	 * "image/jpeg" returns "jpg"
	 */
	public static String getSuitableSuffix(String mimeType) {
		if (mimeType == null) {
			return null;
		}
		else if (mimeType.equals("image/jpeg")) {
			return "jpg";
		}
		else if (mimeType.equals("image/png")) {
			return "png";
		}
		else {
			return "dat";
		}

	}
}