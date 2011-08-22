/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool;

/**
 * Discussion I/O Endpoint
 * @author seh
 */
abstract public class Source {
    
    public final Blogpool pool;

    public Source(Blogpool pool) {
        super();
        this.pool = pool;
    }
    
    
}
