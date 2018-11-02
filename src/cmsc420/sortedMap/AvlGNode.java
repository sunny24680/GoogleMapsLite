package cmsc420.sortedMap;

import java.util.AbstractMap;
import java.util.SortedMap;

public class AvlGNode<K, V> extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V>{
	private V data;
	protected int height;
	AvlGNode<K, V> left, right, parent;
	
	public AvlGNode(K key, V data, AvlGNode<K, V> parent) {
		super(key, data);
		left = null;
		right = null;
		height = 1;
		this.parent = parent;
	}

	public boolean equals(Object o) {
		if (!(o instanceof	AvlGNode))
			return false;
		AvlGNode<K,V> e = (AvlGNode<K,V>)o;
		
		return this.getKey().equals(e.getKey()) && this.getValue().equals(e.getValue());
	}
	
	public int hashCode() {
		int keyHash = (getKey()==null ? 0 : getKey().hashCode());
		int valueHash = (getValue()==null ? 0 : getValue().hashCode());
		return keyHash ^ valueHash;
	}
	
	public String toString() {
		return getKey() + "=" + getValue();
	}
		 
}
