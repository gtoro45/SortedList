import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 * ************************************************************************
 * 
 * <p> A list of elements which are sorted in ascending order by default
 * on insertion. Similar to a priority queue: accessing the minimum and
 * maximum values in the list takes O(1) time, but lacks the efficiency 
 * of a priority queue in other departments (insertion, etc.).
 * 
 * <p> Implements the Collection and Serializable interfaces, with the
 * former allowing SortedList objects to be used in a for-each loop. All
 * SortedLists begin with a capacity for the encapsulated array, which
 * grows as elements are added.
 * 
 * <p> The generic argument for the elements in the SortedList 
 * <strong> MUST extend Comparable, </strong> or else the insertion
 * sorting will NOT work and you will receive an error
 * 
 * @author Gabriel Toro
 * 
 * @version 1.2
 * <p> -Added String hashing algorithm
 * 
 * @apiNote Will implement static sorting methods in a future update.
 * 
 * @param <T>
 * The generic type of the list <strong> MUST extend Comparable </strong> 
 * for sorting to function properly; a compile-time error will 
 * be shown if this condition is not met
 * 
 * ************************************************************************
 */
public class SortedList<T extends Comparable<T>> implements Collection<T>, Serializable
{
	/**
	 * ID for verification during serialization
	 */
	private static final long serialVersionUID = -8239062692166131195L;
	
	/**
	 * SortedList default array capacity
	 */
	public static final int DEFAULT_CAPACITY = 11;
	
	/**
	 * SortedList maximum array capacity
	 */
	public static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;
	
	/**
	 * English alphabet in both cases and numbers 0-9
	 * in a convenient String for hashing
	 */
	private static final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	
	/**
	 * Random number generator for generating a hash string
	 * from characters found in the base64 String. Seed is updated
	 * (set to this SortedList's hash code) upon call to hashString();
	 * otherwise seed is set to system time
	 */
	private static final Random hasher = new Random();
	
	/**
	 * Static length of the hash string for SortedList objects.
	 * All hash strings of SortedList objects will be this length
	 */
	private static final int HASH_STRING_LENGTH = 20;
	
	/**
	 * Array storing all the elements in the SortedList in the 
	 * default or desired sorting order (ascending/descending)
	 */
	private T[] list; 
	
	/**
	 * Array storing all the elements in the SortedList in
	 * the order they were originally put in. Used for debugging
	 * and might have future use cases
	 */
	private T[] unsortedList;
	
	/**
	 * Initial capacity of list; set to DEFAULT_CAPACITY (11)
	 * if not specified in constructor
	 */
	private int initialCapacity;
	
	/**
	 * The size of this SortedList - the count of non-null
	 * elements in the array
	 */
	private int size;
	
	/**
	 * Stores whether or not this SortedList is sorted in
	 * ascending (true) or descending (false) order
	 */
	private boolean ascending;
	
	/**
	 * Stores whether or not the SortedList can have its 
	 * capacity increased. True by default, becomes false
	 * when ensureCapacity() determines the capacity has been
	 * increased to MAX_CAPACITY
	 */
	private boolean capacityIncreaseAllowed;
	
	/**
	 * Default constructor; creates a SortedList with
	 * ascending order and a default array capacity of 11
	 */
	public SortedList()
	{
		this(true);
	}
	
	/**
	 * Creates a SortedList with a given order
	 * and a default array capacity of 11
	 * @param ascendingOrder
	 * The desired order for the SortedList:
	 * ascending order if true, descending if false
	 */
	public SortedList(boolean ascendingOrder)
	{
		this(ascendingOrder, DEFAULT_CAPACITY);
	}
	
	/**
	 * Creates a SortedList with a given order
	 * and a given initial array capacity
	 * @param ascendingOrder
	 * The desired order for the SortedList:
	 * ascending order if true, descending if otherwise
	 * @param cap
	 * The initial capacity of the array
	 */
	public SortedList(boolean ascendingOrder, int cap)
	{
		this(null, ascendingOrder, cap);
	}
	
	/**
	 * Creates a SortedList in ascending order from a
	 * given list of data (var-args)
	 * @param vals
	 * var-args parameter that contains the desired initial
	 * data
	 */
	@SafeVarargs
	public SortedList(T... vals)
	{
		this(true);
		for(T t : vals)
			add(t);
	}
	
	/**
	 * Creates a SortedList in a given order from a
	 * given list of data (var-args)
	 * @param ascendingOrder
	 * The desired order for the SortedList:
	 * ascending order if true, descending if false
	 * @param vals
	 * varargs parameter that contains the desired initial
	 * data
	 */
	@SafeVarargs
	public SortedList(boolean ascendingOrder, T... vals)
	{
		this(ascendingOrder);
		for(T t : vals)
			add(t);
	}
	
	/**
	 * Creates a SortedList in ascending order from a 
	 * given Collection with a capacity equal to 
	 * the size of the Collection
	 * @param c
	 * The Collection to create the SortedList from
	 */
	public SortedList(Collection<? extends T> c)
	{
		this(c, true, c.size());
	}
	
