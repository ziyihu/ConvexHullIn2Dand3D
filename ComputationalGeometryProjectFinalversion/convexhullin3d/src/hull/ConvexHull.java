package hull;

import j3d.*;

import java.util.List;
import java.util.Random;

public class ConvexHull extends Polytope{
   public int  current;		   //Current vertex to add to the hull 
   public List created; 	   //newly created facets 
   public List horizon;		   //edges on the horizon 
   public List visible;		   //faces visible to the current vertex 
   public ConvexHull(){
      this.created = new java.util.ArrayList();
      this.horizon = new java.util.ArrayList();
      this.visible = new java.util.ArrayList();
      restart(1);
   }
   
   public void restart(int k){ 
	      clear();  
	      this.current = 0;
	      this.created.clear();
	      this.horizon.clear();
	      this.visible.clear();
	      Vertex v;
	      Random rand = new Random(System.currentTimeMillis());
	      float d = getDiameter();   
	      float r = d / 2.0F;       
	      for(int i=0; i<100*k; i++){
	         v = new Vertex((rand.nextFloat() * d) - r,(rand.nextFloat() * d) - r,(rand.nextFloat() * d) - r);
	         addVertex(v);
	         v.setData(new ConflictList(false));
	      }
	   }

   public void start(int k){
	   prep();
	   for(int i=0; i<100*k+50; i++){     
		   if(created.size() == 0){
			   Increment();
		   }else{
			   Remove(0);
		   }
	   }
   } 

   public void fillall(int k){
	   prep();
	   for(int i=0; i<100*k+50; i++){     
		   if(created.size() == 0){
			   Increment();
		   }else{
			   Remove(1);
		   }
	   }
   }
    
   public void bruteforce(boolean fill,int k){
	   		this.created.clear();
	   		this.horizon.clear();
	   		this.visible.clear();
	   		for(int i = 0 ; i < getVertexCount() ; i++){
			   for(int j = 0 ; j < getVertexCount() ; j++){
				   for(int m = 0; m < getVertexCount() ; m++){
					   if(i!=j && i!=m && j!=m){
						   Vertex v1 = getVertex(i);
						   Vertex v2 = getVertex(j);
						   Vertex v3 = getVertex(m);
						   Facet f1 = new Facet(v1,v2,v3);
						   boolean face = true;
						   for(int n = 0;n < getVertexCount() ; n++){
							   Vertex d = getVertex(n);
							   if(f1.behind(d) && n!=i && n!=j && n!=m){
								   face = false;
							   }
						   }
			//			   removeFacet(f1);
						   if(face){
						//	   Edge e = new Edge(v2, v3, f1);
							   Facet ff1 = new Facet(v1, v2, v3);	       
							   ff1.setData(new ConflictList(true));
							   addFacet(ff1);
							   created.add(ff1);
							   ff1.setFilled(false);
							   if(fill){
								   ff1.setFilled(true);	   		   
							   }
						   }						   
					   }
				   }
			   }
		   }
		   created.clear();
   }
    
   public void prep(){ 
      Vertex a = getVertex(0);
      Vertex b = getVertex(1);
      Vertex c = getVertex(2);
      Vertex d = getVertex(3);
      Facet f1 = new Facet(a, b, c, d);
      Facet f2 = new Facet(a, c, d, b);
      Facet f3 = new Facet(a, b, d, c);
      Facet f4 = new Facet(b, c, d, a);
      f1.setData(new ConflictList(true));
      f2.setData(new ConflictList(true));
      f3.setData(new ConflictList(true));
      f4.setData(new ConflictList(true));
      addFacet(f1);
      addFacet(f2);
      addFacet(f3);
      addFacet(f4);
      f1.connect(f2, a, c);
      f1.connect(f3, a, b);
      f1.connect(f4, b, c);
      f2.connect(f3, a, d);
      f2.connect(f4, c, d);
      f3.connect(f4, b, d);
      this.current = 4;
      Vertex v;
      for(int i=4; i<getVertexCount(); i++){
         v = getVertex(i);
         if(!f1.behind(v)) addConflict(f1, v);
         if(!f2.behind(v)) addConflict(f2, v);
         if(!f3.behind(v)) addConflict(f3, v);
         if(!f4.behind(v)) addConflict(f4, v);
      }
      a.setVisible(true);
      b.setVisible(true);
      c.setVisible(true);
      d.setVisible(true);
   }

