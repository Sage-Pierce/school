package algorithms;

import java.lang.Comparable;
import java.lang.Math;
import java.lang.RuntimeException;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class MyHeap<K extends Comparable<K>, E> implements Cloneable
{
   private static final int NOT_FOUND = -1;

   private Object[] heap;
   private int size;

      /***********************CONSTRUCTORS**************************/
   public MyHeap()
   {
      this(8);
   }

   public MyHeap(int capacity)
   {
      heap = new Object[capacity + 1];
      size = 0;
   }

   public MyHeap(MyHeap<K, E> copy)
   {
      size = copy.getSize();
      heap = new Object[copy.getCapacity() + 1];
      
      for (int i = 0; i <= size; i++)
      {
         heap[i] = copy.getHeap()[i];
      }
   }

      /*******************PRIVATE MANIPULATOR METHODS**********************/
   private void heapifyUp(int index)
   {
      if (index > 1)
      {
         Handle child = (Handle) heap[index];
         Handle parent = (Handle) heap[index / 2];

            // Swap
         if (child.getKey().compareTo(parent.getKey()) < 0)
         {
            heap[index] = parent;
            heap[index / 2] = child;
            heapifyUp(index / 2);
         }
      }
   }

   private void heapifyDown(int index)
   {
      Handle child, parent;
      int childIndex;

      if ((2 * index) > size)
         return;
      else if ((2 * index) < size)
      {
         Handle left = (Handle) heap[2 * index];
         Handle right = (Handle) heap[(2 * index) + 1];

         if (left.getKey().compareTo(right.getKey()) <= 0)
            childIndex = 2 * index;
         else
            childIndex = (2 * index) + 1;
      }
      else   // (2 * index) == size
         childIndex = 2 * index;

      child = (Handle) heap[childIndex];
      parent = (Handle) heap[index];

         // Swap
      if (child.getKey().compareTo(parent.getKey()) < 0)
      {
         heap[index] = child;
         heap[childIndex] = parent;
         heapifyDown(childIndex);
      }
   }

   private int find(Handle findMe)
   {
      for (int index = 1; index <= size; index++)
      {
         if (findMe.get() == ((Handle)heap[index]).get())
            return index;
      }
      
      return NOT_FOUND;
   }

   private void removeAndFix(int index)
   {
      heap[index] = heap[size];
      heap[size] = null;
      size--;

      if (index < size)
         fix(index);
   }

   private void fix(int index)
   {
      Handle handle = (Handle) heap[index];
      Handle parent = (Handle) heap[index / 2];

      if ((index == 1) || (parent.getKey().compareTo(handle.getKey()) < 0))
         heapifyDown(index);
      else
         heapifyUp(index);
   }

      /*******************MANIPULATOR METHODS*************************/
   public Handle put(K key, E element) throws RuntimeException
   {
         // Create the Handle
      Handle handle = new Handle(key, element);
      int index;

      if ((index = find(handle)) != NOT_FOUND)
      {
         handle = (Handle) heap[index];
         handle.update(key);
         return handle;
      }
      else if (size < (heap.length - 1))
      {
            // Put in the correct place in the heap
         size++;
         heap[size] = handle;
         heapifyUp(size);

         return handle;
      }
      else
         throw new RuntimeException("Heap capacity violated!");
   }

   public E min() throws NoSuchElementException
   {
      if (!isEmpty())
      {
         Handle handle = (Handle) heap[1];
         return handle.get();
      }
      else
         throw new NoSuchElementException("Attempted to retrieve from empty heap!");
   }

   public K minKey()
   {
      if (!isEmpty())
      {
         Handle handle = (Handle) heap[1];
         return handle.getKey();
      }
      else
         throw new NoSuchElementException("Attempted to retrieve from empty heap!");
   }

   public E extractMin() throws NoSuchElementException
   {
      if (!isEmpty())
      {
            // Obtain Handle to min element
         Handle handle = (Handle) heap[1];

            // Replace root node and correct heap
         removeAndFix(1);

         return handle.get();
      }
      else
         throw new NoSuchElementException("Attempted to extract from empty heap!");
   }

   public void ensureCapacity(int capacity)
   {
      if (capacity >= heap.length)
      {
         Object[] temp = new Object[capacity + 1];
         for (int i = 1; i <= size; i++)
         {
            temp[i] = heap[i];
         }

         heap = temp;
      }
   }

      /************************ACCESSOR METHODS****************************/
   public String toString()
   {
      String string = "\n";

      int index = 1;
      int row = 0;
      while (index <= size)
      {
         for (int j = 0; (index <= size) && (j < (int)Math.pow((double)2, (double)(row))); j++, index++)
         {
            Handle handle = (Handle) heap[index];
            string = string.concat(handle.getKey() + " -> " + handle.get() + " ");
         }

         row++;
         string = string + "\n";
      }

      return string;
   }

   public boolean isEmpty()
   {
      if (size == 0)
         return true;
      else
         return false;
   }

   public int getCapacity() { return heap.length - 1; }
   public int getSize() { return size; }
   public Object[] getHeap() { return heap; }

      /*****************INNER CLASS HANDLE*******************/
   public class Handle
   {
      private K key;
      private E element;

      public Handle(K k, E e)
      {
         key = k;
         element = e;
      }

      public void update(K newKey)
      {
         int index = find(this);
         key = newKey;

         if (index != NOT_FOUND)
            fix(index);
      }

      public void remove()
      {
         int index = find(this);
         
         if (index != NOT_FOUND)
            removeAndFix(index);
      }

      public boolean inCollection()
      {
         int index = find(this);
         
         if (index == NOT_FOUND)
            return false;
         else
            return true;
      }

      public K getKey() { return key; }
      public E get() { return element; }
   }
}
