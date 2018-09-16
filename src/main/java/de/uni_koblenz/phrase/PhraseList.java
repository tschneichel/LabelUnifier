package de.uni_koblenz.phrase;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_koblenz.cluster.PhraseCluster;
import de.uni_koblenz.label.*;

public class PhraseList {
	
	private Vector<String> vectorSpace;
	private List<Phrase> phrases;
	private ArrayList<ArrayList<Phrase>> wholeInput;
	private ArrayList<String> phrasesFinal;
	private ArrayList<PhraseCluster> allBuiltClusters;
	private ArrayList<String> finalPhrasesAndTheirLabels = new ArrayList<String>();;
	
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }
	
	public PhraseList() {
		this.phrasesFinal = new ArrayList<String>();
		this.allBuiltClusters = new ArrayList<PhraseCluster>();
		this.wholeInput = new ArrayList<ArrayList<Phrase>>();
 	}
	
	public void addPhraseList (ArrayList<Phrase> phraseList){
		this.wholeInput.add(phraseList);
	}
	
	public Vector<String> getVectorSpace() {
		return vectorSpace;
	}
	
	public void setVectorSpace(Vector<String> vectorSpace) {
		this.vectorSpace = vectorSpace;
	}
	
	public List<Phrase> getPhrases() {
		return phrases;
	}
	
	public void setPhrases(List<Phrase> phrases) {
		this.phrases = phrases;
	}

	public ArrayList<ArrayList<Phrase>> getWholeInput() {
		return wholeInput;
	}

	public void setWholeInput(ArrayList<ArrayList<Phrase>> wholeInput) {
		this.wholeInput = wholeInput;
	}


	public List<String> getPhrasesFinal() {
		return phrasesFinal;
	}

	public void setPhrasesFinal(ArrayList<String> phrasesFinal) {
		this.phrasesFinal = phrasesFinal;
	}
	

	public ArrayList<PhraseCluster> getAllBuiltClusters() {
		return allBuiltClusters;
	}

	public void setAllBuiltClusters(ArrayList<PhraseCluster> allBuiltClusters) {
		this.allBuiltClusters = allBuiltClusters;
	}

	public void addPhrase(Phrase phrase) {
		this.phrases.add(phrase);
	}
	
	public ArrayList<PhraseCluster> createClusters(){
		ArrayList<PhraseCluster> allClusters = new ArrayList<PhraseCluster>();
		ArrayList<ArrayList<Phrase>> leftInput = new ArrayList<ArrayList<Phrase>>();
		while (this.wholeInput.size()>0){
			Map<String,Integer> frequencies = new HashMap<String, Integer>();
			Map<String,ArrayList<Integer>> labelsOfPhrases = new HashMap<String, ArrayList<Integer>>();
			for (int phraseListCounter = 0; phraseListCounter < this.wholeInput.size(); phraseListCounter++){
				for (int phraseCounter = 0; phraseCounter < this.wholeInput.get(phraseListCounter).size(); phraseCounter++){
					if (frequencies.containsKey(this.wholeInput.get(phraseListCounter).get(phraseCounter).getFullContent())){
						frequencies.put(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent(), frequencies.get(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent())+1);
						ArrayList<Integer> tempList = new ArrayList<Integer>();
						tempList = labelsOfPhrases.get(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent());
						tempList.add(phraseListCounter);
						labelsOfPhrases.put(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent(), tempList);
					}
					else {
						frequencies.put(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent(), 1);
						ArrayList<Integer> tempList = new ArrayList<Integer>();
						tempList.add(phraseListCounter);
						labelsOfPhrases.put(this.getWholeInput().get(phraseListCounter).get(phraseCounter).getFullContent(), tempList);
					}
					// Maps Phrases to their respective frequency over all possible Phrases
				}
			}
			frequencies = sortByValue(frequencies);
			// sorts Map by value with highest value first
			PhraseCluster currentCluster = new PhraseCluster();
			// creates Cluster that will be filled
			Map.Entry<String,Integer> entry = frequencies.entrySet().iterator().next();
			currentCluster.setBuiltPhrase(entry.getKey());
			// Writes first KEY of map [a phrase content as string] on the current cluster
			ArrayList<Integer> positions = labelsOfPhrases.get(entry.getKey());
			currentCluster.setLabelPositions(positions);
			// saves the positions of original labels [rather sentences, actually] on the cluster
			for (int removalCounter = 0; removalCounter < positions.size(); removalCounter++){
				leftInput.add(this.getWholeInput().get(removalCounter));
				this.getWholeInput().remove(removalCounter);
			}
			// removes all entries from wholeInput, over which we iterate, while saving them on leftInput from which we will generate alternative Clusters
		}
		
		//TODO: Test whether this actually works up to this point :D Ran out of time sadly
		// Work with leftInput to create alternative clusters. Also, I'm not entirely sure at this point whether we can re-order left input
		// without additional variables in phrase. Namely, I think we need the position of the label from which the phrase was created
		// Since phrases are generated from sentences and labels may have multiple sentences, this is actually a bit tricky again
		return null;
	}
	
	
	
	public void phraseCompareAndDecision(LabelList labelList) {
		System.out.println("Die vollst�ndige Phrasenlist beinhaltet:");
		ArrayList<Integer> controller = new ArrayList<Integer>();
		for (int i = 0; i < this.wholeInput.size(); i++) {
			// add all the  casted i-strings of wholeInput(which is actually an  Arraylist of Phrases) to phraseList as a String. 
			this.phrasesFinal.add(this.wholeInput.get(i).get(0).getFullContent());
			//display these Strings in PhraseFinal, one String in a row
			System.out.println(this.phrasesFinal.get(i));
		}
		//printe all entrys in wholeInput as Strings
		for (int zaehlerLabels = 0; zaehlerLabels < this.wholeInput.size(); zaehlerLabels++) {
			for (int zaehlerPhrasen = 0; zaehlerPhrasen < this.wholeInput.get(zaehlerLabels).size(); zaehlerPhrasen++) {
				System.out.println(this.wholeInput.get(zaehlerLabels).get(zaehlerPhrasen).getFullContent());
			}
		}
		//iterate, zaehlerlabels gets the size of the first Dimension of the wholeInput
		for (int zaehlerLabels = 0; zaehlerLabels < this.wholeInput.size(); zaehlerLabels++) {
				//define currentPhrase as the phrase at wholeInput at the entry zaehlerLabels 
				String currentPhrase = this.wholeInput.get(zaehlerLabels).get(0).getFullContent();
				System.out.println("Die aktuell kontrollierte Phrase ist:");
				System.out.println(currentPhrase);
				//here we go further in the each phrase and check each entry. We start at next entry of zählerlabels and traverse 
				for (int labelDimension = zaehlerLabels + 1; labelDimension < this.wholeInput.size(); labelDimension++) {
						//if currentPhrase is the same as wholeInput at the entry labelDimension
						if (currentPhrase.equals(this.wholeInput.get(labelDimension).get(0).getFullContent())) {
							//define a new Phrasecluster called Currentcluster
							PhraseCluster currentCluster = new PhraseCluster();
							//put the phrase at wholeinput on the entry labelDimension in this Cluster
							currentCluster.setBuiltPhrase(wholeInput.get(labelDimension).get(0).getFullContent());
							//seems like getInputLabels at entry zaehlerLabels and labelDimension are added as a String to currentCluster
							currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(zaehlerLabels).getLabelAsString());
							currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(labelDimension).getLabelAsString());
							// adding currentCluster to allBuiltClusters
							this.allBuiltClusters.add(currentCluster);
							// ans adding labelDimension and zaehlerLabels to controller
							controller.add(zaehlerLabels);
							controller.add(labelDimension);
							break;
						}
						else if(!(controller.contains(zaehlerLabels))){
							PhraseCluster currentCluster = new PhraseCluster();
							currentCluster.setBuiltPhrase(wholeInput.get(zaehlerLabels).get(0).getFullContent());
							currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(zaehlerLabels).getLabelAsString());
							this.allBuiltClusters.add(currentCluster);
							controller.add(zaehlerLabels);
						}
					}
				if(!(controller.contains(zaehlerLabels))) {
					PhraseCluster currentCluster = new PhraseCluster();
					currentCluster.setBuiltPhrase(wholeInput.get(zaehlerLabels).get(0).getFullContent());
					currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(zaehlerLabels).getLabelAsString());
					this.allBuiltClusters.add(currentCluster);
					controller.add(zaehlerLabels);
				}
			}
		System.out.println("Die finalen Phrasen sind: ");
			for (int i = 0; i < this.allBuiltClusters.size(); i++) {
				System.out.println(allBuiltClusters.get(i).getBuiltPhrase());
				
				System.out.println("Mit den Labels: ");
				System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(0));
				//System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(1));
				System.out.println("\n");
				finalPhrasesAndTheirLabels.add("The current Phrase is: " +  allBuiltClusters.get(i).getBuiltPhrase() + "\n" + "The matching labels are:");
				for(int j = 0; j < allBuiltClusters.get(i).getMatchingLabels().size(); j++){
					finalPhrasesAndTheirLabels.add(allBuiltClusters.get(i).getMatchingLabels().get(j));
				}
				finalPhrasesAndTheirLabels.add("\n");	
		}
	}
	
	public void phraseCompareAndDecisionFinal(LabelList labelList) {
		ArrayList<Integer> controller = new ArrayList<Integer>();
		PhraseCluster firstCluster = new PhraseCluster();
		String firstPhrase = this.wholeInput.get(0).get(0).getFullContent();
		firstCluster.setBuiltPhrase(wholeInput.get(0).get(0).getFullContent());
		firstCluster.getMatchingLabels().add(labelList.getInputLabels().get(0).getLabelAsString());
		controller.add(0);
		for(int phrasenDurchzaehler = 1; phrasenDurchzaehler < this.wholeInput.size(); phrasenDurchzaehler++) {
			if(firstPhrase.equals(this.wholeInput.get(phrasenDurchzaehler).get(0).getFullContent())) {
				controller.add(phrasenDurchzaehler);
				firstCluster.getMatchingLabels().add(labelList.getInputLabels().get(phrasenDurchzaehler).getLabelAsString());
				continue;
			}
			else {
				continue;
			}
		}
		this.allBuiltClusters.add(firstCluster);
		for(int labelDurchzaehler = 1; labelDurchzaehler < this.wholeInput.size(); labelDurchzaehler++) { //setzt aktuell betrachtete Phrase
			PhraseCluster currentCluster = new PhraseCluster(); //erzeugt neues leeres Cluster
			String currentPhrase = this.wholeInput.get(labelDurchzaehler).get(0).getFullContent(); //setzt die Phrase auf aktuell betrachtete Position
			if(controller.contains(labelDurchzaehler)) {		//falls das aktuelle Label bereits einen Cluster bildet, wir dieses �bersprungen
				continue;
			}
			else{
				currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(labelDurchzaehler).getLabelAsString()); //schreibt label von Vergleichsbasis in das Cluster (wird aber nicht zwangsl�ufig hinzugef�gt)
				for(int vergleichsblock = labelDurchzaehler + 1; vergleichsblock < this.wholeInput.size(); vergleichsblock++) {
					if(currentPhrase.equals(this.wholeInput.get(vergleichsblock).get(0).getFullContent())) { //Bildung eines Clusters, wenn die Phrasen gleich sind
						controller.add(vergleichsblock);
						controller.add(labelDurchzaehler);
						currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(vergleichsblock).getLabelAsString());
						currentCluster.setBuiltPhrase(wholeInput.get(labelDurchzaehler).get(0).getFullContent());
						continue;
					}
					else if ((vergleichsblock == this.wholeInput.size() - 1) && !(controller.contains(labelDurchzaehler))) {
						PhraseCluster lastCluster = new PhraseCluster();
						currentCluster.getMatchingLabels().clear();
						currentCluster.getMatchingLabels().add(labelList.getInputLabels().get(labelDurchzaehler).getLabelAsString());
						currentCluster.setBuiltPhrase(wholeInput.get(labelDurchzaehler).get(0).getFullContent());
						break;
					}
					else {
						continue;
					}
				}
				this.allBuiltClusters.add(currentCluster);
			}
		}
		
		System.out.println("Die finalen Phrasen sind: ");
		for (int i = 0; i < this.allBuiltClusters.size(); i++) {
			System.out.println(allBuiltClusters.get(i).getBuiltPhrase());
			
			System.out.println("Mit den Labels: ");
			System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(0));
			//System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(1));
			//System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(2));
			//System.out.println(allBuiltClusters.get(i).getMatchingLabels().get(3));
			System.out.println("\n");
			finalPhrasesAndTheirLabels.add("These initial labels:");
			List<Integer> alreadyChecked = new ArrayList<Integer>();
			ArrayList<String> comp = new ArrayList<String>();
			for (Label l : labelList.getInputLabels()) {
				comp.add(l.getLabelAsString());
			}
			for(int j = 0; j < allBuiltClusters.get(i).getMatchingLabels().size(); j++){
				String writer = "";
				String temp = allBuiltClusters.get(i).getMatchingLabels().get(j);
				for(int k = 0; k < comp.size(); k++){
					if(temp.equals(comp.get(k)) && !alreadyChecked.contains(k)){
						writer += "Label ";
						writer += k;
						alreadyChecked.add(k);
						break;
					}
				}
				writer += " (" + allBuiltClusters.get(i).getMatchingLabels().get(j) + ")";
				finalPhrasesAndTheirLabels.add(writer);
			}
			finalPhrasesAndTheirLabels.add("were transformed into this new label: \"" +  allBuiltClusters.get(i).getBuiltPhrase() + "\"");
			finalPhrasesAndTheirLabels.add("\n");
			
		}
	}
	public void writeToFile() throws Exception{
		java.nio.file.Path file = Paths.get("finalFile.txt");
	    Files.write(file, finalPhrasesAndTheirLabels, Charset.forName("UTF-8"));
	}
}


