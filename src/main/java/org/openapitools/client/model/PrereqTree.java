/*
 * NUSMods API
 * NUSMods API contains data used to render <https://nusmods.com>. It includes data on modules offered by NUS and their timetables, as well as information on the locations the classes take place in. You are welcome to use and experiment with the data, which is extracted from official APIs provided by the Registrar's Office.  The API consists of static JSON files scraped daily from the school's APIs. This means it only partially follow REST conventions, and all resources are read only. All successful responses will return JSON, and all endpoints end in `.json`.  The shape of the data returned by these endpoints are designed for NUSMods in mind. If you have any questions or find that you need the data in other shapes for other purposes, feel free to reach out to us:  - **Telegram**: <https://telegram.me/nusmods> - **Mailing list**: <nusmods@googlegroups.com> (for security related issues please email <mods@nusmods.com> instead)  ## Fetching data  Any HTTP client can be used to fetch data from the API. HTTPS is preferred, but the server will also respond to HTTP requests. The server supports HTTP 1.1 as well as HTTP 2 over HTTPS, and supports gzip compression.  The API has no authentication, and is not rate limited. While the server can respond to a large number of requests simultaneously, we request that you be polite with resource usage so as not to disrupt nusmods.com, which relies on the same API server. In general there is no need to fetch data from the API more than once per day, as that is the frequency at which we update the data.  [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) headers are enabled on all endpoints, so client-side JavaScript can use also use the API.  ## TypeScript types  Since the NUSMods is written in TypeScript, typings are available in the source for the scraper. These may be easier to read than the documentation generated by Swagger.  - Module types: <https://github.com/nusmodifications/nusmods/blob/master/scrapers/nus-v2/src/types/modules.ts> - Venue types: <https://github.com/nusmodifications/nusmods/blob/master/scrapers/nus-v2/src/types/venues.ts>  ## Data  Below are some notes about the data returned from the API. Feel free to talk to us or create an issue if any of it is not clear.  ### Module data  Module endpoints return information on modules offered by NUS. Most of the module data is self-explanatory, but some of the data are more complex and is explained here.  #### Lessons  Each lesson in a timetable has a lesson type `lessonType` and class number `ClassNo`. Every student must take one of each lesson type offered by the module. For example, this module offers two tutorials and one lecture. That means the student must attend the lecture, and can choose one of the two tutorials to attend.  ```json {   \"timetable\": [     {       \"classNo\": \"1\",       \"lessonType\": \"Lecture\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Tuesday\",       \"startTime\": \"1600\",       \"endTime\": \"1800\",       \"venue\": \"I3-AUD\"     },     {       \"classNo\": \"01\",       \"lessonType\": \"Tutorial\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Wednesday\",       \"startTime\": \"1100\",       \"endTime\": \"1200\",       \"venue\": \"COM1-0207\"     },     {       \"classNo\": \"02\",       \"lessonType\": \"Tutorial\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Friday\",       \"startTime\": \"0900\",       \"endTime\": \"1000\",       \"venue\": \"COM1-0209\"     }   ] } ```  Each lesson has a `classNo` key. There can be multiple lessons of the same type and class number, in which case students must attend both. In this example, students can choose to attend either lecture group 1 on Tuesdays and Wednesdays, or lecture group 2 on Mondays and Wednesdays.  ```json {   \"timetable\": [     {       \"classNo\": \"1\",       \"lessonType\": \"Lecture\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Tuesday\",       \"startTime\": \"1600\",       \"endTime\": \"1800\",       \"venue\": \"I3-AUD\"     },     {       \"classNo\": \"1\",       \"lessonType\": \"Lecture\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Wednesday\",       \"startTime\": \"1400\",       \"endTime\": \"1500\",       \"venue\": \"I3-AUD\"     },     {       \"classNo\": \"2\",       \"lessonType\": \"Lecture\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Monday\",       \"startTime\": \"1000\",       \"endTime\": \"1200\",       \"venue\": \"I3-AUD\"     },     {       \"classNo\": \"2\",       \"lessonType\": \"Lecture\",       \"weeks\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13],       \"day\": \"Wednesday\",       \"startTime\": \"1500\",       \"endTime\": \"1600\",       \"venue\": \"I3-AUD\"     }   ] } ```  #### Lesson Weeks  The `weeks` key on lessons can return data in one of two forms.  Weeks is usually a sorted array of numbers. In this case it represents the school weeks the lesson occurs on, from 1 to 13.  Some classes have lessons outside of the school timetable. In this case a `WeekRange` object is returned. The object will always contain a `start` and `end` key representing the start and end date of lessons. The example below has classes every week from 17th Jan to 18th April.  ``` json \"weeks\": {   \"start\": \"2019-01-17\",   \"end\": \"2019-04-18\" } ```  Optionally it can also include `weekInterval`, a positive integer describing the number of weeks between each lesson, and `weeks`, an array of positive integers describing the weeks on which the lesson will fall, with week 1 being the starting date. If these are not present you can assume lessons will occur every week.  The following example has lessons on 17th Jan (week 1), 24th Jan (week 2), 7th Feb (week 4) and 21st Feb (week 6).  ``` json \"weeks\": {   \"start\": \"2019-01-17\",   \"end\": \"2019-02-21\",   \"weeks\": [1, 2, 4, 6] } ```  The following example has lessons on 17th Jan (week 1), 31st Jan (week 3), 14th Feb (week 5) and 28th Feb (week 7).  ``` json \"weeks\": {   \"start\": \"2019-01-17\",   \"end\": \"2019-02-28\",   \"weekInterval\": 2 } ```  #### Workload  The `workload` key can return data in one of two forms.  Workload is usually a **5-tuple of numbers**, describing the estimated number of hours per week the student is expected to put in for the module for **lectures, tutorials, laboratory, projects/fieldwork, and preparatory work** respectively. For example, a workload of `[2, 1, 1, 3, 3]` means the student should spend every week  - 2 hours in lectures - 1 hour in tutorials - 1 hour at the lab - 3 hours doing project work - 3 hours preparing for classes  Each module credit represents 2.5 hours of work each week, so the standard 4 MC module represents 10 hours of work each week. Module credit may not be integers.  Note that this is only an estimate, and may be outdated or differ significantly in reality. Some modules also incorrectly lists the **total** workload hours instead of weekly, so very large values may show up.  This value is parsed from a string provided by the school, and occasionally this field will contain unusual values which cannot be parsed. In this case this field will contain the original **string** instead, which should be displayed as-is to the user.  #### Prerequisite, corequisite and preclusions  These three keys determine whether a student can take a module.  **Prerequisites** are requirements you have to meet before you can take a module. These are usually in the form of other modules (see prerequisite tree below for a machine readable format), but can also be things like 'taken A-level H2 math' or '70 MCs and above'.  **Preclusions** refer to modules or requirements that cannot be taken if this module is taken, and vice versa. These are usually modules whose content overlaps significantly with this module, and can usually be used to replace each other to fulfill prerequisites.  **Corequisites** are modules that must be taken together with this module in the same semester. This usually refer to twined modules - modules which have linked syllabuses.  #### Prerequisite Tree  The `prereqTree` key is return on the individual module endpoint (`/modules/{moduleCode`). Not all modules have prerequisites, and some have prerequisites that cannot be properly represented as a tree, in which case this key will not appear.  This describes the prerequisites that need to be fulfilled before this module can be taken. The data structure is recursive and represents a tree.  ```json {   \"and\": [     \"CS1231\",     {       \"or\": [\"CS1010S\", \"CS1010X\"]     }   ] } ```  In the example, this module requires CS1231 and either CS1010S or CS1010X. This can be visualized as  ```            ┌ CS1231 ── all of ─┤            │         ┌ CS1010            └ one of ─┤                      └ CS1010X ```  The module information also contains the inverse of this, that is, modules whose requirements are fulfilled by this module (taking this module will allow you to take these modules in the following semester). The data is found on the `fulfillRequirements` key as an array of module codes.  ### Venue data  Venue data is simply the module timetable restructured to show the lessons happening at each classroom.  The venue list endpoint returns a list of all locations that are used in the semester. Note that this is not a comprehensive list of locations, but rather just a list of venues that appears in module lessons.  The venue information endpoint returns the full class and occupancy information about a venue. The `classes` key contains a list of lessons similar to the `timetable` key in module data, but without a `venue` key and with `moduleCode`.
 *
 * The version of the OpenAPI document: 2.0.0
 * Contact: nusmods@googlegroups.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.openapitools.client.model;

import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.client.model.PrereqTreeOneOf;
import org.openapitools.client.model.PrereqTreeOneOf1;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.openapitools.client.JSON;

@javax.annotation.processing.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-10-03T22:22:26.802458+08:00[Asia/Singapore]")
@JsonDeserialize(using = PrereqTree.PrereqTreeDeserializer.class)
@JsonSerialize(using = PrereqTree.PrereqTreeSerializer.class)
public class PrereqTree extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(PrereqTree.class.getName());

    public static class PrereqTreeSerializer extends StdSerializer<PrereqTree> {
        public PrereqTreeSerializer(Class<PrereqTree> t) {
            super(t);
        }

        public PrereqTreeSerializer() {
            this(null);
        }

        @Override
        public void serialize(PrereqTree value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class PrereqTreeDeserializer extends StdDeserializer<PrereqTree> {
        public PrereqTreeDeserializer() {
            this(PrereqTree.class);
        }

        public PrereqTreeDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public PrereqTree deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            // deserialize PrereqTreeOneOf
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (PrereqTreeOneOf.class.equals(Integer.class) || PrereqTreeOneOf.class.equals(Long.class) || PrereqTreeOneOf.class.equals(Float.class) || PrereqTreeOneOf.class.equals(Double.class) || PrereqTreeOneOf.class.equals(Boolean.class) || PrereqTreeOneOf.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((PrereqTreeOneOf.class.equals(Integer.class) || PrereqTreeOneOf.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((PrereqTreeOneOf.class.equals(Float.class) || PrereqTreeOneOf.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (PrereqTreeOneOf.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (PrereqTreeOneOf.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(PrereqTreeOneOf.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'PrereqTreeOneOf'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'PrereqTreeOneOf'", e);
            }

            // deserialize PrereqTreeOneOf1
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (PrereqTreeOneOf1.class.equals(Integer.class) || PrereqTreeOneOf1.class.equals(Long.class) || PrereqTreeOneOf1.class.equals(Float.class) || PrereqTreeOneOf1.class.equals(Double.class) || PrereqTreeOneOf1.class.equals(Boolean.class) || PrereqTreeOneOf1.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((PrereqTreeOneOf1.class.equals(Integer.class) || PrereqTreeOneOf1.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((PrereqTreeOneOf1.class.equals(Float.class) || PrereqTreeOneOf1.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (PrereqTreeOneOf1.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (PrereqTreeOneOf1.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(PrereqTreeOneOf1.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'PrereqTreeOneOf1'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'PrereqTreeOneOf1'", e);
            }

            // deserialize String
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (String.class.equals(Integer.class) || String.class.equals(Long.class) || String.class.equals(Float.class) || String.class.equals(Double.class) || String.class.equals(Boolean.class) || String.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((String.class.equals(Integer.class) || String.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((String.class.equals(Float.class) || String.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (String.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (String.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(String.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'String'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'String'", e);
            }

            if (match == 1) {
                PrereqTree ret = new PrereqTree();
                ret.setActualInstance(deserialized);
                return ret;
            }
            throw new IOException(String.format("Failed deserialization for PrereqTree: %d classes match result, expected 1", match));
        }

        /**
         * Handle deserialization of the 'null' value.
         */
        @Override
        public PrereqTree getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "PrereqTree cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public PrereqTree() {
        super("oneOf", Boolean.FALSE);
    }

    public PrereqTree(PrereqTreeOneOf o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public PrereqTree(PrereqTreeOneOf1 o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public PrereqTree(String o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("PrereqTreeOneOf", PrereqTreeOneOf.class);
        schemas.put("PrereqTreeOneOf1", PrereqTreeOneOf1.class);
        schemas.put("String", String.class);
        JSON.registerDescendants(PrereqTree.class, Collections.unmodifiableMap(schemas));
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return PrereqTree.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check
     * the instance parameter is valid against the oneOf child schemas:
     * PrereqTreeOneOf, PrereqTreeOneOf1, String
     *
     * It could be an instance of the 'oneOf' schemas.
     * The oneOf child schemas may themselves be a composed schema (allOf, anyOf, oneOf).
     */
    @Override
    public void setActualInstance(Object instance) {
        if (JSON.isInstanceOf(PrereqTreeOneOf.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(PrereqTreeOneOf1.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(String.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be PrereqTreeOneOf, PrereqTreeOneOf1, String");
    }

    /**
     * Get the actual instance, which can be the following:
     * PrereqTreeOneOf, PrereqTreeOneOf1, String
     *
     * @return The actual instance (PrereqTreeOneOf, PrereqTreeOneOf1, String)
     */
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `PrereqTreeOneOf`. If the actual instance is not `PrereqTreeOneOf`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `PrereqTreeOneOf`
     * @throws ClassCastException if the instance is not `PrereqTreeOneOf`
     */
    public PrereqTreeOneOf getPrereqTreeOneOf() throws ClassCastException {
        return (PrereqTreeOneOf)super.getActualInstance();
    }

    /**
     * Get the actual instance of `PrereqTreeOneOf1`. If the actual instance is not `PrereqTreeOneOf1`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `PrereqTreeOneOf1`
     * @throws ClassCastException if the instance is not `PrereqTreeOneOf1`
     */
    public PrereqTreeOneOf1 getPrereqTreeOneOf1() throws ClassCastException {
        return (PrereqTreeOneOf1)super.getActualInstance();
    }

    /**
     * Get the actual instance of `String`. If the actual instance is not `String`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `String`
     * @throws ClassCastException if the instance is not `String`
     */
    public String getString() throws ClassCastException {
        return (String)super.getActualInstance();
    }

}
