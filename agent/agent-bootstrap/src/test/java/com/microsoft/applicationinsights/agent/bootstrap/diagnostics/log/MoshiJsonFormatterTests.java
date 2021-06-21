package com.microsoft.applicationinsights.agent.bootstrap.diagnostics.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MoshiJsonFormatterTests {

    private MoshiJsonFormatter formatter;

    @BeforeEach
    public void setup() {
        formatter = new MoshiJsonFormatter();
    }

    @AfterEach
    public void tearDown() {
        formatter = null;
    }

    @Test
    public void formatterSerializesSimpleMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("s1", "v1");
        m.put("int1", 123);
        m.put("b", true);
        assertThat(formatter.toJsonString(m)).isEqualTo("{\"b\":true,\"int1\":123,\"s1\":\"v1\"}");
    }

    @Test
    public void formatterWithPrettyPrintPrintsPretty() {
        Map<String, Object> m = new HashMap<>();
        m.put("s1", "v1");
        m.put("int1", 123);
        formatter.setPrettyPrint(true);
        assertThat(formatter.toJsonString(m)).isEqualTo("{\n" +
                "  \"int1\": 123,\n" +
                "  \"s1\": \"v1\"\n" +
                "}");
    }
}
