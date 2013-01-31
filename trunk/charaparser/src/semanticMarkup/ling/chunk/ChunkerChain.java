package semanticMarkup.ling.chunk;

import java.util.LinkedList;
import java.util.List;

import semanticMarkup.core.Treatment;
import semanticMarkup.ling.parse.AbstractParseTree;


public class ChunkerChain {
	
	protected List<IChunker> chunkers = new LinkedList<IChunker>();
	
	public ChunkCollector chunk(AbstractParseTree parseTree, String subjectTag, Treatment treatment, String source, String sentenceString) {
		ChunkCollector chunkCollector = new ChunkCollector(parseTree, subjectTag, treatment, source, sentenceString);
		for(IChunker chunker : chunkers) {
			System.out.println("Chunker " + chunker.getName() + " is run ...");
			chunkCollector.resetHasChanged();
			chunker.chunk(chunkCollector);	
			if(chunkCollector.hasChanged()) 
				System.out.println(chunkCollector);
		}
		return chunkCollector;
	}

	public boolean add(IChunker chunker) {
		return chunkers.add(chunker);
	}

	public boolean remove(IChunker chunker) {
		return chunkers.remove(chunker);
	}
	
}
