/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public abstract class ContentSource implements Serializable {
 
    public final List<Content> content = new LinkedList();
    public final String url;
    private Date lastUpdated;

    public ContentSource(String url, Date lastUpdated) {
        this.url = url;
        this.lastUpdated = lastUpdated;
    }

    abstract public String getTitle();
    
}
