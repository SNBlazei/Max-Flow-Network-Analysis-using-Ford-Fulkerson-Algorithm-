/*
w2053181
Sanjula Siriwardhana
 */



import java.io.IOException;
import java.util.*;
public class MaxFlow {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: MaxFlow <input file>");
            return;
        }
        try {
            //read the network from file
            FlowNetwork flowNetwork=NetworkParser.readFromFile(args[0]);

            //print the network
            System.out.println("Network");
            System.out.println(flowNetwork);

            //maximum flow
            int source=0;
            int sink= flowNetwork.V()-1; //Sink the Last node
            FordFulkerson maxflow = new FordFulkerson(flowNetwork, source, sink);

            System.out.println("Maximum flow value = " + maxflow.value());
            System.out.println();
            System.out.println(maxflow.getAugmentingPathInfo());
            System.out.println(maxflow.getFinalFlowInfo(flowNetwork));

        }catch (IOException e){
            System.err.println("Error reading input file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error in input format: " + e.getMessage());
        }


    }
}


