/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool;

/**
 * Piece of content, with an optional parent (for threaded discussions)
 * @author me
 */
public interface Content {

    /**
     * unique identifier
     */
    public String getURL();

    /**
     * title, which may be null
     * @return 
     */
    public String getTitle();

    /**
     * content should NOT be null
     * @return 
     */
    public String getContent();

    public Content getParent();

    /**
     * Posts a comment in reply to this content
     * @param content 
     */
    public void comment(String content);
    
}
