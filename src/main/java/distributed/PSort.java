package distributed;

import java.lang.Math;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.Semaphore;

public class PSort 
{
   public static void parallelSort(int[] A, int begin, int end)
   {
      if ((begin < 0) || ((end - begin) > A.length) || ((begin + 1) >= end))
         return;
      
      if ((begin - end) == 2)
      {
         if (A[begin] > A[begin + 1])
            swap(A, begin, begin + 1);
         
         return;
      }
      
      QuickSort sorter = new QuickSort(A, begin, end);
      sorter.sort();
   }
   
   private static void swap(int[] array, int index1, int index2)
   {
      int temp = array[index1];
      array[index1] = array[index2];
      array[index2] = temp;
   }
   
   private static class QuickSort
   {
      int[] array;
      int begin, end;
      private Semaphore mutex;
      private Semaphore choosing[];
      
      private QuickSort(int[] a, int b, int e)
      {
         array = a;
         begin = b;
         end = e;
         mutex = new Semaphore(1);
         choosing = new Semaphore[array.length];
         
         for (int i = begin; i < end; i++)
            choosing[i] = new Semaphore(1);
      }
      
      Runnable forwardSort = new Runnable()
      {
         public void run()
         {
            boolean sorted = false;
            
            for (int i = 0; (i < (end - begin)) && !sorted; i++)
            {
               sorted = true;
               for (int j = begin; j < (end - 1 - i); j++)
               {
                  try
                  {
                     mutex.acquire();
                     choosing[j].acquire();
                     choosing[j+1].acquire();
                     mutex.release();
                  
                     if (array[j] > array[j+1])
                     {
                        swap(array, j, j+1);
                        sorted = false;
                     }
                     
                     choosing[j].release();
                     choosing[j+1].release();
                  }catch (Exception e){ e.printStackTrace(); }
               }
            }
         }
      };
      
      Runnable backwardSort = new Runnable()
      {
         public void run()
         {
            boolean sorted = false;
            
            for (int i = 0; (i < (end - begin)) && !sorted; i++)
            {
               sorted = true;
               for (int j = end - 1; j > (begin + 1 + i); j--)
               {
                  try
                  {
                     mutex.acquire();
                     choosing[j].acquire();
                     choosing[j-1].acquire();
                     mutex.release();
                  
                     if (array[j] < array[j-1])
                     {
                        swap(array, j, j-1);
                        sorted = false;
                     }
                     
                     choosing[j].release();
                     choosing[j-1].release();
                  }catch (Exception e){ e.printStackTrace(); }
               }
            }
         }
      };
      
      private void sort()
      {
         try
         {
            Thread forward = new Thread(forwardSort);
            Thread backward = new Thread(backwardSort);
            forward.start();
            backward.start();
            forward.join();
            backward.join();
         }catch(Exception e) { e.printStackTrace(); }
      }
   }
   
   public static void main1(String args[])
   {
      int size = 500;
      int[] randoms = new int[size];
      
      for (int i = 0; i < size; i++)
         randoms[i] = (int)(100 * Math.random());

      System.out.println("Before sorting: ");
      for(int i = 0; i < size; i++)
         System.out.print(randoms[i] + " ");
         
      PSort.parallelSort(randoms, 12, size / 2);
      
      System.out.println("\nAfter sorting: ");
      for(int i = 0; i < size; i++)
         System.out.print(randoms[i] + " ");
   }
}
