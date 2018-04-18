package distributed;

import java.lang.Integer;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.LinkedList;

public class Deque extends LinkedList
{
   private int capacity;
   private final ReentrantLock lock;
   private final Condition notFull;;
   private final Condition notEmpty;
   
   public Deque(int cap)
   {
      super();
      capacity = cap;
      lock = new ReentrantLock();
      notFull = lock.newCondition();
      notEmpty = lock.newCondition();
   }
   
   public int getHead()
   {
      lock.lock();
      int returnMe = 0;
      
      try
      {
         while (size() == 0)
            notEmpty.await();
         
         returnMe = ((Integer)getFirst()).intValue();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
      
      return returnMe;
   }
   
   public void insertHead(int value)
   {
      lock.lock();
      try
      {
         while (size() >= capacity)
            notFull.await();
         
         addFirst(new Integer(value));
         notEmpty.signalAll();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
   }
   
   public int deleteHead()
   {
      lock.lock();
      int returnMe = 0;
              
      try
      {
         while (size() == 0)
            notEmpty.await();
         
         returnMe = ((Integer)removeFirst()).intValue();
         notFull.signalAll();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
      
      return returnMe;
   }
   
   public int getTail()
   {
      lock.lock();
      int returnMe = 0;
      
      try
      {
         while (size() == 0)
            notEmpty.await();
         
         returnMe = ((Integer)getLast()).intValue();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
      
      return returnMe;
   }
   
   public void insertTail(int value)
   {
      lock.lock();
      try
      {
         while (size() >= capacity)
            notFull.await();
         
         addLast(new Integer(value));
         notEmpty.signalAll();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
   }
   
   public int deleteTail()
   {       
      lock.lock();
      int returnMe = 0;
       
      try
      {
         while (size() == 0)
            notEmpty.await();
         
         returnMe = ((Integer)removeLast()).intValue();
         notFull.signalAll();
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         lock.unlock();
      }
      
      return returnMe;
   }
   
   public static void main1(String args[])
   {
      int arbitrary = 10;
      
      Tester tests[] = new Tester[arbitrary];
      
      for (int i = 0; i < arbitrary; i++)
      {
         tests[i] = new Tester();
         tests[i].start();
      }
   }
   
   private static class Tester extends Thread
   {
      private static Deque deque = null;
      private int result;
      private int arbitrary = 100;
      
      public Tester()
      {
         if (deque == null)
            deque = new Deque(arbitrary);
      }
      
      public void run()
      {
         while(true)
         {
            deque.insertHead((int)(java.lang.Math.random() * arbitrary));
            result = deque.getHead();
            result = deque.deleteHead();
            deque.insertHead((int)(java.lang.Math.random() * arbitrary));
            result = deque.deleteHead();
            deque.insertTail((int)(java.lang.Math.random() * arbitrary));
            result = deque.getTail();
            result = deque.deleteTail();
            deque.insertTail((int)(java.lang.Math.random() * arbitrary));
            result = deque.deleteTail();
            System.out.println("Thread: " + Thread.currentThread().getName() +
                               " result: " + result);
            
            //Thread.currentThread().wait();
         }
      }
   }
}
