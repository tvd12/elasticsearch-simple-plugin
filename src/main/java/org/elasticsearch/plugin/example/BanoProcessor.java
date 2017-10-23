package org.elasticsearch.plugin.example;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;

public class BanoProcessor extends AbstractProcessor {

	private final String sourceField;
	private final String targetField;
	
	public final static String NAME = "bano";

    protected BanoProcessor(String tag, String sourceField, String targetField) {
        super(tag);
        this.sourceField = sourceField;
        this.targetField = targetField;
    }

	@Override
	public void execute(IngestDocument document) throws Exception {
		System.out.println("\n\n\ni fired\n\n\n");
		if (document.hasField(sourceField)) {
			document.setFieldValue(targetField, document.getFieldValue(sourceField, String.class));
	    }
	}

	@Override
	public String getType() {
		return NAME;
	}
	
	
	
}
