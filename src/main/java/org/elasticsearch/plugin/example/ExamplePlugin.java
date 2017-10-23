package org.elasticsearch.plugin.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.script.ScriptEngineService;

public class ExamplePlugin 
		extends Plugin
		implements ActionPlugin, IngestPlugin, ScriptPlugin {
	
	@Override
	public List<RestHandler> getRestHandlers(
			Settings settings, 
			RestController restController,
			ClusterSettings clusterSettings, 
			IndexScopedSettings indexScopedSettings, 
			SettingsFilter settingsFilter,
			IndexNameExpressionResolver indexNameExpressionResolver, 
			Supplier<DiscoveryNodes> nodesInCluster) {
		return Arrays.asList(new HelloRestAction(settings, restController));
	}
	
	@Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        return Collections.singletonMap("bano", new BanoProcessorFactory());
    }
	
	@Override
	public ScriptEngineService getScriptEngineService(Settings settings) {
		System.out.println("\n\nScriptEngineService get, setting = " + settings + "\n\n");
		return new MyExpertScriptEngine();
	}
	
}