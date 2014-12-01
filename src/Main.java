import java.io.IOException;

/**
 * @author peanut
 * Test class for the web crawler
 */
public class Main {
	public static void main(String[] args){
		WebGraph graph=null;
		try{
			graph = new WebGraph("http://www.apple.com");
			graph.startExplore(1);
		} catch( Exception e ){
			e.printStackTrace();
		}
		try {
			System.out.println("Writing to file...");
			graph.writeToFile("applegraph.grp");
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nFinal Graph: "+graph);
	}
}
