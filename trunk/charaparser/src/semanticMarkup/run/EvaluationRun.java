package semanticMarkup.run;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;

import semanticMarkup.core.Treatment;
import semanticMarkup.eval.IEvaluator;
import semanticMarkup.io.input.IVolumeReader;
import semanticMarkup.log.LogLevel;
import semanticMarkup.markup.IMarkupCreator;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class EvaluationRun extends AbstractRun {

	private String outFile;
	private IEvaluator evaluator;
	private IVolumeReader createdVolumeReader;
	private IVolumeReader goldStandardReader;

	@Inject
	public EvaluationRun(@Named("Run_OutFile")String outFile,
			@Named("GuiceModuleFile")String guiceModuleFile, 
			@Named("MarkupCreator") IMarkupCreator creator, 
			@Named("EvaluationRun_Evaluator")IEvaluator evaluator, 
			@Named("EvaluationRun_CreatedVolumeReader")IVolumeReader createdVolumeReader,
			@Named("EvaluationRun_GoldStandardReader")IVolumeReader goldStandardReader) {
		super(guiceModuleFile);
		this.outFile = outFile;
		this.createdVolumeReader = createdVolumeReader;
		this.goldStandardReader = goldStandardReader;
		this.evaluator = evaluator;
	}
	
	@Override
	public void run() throws Exception {
		BufferedWriter bwSetup = new BufferedWriter(new FileWriter(outFile + ".config.txt"));
		appendConfigFile(bwSetup);
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		String startedAt = "started at " + startTime;
		bwSetup.append(startedAt + "\n\n");
		log(LogLevel.INFO, startedAt);
		
		log(LogLevel.INFO, "Evaluating markup using " + evaluator.getDescription() + "...");
		log(LogLevel.INFO, "read marked up result using " + createdVolumeReader.getClass());
		List<Treatment> markedUpResult = createdVolumeReader.read();
		log(LogLevel.INFO, "read gold standard using " + goldStandardReader.getClass());
		List<Treatment> goldStandard = goldStandardReader.read();
		
		evaluator.evaluate(markedUpResult, goldStandard);
		log(LogLevel.INFO, "Evaluation result: \n" + evaluator.getResult());
		
		long endEvaluationTime = Calendar.getInstance().getTimeInMillis();
		String wasDoneEvaluating = "was done at " + endEvaluationTime;
		bwSetup.append(wasDoneEvaluating + "\n");
		log(LogLevel.INFO, wasDoneEvaluating);
		long millisecondsEvaluating = endEvaluationTime - startTime;
		String tookMeEvaluating = "took me " + (endEvaluationTime - startTime) + " milliseconds";
		bwSetup.append(tookMeEvaluating + "\n");
		log(LogLevel.INFO, tookMeEvaluating);
		
		String timeStringEvaluating = getTimeString(millisecondsEvaluating);
		bwSetup.append(timeStringEvaluating + "\n");
		log(LogLevel.INFO, timeStringEvaluating);
		bwSetup.flush();
		bwSetup.close();
	}
	


	@Override
	public String getDescription() {
		return "Evaluation Run";
	}

}
