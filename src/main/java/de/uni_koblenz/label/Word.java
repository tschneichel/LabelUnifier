package de.uni_koblenz.label;

import java.io.IOException;
import java.util.ArrayList;

import de.uni_koblenz.cluster.GrammaticalRelationBetweenWords;
import de.uni_koblenz.enums.PartOfSpeechTypes;
import de.uni_koblenz.enums.RelationName;
import de.uni_koblenz.enums.RoleLeopold;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.patterns.surface.Token;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

public class Word {
	
	private CoreLabel token;
	
	private PartOfSpeechTypes partOfSpeech;
	private ArrayList<GrammaticalRelationBetweenWords> grammaticalRelations=new ArrayList<GrammaticalRelationBetweenWords>();
	private String baseform;
	private String originalForm;
	private RoleLeopold role;
	private Integer dominance;
	
	public Word() {
		
	}
	public Word(String originalForm) {
		this.originalForm = originalForm;
	}
	// main method
	public Word(CoreLabel token) throws JWNLException {
		this.token=token;
		// gets overwritten by method in Label/Sentence Object
		role=RoleLeopold.OPTIONAL_INFORMATION_FRAGMENT;
		setOriginalForm(token.originalText());
		tagLabel();
		stemWord();
	}
	public Word(PartOfSpeechTypes partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	public Word(String originalForm, PartOfSpeechTypes partOfSpeech) {
		this.originalForm = originalForm;
		this.partOfSpeech = partOfSpeech;
	}
	
	public PartOfSpeechTypes getPartOfSpeech() {
		return partOfSpeech;
	}
	
	public void setPartOfSpeech(PartOfSpeechTypes partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	
	public ArrayList<GrammaticalRelationBetweenWords> getGrammaticalRelations() {
		return grammaticalRelations;
	}
	
	public ArrayList<GrammaticalRelationBetweenWords> getGrammaticalRelationsByName(RelationName relationName) {
		ArrayList<GrammaticalRelationBetweenWords> grammaticalRelationsSimplified=new ArrayList<GrammaticalRelationBetweenWords>();
		for(GrammaticalRelationBetweenWords grammaticalRelation:grammaticalRelations) {
			if(grammaticalRelation.getGrammaticalRelationName()==relationName) {
				grammaticalRelationsSimplified.add(grammaticalRelation);
			}
		}
		return grammaticalRelationsSimplified;
	}
	
	public void setGrammaticalRelations(ArrayList<GrammaticalRelationBetweenWords> grammaticalRelations) {
		this.grammaticalRelations = grammaticalRelations;
	}
	
	public String getBaseform() {
		return baseform;
	}
	
	public void setBaseform(String baseform) {
		
		this.baseform = baseform;
	}
	
	public String getOriginalForm() {
		return originalForm;
	}
	
	public void setOriginalForm(String originalForm) {
		this.originalForm = originalForm;
	}
	
	public RoleLeopold getRole() {
		return role;
	}
	
	public void setRole(RoleLeopold role) {
		this.role = role;
	}
	
	public Integer getDominance() {
		return dominance;
	}
	
	public void setDominance(Integer dominance) {
		this.dominance = dominance;
	}
	
	/*
	 * method to tag a label into PartOfSpeechTypes
	 */
	public void tagLabel() {
		String pos=token.tag();

        
        // NEUE PART OF SPEECH ABFRAGE
        for (PartOfSpeechTypes type : PartOfSpeechTypes.values()) {
        	if (pos.equals(type.getShortType())) {
        		this.setPartOfSpeech(type);
        		break;
        	}
        }
	}
	
	/*  stem(String toStem)
	 *  method to get the lemma of a single Word using the CoreNLP Lemmatizer.
	 
	public void stem(String toStem){		 
		StanfordCoreNLP pipeline = new StanfordCoreNLP(new Properties(){
			
			private static final long serialVersionUID = 1L;
			{
			  setProperty("annotators", "tokenize,ssplit,pos,lemma"); 
			  	// initialize annotator dependencies 
			}});

			Annotation token = new Annotation(toStem);
			pipeline.annotate(token); 
			List<CoreMap> list = token.get(SentencesAnnotation.class);
			String stemmed = list
			                        .get(0).get(TokensAnnotation.class)
			                        .get(0).get(LemmaAnnotation.class);
			toStem = stemmed;
	}*/ 


	public void stemWord() throws JWNLException {
		
		Dictionary dict = Dictionary.getDefaultResourceInstance();
		POS pos = null;
		if(partOfSpeech!=null) {
			pos = partOfSpeech.getJwnlType();
		}
		//System.out.println(pos);
	    
		if(pos == null) {
			setBaseform(getOriginalForm());
		} else {
            IndexWord word = dict.getIndexWord(pos,getOriginalForm());
            String lemma = null;
            if (word != null) {
                lemma = word.getLemma();
            } else {
                IndexWord toForm = dict.getMorphologicalProcessor().lookupBaseForm(pos,getOriginalForm());
                if (toForm != null) {
                    lemma = toForm.getLemma();
                } else {
                    lemma = getOriginalForm();
                }
            }
            setBaseform(lemma);	
		}
	}
	@Override
	public String toString() {
		String grammaticalRelationAsString=" Grammatical relations: ";
		for(GrammaticalRelationBetweenWords grammaticalRelation:grammaticalRelations) {
			grammaticalRelationAsString+=grammaticalRelation.getGrammaticalRelationName()+"; ";
		}
		
		return "Word: "+ originalForm +" Base: " + baseform + " POS: " + partOfSpeech + " Role: " + role + grammaticalRelationAsString;
	}
}
