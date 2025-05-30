/*
w2053181
Sanjula Siriwardhana
 */

import java.util.*;

public class FordFulkerson {
    private boolean[] marked;
    private Edge[] edgeTo;
    private final List<List<Integer>> augmentingPaths; //store all augmenting paths
    private final List<Integer> augmentingFlows;//store flow of each augmenting path
    private int value;
    private int pathCount;
    private final boolean storeAllPaths;

    public FordFulkerson(FlowNetwork G, int s, int t) {
        this(G, s, t, true);
    }

    public FordFulkerson(FlowNetwork G, int s, int t, boolean storeAllPaths) {
        validate(G, s, t);

        value = 0;
        pathCount = 0;
        this.storeAllPaths = storeAllPaths;

        // Only allocate memory for path storage if needed
        if (storeAllPaths) {
            augmentingPaths = new ArrayList<>();
            augmentingFlows = new ArrayList<>();
        } else {
            augmentingPaths = null;
            augmentingFlows = null;
        }

        while (hasAugmentingPath(G, s, t)) {
            // Calculate bottleneck capacity
            int bottle = Integer.MAX_VALUE;
            for (int v = t; v != s; v = edgeTo[v].from()) {
                bottle = Math.min(bottle, edgeTo[v].remainingCapacity());
            }

            // Only store paths if enabled
            if (storeAllPaths) {
                // Build the path from sink to source
                List<Integer> reversePath = new ArrayList<>();
                for (int v = t; v != s; v = edgeTo[v].from()) {
                    reversePath.add(v);
                }
                reversePath.add(s); // Add source at the end

                // Reverse to get source to sink order
                Collections.reverse(reversePath);

                // Store the path and flow
                augmentingPaths.add(reversePath);
                augmentingFlows.add(bottle);
            }

            pathCount++;

            // Augment flow
            for (int v = t; v != s; v = edgeTo[v].from()) {
                edgeTo[v].addFlow(bottle);
            }

            value += bottle;
        }
    }

    public int value() {
        return value;
    }

    public int getPathCount() {
        return pathCount;
    }

    public boolean inCut(int v) {
        validate(v);
        return marked[v];
    }

    public String getAugmentingPathInfo() {
        if (!storeAllPaths) {
            return "Augmenting Paths: " + pathCount + " paths found (details not stored to save memory)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Augmenting Paths:\n");
        for (int i = 0; i < augmentingPaths.size(); i++) {
            List<Integer> path = augmentingPaths.get(i);
            int flow = augmentingFlows.get(i);

            sb.append("Path ").append(i+1).append(": ");
            for (int j = 0; j < path.size(); j++) {
                sb.append(path.get(j));
                if (j < path.size()-1) sb.append(" -> ");
            }
            sb.append(" (Flow: ").append(flow).append(")\n");
        }
        return sb.toString();
    }

    //final flow value of all edges
    public String getFinalFlowInfo(FlowNetwork G) {
        StringBuilder sb = new StringBuilder();

        sb.append("Final Flow Values:\n");
        for (Edge e : G.edges()) {
            if (e.flow() > 0) {
                sb.append("f(").append(e.from()).append(",").append(e.to())
                        .append(") = ").append(e.flow()).append("\n");
            }
        }

        return sb.toString();
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new Edge[G.V()];
        marked = new boolean[G.V()];

        // Breadth-first search
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        marked[s] = true;

        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.remove();

            for (Edge e : G.adj(v)) {
                int w = e.to();

                // If residual capacity from v to w
                if (e.remainingCapacity() > 0 && !marked[w]) {
                    edgeTo[w] = e;
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }

        return marked[t];
    }

    // Throw an exception if vertex is outside range
    private void validate(int v) {
        if (v < 0)
            throw new IllegalArgumentException("vertex " + v + " is negative");
    }

    private void validate(FlowNetwork G, int s, int t) {
        if (s < 0 || s >= G.V())
            throw new IllegalArgumentException("source " + s + " is outside range 0 to " + (G.V()-1));
        if (t < 0 || t >= G.V())
            throw new IllegalArgumentException("sink " + t + " is outside range 0 to " + (G.V()-1));
        if (s == t)
            throw new IllegalArgumentException("source equals sink");
    }
}