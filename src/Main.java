/**
 * @author peanut
 * Test class for the web crawler
 */
public class Main {
	public static void main(String[] args){
		try{
			WebGraph graph = new WebGraph("http://www.explosm.net");
			graph.startExplore();
		} catch( Exception e ){
			e.printStackTrace();
		}
	}
}
