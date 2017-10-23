package org.elasticsearch.plugin.example;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.function.Function;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.script.CompiledScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.LeafSearchScript;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.SearchScript;
import org.elasticsearch.search.lookup.SearchLookup;

public class MyExpertScriptEngine implements ScriptEngineService {
    @Override
    public String getType() {
        return "expert_scripts";
    }

    @Override
    public Function<Map<String,Object>,SearchScript> compile(String scriptName, String scriptSource, Map<String, String> params) {
    	System.out.println("\n\ni compile, scriptName = " + scriptName + ", scriptSource = " + scriptSource + ", params = " + params + "\n\n");
        // we use the script "source" as the script identifier
        if ("pure_df".equals(scriptSource)) {
            return p -> new SearchScript() {
                final String field;
                final String term;
                {
                    if (p.containsKey("field") == false) {
                        throw new IllegalArgumentException("Missing parameter [field]");
                    }
                    if (p.containsKey("term") == false) {
                        throw new IllegalArgumentException("Missing parameter [term]");
                    }
                    field = p.get("field").toString();
                    term = p.get("term").toString();
                }

                @Override
                public LeafSearchScript getLeafSearchScript(LeafReaderContext context) throws IOException {
                    PostingsEnum postings = context.reader().postings(new Term(field, term));
                    if (postings == null) {
                        // the field and/or term don't exist in this segment, so always return 0
                        return () -> 0.0d;
                    }
                    return new LeafSearchScript() {
                        int currentDocid = -1;
                        @Override
                        public void setDocument(int docid) {
                            // advance has undefined behavior calling with a docid <= its current docid
                            if (postings.docID() < docid) {
                                try {
                                    postings.advance(docid);
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            }
                            currentDocid = docid;
                        }
                        @Override
                        public double runAsDouble() {
                            if (postings.docID() != currentDocid) {
                                // advance moved past the current doc, so this doc has no occurrences of the term
                                return 0.0d;
                            }
                            try {
                                return postings.freq();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                    };
                }

                @Override
                public boolean needsScores() {
                    return false;
                }
            };
        }
        throw new IllegalArgumentException("Unknown script name " + scriptSource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SearchScript search(CompiledScript compiledScript, SearchLookup lookup, @Nullable Map<String, Object> params) {
    	System.out.println("i search,  compiledScript = " + compiledScript + ", lookup = " + lookup + ", params = " + params);
      Function<Map<String,Object>,SearchScript> scriptFactory = (Function<Map<String,Object>,SearchScript>) compiledScript.compiled();
      return scriptFactory.apply(params);
    }

    @Override
    public ExecutableScript executable(CompiledScript compiledScript, @Nullable Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInlineScriptEnabled() {
        return true;
    }

    @Override
    public void close() {}
}