	/**
	 * Creates a SortedList in ascending order from a 
	 * given Collection with a capacity equal to 
	 * the size of the Collection
	 * @param c
	 * The Collection to create the SortedList from
	 */
	public SortedList(Collection<? extends T> c, boolean ascendingOrder)
	{
		this(c, ascendingOrder, c.size());
	}
	
	/**
	 * Creates a Sorted list with a given order from a
	 * given Collection with a given capacity
	 * @param c
	 * The Collection whose elements will be put inside
	 * this SortedList
	 * @param ascendingOrder
	 * The desired order for the SortedList:
	 * ascending order if true, descending if otherwise
	 * @param cap
	 * The initial capacity of the array; must be >= the 
	 * size of the given Collection
	 */
	@SuppressWarnings("unchecked")
	public SortedList(Collection<? extends T> c, boolean ascendingOrder, int cap)
	{
		list = (T[]) new Comparable[cap];
		unsortedList = (T[]) new Comparable[cap];
		size = 0;
		initialCapacity = cap;
		this.ascending = ascendingOrder;
		capacityIncreaseAllowed = true;
		if(c != null)
		{
			if(cap < c.size())
				throw new IllegalArgumentException("Capacity must be at least the size of the given Collection");
			addAll(c);
		}
	}
	
	/**
	 * Gets and returns the element in the list at
	 * a given index
	 * @param index
	 * The index to look for the element at
	 * @return
	 * The element at the given index 
	 */
	public T get(int index)
	{
		if(index < 0 || index >= size)
			throw new ArrayIndexOutOfBoundsException();
		return list[index];
	}
	
	/**
	 * Gets the minimum-value element in the list
	 * @return
	 * The minimum-value element in the list
	 */
	public T getMin()
	{
		if(size == 0)
			throw new IllegalAccessError("There are no elements in the list");
		if(ascending)
			return list[0];
		return list[size - 1];
	}
	
	/**
	 * Gets the maximum-value element in the list
	 * @return
	 * The maximum-value element in the list
	 */
	public T getMax()
	{
		if(size == 0)
			throw new IllegalAccessError("There are no elements in the list");
		if(ascending)
			return list[size - 1];
		return list[0];
	}
	
	public int size()
	{
		return size;
	}

	@Override
	public boolean add(T e) 
	{
		size++;
		if(size == list.length)
			ensureCapacity();
		unsortedList[size - 1] = e;
		list[size - 1] = e;
		for(int i = size - 1; i > 0; i--)
		{
			if(ascending) 
			{
				if(list[i].compareTo(list[i - 1]) < 0)
					swap(i, i - 1);
				else { return true; }
			} 
			else 
			{
				if(list[i].compareTo(list[i - 1]) > 0)
					swap(i, i - 1);
				else { return true; }
			}
		}
		if(size == 1) // resolves loop skip if e is first element added to list
			return true;
		if(indexOf(e) == 0) // resolves loop break when final position of e is at [0]
			return true;
		return false;
	}
	
	/**
	 * Doubles the capacity of the encapsulated array
	 * for size mutability. If capacity is at MAX_CAPACITY,
	 * the method does not allow further capacity increases.
	 * If Collection size is at MAX_CAPACITY, throws 
	 * OutOfMemoryError
	 */
	private void ensureCapacity()
	{
		if(!capacityIncreaseAllowed)
			return;
		if(size == MAX_CAPACITY)
			throw new OutOfMemoryError("No more elements are allowed in the List");
		if(list.length * 2 >= MAX_CAPACITY)
		{
			unsortedList = Arrays.copyOf(unsortedList, MAX_CAPACITY);
			list = Arrays.copyOf(list, MAX_CAPACITY);
			capacityIncreaseAllowed = false;
		}
		else
		{
			unsortedList = Arrays.copyOf(unsortedList, unsortedList.length * 2);
			list = Arrays.copyOf(list, list.length * 2);
		}
	}
	
