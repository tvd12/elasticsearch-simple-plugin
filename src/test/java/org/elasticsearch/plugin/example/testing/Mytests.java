package org.elasticsearch.plugin.example.testing;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ESIntegTestCase;

public class Mytests extends ESIntegTestCase {

	public Mytests() {
		
	}

	@Override
	protected Settings nodeSettings(int nodeOrdinal) {
		return Settings.builder()
				.put(super.nodeSettings(nodeOrdinal))
				.put("node.mode", "network")
				.build();
	}

}