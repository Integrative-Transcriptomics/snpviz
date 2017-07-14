package datastructures;
/**
 * 
 */

import java.io.Serializable;

/**
 * @author Alexander Seitz
 *
 */
public class Triplet<A extends Comparable<A>, B extends Comparable<B>, C> implements Serializable, Comparable<Triplet<A,B,C>> {
	
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Triplet<A, B, C> o) {
		int res = this.first.compareTo(o.getFirst());
		if(res == 0){
			res = this.second.compareTo(o.getSecond());
		}
		return res;
	}

}
