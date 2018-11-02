package cmsc420.sortedMap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;


/*
 * PUBLIC METHODS 
 * 		put
 * 		size
 * 		isEmpty
 * 		containsKey
 * 		containsValue
 * 		get 
 * 		putAll
 * 		clear
 * 		comparator
 * 		firstKey
 * 		lastKey
 * 		subMap
 * 		entrySet
 * 
 * NEED TO DO
 * 		subMap
 * 		
 * FUTURE METHODS 
 * 		remove
 * 		headMap
 * 		keySet
 * 		tailMap
 * 		values
 */

public class AvlGTree<K,V> extends AbstractMap<K,V> implements SortedMap<K,V>{
	AvlGNode<K,V> root;
	EntrySet entrySet = null;
	private int g = 1;
	private int modCount,size = 0;
	Comparator<? super K> comparator;
	
	public AvlGTree(final int g) {
		this.g = g;
		this.comparator = null;
	}
	
	public AvlGTree(final int g, final Comparator<K> comparator) {
		this.g= g;
		this.comparator = comparator;
	}
    
	@Override
	public V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		insert(key, value, root, null);
		modCount++;
		return null;
	}
	
	@Override
	public int size() {
		return size(root);
	}
	
	@Override
	public boolean isEmpty() {
		return (root == null) ? false : true;
	}

	@Override
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		for (AvlGNode<K,V> e = getFirstEntry(); e != null; e = successor(e))
			if (value.equals(e.getValue()))
				return true;
		return false;
	}

	@Override
	public V get(Object key) {
		AvlGNode<K,V> p = getEntry(key);
		return (p==null ? null : p.getValue());
	}

	@Override
	public void putAll(Map m) {
		for (Object key: m.keySet()) {
			put((K) key, (V) m.get(key));
		}
			
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
		comparator = null;
		modCount++;
	}

	@Override
	public Comparator<? super K> comparator() {
		return this.comparator;
	}

	@Override
	public K firstKey() {
		return getFirstEntry().getKey();
	}

	@Override
	public K lastKey() {
		return getLastEntry().getKey();
	}

	@Override
	public SortedMap<K,V> subMap(Object fromKey, Object toKey) {
		return null;
	}
	
	@Override
	public Set entrySet() {
		EntrySet es = entrySet;
		return (es != null) ? es : (entrySet = new EntrySet());
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Part 3 Methods or unneeded 
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@Override
	public V remove(Object key) throws UnsupportedOperationException{	
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set keySet() throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection values() throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedMap headMap(Object toKey) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedMap tailMap(Object fromKey) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Methods not visible to public
	 *  helper functions 
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	private AvlGNode<K, V> insert(K key, V data, AvlGNode<K, V> cur, AvlGNode<K, V> parent)
    {
		//adds the node to the root/end of the tree
        if (cur == null)
            cur = new AvlGNode<K, V>(key, data, parent);
        else { 
        	//adding to the left side of the tree
        	if (compare(key, cur.getKey()) < 0) {
	            cur.left = insert(key, data, cur.left, cur);
	            
	            //checks height difference 
	            if(cur.left.height - cur.right.height > g)
	                if(compare(key, (K) cur.left.getKey()) < 0 )
	                    cur = rotateLeftChild(cur);
	                else
	                    cur = rotateLeftRightChild(cur);
        	} 
        	else {
	        	//add to the right subtree
	        	cur.right = insert(key, data, cur.right, cur);
	            if(cur.right.height - cur.left.height > g)
	                if(compare(key,  (K) cur.right.getKey()) > 0) {
	                	//if an outside case
	                    cur = rotateRightChild(cur);
	                }
	                else {
	                	//if an inside case
	                    cur = rotateRightLeftChild(cur);
	                }
	        }
        }
	    cur.height = Math.max(cur.left.height, cur.right.height) + 1;
	    return cur;
    }
	
	private AvlGNode<K, V> rotateLeftChild(AvlGNode<K, V> pivot)
    {
        AvlGNode<K, V> temp = pivot.left;
        pivot.left = temp.right;
        temp.right = pivot;
        pivot.height = Math.max(pivot.left.height, pivot.right.height) + 1;
        temp.height = Math.max(temp.left.height, pivot.height) + 1;
        return temp;
    }
	
	private AvlGNode<K, V> rotateRightChild(AvlGNode<K, V> pivot)
    {
        AvlGNode<K, V> temp = pivot.right;
        pivot.right = temp.left;
        temp.left = pivot;
        pivot.height = Math.max(pivot.left.height, pivot.right.height) + 1;
        temp.height = Math.max(temp.left.height, pivot.height) + 1;
        return temp;
    }
	
	private AvlGNode<K, V> rotateLeftRightChild(AvlGNode<K, V> pivot)
    {
        pivot.left = rotateRightChild(pivot.left);
        return rotateLeftChild(pivot);
    }
	
	private AvlGNode<K, V> rotateRightLeftChild(AvlGNode<K, V> pivot)
    {
        pivot.right = rotateLeftChild(pivot.right);
        return rotateRightChild(pivot);
    }
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  methods not related to insertion
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	private int size(AvlGNode<K, V> cur) {
		return (root == null) ? 0 : 1+size(cur.left)+size(cur.right);
	}
	
	final AvlGNode<K,V> getEntry(Object key) {
		if (key == null)
			throw new NullPointerException();
		AvlGNode<K,V> cur = root;
		while (cur != null) {
			int cmp = compare((K) key, cur.getKey());
		    if (cmp < 0)
		    	cur = cur.left;
		    else if (cmp > 0)
		        cur = cur.right;
		    else
		        return cur;
		  }
		  return null;
	}
	
	final AvlGNode<K,V> getFirstEntry() {
		 AvlGNode<K,V> p = root;
		 if (p != null)
			 while (p.left != null)
				 p = p.left;
		 return p;
	}
	
	final AvlGNode<K,V> getLastEntry() {
		 AvlGNode<K,V> p = root;
		 if (p != null)
			 while (p.right != null)
				 p = p.right;
		 return p;
	}
	
	static <K,V> AvlGNode<K,V> successor(AvlGNode<K,V> t) {
		  if (t == null)
		      return null;
		  else if (t.right != null) {
		      AvlGNode<K,V> p = t.right;
		      while (p.left != null)
		          p = p.left;
		     return p;
		  } else {
		      AvlGNode<K,V> p = t.parent;
		      AvlGNode<K,V> ch = t;
		      while (p != null && ch == p.right) {
		          ch = p;
		          p = p.parent;
		      }
		      return p;
		  }
	}
	
	static <K,V> AvlGNode<K,V> predecessor(AvlGNode<K,V> t) {
		if (t == null)
			return null;
		else if (t.left != null) {
			AvlGNode<K,V> p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
			} else {
				AvlGNode<K,V> p = t.parent;
				AvlGNode<K,V> ch = t;
				while (p != null && ch == p.left) {
					ch = p;
					p = p.parent;
				}
				return p;
			}
	}
	
	private int compare(K o1, K o2) {
		if (comparator != null) {
			return comparator.compare(o1, o2);
		} else {
			return ((Comparable<? super K>)o1).compareTo((K)o2);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  EntrySet 
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	class EntrySet extends AbstractSet<AvlGNode<K, V>> {

		@Override
		public Iterator<AvlGNode<K, V>> iterator() {
			return new EntryIterator(getFirstEntry());
		}
		
		@Override
		public int size() {
			return AvlGTree.this.size();
		}
		
		public void clear() {
			AvlGTree.this.clear();
		}
		
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			AvlGNode<K, V> entry = (AvlGNode<K, V>) o;
			V val = entry.getValue();
			AvlGNode<K, V> p = getEntry(entry.getKey());
			return p != null && p.getValue().equals(val);
		}
		
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  Iterators
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	abstract class PrivateEntryIterator<T> implements Iterator<T> {
		AvlGNode<K,V> next;
		AvlGNode<K,V> lastReturned;
        int expectedModCount;

        PrivateEntryIterator(AvlGNode<K,V> first) {
            expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

	    final AvlGNode<K,V> nextEntry() {
	    	AvlGNode<K,V> e = next;
	        if (e == null)
	        	throw new NoSuchElementException();
	        if (modCount != expectedModCount)
	        	throw new ConcurrentModificationException();
	        next = successor(e);
	        lastReturned = e;
	        return e;
	    }
	    
	    final AvlGNode<K,V> prevEntry() {
	    	AvlGNode<K,V> e = next;
	    	if (e == null)
	    		throw new NoSuchElementException();
	    	if (modCount != expectedModCount)
	    		throw new ConcurrentModificationException();
	    	next = predecessor(e);
	    	lastReturned = e;
	    	return e;
	    }
	}
	
	final class EntryIterator extends PrivateEntryIterator<AvlGNode<K,V>> {
		EntryIterator(AvlGNode<K,V> first) {
			super(first);
		}
		
		public AvlGNode<K,V> next() {
			return nextEntry();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  SubMap
	 */
    /////////////////////////////////////////////////////////////////////////////
 
}