   public void Increment(){ 
      if(this.current >= getVertexCount()) return;
      this.created.clear();
      this.horizon.clear();
      this.visible.clear();
      Vertex v = getVertex(current);
      ((ConflictList)v.getData()).getFacets(visible);   
      // If v is already inside the convex hull, try the next point
      if(visible.size() == 0){
         v.setVisible(false);
         current++;
         Increment();
         return;
      }
      //Flag visible facets 
      for(int i=0; i<visible.size(); i++){ 
         ((Facet)visible.get(i)).setMarked(true);
      }
      // Find horizon edges 
      Edge e;
      for(int i=0; i<visible.size(); i++){
         e = ((Facet)visible.get(i)).getHorizonEdge();
         if(e != null){
            e.findHorizon(horizon);
            break;
         }
      }
      if(this.current >= getVertexCount()) return;    
      Vertex v1 = getVertex(current);
      // Create new facets to connect to the horizontal edges
      Facet old, last = null, first = null;
      for(int i=0; i<horizon.size(); i++){
         Edge e1 = (Edge)horizon.get(i);
         old = e1.getTwin().getFacet();      
         // Create a new facet
         Facet f = new Facet(v, e1.getDest(), e1.getSource());
         f.setData(new ConflictList(true));
         addFacet(f);
         created.add(f);
         f.setFilled(false);
         // Connect it to the hull
         f.connect(e1);
         if(last != null) f.connect(last, v1, e1.getSource());
         last = f;
         if(first == null) first = f;
         // reassign points to the new facet
         addConflicts(f, old, e1.getFacet());
      }    
      if(last != null && first != null){
         last.connect(first, v1, first.getEdge(1).getDest());
      }
   }

   public void Remove(int k){
      getVertex(current).setVisible(true); 
      // Remove all previously visible facets
      Facet f;
      for(int i=0; i<visible.size(); i++){
         f = (Facet)visible.get(i);
         removeConflicts(f);
         removeFacet(f);
      }
      if(k==1){
    	  for(int i=0; i<created.size(); i++){
    	         ((Facet)created.get(i)).setFilled(true);
    	      }
      }
      created.clear();
      this.current++;
   }

   //Add an arc to the conflict graph connecting the given facet and vertex.
   public void addConflict(Facet f, Vertex v){
	   GraphArc arc = new GraphArc(f, v);
	   ((ConflictList)f.getData()).add(arc);
	   ((ConflictList)v.getData()).add(arc);
   }  
   //Remove all conflicts for the given facet.
   public void removeConflicts(Facet f){
	   ((ConflictList)f.getData()).clear();
   }  
   //Test all conflicts for the existing facet with the new facet.  Add conflict arc for all vertices that can now see the new facet.
   public void addConflicts(Facet f, Facet old1, Facet old2){
	   List l1 = new java.util.ArrayList();
	   List l2 = new java.util.ArrayList();
	   List l = new java.util.ArrayList();
	   ((ConflictList)old1.getData()).getVertices(l1);
	   ((ConflictList)old2.getData()).getVertices(l2);
	   Vertex v1, v2;
	   int i1 = 0, i2 = 0;
	   while(i1 < l1.size() || i2 < l2.size()){
		   if(i1 < l1.size() && i2 < l2.size()){
			   v1 = (Vertex)l1.get(i1);
			   v2 = (Vertex)l2.get(i2);
			   if(v1.getIndex() == v2.getIndex()){
				   l.add(v1);
				   i1++;
				   i2++;
			   }
			   else if(v1.getIndex() > v2.getIndex()){
				   l.add(v1);
				   i1++;
			   }
			   else{
				   l.add(v2);
				   i2++;
			   }
		   }
		   else if(i1 < l1.size()){
			   l.add(l1.get(i1++));
		   }
		   else{
			   l.add(l2.get(i2++));
		   }
	   }
	   Vertex v;
	   for(int i=l.size() - 1; i >= 0; i--){
		   v = (Vertex)l.get(i);
		   if(!f.behind(v)) addConflict(f, v);
	   }
   }

}

class GraphArc{
	   // The facet and vertex that are visible to each other
	   protected Facet facet;
	   protected Vertex vertex;
	   // Doubly-linked list for a vertex
	   protected GraphArc nextv;
	   protected GraphArc prevv;
	   // Doubly-linked list for a facet
	   protected GraphArc nextf;
	   protected GraphArc prevf;
	//Create a new arc for the conflict graph.  This arc won't be
	//connected until add() is called. 
	   public GraphArc(Facet f, Vertex v){
	      this.vertex = v;
	      this.facet = f;
	      this.nextv = null;
	      this.prevv = null;
	      this.nextf = null;
	      this.prevf = null;
	   }
	//Delete this GraphArc from both doubly-linked lists
	   public void delete(){
	      if(this.prevv != null){ this.prevv.nextv = this.nextv; }
	      if(this.nextv != null){ this.nextv.prevv = this.prevv; }
	      if(this.prevf != null){ this.prevf.nextf = this.nextf; }
	      if(this.nextf != null){ this.nextf.prevf = this.prevf; }
	      ConflictList list;
	      if(this.prevv == null){ 
	         list = (ConflictList)this.vertex.getData();
	         list.head = this.nextv;
	      }
	      if(this.prevf == null){
	         list = (ConflictList)this.facet.getData();
	         list.head = this.nextf;
	      }
	   }

	}
