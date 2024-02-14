import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Runner 
{
	public static final Random rng = new Random();

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException
	{
		SortedList<Integer> nums = new SortedList<>(39, 24, 65, 48, 42, 75, 54, 42, 54, 45, 47, 54, 28, 46);
		System.out.println(nums.hashCode());
		System.out.println(nums.hashString());
		
	}
	
	public static SortedList<Integer> SortedListIntegerTests(SortedList<Integer> s)
	{
		// add()
		System.out.println(s.add(4)); // true
		System.out.println(s.add(15)); // true
		System.out.println(s.add(5)); // true
		System.out.println(s.add(-5)); // true
		System.out.println(s.add(0)); // true
		System.out.println(s);
		System.out.println();
		
		// setOrder()
		s.setOrder(false);
		System.out.println(s);
		s.setOrder(true);
		System.out.println(s);
		
		// remove()
		System.out.println(s.remove(5)); // true
		System.out.println(s.remove(15)); // true
		System.out.println(s);
		System.out.println();
		
		// contains()
		System.out.println(s.contains(15)); // false
		System.out.println(s.contains(0)); // true
		System.out.println(s);
		System.out.println();
		
		// get()
		System.out.println(s.get(0)); // -5
		System.out.println(s.get(2)); // 4
		System.out.println(s);
		System.out.println();
		
		// getMax()
		System.out.println(s.getMax()); // 4
		
		// getMin()
		System.out.println(s.getMin()); // -5
		System.out.println();
		
		// clear();
		s.clear();
		System.out.println(s); // []
		System.out.println();
		
		// isEmpty()
		System.out.println(s.isEmpty()); // true
		System.out.println();
		
		// addAll()
		List<Integer> nums = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			nums.add(i);
		System.out.println("Collection to add: " + nums);
		s.addAll(nums);
		System.out.println(s);
		System.out.println();
		
		// containsAll()
		System.out.println(s.containsAll(nums)); // true
		nums.add(10);
		System.out.println(s.containsAll(nums)); // false
		System.out.println();
		
		// removeAll()
		List<Integer> nums2 = new ArrayList<>();
		nums2.add(1);
		nums2.add(5);
		nums2.add(6);
		nums2.add(-11);
		System.out.println("Removing: " + nums2);
		s.removeAll(nums2);
		System.out.println(s);
		System.out.println();
		
		// retainAll()
		List<Integer> nums3 = new ArrayList<>();
		nums3.add(3);
		nums3.add(8);
		nums3.add(0);
		System.out.println("Retaining: " + nums3);
		s.retainAll(nums3);
		System.out.println(s);
		System.out.println();
		
		// toArray()
		Object[] sCopy = s.toArray();
		System.out.println(Arrays.toString(sCopy));
		System.out.println();
		
		// toArray(T[] a)
		Object[] sCopy2 = new Object[7];
		sCopy2 = s.toArray(sCopy2);
		System.out.println(Arrays.toString(sCopy2));
		System.out.println();
		
		// size()
		System.out.println(s.size());
		System.out.println();
		
		// iterator (for-each)
		for(Integer i : s)
			System.out.print(i + " -- ");
		System.out.println();
		
		// hashString()
		System.out.println(s.hashString());
		
		// hashCode()
		System.out.println(s.hashCode());
		
		return s;
	}
}


