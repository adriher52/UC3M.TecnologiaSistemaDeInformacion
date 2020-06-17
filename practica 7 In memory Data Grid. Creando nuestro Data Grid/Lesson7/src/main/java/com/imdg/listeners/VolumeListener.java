package com.imdg.listeners;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import com.imdg.pojos.MarketOrder;

import java.io.Serializable;

/**
 * Created by Sobremesa on 31/10/2016.
 */
public class VolumeListener
            implements EntryAddedListener<String, MarketOrder>,
        EntryUpdatedListener<String, MarketOrder>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String instrumentoAControlar;
    private int volumenAcumulado=0;

    public VolumeListener(String instrument) {
        this.instrumentoAControlar=instrument;
    }

    /**
     * Escuchar entradas que se añaden y sumarlo al volumen/imprimir alerta si llegamos a 30000
     * @param entryEvent
     */
    @Override
    public void entryAdded(EntryEvent<String, MarketOrder> entryEvent) {
    	
    	if (entryEvent.getValue().getInstrument().equals(instrumentoAControlar)){
            volumenAcumulado = volumenAcumulado + entryEvent.getValue().getVolume();
            System.out.print("Volumen acumulado: "+ volumenAcumulado + "\n");
           
            if (volumenAcumulado >= 30000){
                System.out.print("El volumen acumulado ha llegado a 30.000"+ "\n");
                volumenAcumulado = 0;}
        }
    }

    /**
     * Escuchar entradas que se añaden, restar valor antiguo y
     * sumar el nuevo al volumen/imprimir alerta si llegamos a 30000
     * @param entryEvent
     */
    @Override
    public void entryUpdated(EntryEvent<String, MarketOrder> entryEvent) {
    	
    	if (entryEvent.getValue().getInstrument().equals(instrumentoAControlar)){
            volumenAcumulado = volumenAcumulado - entryEvent.getOldValue().getVolume();
            volumenAcumulado = volumenAcumulado + entryEvent.getValue().getVolume();
            System.out.print("Volumen acumulado: "+ volumenAcumulado + "\n");
            
            if (volumenAcumulado >= 30000){
                System.out.print("El volumen acumulado ha llegado a 30.000"+ "\n");
                volumenAcumulado = 0;}
        }
    }
    
    
}
