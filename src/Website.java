import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * @author peanut
 * Website: a note in the internet graph representing the Internet
 */
public class Website implements Comparable<Website>{
	public URL url;
	public ArrayList<Website> neighbors;
	public boolean explored = false;
	
	/**
	 * Create a Website Node 
	 */
	public Website( URL url ){
		this.url = url;
		this.neighbors = new ArrayList<Website>();
	}
	
	/**
	 * Create a Website Node
	 */
	public Website( URL url, ArrayList<Website> neighbors){
		this(url);
		this.neighbors = neighbors; 
	}

	/**
	 * Compare this Website to another, using url
	 */
	public int compareTo(Website other) {
		return compareTo(other.getUrl());
	}
	
	public int compareTo(URL other){
		return url.toString().compareTo(other.toString());
	}
	
	public boolean equals(URL other){
		return other.toString().equals(url.toString());
	}
	
	@Override 
	public boolean equals(Object other){
		return this.url.toString().equals(((Website)other).url.toString());
	}
	
	public URL getUrl(){
		return url;
	}
	
	public ArrayList<Website> getNeighbors(){
		return neighbors;
	}
	
	public void addNeighbor( Website site ){
		neighbors.add(site);
	}
	
	public void setExplored( boolean explored ){
		this.explored = explored;
	}
	
	public boolean isExplored(){
		return explored;
	}
	
	
	public String toString(){
		return url.toString();
	}
	
	/**
	 * Returns a string holding all the information
	 */
	public String toCleverString(){
		return (this.explored?'0':'1') + this.toString();
	}
	
	/**
	 * Create a website from a clever string
	 * @param s: clever string holding information for the website
	 * @throws MalformedURLException
	 */
	public static Website fromCleverString(String s) throws MalformedURLException{
		Website result = new Website( new URL(s.substring(1)) );
		result.setExplored(s.charAt(0) == '1');
		return result;
	}
	
}
