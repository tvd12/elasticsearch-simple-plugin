package org.elasticsearch.plugin.example;

import java.util.Map;

import org.elasticsearch.ingest.Processor;
import org.elasticsearch.ingest.Processor.Factory;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;
import static org.elasticsearch.plugin.example.BanoProcessor.NAME;

public class BanoProcessorFactory implements Processor.Factory {

	@Override
	public Processor create(
			Map<String, Factory> processorFactories, 
			String tag, 
			Map<String, Object> config) throws Exception {
		String source = readStringProperty(NAME, tag, config, "source", "foo");
		String target = readStringProperty(NAME, tag, config, "target", "new_" + source);
		BanoProcessor processor = new BanoProcessor(tag, source, target);
		return processor;
	}

}
