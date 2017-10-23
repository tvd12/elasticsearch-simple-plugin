package org.elasticsearch.plugin.example;

import static org.elasticsearch.rest.RestRequest.Method.GET;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestToXContentListener;;

public class HelloRestAction extends BaseRestHandler {

	protected HelloRestAction(Settings settings, RestController controller) {
		super(settings);
		controller.registerHandler(GET, "/_hello", this);
		controller.registerHandler(GET, "/_hello/{name}", this);
	}

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        String name = request.param("name");
        String lang = request.param("lang");
        
		GetStoredScriptRequest getRequest = new GetStoredScriptRequest(name, lang);
		getRequest.masterNodeTimeout(request.paramAsTime("master_timeout", getRequest.masterNodeTimeout()));
        getRequest.masterNodeTimeout(request.paramAsTime("timeout", getRequest.masterNodeTimeout()));
		
		return new RestChannelConsumer() {
			
			@Override
			public void accept(RestChannel channel) throws Exception {
				client.admin().cluster().getStoredScript(getRequest); 
				RestToXContentListener<ToXContentObject> listener = new RestToXContentListener<>(channel);
				listener.onResponse(new Message(name));
			}
		};
	}

	
}
