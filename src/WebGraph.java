import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
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
	
	private ArrayList<String> restrictions;
	
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
		this( new Website( url ) );
	}
	
	/**
	 * Create a web graph with given root
	 * @param root: the root of the graph
	 */
	public WebGraph( Website root ){
		this.root = root;
		allsites = new ArrayList<Website>();
		allsites.add(root);
		restrictions = new ArrayList<String>();
	}
	
	/**
	 * Add a restriction domain to this graph, that is, a keyword which must be found in every kept url.
	 * By default, there is no restriction
	 * @param s: the restriction to add
	 */
	public void addRestriction(String s){
		restrictions.add(s);
	}
	
	/**
	 * Checks if a given url (as a string) matches the restriction imposed to this graph
	 * @param url: the url to check
	 * @return true if this url is valid in this graph
	 */
	public boolean matchesRestrictions( String url ){
		if(restrictions.isEmpty())
			return true;
		// if not empty, make sure one filter is ok
		boolean found1ok = false;
		for(int i=0; !found1ok && i<restrictions.size(); i++){
			String filter = restrictions.get(i);
			found1ok = url.contains(filter);
		}
		// if empty or not found
		return found1ok;
	}
	
	
	// web IO methods
	
	/**
	 * Explore the web starting from root
	 * @throws IOException 
	 */
	public void startExplore(int maxDepth) throws IOException{
		explore( root, maxDepth );
	}
	
	//public void continueExplore
	
	/**
	 * Explore the web starting from a certain website
	 * @param start: the start point node
	 * @param maxDepth: maximum depth of the recursion (if 0, exploration stops)
	 * @throws IOException : in case of error in connection
	 */
	public void explore( Website start, int maxDepth ) throws IOException{
		if(start.isExplored()){
			return ;
		}
		ArrayList<Website> toExplore = null;
		boolean stillSthToExplore;
		do{
			stillSthToExplore = false; // expect nothing is to be explored
			try{
				if(maxDepth < 0)
					return;
				System.out.println( "\n("+maxDepth+"): Exploring: " + start );
				toExplore = crawlOneSite( start );
				start.setExplored(true);
				if(!toExplore.isEmpty()){
					stillSthToExplore = true; // there still are some sites out there!
				}
				for(Website current : toExplore){
					explore( current, maxDepth-1 );
				}
			} catch (IOException E){
				System.out.println("! Encountered error: "+E);
			}
		} while( toExplore!=null && stillSthToExplore && !toExplore.isEmpty() && maxDepth>0 );
		if(!stillSthToExplore){
			System.out.println("The web was explored!");
		}
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
			
			if(!matchesRestrictions(url)){
				System.out.println("- IGNORED URL: \""+url+"\"");
				continue; // this url is ignored
			}
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
	 * @param filename: the file to which to write
	 * @throws IOException in case of input error
	 */
	public void writeToFile(String filename) throws IOException{
		FileWriter file = new FileWriter( filename );
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		int index = 0;
		// write sites
		for(Website site : allsites){
			table.put(site.toString(), index);
			file.write(site.toCleverString()+"\n");
			index ++;
		}
		// write links
		file.write("\n");
		for(Website site : allsites){
			int i = table.get( site.toString() );
			for(Website linked : site.getNeighbors()){
				int j = table.get( linked.toString() );
				file.write(i+" "+j+"\n");
			}
		}
		// close file
		file.close();
	}
	
	/**
	 * Read a file containing a graph
	 * @param filename: the file from which to read
	 * @return the web graph
	 * @throws IOException in case of input error
	 */
	public static WebGraph readFromFile(String filename) throws IOException{
		BufferedReader file = new BufferedReader( new FileReader(filename) ) ;
		Hashtable<Integer, Website> table = new Hashtable<Integer, Website>();
		// read root from file
		String line = file.readLine();
		Website root = Website.fromCleverString(line);
		WebGraph result = new WebGraph(root);
		table.put(0, result.getRoot());
		// read all sites
		int index = 1;
		do{
			line = file.readLine();
			if(line.isEmpty()){
				break;
			}
			Website site = Website.fromCleverString(line);
			System.out.println("read: "+site);
			table.put(index, site);
			result.allsites.add(site);
			index++;
		} while( line!=null && !line.isEmpty() );
		// read all links
		do{
			line = file.readLine();
			if(line == null){
				break;
			}
			String[] elements = line.split(" ");
			int source, dest;
			try{
				source = Integer.parseInt(elements[0]);
				dest   = Integer.parseInt(elements[1]);
			} catch( NumberFormatException E ){
				System.out.println("Bad format: \""+line+"\"");
				continue;
			}
			table.get(source).neighbors.add( table.get(dest) );
		} while( line!=null && !line.isEmpty() );
		// return the built webgraph
		file.close();
		return result;
	}

	
	// Graph specific methods
	
	/**
	 * Returns the root of this graph
	 */
	public Website getRoot(){
		return root;
	}
	
	/**
	 * Add a website to this graph
	 * @param site: the website to add
	 */
	public void addWebsite( Website site ){
		allsites.add(site);
	}
	
	/**
	 * Quick display of information for this graph
	 */
	public String toString(){
		return "WEBGRAPH of root = \""+this.root+"\"\n"+
				"            size = "+this.allsites.size();
	}

}
