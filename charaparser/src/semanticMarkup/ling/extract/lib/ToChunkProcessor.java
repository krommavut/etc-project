package semanticMarkup.ling.extract.lib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import semanticMarkup.core.description.DescriptionTreatmentElement;
import semanticMarkup.know.ICharacterKnowledgeBase;
import semanticMarkup.know.IGlossary;
import semanticMarkup.know.IPOSKnowledgeBase;
import semanticMarkup.ling.chunk.Chunk;
import semanticMarkup.ling.chunk.ChunkType;
import semanticMarkup.ling.extract.AbstractChunkProcessor;
import semanticMarkup.ling.extract.IChunkProcessor;
import semanticMarkup.ling.extract.ProcessingContext;
import semanticMarkup.ling.extract.ProcessingContextState;
import semanticMarkup.ling.learn.ITerminologyLearner;
import semanticMarkup.ling.transform.IInflector;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ToChunkProcessor extends AbstractChunkProcessor {

	@Inject
	public ToChunkProcessor(IInflector inflector, IGlossary glossary, ITerminologyLearner terminologyLearner, 
			ICharacterKnowledgeBase characterKnowledgeBase, @Named("LearnedPOSKnowledgeBase") IPOSKnowledgeBase posKnowledgeBase,
			@Named("BaseCountWords")Set<String> baseCountWords, @Named("LocationPrepositionWords")Set<String> locationPrepositions, 
			@Named("Clusters")Set<String> clusters, @Named("Units")String units, @Named("EqualCharacters")HashMap<String, String> equalCharacters, 
			@Named("NumberPattern")String numberPattern, @Named("AttachToLast")boolean attachToLast, @Named("TimesWords")String times) {
		super(inflector, glossary, terminologyLearner, characterKnowledgeBase,
				posKnowledgeBase, baseCountWords, locationPrepositions, clusters,
				units, equalCharacters, numberPattern, attachToLast, times);
	}

	@Override
	protected List<DescriptionTreatmentElement> processChunk(Chunk chunk,
			ProcessingContext processingContext) {
		LinkedList<DescriptionTreatmentElement> results = new LinkedList<DescriptionTreatmentElement>();
		ProcessingContextState processingContextState = processingContext.getCurrentState();
		LinkedList<DescriptionTreatmentElement> parents = lastStructures(processingContext, processingContextState);
		List<Chunk> modifiers = new LinkedList<Chunk>();
		List<Chunk> unassignedModifiers = processingContextState.getUnassignedModifiers();
		modifiers.addAll(unassignedModifiers);
		
		//process characters recursively with the appropriate processor
		List<Chunk> characterStateChunks = chunk.getChunks(ChunkType.CHARACTER_STATE);
		for(Chunk characterStateChunk : characterStateChunks) {
			IChunkProcessor processor = processingContext.getChunkProcessor(ChunkType.CHARACTER_STATE);
			results.addAll(processor.process(characterStateChunk, processingContext));
		}
		
		//and create a range charater too
		String characterStateString = characterStateChunks.get(0).getProperty("characterName");
		String character = chunk.getTerminalsText();
		this.createRangeCharacterElement(parents, results, modifiers, character, characterStateString, processingContextState);
		
		return new LinkedList<DescriptionTreatmentElement>();
	}

}