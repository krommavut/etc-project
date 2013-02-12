package semanticMarkup.ling.chunk.lib.chunker;

import java.util.HashMap;
import java.util.Set;

import semanticMarkup.know.IGlossary;
import semanticMarkup.ling.chunk.AbstractChunker;
import semanticMarkup.ling.chunk.Chunk;
import semanticMarkup.ling.chunk.ChunkCollector;
import semanticMarkup.ling.chunk.ChunkType;
import semanticMarkup.ling.learn.ITerminologyLearner;
import semanticMarkup.ling.parse.AbstractParseTree;
import semanticMarkup.ling.parse.ParseTreeFactory;
import semanticMarkup.ling.transform.IInflector;

public class AreaChunker extends AbstractChunker {
	
	public AreaChunker(ParseTreeFactory parseTreeFactory,
			String prepositionWords, Set<String> stopWords, String units,
			HashMap<String, String> equalCharacters, IGlossary glossary,
			ITerminologyLearner terminologyLearner, IInflector inflector) {
		super(parseTreeFactory, prepositionWords, stopWords, units, equalCharacters,
				glossary, terminologyLearner, inflector);
	}


	@Override
	public void chunk(ChunkCollector chunkCollector) {
		for(AbstractParseTree terminal : chunkCollector.getTerminals()) {
			//token: 4-9cm�usually15-25mm		
			String terminalsText = terminal.getTerminalsText();
			if(terminalsText.contains("x") && !terminalsText.contains("\\s")) {
				String dim[] = terminalsText.split("x");
				boolean hasValidDimensions = true;
				int dimensions = 0;
				for(int i = 0; i < dim.length; i++){
					hasValidDimensions = dim[i].matches(".*?\\d.*");
					if(!hasValidDimensions)
						return;
					dimensions++;
				}
				if(dimensions >= 2) {
					terminalsText = terminalsText.replaceAll("�[^0-9]*", " � ").replaceAll("(?<=[^a-z])(?=[a-z])", " ").replaceAll("(?<=[a-z])(?=[^a-z])", " ").replaceAll("\\s+", " ").trim();
					terminal.setTerminalsText(terminalsText);
					chunkCollector.addChunk(new Chunk(ChunkType.AREA, terminal));
				}
			}
		}
	}
}