	/**
	 * Swaps the elements at the given indexes in the list
	 * @param x
	 * The index of the first element to swap
	 * @param y
	 * The index of the second element to swap
	 */
	private void swap(int x, int y)
	{
		if(x == y)
			return;
		T temp = list[x];
		list[x] = list[y];
		list[y] = temp;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) 
	{
		for(T t : c)
			if(!add(t))
				return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() 
	{
		list = (T[]) new Comparable[initialCapacity];
		size = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) 
	{
		return indexOf((T) o) != -1;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		int count = 0;
		for (int i = 0; i < size; i++) {
			if(c.contains(get(i)))
				count++;
		}
		return count == c.size();
	}

	@Override
	public boolean isEmpty() 
	{
		return size == 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterator<T> iterator() 
	{
		return new SortedListIterator(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) 
	{
		removeFromUnsorted(o);
		return remove(indexOf((T) o)) != null;
	}
	
	/**
	 * Removes a single instance of the specified element from 
	 * the unsorted list, if it is present 
	 * @param o
	 * The object to find and remove
	 */
	@SuppressWarnings("unchecked")
	private void removeFromUnsorted(Object o)
	{
		int index = 0;
		for(int i = 0; i < size; i++)
			if(unsortedList[i].compareTo((T) o) == 0)
				index = i;
		for(int x = index; x < unsortedList.length - 1; x++)
			unsortedList[x] = unsortedList[x + 1];
	}
	
	/**
	 * Helper method to remove an object at a given
	 * index in the list
	 * @param index
	 * The index to remove the object from
	 * @return
	 * The removed element
	 */
	private T remove(int index)
	{
		if(index < 0 || index >= size)
			throw new ArrayIndexOutOfBoundsException();
		size--;
		T temp = list[index];
		list[index] = null;
		for(int i = index; i < list.length - 1; i++)
			list[i] = list[i + 1];
		list[list.length - 1] = null;
		return temp;
	}
	
	/**
	 * Helper method to determine the index
	 * of a given element in the list
	 * @param o
	 * The object to look for
	 * @return
	 * The index of the desired object, -1 if
	 * the object does not exist in the list
	 */
	private int indexOf(T o)
	{
		for (int i = 0; i < size; i++) {
			if(list[i].compareTo(o) == 0)
				return i;
		}
		return -1;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean changed = false;
		for (int i = 0; i < size; i++) {
			if(c.contains(get(i)))
			{
				remove(i);
				i--;
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) 
	{
		boolean changed = false;
		for (int i = 0; i < size; i++) {
			if(!c.contains(get(i)))
			{
				remove(i);
				i--;
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public Object[] toArray() 
	{
		return Arrays.copyOf(list, size);
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] a) 
	{
		return (T[]) Arrays.copyOf(list, a.length);
	}
	
	/**
	 * Returns an array containing all the elements
	 * in this SortedList in their original, insertion
	 * order
	 * @return
	 * The unsorted array of elements
	 */
	public Object[] unsortedArray() 
	{
		return Arrays.copyOf(unsortedList, size);
	}
	
	/**
	 * Sets the order for which elements in this SortedList
	 * are arranged. If not empty, elements will be re-sorted
	 * to match the new desired order
	 * @param ascending
	 * The given order to arrange the elements; ascending if true,
	 * descending if false
	 */
	public void setOrder(boolean ascending)
	{
		if(ascending == this.ascending)
			return;
		this.ascending = ascending;
		for(int i = 0; i < size / 2; i++)
			swap(i, size - 1 - i);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o)
	{
		o = (SortedList<T>) o;
		return this.hashCode() == o.hashCode();
	}
	
	public String toString()
	{
		return toString(true);
	}
	
	/**
	 * String representation of this SortedList in
	 * either its sorted order or insertion order
	 * @param sorted
	 * Whether or not the list representation will be
	 * in sorted order (true) or insertion order (false)
	 * @return
	 * A String representation of this SortedList
	 */
	public String toString(boolean sorted)
	{
		if(size == 0)
			return "[]";
		T[] temp = (sorted) ? list : unsortedList;
		String info = "[";
		for (int i = 0; i < size; i++) 
			info += temp[i].toString() + ", ";
		return info.substring(0, info.length() - 2) + "]";
	}
	
	public int hashCode()
	{
		long seed = System.currentTimeMillis();
		boolean negative = ascending;
		for(int i = 1; i <= size; i++)
		{
			if(!negative)
				seed += (list[i - 1].hashCode() % list.length) + Math.pow(i, 2);
			else
				seed -= (list[i - 1].hashCode() % list.length) + Math.pow(i, 2);
			negative = !negative;
		}	
		String binaryString = "";
		Random generator = new Random(seed);
		for(int i = 0; i < 31; i++)
			binaryString += (generator.nextBoolean()) ? "0" : "1";
		return Integer.parseInt(binaryString, 2);
	}
	
	/**
	 * Creates a hash code in the form of a String for this 
	 * Sorted List
	 * @return
	 * This SortedList's hash code
	 */
	public String hashString()
	{
		hasher.setSeed(hashCode());
		String hash = "";
		for(int i = 0; i < HASH_STRING_LENGTH; i++)
			hash += base64.charAt(hasher.nextInt(base64.length()));
		return hash;
	}
	
	/**
	 * Iterator implementation for SortedList, allowing 
	 * for-each loop use
	 */
	@SuppressWarnings("hiding")
	private class SortedListIterator<T extends Comparable<T>> implements Iterator<T> 
	{
		private SortedList<T> list;
		private int index;
		private T current;
		
		/**
		 * <p> Creates an Iterator for the SortedList data structure,
		 * allowing SortedList to be iterable in a for-each loop 
		 * @param list
		 * The SortedList object being iterated through
		 */
		public SortedListIterator(SortedList<T> list) 
		{
			this.list = list;
			index = 0;
			current = this.list.get(index);
		}

		@Override
		public boolean hasNext() {
			return index != list.size();
		}

		@Override
		public T next() {
			current = list.get(index);
			index++;
			return current;
		}
	}
}