import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author peanut
 * WebGraph: graph containing the internet using a certain base URL
 */
public class WebGraph {
	private Website root;
	public ArrayList<Website> allsites;
	
	/**
	 * Create a web graph building the url from string
	 * @param url: a string giving url of the root
	 * @throws MalformedURLException if the string is malformed
	 */
	public WebGraph( String url ) throws MalformedURLException{
		this( new URL(url) );
	}
	
	/**
	 * Create a web graph with given root
	 * @param url: the root of the graph
	 */
	public WebGraph( URL url ){
		root = new Website( url );
		allsites = new ArrayList<Website>();
	}
	
	
	// web IO methods
	
	/**
	 * Explore the web starting from root
	 * @throws IOException 
	 */
	public void startExplore(int maxDepth) throws IOException{
		explore( root, maxDepth );
	}
	
	/**
	 * Explore the web starting from a certain website
	 * @param start: the start point node
	 * @param maxDepth: maximum depth of the recursion (if 0, exploration stops)
	 * @throws IOException : in case of error in connection
	 */
	public void explore( Website start, int maxDepth ) throws IOException{
		ArrayList<Website> toExplore = null;
		do{
			try{
				if(maxDepth < 0)
					return;
				System.out.println( "\n("+maxDepth+"): Exploring: " + start );
				toExplore = crawlOneSite( start );
				for(Website current : toExplore){
					explore( current, maxDepth-1 );
				}
			} catch (IOException E){
				System.out.println("! Encountered error: "+E);
			}
		} while( toExplore!=null && !toExplore.isEmpty() && maxDepth>0 );
	}
	
	/**
	 * Crawl the web html at a url to get hyperlinks from it
	 * @param start: the start point to crawl from
	 * @return an arraylist containing all sites newly discovered (to be crawled)
	 * @throws IOException: if a web-related exception occurs
	 */
	public ArrayList<Website> crawlOneSite( Website start ) throws IOException{
		if(start.isExplored()){
			return new ArrayList<Website> ();
		}
		// establish connection
		URLConnection conn = start.getUrl().openConnection();
		BufferedReader stream = new BufferedReader(
					new InputStreamReader(conn.getInputStream())
				);
		
		// get all the html from website
		StringBuilder builder = new StringBuilder();
		String inputline;
		while( (inputline = stream.readLine()) != null){
			builder.append(inputline);
		}
		
		// crawl the html
		return crawlHTML( start, builder.toString() );
	}
	
	/**
	 * Crawl the html for websites
	 * @param node: the website to update with content
	 * @param html: the html of the page corresponding to node
	 * @return a list of all sites newly crawled
	 * @throws MalformedURLException if the urls in the website are bad
	 */
	private ArrayList<Website> crawlHTML( Website node, String html ) throws MalformedURLException{
		ArrayList<Website> discovered = new ArrayList<Website>();
		
		Document doc = Jsoup.parse( html, node.url.toString() );
		Elements elinks = doc.select("a[href]");
		
		// crawl every link 
		for(Element clink: elinks){
			String url = clink.attr("abs:href");
			System.out.println("Found url: \""+url+"\"");
			
			if(url.isEmpty()){ continue;}
			URL link = new URL( url );
			// find the link in allsites
			Website cnode = null;
			for(Website site : allsites){
				if( site.equals(link) ){
					cnode = site;
					break;
				}
			}
			// if new, add to discovered array list
			if(cnode == null){
				System.out.println("* NEW URL: \""+url+"\"");
				cnode = new Website(link);
				discovered.add(cnode);
				allsites.add(cnode);
			}
			// anyway, add as neighbor to the current site
			node.addNeighbor(cnode);
		}
		// set the current node as explored
		node.setExplored(true);
		return discovered;
	}


	
	// file IO METHODS
	
	/**
	 * Write this graph to a file
	 * @throws IOError in case of input error
	 */
	public void writeToFile() throws IOError{
		// TODO
	}
	
	/**
	 * Read a file containing a graph
	 * @return the web graph
	 * @throws IOError in case of input error
	 */
	public static WebGraph readFromFile( ) throws IOError{
		return null; // TODO
	}

	
	// Graph specific methods
	public Website getRoot(){
		return root;
	}
	
	public String toString(){
		return "WEBGRAPH of root = \""+this.root+"\"\n"+
				"            size = "+this.allsites.size();
	}

}
