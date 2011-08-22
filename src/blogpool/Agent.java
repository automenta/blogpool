/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool;

/**
 *
 * @author seh
 */
public interface Agent {
 
    public void read(Content c);
    public boolean hasRead(Content c);
    
    public void run(double maxTimeSeconds);
    
}
