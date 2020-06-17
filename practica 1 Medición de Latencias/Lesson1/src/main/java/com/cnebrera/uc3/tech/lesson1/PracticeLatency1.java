//AH
package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulRndPark;
import java.util.concurrent.TimeUnit;
import org.HdrHistogram.Histogram;


/**
 * First practice, measure latency on a simple operation
 */
public class PracticeLatency1
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
    	int significantValueDigits = 2;
    	long highestTrackableValue = 100000L;
    	long lowestDiscernibleValue = 1;    	
    	Histogram hg = new Histogram(lowestDiscernibleValue, highestTrackableValue, significantValueDigits);
    	
    	// Create a random park time simulator
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulRndPark(TimeUnit.NANOSECONDS.toNanos(100), TimeUnit.MICROSECONDS.toNanos(100));
        hg.setAutoResize(true);
        // Execute the operation lot of times
        for(int i = 0; i < 100000; i++)
        {    
            long startTime = System.nanoTime(); 
            syncOpSimulator.executeOp(); 
            long endTime = System.nanoTime();
            hg.recordValue(endTime-startTime);
        }
        
        hg.outputPercentileDistribution(System.out, 1000.00);
        

        // TODO Show the percentile distribution of the latency calculation of each executeOp call
    }
}
