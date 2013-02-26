package semanticMarkup.markup;

import java.util.List;

import semanticMarkup.core.Treatment;
import semanticMarkup.core.transformation.TreatmentTransformerChain;
import semanticMarkup.io.input.IVolumeReader;
import semanticMarkup.io.output.IVolumeWriter;
import semanticMarkup.log.LogLevel;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Annotates a volume based on the CharaParser methodology
 * @author thomas rodenhausen
 */
public class CharaParser implements IMarkupCreator {

	private IVolumeReader volumeReader;
	private TreatmentTransformerChain transformerChain;
	private IVolumeWriter volumeWriter;
	private List<Treatment> treatments;

	@Inject
	public CharaParser(@Named("MarkupCreator_VolumeReader")IVolumeReader volumeReader, 
			TreatmentTransformerChain transformerChain,
			@Named("MarkupCreator_VolumeWriter")IVolumeWriter volumeWriter) {
		this.volumeReader = volumeReader;
		this.transformerChain = transformerChain;
		this.volumeWriter = volumeWriter;
	}
	
	@Override
	public void create() {
		try {
			log(LogLevel.DEBUG, "reading treatments using " + volumeReader.getClass());
			treatments = volumeReader.read();
			
			log(LogLevel.DEBUG, "transform treatments using " + transformerChain.getClass());
			treatments = transformerChain.transform(treatments);
			
			log(LogLevel.DEBUG, "writing result using " + volumeWriter.getClass());
			volumeWriter.write(treatments);
				
		} catch (Exception e) {
			log(LogLevel.ERROR, e);
		}
	}

	@Override
	public String getDescription() {
		return "Charaparser";
	}

	@Override
	public List<Treatment> getResult() {
		return treatments;
	}
}