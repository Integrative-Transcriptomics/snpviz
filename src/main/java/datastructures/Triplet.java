package datastructures;
/**
 * 
 */

import java.io.Serializable;

/**
 * @author Alexander Seitz
 *
 */
public class Triplet<A, B,C> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5966570617768344553L;
	private A first;
	private B second;
	private C third;
	
	public Triplet(A first, B second, C third){
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public A getFirst(){
		return this.first;
	}
	
	public B getSecond(){
		return this.second;
	}
	
	public C getThird(){
		return this.third;
	}

}
