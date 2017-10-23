package org.elasticsearch.plugin.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class Message implements ToXContentObject {

	private final String name;

	public Message(String name) {
		if (name == null) {
			this.name = "World";
		} else {
			this.name = name;
		}
	}

	@Override
	public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("message", "Hello " + name + "!");
		return builder.map(map);
	}

}
