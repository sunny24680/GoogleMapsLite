package cmsc420.sortedmap;

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
import java.util.Map.Entry;
import java.util.NavigableMap;


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
	public AvlGNode<K,V> root = null;
	EntrySet entrySet = null;
	private int g = 1;
	private int modCount,size = 0;
	Comparator<? super K> comparator = null;
	
	public AvlGTree(final Comparator<? super K> c) {
		comparator = c;
	}
	
	public AvlGTree(final int g) {
		if (g > 1)	
			this.g = g;
	}
	
	public AvlGTree(final Comparator<? super K> comparator, final int g) {
		if (g > 1)
			this.g= g;
		this.comparator = comparator;
	}
    
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  InnerClass 
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	public class AvlGNode<K, V> extends AbstractMap.SimpleEntry<K,V> implements SortedMap.Entry<K,V>{
		private V data;
		public int height;
		AvlGNode<K, V> left, right, parent;
		
		public AvlGNode(K key, V data, AvlGNode<K, V> parent) {
			super(key, data);
			left = null;
			right = null;
			height = 1;
			this.parent = parent;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Entry<?, ?>))
				return false;
			Entry<K,V> e = (Entry<K,V>)o;
			
			return this.getKey().equals(e.getKey()) && this.getValue().equals(e.getValue());
		}
		
		public int hashCode() {
			int keyHash = (getKey()==null ? 0 : getKey().hashCode());
			int valueHash = (getValue()==null ? 0 : getValue().hashCode());
			return keyHash ^ valueHash;
		}
		
		public String toString() {
			return getKey() + "=" + getValue()+ " height = "+height;
		}
			 
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  main methods in AvlGTree
	 */
    /////////////////////////////////////////////////////////////////////////////
	
	@Override
	public V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		modCount++;
		if (this.containsKey(key)) {
			V temp = this.get(key);
			root = insert(key, value, root, null);
			return temp;
		} else
			root = insert(key, value, root, null);
		//System.out.println("root "+root);
		return null;
		
	}
	
	@Override
	public int size() {
		return size(root);
	}
	
	@Override
	public boolean isEmpty() {
		return (root == null) ? true : false;
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
	public SortedMap<K,V> subMap(K fromKey, K toKey) {
		if ((compare(fromKey, toKey) <= 0) && (compare(fromKey, firstKey()) >= 0) && (compare(toKey, lastKey()) <= 0))
			return new subMap(fromKey, toKey);
		else 
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
		//System.out.println("IN NODE "+cur);
        if (cur == null) {
            cur = new AvlGNode<K, V>(key, data, parent);
        	return cur;
        }
        else { 
        	//adding to the left side of the tree
        	if (compare(key, cur.getKey()) < 0) {
	            cur.left = insert(key, data, cur.left, cur);
	            
	            //checks height difference 
	            if(heightDifference(cur.left, cur.right) > g)
	                if(compare(key, (K) cur.left.getKey()) < 0 )
	                    cur = rotateLeftChild(cur);
	                else
	                    cur = rotateLeftRightChild(cur);
        	} 
        	else if (compare(key, cur.getKey()) > 0){
	        	//add to the right subtree	
	        	cur.right = insert(key, data, cur.right, cur);
	        	//System.out.println("checing right"+cur.right);
	            if(heightDifference(cur.right, cur.left) > g)
	                if(compare(key,  (K) cur.right.getKey()) > 0) {
	                	//if an outside case
	                    cur = rotateRightChild(cur);
	                }
	                else {
	                	//if an inside case
	                    cur = rotateRightLeftChild(cur);
	                }
	        }
        	else {	
        		cur.setValue(data);
        		return cur;
        	}
        }
        
        
	    cur.height = maxHeight(cur.left, cur.right) + 1;
	    return cur;
    }
	
	private int heightDifference(AvlGNode<K, V> first, AvlGNode<K, V> second) {
		if (first == null && second == null)
			return 0;
		if (first == null)
			return second.height;
		if (second == null)
			return first.height;
		return first.height-second.height;
	}
	
	private int maxHeight(AvlGNode<K, V> first, AvlGNode<K, V> second) {
		if (first == null && second == null)
			return 1;
		if (first == null)
			return second.height;
		if (second == null)
			return first.height;
		return Math.max(first.height, second.height);
	}
	
	private AvlGNode<K, V> rotateLeftChild(AvlGNode<K, V> pivot)
    {
        AvlGNode<K, V> temp = pivot.left;        
        pivot.left = temp.right;
        if (temp.right != null) {
        	temp.right.parent = pivot;
        	temp.height = maxHeight(temp.left, temp.right) + 1;
        	pivot.height = maxHeight(pivot.left, pivot.right) + 1;
        }        	
        temp.right = pivot;
        temp.parent = pivot.parent;
        pivot.parent = temp;        
        pivot.height = maxHeight(pivot.left, pivot.right) + 1;
        temp.height = maxHeight(temp.left, pivot) + 1;
        return temp;
    }
	
	private AvlGNode<K, V> rotateRightChild(AvlGNode<K, V> pivot)
    {
        AvlGNode<K, V> temp = pivot.right;
        pivot.right = temp.left;
        if (temp.left != null) {
        	temp.left.parent = pivot;
        	temp.height = maxHeight(temp.left, temp.right) + 1;
        	pivot.height = maxHeight(pivot.left, pivot.right) + 1;
        } 
        temp.left = pivot;
        temp.parent = pivot.parent;
        pivot.parent = temp;
        pivot.height = maxHeight(pivot.left, pivot.right) + 1;
        temp.height = maxHeight(temp.left, pivot) + 1;
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
		if (cur == null) 
			return 0;
		if (cur.left == null && cur.right == null)
			return 1;
		if (cur.left == null)
			return size(cur.right) + 1;
		if (cur.right == null)
			return size(cur.left) + 1;
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
	
	<K,V> AvlGNode<K,V> successor(AvlGNode<K,V> t) {
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
	
	<K,V> AvlGNode<K,V> predecessor(AvlGNode<K,V> t) {
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
	
	public void print(AvlGNode<K, V> cur) {
		if (cur != null) {
			System.out.println("NODE = "+cur.toString());
			System.out.println("PARENT NODE = "+cur.parent);
			if (cur.left != null)
				System.out.print("Left Child = "+cur.left.toString());
			else 
				System.out.print("No Left Child ");
			if (cur.right != null)
				System.out.println(": Right Child = "+cur.right.toString());
			else 
				System.out.println(": No Right Child");
			print(cur.left);
			print(cur.right);
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
	
	class subMapEntrySet extends AbstractSet<AvlGNode<K, V>> {

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
	
	abstract class SubMapIterator<T> implements Iterator<T> {
	     AvlGNode<K,V> lastReturned;
	     AvlGNode<K,V> next;
	     final Object fenceKey;
	     int expectedModCount;

	     SubMapIterator(AvlGNode<K,V> first,
	    		 AvlGNode<K,V> fence) {
	         expectedModCount = AvlGTree.this.modCount;
	         lastReturned = null;
	         next = first;
	         fenceKey = fence == null ? null : fence.getKey();
	     }

	     public final boolean hasNext() {
	         return next != null && next.key != fenceKey;
	     }

	     final AvlGNode<K,V> nextEntry() {
	    	 AvlGNode<K,V> e = next;
	         if (e == null || e.getKey() == fenceKey)
	             throw new NoSuchElementException();
	         if (AvlGTree.this.modCount != expectedModCount)
	             throw new ConcurrentModificationException();
	         next = successor(e);
	         lastReturned = e;
	         return e;
	     }

	 }

	 final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
	     SubMapEntryIterator(AvlGNode<K,V> first,
	    		 	AvlGNode<K,V> fence) {
	         super(first, fence);
	     }
	     public Map.Entry<K,V> next() {
	         return nextEntry();
	     }
	 }
	        	 
	
	/////////////////////////////////////////////////////////////////////////////
	/*
	 *  SubMap
	 */
    /////////////////////////////////////////////////////////////////////////////

	private class subMap extends AbstractMap<K,V> implements SortedMap<K,V>{
		private K fromKey, toKey;
		private Comparator<? super K> comp = AvlGTree.this.comparator;
		
		public subMap(K from, K to) {
			fromKey = from;
			Iterator i = AvlGTree.this.entrySet().iterator();
			while (i.next().)
			toKey = to;
		}
		
		@Override
		public Set<Entry<K, V>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator<? super K> comparator() {
			return comp;
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			if ((AvlGTree.this.compare(fromKey, toKey) <= 0) && (AvlGTree.this.compare(fromKey, this.firstKey()) >= 0) && (AvlGTree.this.compare(toKey, this.lastKey()) <= 0))
				return new subMap(fromKey, toKey);
			else 
				return null;
		}
		
		final AvlGNode<K,V> getLastEntry() {
			 AvlGNode<K,V> p = AvlGTree.this.getFirstEntry();
			 AvlGNode<K,V> prev = null;
			 while (AvlGTree.this.compare(p.getKey(), toKey) < 0) {
				 prev = p;
				 p = successor(p);
			 }
			 return prev;
		}
		
		final AvlGNode<K,V> getFirstEntry() {
			 AvlGNode<K,V> p = AvlGTree.this.getLastEntry();
			 AvlGNode<K,V> prev = null;
			 while (AvlGTree.this.compare(p.getKey(), fromKey) > 0) {
				 prev = p;
				 p = successor(p);
			 }
			 return prev;
		}

		@Override
		//dont need to do 
		public SortedMap<K, V> headMap(K toKey) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		//dont need to do 
		public SortedMap<K, V> tailMap(K fromKey) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public K firstKey() {
			return this.getFirstEntry().getKey();
		}

		@Override
		public K lastKey() {
			return this.getLastEntry().getKey();
		}
		
		public boolean inRange(K x) {
			if ((AvlGTree.this.compare(x, fromKey) >= 0) && (AvlGTree.this.compare(x, toKey)) < 0 )
				return true;
			else 
				return false;
		}
	}
}
