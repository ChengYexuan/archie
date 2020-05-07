package com.nedap.archie.json.flat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class FlatJsonGeneratorTest {

    private static final String BLOOD_PRESSURE_PATH = "/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls";
    private static final double EPSILON = 0.00000001d;

    @Test
    public void testBloodPressureExample() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        RMObject rmObject = createExampleInstance(bloodPressureOpt);

        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
        config.setWritePipesForPrimitiveTypes(false);
        Map<String, Object> stringObjectMap = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), config)
                .buildPathsAndValues(rmObject);
        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/_type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name/value"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]:1/data[id4]/items[id5]/name/value"));

    }



    @Test
    public void testBloodPressureExampleWithPipesForFinalFields() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        RMObject rmObject = createExampleInstance(bloodPressureOpt);

        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
        Map<String, Object> stringObjectMap = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), config)
                .buildPathsAndValues(rmObject);
        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/_type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name|value"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin|value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value|magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value|precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]:1/data[id4]/items[id5]/name|value"));
    }

    @Test
    public void testBloodPressureHumanReadable() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        RMObject rmObject = createExampleInstance(bloodPressureOpt);

        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.humanReadableStandardFormatInDevelopment();
        Map<String, Object> stringObjectMap = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), config)
                .buildPathsAndValues(rmObject);
        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));



    }

    @Test
    public void testNedapInternalFormat() throws Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        RMObject rmObject = createExampleInstance(bloodPressureOpt);

        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        Map<String, Object> stringObjectMap = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), config)
                .buildPathsAndValues(rmObject);
        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/@type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name/value"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5]/name/value"));
    }

    private OperationalTemplate parseBloodPressure() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(BLOOD_PRESSURE_PATH)) {
            Archetype bloodPressure = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
            return (OperationalTemplate) flattener.flatten(bloodPressure);
        }
    }

    private RMObject createExampleInstance(OperationalTemplate bloodPressureOpt) throws IOException {
        ExampleJsonInstanceGenerator exampleGenerator = new ExampleJsonInstanceGenerator(BuiltinReferenceModels.getMetaModels(), "en");
        Map<String, Object> generate = exampleGenerator.generate(bloodPressureOpt);
        String rmObjectJson = JacksonUtil.getObjectMapper().writeValueAsString(generate);
        return JacksonUtil.getObjectMapper().readValue(rmObjectJson, RMObject.class);
    }

}
