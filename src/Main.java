import java.io.IOException;

/**
 * @author peanut
 * Test class for the web crawler
 */
public class Main {
	public static void main(String[] args){
		WebGraph graph=null;
		try{
			graph = new WebGraph("http://www.wikipedia.org");
			graph.startExplore(2);
			//graph = graph.readFromFile("applegraph.grp");
		} catch( Exception e ){
			e.printStackTrace();
		}
		try {
			System.out.println("Writing to file...");
			graph.writeToFile("wikipedia.grp");
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nFinal Graph: "+graph);
	}
}
