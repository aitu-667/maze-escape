import java.util.*;

/**
 * Missions 4 and 5. Strongly Connected Components (Kosaraju) and DAG shortest path.
 */
public class Zones {

    /**
     * Mission 4. Kosaraju SCC on slides graph.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] outSlides = Graphs.slidesFrom(s);
        int[][] inSlides = Graphs.slidesInto(s);

        // Pass 1: Iterative DFS to record finish order
        boolean[] visited = new boolean[n];
        int[] finishedOrder = new int[n];
        int finishPtr = 0;

        int[] stackCell = new int[n + 1];
        int[] stackIndex = new int[n + 1];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                int top = 0;
                stackCell[top] = i;
                stackIndex[top] = 0;
                visited[i] = true;

                while (top >= 0) {
                    int u = stackCell[top];
                    int idx = stackIndex[top];

                    if (idx < outSlides[u].length) {
                        stackIndex[top]++;
                        int slideIdx = outSlides[u][idx];
                        int v = s.to()[slideIdx];
                        if (!visited[v]) {
                            visited[v] = true;
                            top++;
                            stackCell[top] = v;
                            stackIndex[top] = 0;
                        }
                    } else {
                        finishedOrder[finishPtr++] = u;
                        top--;
                    }
                }
            }
        }

        // Pass 2: DFS on reversed graph in reverse finish order
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int currentZone = 0;

        int[] revStack = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            int root = finishedOrder[i];
            if (zoneOf[root] == -1) {
                int top = 0;
                revStack[top] = root;
                zoneOf[root] = currentZone;

                while (top >= 0) {
                    int u = revStack[top--];

                    for (int slideIdx : inSlides[u]) {
                        int v = s.from()[slideIdx];
                        if (zoneOf[v] == -1) {
                            zoneOf[v] = currentZone;
                            revStack[++top] = v;
                        }
                    }
                }
                currentZone++;
            }
        }

        return zoneOf;
    }

    /**
     * Mission 5. Graph of zones, Flow stages and shortest path cost in DAG.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int startZone = zoneOf[0];
        int exitZone = zoneOf[s.n() - 1];

        int[] longestChain = new int[zoneCount];
        Arrays.fill(longestChain, 1);

        long INF = Long.MAX_VALUE / 2;
        long[] minCost = new long[zoneCount];
        Arrays.fill(minCost, INF);
        minCost[startZone] = 0;

        Map<Long, Long> zoneEdges = new HashMap<>();

        for (int i = 0; i < s.count(); i++) {
            int uZone = zoneOf[s.from()[i]];
            int vZone = zoneOf[s.to()[i]];
            long cost = s.time()[i];

            if (uZone != vZone) {
                long key = (((long) uZone) << 32) | (vZone & 0xFFFFFFFFL);
                zoneEdges.merge(key, cost, Math::min);
            }
        }

        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < zoneCount; i++) {
            adj.add(new ArrayList<>());
        }

        for (Map.Entry<Long, Long> entry : zoneEdges.entrySet()) {
            long key = entry.getKey();
            int u = (int) (key >> 32);
            int v = (int) (key & 0xFFFFFFFFL);
            long cost = entry.getValue();
            adj.get(u).add(new int[]{v, (int) cost});
        }

        for (int u = 0; u < zoneCount; u++) {
            for (int[] edge : adj.get(u)) {
                int v = edge[0];
                int weight = edge[1];

                if (longestChain[u] + 1 > longestChain[v]) {
                    longestChain[v] = longestChain[u] + 1;
                }

                if (minCost[u] != INF) {
                    if (minCost[u] + weight < minCost[v]) {
                        minCost[v] = minCost[u] + weight;
                    }
                }
            }
        }

        int maxFlowStages = 0;
        for (int i = 0; i < zoneCount; i++) {
            if (longestChain[i] > maxFlowStages) {
                maxFlowStages = longestChain[i];
            }
        }

        long fastestRoute = minCost[exitZone];

        return new long[]{maxFlowStages, fastestRoute};
    }
}