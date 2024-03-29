package semanticMarkup.ling.chunk;

import java.util.Iterator;

import semanticMarkup.ling.parse.AbstractParseTree;

public class ChunkCollectorIterator<T> implements Iterator<Chunk> {

	private ChunkCollector chunkCollector;
	private Iterator<AbstractParseTree> terminalsIterator;

	public ChunkCollectorIterator(ChunkCollector chunkCollector) {
		this.chunkCollector = chunkCollector;
		this.terminalsIterator = chunkCollector.getTerminals().iterator();
	}

	@Override
	public boolean hasNext() {
		return terminalsIterator.hasNext();
	}

	@Override
	public Chunk next() {
		return chunkCollector.getChunk(terminalsIterator.next());
	}

	@Override
	public void remove() {
		//remove is not supported
	}

}
