//AH
package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulSleep;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.HdrHistogram.*;

/**
 * Third practice, measure accumulated latency with multiple threads
 */
public class PracticeLatency3 {
	/** Number of consumer threads to run */
	final static int NUM_THREADS = 8; // se probo con 1 y 8 hilos
	/** Number of executions per thread */
	final static int NUM_EXECUTIONS = 100;
	/** Expected max executions per second */
	final static int MAX_EXPECTED_EXECUTIONS_PER_SECOND = 50;

	/**
	 * Main method to run the practice
	 * 
	 * @param args command line argument
	 */
	public static void main(String[] args) {
		runCalculations();
	}

	/**
	 * Run the practice calculations
	 */
	private static void runCalculations() {
		// Create a sleep time simulator, it will sleep for 10 milliseconds in each call
		BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulSleep(10);

		SynchronizedHistogram hg = new SynchronizedHistogram(1, 100000L, 2);
		SynchronizedHistogram hgAcumulado = new SynchronizedHistogram(1, 100000L, 2);

		// List of threads
		List<Runner> runners = new LinkedList<>();

		// Create the threads and start them
		for (int i = 0; i < NUM_THREADS; i++) {
			final Runner runner = new Runner(syncOpSimulator, hg, hgAcumulado);
			runners.add(runner);
			new Thread(runner).start();
		}

		// Wait for runners to finish
		runners.forEach(Runner::waitToFinish);
		System.out.print("Latencia" + "\n");
		System.out.print("Mean: " + hg.getMean() + "\n");
		System.out.print("Min: " + hg.getMinValue() + "\n");
		System.out.print("Max: " + hg.getMaxValue() + "\n");
		System.out.print("Hilos " + hg.getTotalCount() + "\n");
		System.out.println();

		System.out.print("\n" + "\n" + "Latencia Acumulada" + "\n");
		System.out.print("Mean: " + hgAcumulado.getMean() + "\n");
		System.out.print("Min: " + hgAcumulado.getMinValue() + "\n");
		System.out.print("Max: " + hgAcumulado.getMaxValue() + "\n");
		System.out.print("Hilos " + hgAcumulado.getTotalCount() + "\n");
		System.out.println();

		// TODO Show the percentile distribution of the latency calculation of each
		// executeOp call for all threads
	}

	/**
	 * The runner that represent a thread execution
	 */
	private static class Runner implements Runnable {
		/** The shared operation simulator */
		final BaseSyncOpSimulator syncOpSimulator;

		/** True if finished */
		volatile boolean finished = false;

		// histogram
		private SynchronizedHistogram hg;
		private SynchronizedHistogram hgAcumulado;

		/**
		 * Create a new runner
		 *
		 * @param syncOpSimulator shared operation simulator
		 */
		private Runner(BaseSyncOpSimulator syncOpSimulator, SynchronizedHistogram hg,
				SynchronizedHistogram hgAcumulado) {
			this.syncOpSimulator = syncOpSimulator;
			this.setHistograma(hg);
			this.setHistogramaAcumulado(hgAcumulado);
		}

		@Override
		public void run() {
			// Calculate the expected time between consecutive calls, considering the number
			// of executions per second
			final long expectedTimeBetweenCalls = TimeUnit.SECONDS.toMillis(1) / MAX_EXPECTED_EXECUTIONS_PER_SECOND;

			// Calculate the next call time, the first time should be immediate
			long nextCallTime = System.currentTimeMillis();
			long latenciaAcumulada = 0;
			// Execute the operation the required number of times
			for (int i = 0; i < NUM_EXECUTIONS; i++) {
				// Wait until there is the time for the next call
				while (System.currentTimeMillis() < nextCallTime)
					;

				// Execute the operation, it will sleep for 10 milliseconds
				long start = System.currentTimeMillis();
				syncOpSimulator.executeOp();
				long fin = System.currentTimeMillis();

				long total = fin - start;

				latenciaAcumulada = fin - start + fin - nextCallTime; // accumulate latency

				hg.recordValue(total);
				hgAcumulado.recordValue(latenciaAcumulada);

				// Calculate the next time to call execute op
				nextCallTime += expectedTimeBetweenCalls;
			}

			finished = true;
		}

		/** Wait for the runner execution to complete */
		public void waitToFinish() {
			while (!this.finished) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		public void setHistograma(SynchronizedHistogram hg) {
			this.hg = hg;
		}

		public void setHistogramaAcumulado(SynchronizedHistogram hgAcumulado) {
			this.hgAcumulado = hgAcumulado;
		}
	}
}
