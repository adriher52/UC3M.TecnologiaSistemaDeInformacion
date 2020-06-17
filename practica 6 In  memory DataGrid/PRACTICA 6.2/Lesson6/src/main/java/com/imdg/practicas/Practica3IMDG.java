package com.imdg.practicas;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.ArrayList;


import java.util.Map.Entry;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IdGenerator;
import com.imdg.pojos.Person;
public class Practica3IMDG {

    public static void main(String[] args) {
	    // Instanciar hazelcast Cliente y crear una cache
        
    	ClientConfig config = new ClientConfig();
        ArrayList<String> ips= new ArrayList<String>();
        ips.add("127.0.0.1");
        config.getNetworkConfig().setAddresses(ips);
        HazelcastInstance client = HazelcastClient.newHazelcastClient( config );
       
        IMap<String, String> mapCustomers = client.getMap("practica6");
       
        System.out.println("Antes : " + mapCustomers.get("CLAVE"));
        mapCustomers.put("CLAVE", "VALOR");
        System.out.println("Después: " + mapCustomers.get("CLAVE"));
        
       
        client.shutdown();

    }
}
