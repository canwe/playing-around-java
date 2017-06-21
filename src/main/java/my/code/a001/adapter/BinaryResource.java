package my.code.a001.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Internal binary resource description object, represents binary file from a
 * certain URL.
 * <p>
 * Let's say a structure to keep all resource referenced from a web page, to be
 * packed in one archive for offline web page rendering, or system for automatic
 * web site (or other network node) replication.
 */
public class BinaryResource {

	/**
	 * Example: "http://upload.wikimedia.org/wikipedia/commons/d/d7/ObjectAdapter.png"
	 */
	private String url;

	/**
	 * Example: "596175812392525317"
	 */
	private String internalId;

	/**
	 * MIME type of content, example: "image/jpeg"
	 */
	private String contentType;

	/**
	 * Size of the binary resource (content length)
	 */
	private Long fileSize = null;

	/**
	 * Eventual content when/if loaded into to memory
	 * (preferentially not to memory heap)
	 */
	private byte[] content;

	public BinaryResource(String url, String internalId, String contentType, Long fileSize) {
		this.url = url;
		this.internalId = internalId;
		this.contentType = contentType;
		this.fileSize = fileSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public InputStream getContentAsStream() {
		return new ByteArrayInputStream(content);
	}
}