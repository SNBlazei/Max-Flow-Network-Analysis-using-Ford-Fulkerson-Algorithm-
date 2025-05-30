/*
w2053181
Sanjula Siriwardhana
 */

public class Edge {
    private final int v; //vertex
    private final int w; //Destination vertex
    private final int capacity; //Capacity
    private  int flow; //flow
    private Edge residual; //visual graph

    public Edge(int v, int w, int capacity, int flow) {
        this.v = v;
        this.w = w;
        this.capacity = capacity;
        this.flow = flow;
    }


    //return the vertex
    public int from(){
        return v;
    }

    //return the destination vertex
    public int to(){
        return w;
    }

    //return the capacity of edge
    public int capacity(){
        return capacity;
    }

    //return the original capacity
    public int getOriginalCapacity(){
        return capacity;
    }

    //return the flow of edge
    public int flow(){
        return flow;
    }



    public int remainingCapacity(){
        return capacity - flow;
    }

    public Edge getResidual(){
        return residual;
    }

    public void setResidual(Edge residual){
        this.residual = residual;

    }

    //method for add given amount of flow to edge
    public void addFlow(int delta){
        if(delta>remainingCapacity())
            throw new IllegalArgumentException("flow limit exceeded");


        flow +=delta;
        residual.flow -=delta;


    }

    public String toString(){
        return v + "->" +w + " " +flow + "/" + capacity;
    }


}
