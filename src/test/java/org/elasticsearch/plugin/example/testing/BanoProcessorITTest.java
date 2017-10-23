package org.elasticsearch.plugin.example.testing;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.Test;

public class BanoProcessorITTest extends ESRestTestCase {
	
	public BanoProcessorITTest() {
		System.setProperty("tests.rest.cluster", "localhost:9200");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSimulateProcessor() throws Exception {
        String json = jsonBuilder().startObject()
                .startObject("pipeline")
                    .startArray("processors")
                        .startObject()
                            .startObject("bano")
                            .endObject()
                        .endObject()
                    .endArray()
                .endObject()
                .startArray("docs")
                    .startObject()
                        .field("_index", "index")
                        .field("_type", "type")
                        .field("_id", "id")
                        .startObject("_source")
                            .field("foo", "bar")
                        .endObject()
                    .endObject()
                .endArray()
                .endObject().string();

        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");

        Response response = client().performRequest("POST", "/_ingest/pipeline/_simulate",
                Collections.emptyMap(), new NStringEntity(json, ContentType.APPLICATION_JSON));

        Map<String, Object> responseMap = entityAsMap(response);
        assert responseMap.containsKey("docs");
        List<Map<String, Object>> docs = (List<Map<String, Object>>) responseMap.get("docs");
        assert docs.size() == 1;
        Map<String, Object> doc1 = docs.get(0);
        assert doc1.containsKey("doc");
        Map<String, Object> doc = (Map<String, Object>) doc1.get("doc");
        assert doc.containsKey("_source");
        Map<String, Object> docSource = (Map<String, Object>) doc.get("_source");

        assert docSource.containsKey("foo");
        assert docSource.containsKey("new_foo");
    }
	
}
