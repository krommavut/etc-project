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
import semanticMarkup.ling.extract.ProcessingContext;
import semanticMarkup.ling.extract.ProcessingContextState;
import semanticMarkup.ling.learn.ITerminologyLearner;
import semanticMarkup.ling.transform.IInflector;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class NumericalChunkProcessor extends AbstractChunkProcessor {

	private boolean attachToLast;

	@Inject
	public NumericalChunkProcessor(IInflector inflector, IGlossary glossary, ITerminologyLearner terminologyLearner, 
			ICharacterKnowledgeBase characterKnowledgeBase, @Named("LearnedPOSKnowledgeBase") IPOSKnowledgeBase posKnowledgeBase,
			@Named("BaseCountWords")Set<String> baseCountWords, @Named("LocationPrepositionWords")Set<String> locationPrepositions, 
			@Named("Clusters")Set<String> clusters, @Named("Units")String units, @Named("EqualCharacters")HashMap<String, String> equalCharacters, 
			@Named("NumberPattern")String numberPattern, @Named("AttachToLast")boolean attachToLast, @Named("TimesWords")String times) {
		super(inflector, glossary, terminologyLearner, characterKnowledgeBase, posKnowledgeBase, baseCountWords, locationPrepositions, clusters, units, equalCharacters, 
				numberPattern, attachToLast, times);
	}

	@Override
	protected List<DescriptionTreatmentElement> processChunk(Chunk chunk, ProcessingContext processingContext) {
		ProcessingContextState processingContextState = processingContext.getCurrentState();
		//** find parents, modifiers
		//TODO: check the use of [ and ( in extreme values
		//ArrayList<Element> parents = lastStructures();
		String text = chunk.getTerminalsText().replaceAll("�", "-");
		boolean resetFrom = false;
		if(text.matches(".*\\bto \\d.*")){ //m[mostly] to 6 m ==> m[mostly] 0-6 m
			text = text.replaceFirst("to\\s+", "0-");
			resetFrom = true;
		}
		
		LinkedList<DescriptionTreatmentElement> parents = this.attachToLast? lastStructures(processingContext, processingContextState
				) : processingContextState.getSubjects();
		
		/*String modifier1 = "";
		//m[mostly] [4-]8�12[-19] mm m[distally]; m[usually] 1.5-2 times n[size[{longer} than {wide}]]:consider a constraint
		String modifier2 = "";
		modifier1 = text.replaceFirst("\\[?\\d.*$", "");
		String rest = text.replace(modifier1, "");
		modifier1 = modifier1.replaceAll("(\\w\\[|\\]|\\{|\\})", "").trim();
		modifier2 = rest.replaceFirst(".*?(\\d|\\[|\\+|\\-|\\]|%|\\s|" + units + ")+\\s?(?=[a-z]|$)", "");// 4-5[+]
		String content = rest.replace(modifier2, "").replaceAll("(\\{|\\})", "").trim();
		modifier2 = modifier2.replaceAll("(\\w+\\[|\\]|\\{|\\})", "").trim(); */
		
		String content = "";
		for(Chunk childChunk : chunk.getChunks()) {
			if(!childChunk.isOfChunkType(ChunkType.MODIFIER))
				content += childChunk.getTerminalsText() + " ";
		}
		List<Chunk> modifiers = chunk.getChunks(ChunkType.MODIFIER);
		modifiers.addAll(processingContextState.getUnassignedModifiers());
		
		String character = text.indexOf("size") >= 0 || content.indexOf('/') > 0 || content.indexOf('%') > 0 || content.indexOf('.') > 0 ? "size" : null;
		character = "size";
		LinkedList<DescriptionTreatmentElement> characters = annotateNumericals(content, character,
				modifiers, lastStructures(processingContext, processingContextState), resetFrom, processingContextState);
		processingContextState.setLastElements(characters);
		
		if(parents.isEmpty()) {
			processingContextState.getUnassignedCharacters().addAll(characters);
		} else {
			for(DescriptionTreatmentElement parent : parents) {
				for(DescriptionTreatmentElement characterElement : characters) {
					parent.addTreatmentElement(characterElement);
				}
			}
		}
		processingContextState.setCommaAndOrEosEolAfterLastElements(false);
		return characters;
	}
	
	//** find parents, modifiers
	//TODO: check the use of [ and ( in extreme values
	//ArrayList<Element> parents = lastStructures();
	/*String text = ck.toString().replaceAll("�", "-");
	boolean resetfrom = false;
	if(text.matches(".*\\bto \\d.*")){ //m[mostly] to 6 m ==> m[mostly] 0-6 m
		text = text.replaceFirst("to\\s+", "0-");
		resetfrom = true;
	}
	ArrayList<Element> parents = this.attachToLast? lastStructures() : subjects;
	if(printAttach && subjects.get(0).getAttributeValue("name").compareTo(lastStructures().get(0).getAttributeValue("name")) != 0){
		log(LogLevel.DEBUG, text + " attached to "+parents.get(0).getAttributeValue("name"));
	}				
	if(debugNum){
		log(LogLevel.DEBUG, );
		log(LogLevel.DEBUG, ">>>>>>>>>>>>>"+text);
	}
	String modifier1 = "";//m[mostly] [4-]8�12[-19] mm m[distally]; m[usually] 1.5-2 times n[size[{longer} than {wide}]]:consider a constraint
	String modifier2 = "";
	modifier1 = text.replaceFirst("\\[?\\d.*$", "");
	String rest = text.replace(modifier1, "");
	modifier1 =modifier1.replaceAll("(\\w\\[|\\]|\\{|\\})", "").trim();
	modifier2 = rest.replaceFirst(".*?(\\d|\\[|\\+|\\-|\\]|%|\\s|"+ChunkedSentence.units+")+\\s?(?=[a-z]|$)", "");//4-5[+]
	String content = rest.replace(modifier2, "").replaceAll("(\\{|\\})", "").trim();
	modifier2 = modifier2.replaceAll("(\\w+\\[|\\]|\\{|\\})", "").trim();
	ArrayList<Element> chars = annotateNumericals(content, text.indexOf("size")>=0 || content.indexOf('/')>0 || content.indexOf('%')>0 || content.indexOf('.')>0? "size" : null, (modifier1+";"+modifier2).replaceAll("(^\\W|\\W$)", ""), lastStructures(), resetfrom);
	updateLatestElements(chars);*/

}
