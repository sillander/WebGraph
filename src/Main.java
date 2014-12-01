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
		System.out.println("\n\nFinal Graph: "+graph);
	}
}
