/*
w2053181
Sanjula Siriwardhana
 */

import java.util.*;

public class FlowNetwork {
    private final int V;
    private int E;
    private final ArrayList<Edge>[] adj;

    //Empty flow network
    @SuppressWarnings("unchecked")
    public FlowNetwork(int V) {
        this.V = V;
        this.E = 0;
        adj = (ArrayList<Edge>[]) new ArrayList[V];
        for(int v=0;v < V;v++){
            adj[v] = new ArrayList<>();
        }

    }
    //return the number of vertices in this flow network
    public int V() {
        return V;
    }

    //return the number of edges in this flow network
    public int E() {
        return E;
    }
    /*
     * Insert a new edge into the network
     * @param edge the edge to add
     */
    public void addEdge(Edge edge) {
        int v = edge.from();
        int w = edge.to();
        validateVertex(v);
        validateVertex(w);
        adj[v].add(edge);
        adj[w].add(edge.getResidual());
        E++;

    }

    //Method for add a new edge with given capacity
    public void addEdge(int from, int to, int capacity) {
        validateVertex(from);
        validateVertex(to);

        //create a real forward
        Edge forward = new Edge(from, to, capacity,0);
        //create a fake backward
        Edge backward=new Edge(to,from,0,0);

        //link them
        forward.setResidual(backward);
        backward.setResidual(forward);

        adj[from].add(forward);
        adj[to].add(backward);
        E++;
    }

    //Method for delete an edge from the network
    public boolean deleteEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        //Find and remove the edge
        Iterator<Edge> i = adj[from].iterator();
        while(i.hasNext()){
            Edge e = i.next();
            if(e.to() == to && e.from() == from){
                //remove the edge
                i.remove();

                //Remove the corresponding residual edge
                Iterator<Edge> j = adj[to].iterator();
                while(j.hasNext()){
                    Edge e2 = j.next();
                    if(e2.to() == from && e2.from() == to && e2.getOriginalCapacity()==0){
                        j.remove();
                        E--;
                        return true;
                    }

                }
            }
        }
        return false;
    }

    //Find an edge between two vertices

    public Edge findEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        for(Edge e: adj[from]){
            if(e.to()==to){
                return e;
            }
        }
        return null;
    }

    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    //list of all forward edges in the network
    public List<Edge> edges(){
        List<Edge> list=new ArrayList<Edge>();
        for (int v = 0; v < V; v++) {
            for (Edge e : adj[v]) {
                if(e.from() == v && e.getOriginalCapacity()>0)
                    list.add(e);
            }
        }
        return list;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * String representation of the graph
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V).append(" vertices, ").append(E).append(" edges\n");
        for (int v = 0; v < V; v++) {
            s.append(v).append(": ");
            for (Edge e : adj[v]) {
                if (e.from() == v) // only print outgoing edges
                    s.append(e).append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }

}
