import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
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
		EmbeddedGraphDatabase graphDb;
		
		//Creation graphe
		graphDb=(EmbeddedGraphDatabase) new GraphDatabaseFactory().
	    newEmbeddedDatabaseBuilder(args[0]).
	    setConfig( GraphDatabaseSettings.node_keys_indexable, "name" ).
	    setConfig( GraphDatabaseSettings.node_auto_indexing, "true" ).
	    newGraphDatabase();
		//graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(args[0]);
		registerShutdownHook( graphDb );
		System.out.println(graphDb);
		
		//Creation des nodes
		Transaction tx = graphDb.beginTx();
		try
		{
			
			ImportData importUtil = new ImportData();
			System.out.println(args[1]);
			importUtil.importDataFromFolder(new File(args[1]), graphDb);
			tx.success();
		}
		finally
		{
		    tx.finish();
		}
		
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
