import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public class Launcher {
	
	public static enum RelTypes implements RelationshipType
	{
	    HAS_SUBFOLDER, HAS_COURSE, HAS_QUESTION, HAS_EXERCISE
	}
	
	public static enum NodeTypes implements RelationshipType
	{
	    ROOT("root"), FOLDER("folder"), COURSE("course"), QUESTION("question"), EXERCISE("exercise");
	    
	    private String name;
	    
	    private NodeTypes(String name){
	    	this.name=name;
	    }
	    public String toString(){
	    	return this.name;
	    }
	}
	
	public static void main (String...args) {
		/*Node firstNode;
		Node secondNode;
		Relationship relationship;*/
		EmbeddedGraphDatabase graphDb;
		
		//Creation graphe
		graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(args[0]);
		registerShutdownHook( graphDb );
		System.out.println(graphDb);
		
		//Creation des nodes
		Transaction tx = graphDb.beginTx();
		try
		{
			/*firstNode = graphDb.createNode();
			firstNode.setProperty( "message", "Hello, " );
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", "World!" );
			 
			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.HAS_COURSE );
			relationship.setProperty( "message", "brave Neo4j " );
		    tx.success();*/
			
			ImportData importUtil = new ImportData();
			System.out.println(args[1]);
			importUtil.importDataFromFolder(new File(args[1]), graphDb);
		}
		finally
		{
		    tx.finish();
		}
		
		/*System.out.print( firstNode.getProperty( "message" ) );
		System.out.print( relationship.getProperty( "message" ) );
		System.out.print( secondNode.getProperty( "message" ) );*/
		
		graphDb.shutdown();
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            if(graphDb!=null) {
	            	graphDb.shutdown();
	            }
	        }
	    } );
	}
}
