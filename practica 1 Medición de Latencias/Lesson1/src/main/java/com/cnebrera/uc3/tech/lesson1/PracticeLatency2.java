//AH
package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulLongOperation;
import org.HdrHistogram.Histogram;

/**
 * Second practice, measure latency with and without warmup
 */
public class PracticeLatency2
{
    /**
     * Main method to run the practice
     * @param args command line arument
     */
    public static void main(String [] args)
    {
        runCalculations();
    }

    /**
     * Run the practice calculations
     */
    private static void runCalculations()
    {
        
    	Histogram hg = new Histogram(1,100000L,2);
    	hg.setAutoResize(true);
    	
    	// Create a random park time simulator
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulLongOperation();

        for (int j=0; j<100; j++)
        { // Execute the operation lot of times 
        	for (int i = 0; i < 200000; i++) { 
        	
        		long startTime = System.nanoTime();
        		syncOpSimulator.executeOp();
        		long endTime = System.nanoTime() - startTime; 
        		hg.recordValue(endTime); } 
        	
        		if (j==0) {
        			System.out.print("Sistema Frio" + "\n");
        			System.out.print("Mean: " + hg.getMean() + "\n"); 
        			System.out.print("Min: " + hg.getMinValue() + "\n"); 
        			System.out.print("Max: " + hg.getMaxValue() + "\n"); 
        			System.out.print("percentile 99: " +hg.getValueAtPercentile(99)+ "\n"); 
        			System.out.print("percentile 99,9: " +hg.getValueAtPercentile(99.9)+ "\n");
        			System.out.println();}
        		if (j==99) {
        			System.out.print("Sistema Caliente, repetición número:" + 100 +"\n" ); 
        			System.out.print("Mean: " + hg.getMean() + "\n"); 
        			System.out.print("Min: " + hg.getMinValue() + "\n"); 
        			System.out.print("Max: " + hg.getMaxValue() + "\n"); 
        			System.out.print("percentile 99: " +hg.getValueAtPercentile(99)+ "\n"); 
        			System.out.print("percentile 99,9: " +hg.getValueAtPercentile(99.9)+ "\n");
        			System.out.println();}
        		 
        	hg.reset(); 
        }

        // TODO Show min, max, mean and percentiles 99 and 99,9 with and without warm up
    }
}