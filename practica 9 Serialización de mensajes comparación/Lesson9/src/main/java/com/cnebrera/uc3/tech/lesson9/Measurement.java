package com.cnebrera.uc3.tech.lesson9;

import com.cnebrera.uc3.tech.lesson9.jaxb.JaxbSerializer;
import com.cnebrera.uc3.tech.lesson9.json.JsonSerializer;
import com.cnebrera.uc3.tech.lesson9.kryo.KryoSerializer;
import com.cnebrera.uc3.tech.lesson9.model.ReferenceData;
import com.cnebrera.uc3.tech.lesson9.proto.Lesson9;
import com.cnebrera.uc3.tech.lesson9.proto.ProtoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main Class that measure the performance
 */
public class Measurement
{
    private static long NUM_ITERATIONS = 100000000;
    /** a org.slf4j.Logger with the instance of this class given by org.slf4j.LoggerFactory */
    private final static Logger LOGGER = LoggerFactory.getLogger(Measurement.class);

    private final static JaxbSerializer jaxbSerializer = new JaxbSerializer();
    private final static JsonSerializer jsonSerializer = new JsonSerializer();
    private final static KryoSerializer kryoSerializer = new KryoSerializer();
    private final static ProtoSerializer protoSerializer = new ProtoSerializer();

    public static void main(String[] args) throws URISyntaxException, IOException
    {


        //Read the info from a xml and populate the class

        URL url = Measurement.class.getClassLoader().getResource("Example.xml");
        URL urlJson = Measurement.class.getClassLoader().getResource("Example.json");

        String str = new String(Files.readAllBytes(Paths.get(url.toURI())));
        String json = new String(Files.readAllBytes(Paths.get(urlJson.toURI())));

        ReferenceData referenceData = jaxbSerializer.deserialize(str);

        LOGGER.debug("[Practica 1] Size of referenceData instrument list {}", referenceData.getListOfInstruments().size());
        LOGGER.debug("[Practica 1] Algorithm identifier{}", referenceData.getAlgorithmIdentifier());
        LOGGER.debug("[Practica 1] Algorithm marketId{}", referenceData.getMarketId());

        LOGGER.debug("[Practica 2] Json Serializer [{}] ", referenceData.equals(jsonSerializer.deserialize(json)));

        // TODO set the parameters in the builder using the values read in referenceData from JSON to ensure both have the same contents
        Lesson9.ReferenceData.Builder referenceDataBuilder = Lesson9.ReferenceData.newBuilder();
        Lesson9.Instrument.Builder instrumentBuilder = Lesson9.Instrument.newBuilder();


        referenceDataBuilder.setAlgorithmIdentifier(referenceData.getAlgorithmIdentifier());
        referenceDataBuilder.setMarketId(referenceData.getMarketId());
        for(int i = 0; i < referenceData.getListOfInstruments().size(); i++){
            instrumentBuilder.setInstrumentId(referenceData.getListOfInstruments().get(i).getInstrumentId());
            instrumentBuilder.setSymbol(referenceData.getListOfInstruments().get(i).getSymbol());
            referenceDataBuilder.addInstrument(instrumentBuilder);
        }


        //Test Proto
        Lesson9.ReferenceData referenceDataProto = referenceDataBuilder.build();
        LOGGER.debug("[Practica 3] Proto Serializer [{}] ", referenceDataProto.equals(protoSerializer.deserialize(protoSerializer.serialize(referenceDataProto))));

        //Test Kryo
        LOGGER.debug("[Practica 4] Kryo Serializer [{}] ",referenceData.equals(kryoSerializer.deserialize(kryoSerializer.serialize(referenceData))));

        //Test performance serialization
        testPerformanceSerialization(referenceData, referenceDataProto);

        //Test performance deserialization
        testPerformanceDeSerialization(str, jsonSerializer.serialize(referenceData), kryoSerializer.serialize(referenceData), referenceDataProto.toByteArray());

        //Test performance serialization and deserialization
        testPerformanceSerializationAndDeserialization(referenceData, referenceDataProto);
    }

    private static void testPerformanceSerialization(ReferenceData referenceData, Lesson9.ReferenceData referenceDataProto)
    {
        System.out.println("Serializaci�n.");
        //JAXB serialization
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            jaxbSerializer.serialize(referenceData);
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serializaci�n Jaxb = " +meanJaxb);

        //Json serialization
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            jsonSerializer.serialize(referenceData);
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serializaci�n Json= " +meanJson);

        //Protocol Buffers serialization
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            protoSerializer.serialize(referenceDataProto);
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serializaci�n Proto= " +meanProto);

        //Kryo serialization
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            kryoSerializer.serialize(referenceData);
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serializaci�n Kryo= " +meanKryo);
    }

    private static void testPerformanceDeSerialization(String jaxbSerialize, String jsonSerlize, byte[] kryoSerialize, byte[] protoSerialize)
    {
        System.out.println("Deserealizaci�n .");
        //JAXB serialization
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            jaxbSerializer.deserialize(jaxbSerialize);
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Desrializaci�n Jaxb = " +meanJaxb);

        //Json serialization
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            jsonSerializer.deserialize(jsonSerlize);
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Desrializaci�n  Json= " +meanJson);

        //Protocol Buffers serialization
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            protoSerializer.deserialize(protoSerialize);
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Desrializaci�n Proto= " +meanProto);

        //Kryo serialization
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            kryoSerializer.deserialize(kryoSerialize);
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Desrializaci�n Kryo= " +meanKryo);
    }

    private static void testPerformanceSerializationAndDeserialization(ReferenceData referenceData, Lesson9.ReferenceData referenceDataProto)
    {
        System.out.println("Serealizaci�n y deserealizaci�n.");
        //JAXB serialization
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            String jaxbSerialized = jaxbSerializer.serialize(referenceData);
            jaxbSerializer.deserialize(jaxbSerialized);
            //TODO fillWith Serialization And Deserialization
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serealizaci�n y deserealizaci�n Jaxb= " + meanJaxb);

        //Json serialization
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            String jsonSerialized = jsonSerializer.serialize(referenceData);
            jsonSerializer.deserialize(jsonSerialized);
            //TODO fillWith Serialization And Deserialization
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serealizaci�n y deserealizaci�n  Json= " +meanJson);

        //Protocol Buffers serialization
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {

            byte [] protoSerialized = protoSerializer.serialize(referenceDataProto);
            protoSerializer.deserialize(protoSerialized);
            //TODO fillWith Serialization And Deserialization
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serealizaci�n y deserealizaci�n Proto= " +meanProto);

        //Kryo serialization
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++)
        {
            byte [] kryoSerialized = kryoSerializer.serialize(referenceData);
            kryoSerializer.deserialize(kryoSerialized);
            //TODO fillWith Serialization And Deserialization
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni)/NUM_ITERATIONS;
        System.out.println("media Serealizaci�n y deserealizaci�n Kryo= " +meanKryo);
    }
}
