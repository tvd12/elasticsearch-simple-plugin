package org.elasticsearch.plugin.example.testing;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.plugin.example.ExamplePlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.carrotsearch.randomizedtesting.RandomizedRunner;

@ClusterScope(scope = Scope.SUITE)
@RunWith(RandomizedRunner.class)
public class ExamplePluginTest extends ESIntegTestCase {
	
	public ExamplePluginTest() {
		System.setProperty(TESTS_CLUSTER, "localhost:9300");
		System.setProperty("tests.rest.cluster", "localhost:9200");
	}
	
	@Override
	protected boolean ignoreExternalCluster() {
		return true;
	}

	@Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singleton(ExamplePlugin.class);
    }

	@Test
    public void testPluginIsLoaded() throws Exception {
        NodesInfoResponse response = client().admin().cluster().prepareNodesInfo().setPlugins(true).get();
        for (NodeInfo nodeInfo : response.getNodes()) {
            boolean pluginFound = false;
            for (PluginInfo pluginInfo : nodeInfo.getPlugins().getPluginInfos()) {
                if (pluginInfo.getName().equals(ExamplePlugin.class.getName())) {
                    pluginFound = true;
                    break;
                }
            }
            assert pluginFound;
        }
    }
	
}
