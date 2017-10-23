package org.elasticsearch.plugin.example.testing;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugin.example.ExamplePlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1, numClientNodes = 1)
public class MyExpertScriptEngineTest extends ESIntegTestCase {
	
	public MyExpertScriptEngineTest() {
		System.setProperty(TESTS_CLUSTER, "localhost:9300");
		System.setProperty("tests.rest.cluster", "localhost:9200");
	}
	
	@Override
	protected boolean ignoreExternalCluster() {
		return true;
	}
	
	@Override
	protected Collection<Class<? extends Plugin>> transportClientPlugins() {
		return Collections.singleton(ExamplePlugin.class);
	}
	
	@Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singleton(ExamplePlugin.class);
    }

	@Test
	public void testSimulateProcessor() throws Exception {
        String json = XContentFactory.jsonBuilder()
        		.startObject()
	                .startObject("query")
	                	.startObject("function_score")
	                		.startObject("query")
	                			.startObject("match")
	                				.field("body", "foo")
	                			.endObject()
	                		.endObject()
	                		.startArray("functions")
	                		.startObject()
		                		.startObject("script_score")
		                			.startObject("script")
		                				.field("source", "pure_df")
		                				.field("lang", "expert_scripts")
		                				.startObject("params")
		                					.field("field", "body")
		                					.field("term", "foo")
		                				.endObject()
		                			.endObject()
		                		.endObject()
		                	.endObject()
	                	.endArray()
	                	.endObject()
	                .endObject()
	            .endObject()
                .string();
        
        Response response = getRestClient().performRequest("POST", "/_search?pretty",
                Collections.emptyMap(), new NStringEntity(json, ContentType.APPLICATION_JSON));
        Map<String, Object> responseMap = entityAsMap(response);
        System.out.println(responseMap);
    }
	
	public Map<String, Object> entityAsMap(Response response) throws IOException {
        XContentType xContentType = XContentType.fromMediaTypeOrFormat(response.getEntity().getContentType().getValue());
        try (XContentParser parser = createParser(xContentType.xContent(), response.getEntity().getContent())) {
            return parser.map();
        }
    }
	
}
