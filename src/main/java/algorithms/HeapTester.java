package algorithms;

public class HeapTester {

   @SuppressWarnings("unchecked")
   public static void main(String args[]){
      MyHeap<Double,String> h = new MyHeap<Double,String>(5);
      MyHeap<Double,String>.Handle[] handle = new MyHeap.Handle[10];

      System.out.println("The isEmpty method returns " + h.isEmpty() + ".");
      handle[0] = h.put(5.25,"a");
      handle[1] = h.put(4.5,"b");
      handle[2] = h.put(3.5,"c");
      System.out.println("After inserting 5.25 (data a), 4.5 (data b), 3.5 (data c), the heap is:\n" + h);
      
      handle[3] = h.put(3.14,"d");
      System.out.println("After inserting 3.14 (data d), the heap is:\n" + h);
      handle[4] = h.put(7.3,"e");
      
      try {  //checking that you throw a RuntimeException here
         h.put(5.0,"x");
      } catch (RuntimeException e) {
         System.out.println("Increasing the capacity from 5 to 10.\n");
         h.ensureCapacity(10);
      }
   
      handle[5] = h.put(20.0,"f");
      handle[6] = h.put(4.75,"g");
      System.out.println("After inserting 7.3 (data e), 20 (data f), 4.75 (data g), the heap is:\n" + h);
      handle[7] = h.put(1.5,"h");
      System.out.println("After inserting 1.5 (data h), the heap is:\n" + h);
      handle[8] = h.put(8.8,"i");
      handle[9] = h.put(9.0,"j");
      System.out.println("After inserting 8.8 (data i), 9.0 (data j), the heap is:\n" + h);

      System.out.println("There are " +  h.getSize() + " elements in the heap.");
      System.out.println("The isEmpty method returns " + h.isEmpty() + ".\n");
      

      System.out.println("removing d");
      System.out.println("  get returns " + handle[3].get());
      handle[3].remove();
      System.out.println("The heap is:\n" + h);
      
      System.out.println("Updating tag for a from 5.25 to 1.0");
      handle[0].update(1.0);
      System.out.println("The heap is:\n" + h);
      
      System.out.println("Updating tag for c from 3.5 to 1.25");
      handle[2].update(1.25);
      System.out.println("The heap is:\n" + h);
      
      for (int i=0; i < 2; i++){
         System.out.println("About to remove " + h.min());
         h.extractMin();
         for (int j = 0; j < 3; j++)
            System.out.println("   handle[" + j + "].inCollection() returns " + handle[j].inCollection());
         System.out.println("The heap is:\n" + h);
      }
      
      System.out.println("Removing i");
      System.out.println("  get returns " + handle[8].get());
      handle[8].remove();
      System.out.println("The heap is:\n" + h);

      System.out.println("Updating tag for h from 1.5 to 10.5");
      handle[7].update(10.5);
      System.out.println("The heap is:\n" + h);

      System.out.println("updating tag for b from 4.5 to 12.5");
      handle[1].update(12.5);
      System.out.println("The heap is:\n" + h);

      System.out.println("There are " +  h.getSize() + " elements in the heap.");
   }
}