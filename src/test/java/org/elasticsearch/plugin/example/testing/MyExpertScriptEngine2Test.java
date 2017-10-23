package org.elasticsearch.plugin.example.testing;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.plugin.example.ExamplePlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@ClusterScope(scope = Scope.SUITE, numDataNodes = 1, numClientNodes = 1)
public class MyExpertScriptEngine2Test extends ESIntegTestCase {
	
	public MyExpertScriptEngine2Test() {
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
        Map<String, Object> params = new HashMap<>();
        Script script = new Script(
        		ScriptType.INLINE, 
        		"expert_scripts", 
        		"pure_df", 
        		params);
        FilterFunctionBuilder filterFunctionBuilder = new FilterFunctionBuilder(
        		ScoreFunctionBuilders.scriptFunction(script));
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
        		QueryBuilders.matchQuery("body", "foo"), 
        		new FilterFunctionBuilder[] {filterFunctionBuilder});

        SearchResponse response = client().prepareSearch()
        		.setQuery(functionScoreQueryBuilder)
        		.execute().actionGet();
        SearchHits hits = response.getHits();
        hits.forEach(hit -> System.out.println(hit.getSourceAsString()));
    }
	
	public Map<String, Object> entityAsMap(Response response) throws IOException {
        XContentType xContentType = XContentType.fromMediaTypeOrFormat(response.getEntity().getContentType().getValue());
        try (XContentParser parser = createParser(xContentType.xContent(), response.getEntity().getContent())) {
            return parser.map();
        }
    }
	
}
