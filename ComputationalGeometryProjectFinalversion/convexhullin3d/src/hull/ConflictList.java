package hull;

import java.util.List;

//this list contains all of the facet that are visible. For a facet, the list contains all of the
// vertices that can see it.  The conflict lists are stored as doubly-connected lists of GraphArcs 
public class ConflictList{
   protected GraphArc head;
   private boolean facet;
   
   public ConflictList(boolean facet){
      this.head = null;
      this.facet = facet;
   }

   public void add(GraphArc arc){
      if(this.facet){
         if(this.head != null){ this.head.prevf = arc; }
         arc.nextf = this.head;
         this.head = arc;
      }else{
         if(this.head != null){ this.head.prevv = arc; }
         arc.nextv = this.head;
         this.head = arc;
      }
   }

   public boolean empty(){
      return(this.head == null);
   }

   public void clear(){
      while(this.head != null){
         this.head.delete();
      }
   }
   // Fill a list of verticies from the doubly-linked facet list.
   public void getVertices(List list){
      GraphArc arc = this.head;
      while(arc != null){ 
         list.add(arc.vertex);
         arc = arc.nextf;
      }
   }
   // Fill a list of facets from the doubly-linked facet list.
   public void getFacets(List list){
      GraphArc arc = this.head;
      while(arc != null){ 
         list.add(arc.facet);
         arc = arc.nextv;
      }
   }
}
