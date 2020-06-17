package com.imdg.practicas;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;
import com.imdg.pojos.Person;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PracticaIMDG2a {
	 private final static Logger LOGGER = Logger.getLogger("com.imdg.practicas.PracticaIMDG2a");
	    public static void main(String[] args) throws InterruptedException {
		    // Instanciar hazelcast y crear una cache
	        // Insertar un dato y arrancar 3 veces el main,
	        // Leer el output de consola y ver como hazelcast va encontrando "miembros"
	        // Comprobar que se conectan (en el output deberian verse 3 miembros en la consola) y capturarlo	    	
	        //Leader node
	    	HazelcastInstance hazInstance = Hazelcast.newHazelcastInstance();
	        IMap<String, Person> mapCustomers = hazInstance.getMap("practica6");
	         ICountDownLatch latch = hazInstance.getCountDownLatch( "practica6" );
	        	        
	        try{	       
		        System.out.println( "Leader Starting" );
		        latch.trySetCount( 3 );
		        Thread.sleep( 30000 );
		        
	        mapCustomers.put("nodo1", new Person("Adrian", 28051, "Rejas", "calle alacala 62, Madrid"));
	        latch.countDown();
	        Config config = new Config();
	        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("localhost").setEnabled(true);
	        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
	            latch.await(30, TimeUnit.SECONDS);
	           System.out.println(mapCustomers.get("nodo1"));
	            System.out.println(mapCustomers.get("nodo2"));
	            System.out.println(mapCustomers.get("nodo3"));
	            latch.destroy();
	        latch.countDown();
	        System.out.println( "Leader finished" );
	        latch.destroy();}
	        catch (InterruptedException e){
	            LOGGER.log(Level.WARNING, "Hazelcast interrupted");
	        }
	        
	        
	    }
}
