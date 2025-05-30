/*
w2053181
Sanjula Siriwardhana
 */


import java.io.*;


public class NetworkParser {
    //Read a flow network from a file

    public static FlowNetwork readFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        //Read number of nodes
        int V=Integer.parseInt(br.readLine().trim());
        FlowNetwork network = new FlowNetwork(V);

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if(line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            if(tokens.length ==3){
                int from = Integer.parseInt(tokens[0]);
                int to = Integer.parseInt(tokens[1]);
                int capacity= Integer.parseInt(tokens[2]);
                network.addEdge(from, to, capacity);
            }else{
                br.close();
                throw new IOException(filename + " is not a valid file");
            }
        }
        br.close();
        return network;
    }

    //Write a flow network to a file

    public static void writeToFile(String filename, FlowNetwork network) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(filename));

        //number of nodes
        pw.println(network.V());

        //write edges
        for(Edge edge:network.edges()){
            pw.println(edge.from() + "," + edge.to() + "," + edge.capacity());
        }
        pw.close();
    }

}
