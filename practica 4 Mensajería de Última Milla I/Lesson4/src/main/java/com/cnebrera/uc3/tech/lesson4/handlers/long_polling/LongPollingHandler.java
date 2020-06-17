package com.cnebrera.uc3.tech.lesson4.handlers.long_polling ;

import java.io.IOException;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;

import com.cnebrera.uc3.tech.lesson4.handlers.IPricesListener;
import com.cnebrera.uc3.tech.lesson4.handlers.PricesPublisher;

/**
 * Atmosphere handler
 * --------------------------------------
 * @author Francisco Manuel Benitez Chico
 * --------------------------------------
 */

// TODO 1
@AtmosphereHandlerService(path="/prices/long-polling")
public class LongPollingHandler implements AtmosphereHandler
{
	/** Attribute - Prices Listener - Long Polling */
	private IPricesListener pricesListener ;
	
    @Override
    public void onRequest(final AtmosphereResource atmosphereResource) throws IOException
    {
    	if (this.pricesListener == null)
    	{
    		// New prices listener
    		this.pricesListener = new PricesListenerLongPolling() ;
    		
    		// Add the listener to the publisher
    		PricesPublisher.getInstance().addPricesListener(this.pricesListener) ;
    	}
    	
    	// Get the Atmosphere request
    	final AtmosphereRequest atmosphereRequest = atmosphereResource.getRequest() ;
    	
    	// TODO 2
    	atmosphereRequest.resource().suspend(-1);
		((PricesListenerLongPolling)this.pricesListener).sendPendingPricesChange(atmosphereRequest.resource().getResponse());
		atmosphereRequest.resource().resume();

    }   
    
    @Override
    public void onStateChange(final AtmosphereResourceEvent atmosphereResourceEvent) throws IOException
    {
    	// TODO 3
    	if (atmosphereResourceEvent.isCancelled() || atmosphereResourceEvent.isClosedByApplication() ||
    			atmosphereResourceEvent.isClosedByClient())
    		{
    			this.removeListener() ;
    		}
    }

	@Override
	public void destroy()
	{
		// TODO 4
		this.removeListener();
	}
	
	/**
	 * Remove the listener
	 */
	private void removeListener()
	{
		if (this.pricesListener != null)
		{
			// Add the listener to the publisher
			PricesPublisher.getInstance().removePricesListener(this.pricesListener) ;
			
			// Set to null
			this.pricesListener = null ;
		}
	}
}   