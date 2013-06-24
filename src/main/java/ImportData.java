import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public class ImportData {
	
	private static final Pattern regexCours = Pattern.compile("c\\d+-.*");
	private static final Pattern regexExercise = Pattern.compile("e\\d+-.*");
	private static final Pattern regexQuestion = Pattern.compile("q\\d+-.*");
	
	public void importDataFromFolder(File file, EmbeddedGraphDatabase graph) {
		
		Node node;
		Label labelNode;
		
		//Creation du noeud root
		labelNode = DynamicLabel.label(Launcher.NodeTypes.ROOT.toString());
		node = graph.createNode(labelNode);
		System.out.println("root créé");
		//Parcours de l arborescence et creation des noeuds
		parcoursFolder(file, node, graph);
		
	}
	
	private void parcoursFolder(File file, Node nodeParent, EmbeddedGraphDatabase graph) {
		Node node;
		Label labelNode;
		File []list = file.listFiles();
		
		for (File f : list){
			if (f.isDirectory()){
				labelNode = DynamicLabel.label(f.getName());
				node = graph.createNode(labelNode);
				Launcher.RelTypes type = getRelTypes(f);
				nodeParent.createRelationshipTo( node, type);
				if(type == Launcher.RelTypes.HAS_SUBFOLDER){
					parcoursFolder(f, node, graph);
				}
			}
		}
	}
	
	private Launcher.RelTypes getRelTypes(File file){
		Matcher matcher = regexCours.matcher(file.getName());
		if(matcher.find()){
			return Launcher.RelTypes.HAS_COURSE;
		}
		matcher = regexExercise.matcher(file.getName());
		if(matcher.find()){
			return Launcher.RelTypes.HAS_EXERCISE;
		}
		matcher = regexQuestion.matcher(file.getName());
		if(matcher.find()){
			return Launcher.RelTypes.HAS_QUESTION;
		}
		
		return Launcher.RelTypes.HAS_SUBFOLDER;
	}
}
