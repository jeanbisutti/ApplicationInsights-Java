/*
 * ApplicationInsights-Java
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the ""Software""), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.microsoft.applicationinsights.agent.bootstrap.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

public class InstrumentationSettings {

    public String connectionString;
    public PreviewConfiguration preview = new PreviewConfiguration();

    public enum SpanProcessorMatchType {
        //Moshi JSON builder donot allow case insensitive mapping
        strict, regexp
    }

    public enum SpanProcessorActionType {
        //Moshi JSON builder donot allow case insensitive mapping
        insert, update, delete, hash
    }

    public static class PreviewConfiguration {

        public String roleName;
        public String roleInstance;
        public SelfDiagnostics selfDiagnostics = new SelfDiagnostics();
        public Sampling sampling = new Sampling();
        public Heartbeat heartbeat = new Heartbeat();
        public HttpProxy httpProxy = new HttpProxy();
        public boolean developerMode;

        public List<JmxMetric> jmxMetrics = new ArrayList<>();
        public Map<String, SpanProcessorConfig> spanProcessors = new HashMap<>();
        public Map<String, Map<String, Object>> instrumentation = new HashMap<String, Map<String, Object>>();
    }

    public static class SelfDiagnostics {

        public String destination;
        public String directory;
        public String level = "error";
        public int maxSizeMB = 10;
    }

    public static class Sampling {

        public FixedRateSampling fixedRate = new FixedRateSampling();
    }

    public static class FixedRateSampling {

        public Double percentage;
    }

    public static class Heartbeat {

        public long intervalSeconds = MINUTES.toSeconds(15);
    }

    public static class HttpProxy {

        public String host;
        public int port = 80;
    }

    public static class JmxMetric {

        public String objectName;
        public String attribute;
        public String display;
    }

    public static class SpanProcessorConfig {
        public SpanProcessorIncludeExclude include;
        public SpanProcessorIncludeExclude exclude;
        public List<SpanProcessorAction> actions;

        public boolean isValid() {
            if (actions == null || actions.isEmpty()) {
                return false;
            }
            if (include != null && !include.isValid()) {
                return false;
            }
            if (exclude != null && !exclude.isValid()) {
                return false;
            }
            for (SpanProcessorAction action : actions) {
                if (!action.isValid()) return false;
            }
            return true;
        }
    }

    public static class SpanProcessorIncludeExclude {
        public SpanProcessorMatchType matchType;
        public List<String> spanNames;
        //All of these attributes must match exactly for a match to occur
        //Only match_type=strict is allowed if "attributes" are specified.
        public List<SpanProcessorAttribute> attributes;

        public boolean isValid() {
            if (this.matchType == null) return false;
            if (this.spanNames == null && this.attributes == null) return false;
            if (this.attributes != null) {
                for (SpanProcessorAttribute attribute : this.attributes) {
                    if (attribute.key == null) return false;
                }
            }
            return true;
        }
    }

    public static class SpanProcessorAttribute {
        public String key;
        public String value;
    }

    public static class SpanProcessorAction {
        public String key;
        public SpanProcessorActionType action;
        public String value;
        public String fromAttribute;

        public boolean isValid() {
            if (this.key == null) return false;
            if (this.action == null) return false;
            if (this.action == SpanProcessorActionType.insert || this.action == SpanProcessorActionType.update) {
                return this.value != null || this.fromAttribute != null;
            }
            return true;
        }
    }
}
