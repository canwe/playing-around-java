package my.code.a001.listadapter;

import java.util.AbstractList;
import java.util.List;

/**
 * List with transformation to a different type on demand. Transformation is
 * provided by overriding {@link #transformItem(Object)}.
 * 
 * List adaptor is often overlooked adaptor pattern application but it is a
 * handy alternative to transformation loops and building additional lists just
 * to adapt individual items.
 * 
 * @param <T1>
 *            type/class of item in the original list
 * @param <T2>
 *            type/class of item for the adapted/transformed list
 */
public abstract class AbstractImmutableAdapterList<T1, T2> extends AbstractList<T2> {

	/**
	 * Adaptee, wrapped original list
	 */
	private final List<T1> originalList;

	/**
	 * Setting constructor
	 * 
	 * @param originalList
	 *            list to be wrapped and its lements transformed
	 */
	public AbstractImmutableAdapterList(List<T1> originalList) {
		this.originalList = originalList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T2 get(int index) {
		return transformItem(originalList.get(index));
	}

	/**
	 * Transform given item from original type to adapted (target) type.
	 * 
	 * @param item
	 *            item to be transformed
	 * @return transformed item, or adapter
	 */
	protected abstract T2 transformItem(T1 item);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T2 set(int index, T2 element) {
		throw new UnsupportedOperationException("Attempt to change immutable list");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return originalList.size();
	}
}
