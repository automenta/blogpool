/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.assistant;

import blogpool.Content;

/**
 *
 * @author seh
 */
public interface Assistant {
 
    public void read(Content c);
    public boolean hasRead(Content c);
    
    public void run(double maxTimeSeconds);
    
}
