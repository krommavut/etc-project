package semanticMarkup.ling.extract.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import semanticMarkup.core.description.DescriptionTreatmentElement;
import semanticMarkup.core.description.DescriptionType;
import semanticMarkup.know.ICharacterKnowledgeBase;
import semanticMarkup.know.IGlossary;
import semanticMarkup.know.IPOSKnowledgeBase;
import semanticMarkup.ling.chunk.Chunk;
import semanticMarkup.ling.extract.AbstractChunkProcessor;
import semanticMarkup.ling.extract.ProcessingContext;
import semanticMarkup.ling.extract.ProcessingContextState;
import semanticMarkup.ling.learn.ITerminologyLearner;
import semanticMarkup.ling.transform.IInflector;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class EosEolChunkProcessor extends AbstractChunkProcessor {

	@Inject
	public EosEolChunkProcessor(IInflector inflector, IGlossary glossary, ITerminologyLearner terminologyLearner, 
			ICharacterKnowledgeBase characterKnowledgeBase, @Named("LearnedPOSKnowledgeBase") IPOSKnowledgeBase posKnowledgeBase,
			@Named("BaseCountWords")Set<String> baseCountWords, @Named("LocationPrepositionWords")Set<String> locationPrepositions, 
			@Named("Clusters")Set<String> clusters, @Named("Units")String units, @Named("EqualCharacters")HashMap<String, String> equalCharacters, 
			@Named("NumberPattern")String numberPattern, @Named("AttachToLast")boolean attachToLast, @Named("TimesWords")String times) {
		super(inflector, glossary, terminologyLearner, characterKnowledgeBase, posKnowledgeBase, baseCountWords, locationPrepositions, clusters, units, equalCharacters, 
				numberPattern, attachToLast, times);
	}

	@Override
	protected List<DescriptionTreatmentElement> processChunk(Chunk chunk, ProcessingContext processingContext) {
		List<DescriptionTreatmentElement> result = new ArrayList<DescriptionTreatmentElement>();
		ProcessingContextState processingContextState = processingContext.getCurrentState();
		String modifierString = "";
		List<Chunk> unassignedModifiers = processingContextState.getUnassignedModifiers();
		for(Chunk modifier : unassignedModifiers)
			modifierString += modifier.getTerminalsText() + " ";
		
		if(!unassignedModifiers.isEmpty()) {
			LinkedList<DescriptionTreatmentElement> lastElements = processingContextState.getLastElements();
			if(!lastElements.isEmpty() && lastElements.getLast().isOfDescriptionType(DescriptionType.STRUCTURE)) {
				for(DescriptionTreatmentElement element : lastElements) {
					int structureId = Integer.valueOf(element.getProperty("id").substring(1));
					
					Set<DescriptionTreatmentElement> relations = processingContextState.getRelationsTo(structureId);
					int greatestId = 0;
					DescriptionTreatmentElement latestRelation = null;
					for(DescriptionTreatmentElement relation : relations) {
						int id = Integer.valueOf(relation.getProperty("id").substring(1));
						if(id > greatestId) {
							greatestId = id;
							latestRelation = relation;
						}
					}
					
					if(latestRelation != null) {
						latestRelation.appendProperty("modifier", modifierString);
						result.add(latestRelation);
					}
					//TODO: otherwise, categorize modifier and create a character for the structure e.g.{thin} {dorsal} {median} <septum> {centrally} only ;
				}
				
			} else if(!lastElements.isEmpty() && lastElements.getLast().isOfDescriptionType(DescriptionType.CHARACTER)) {
				for(DescriptionTreatmentElement element : lastElements) {
					element.appendProperty("modifier", modifierString);
					result.add(element);
				}
			}
		}
		
		processingContextState.clearUnassignedModifiers();
		processingContextState.clearUnassignedCharacters();
		
		return result;
	}
}