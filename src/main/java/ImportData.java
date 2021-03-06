import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;


public class ImportData {
	
	private static final Pattern regexCours = Pattern.compile("c\\d+-.*");
	private static final Pattern regexExercise = Pattern.compile("e\\d+-.*");
	private static final Pattern regexQuestion = Pattern.compile("q\\d+-.*");
	
	private DB db;
	
	public void importDataFromFolder(File file, EmbeddedGraphDatabase graph) {
		//Parcours de l arborescence et creation des noeuds
		parcoursFolder(file, graph.getReferenceNode(), graph);
	}
	
	private void parcoursFolder(File file, Node nodeParent, EmbeddedGraphDatabase graph) {
		Node node;
		File []list = file.listFiles();
		
		for (File f : list){
			if (f.isDirectory()){
				node = graph.createNode();
				System.out.println(node);
				node.setProperty("name", f.getName());
				System.out.println(f.getName());
				Launcher.RelTypes type = getRelTypes(f);
				Relationship relationship = nodeParent.createRelationshipTo( node, type);
				System.out.println(relationship);
				parcoursFolder(f, node, graph);
			} else if(f.isFile()) {
				this.addResource(nodeParent, f);
			}
		}
	}
	
	private void addResource(Node nodeParent, File file){
		
		String mongoId = insertResourceIntoMongoDB (file); 
		
		String[] resources = (String[]) nodeParent.getProperty("resources",null);
		List<String> resourcesList = new ArrayList<String>();
		if(resources!=null) {
			List<String> list = Arrays.asList(resources);
			resourcesList.addAll(list);
		}
		
		resourcesList.add(mongoId);
		String [] strings = new String[resourcesList.size()];
		for(int i=0;i<resourcesList.size();i++){
			strings[i]=resourcesList.get(i);
		}
		nodeParent.setProperty("resources", strings);
	}
	
	private String insertResourceIntoMongoDB(File file) {
		db = MongoDBHandler.getMongoClient().getDB("test");
		GridFS fs = new GridFS(db, "");
		GridFSInputFile in;
		
		String id =null;
		
		try {
			in = fs.createFile(file);
			in.setFilename(file.getName());
			in.save();
			id = ((ObjectId) in.getId()).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
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
