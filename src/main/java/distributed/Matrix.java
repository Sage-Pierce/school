package distributed;

/**
 * @author Sage
 */
import java.lang.Thread;

public class Matrix
{
   public static boolean MatrixMult(int rowA, int colA, double[][] A,
                                    int rowB, int colB, double[][] B,
                                    double[][] C, int numThreads)
   {
      int threadCount, numOps, opsPerThread, modulus;
      
      if ((numThreads <= 0) || (colA != rowB))
         return false;
      
      numOps = rowA * colB;
      
      if (numOps <= numThreads)
         threadCount = numOps;
      else
         threadCount = numThreads;
      
      opsPerThread = numOps / threadCount;
      modulus = numOps % threadCount;
      
      int opCount, opIndex = 0;   
      Multiplier[] multiplyThreads = new Multiplier[threadCount];
      for (int i = 0; i < threadCount; i++)
      {
         opCount = opsPerThread;
         if (modulus > 0)
         {
            opCount++;
            modulus--;
         }
         
         multiplyThreads[i] = new Multiplier(A, B, C, colA, colB, 
                                             opIndex, opCount);
         multiplyThreads[i].start();
         opIndex += opCount;
      }
      
      try
      {
         for (int i = 0; i < threadCount; i++)
           multiplyThreads[i].join();
      } catch (Exception e) { e.printStackTrace(); }
      
      return true;
   }
   
   private static class Multiplier extends Thread
   {
      double matrixA[][], matrixB[][], matrixC[][];
      int colA, colB;
      int start, numOps;
      
      private Multiplier(double[][] A, double[][] B, double[][] C,
                         int cA, int cB, int s, int nOps)
      {
         matrixA = A;
         matrixB = B;
         matrixC = C;
         colA = cA;
         colB = cB;
         start = s;
         numOps = nOps;
      }
      
      public void run()
      {
         int sum;
         for (int op = start; op < (start + numOps); op++)
         {
            sum = 0;
            for (int j = 0; j < colA; j++)
            {
               sum += (matrixA[op / colB][j] * matrixB[j][op % colB]);
            }
            
            matrixC[op / colB][op % colB] = sum;
         }
      }
   }
   
   public static void main1 (String args[])
   {
      double[][] A = {{6,  3,  0,  14, 81},
                      {2,  5,  1,  10, 1 },
                      {9,  8,  6,  8,  21},
                      {5,  4,  3,  2,  1 }};
      double[][] B = {{7,  4,  61},
                      {6,  7,  10},
                      {5,  0,  54},
                      {25, 13, 10},
                      {45, 2,  7 }};
      double[][] C = new double[A.length][B[0].length];
      
      Matrix.MatrixMult(A.length, A[0].length, A, B.length, B[0].length, B, C, 6);
      
      System.out.println("Result:\n");
      for (int i = 0; i < A.length; i++)
      {
         for (int j = 0; j < B[0].length; j++)
         {
            System.out.print(C[i][j] + " ");
         }
         System.out.print("\n");
      }
      
   }
}
