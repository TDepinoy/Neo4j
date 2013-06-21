import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public class Launcher {

	public static void main (String...args) {
		System.out.println("MARC ! ");
		
		EmbeddedGraphDatabase graphDb = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase( args[0]);
		registerShutdownHook( graphDb );
		System.out.println(graphDb);
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
	            graphDb.shutdown();
	        }
	    } );
	}
}
