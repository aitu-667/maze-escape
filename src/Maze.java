import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     */
    public static int[] breakWalls(Input.Walls w) {
        int n = w.cells();
        DisjointSet dsu = new DisjointSet(n);
        int totalWalls = w.count();
        int[] temp = new int[totalWalls];
        int count = 0;

        for (int i = 0; i < totalWalls; i++) {
            if (dsu.union(w.a()[i], w.b()[i])) {
                temp[count++] = i;
            }
        }

        return Arrays.copyOf(temp, count);
    }

    /**
     * Mission 2. Kruskal: take the walls from the cheapest to the most expensive.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int totalWalls = w.count();
        Integer[] order = new Integer[totalWalls];
        for (int i = 0; i < totalWalls; i++) {
            order[i] = i;
        }

        Arrays.sort(order, Comparator.comparingInt(i -> w.cost()[i]));

        int n = w.cells();
        DisjointSet dsu = new DisjointSet(n);
        int[] temp = new int[totalWalls];
        int count = 0;

        for (int idx : order) {
            if (dsu.union(w.a()[idx], w.b()[idx])) {
                temp[count++] = idx;
            }
        }

        return Arrays.copyOf(temp, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Total cost of the cheapest maze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int totalWalls = w.count();

        int[] head = new int[n];
        Arrays.fill(head, -1);

        int edgeCount = totalWalls * 2;
        int[] to = new int[edgeCount];
        int[] cost = new int[edgeCount];
        int[] next = new int[edgeCount];
        int edgeIdx = 0;

        for (int i = 0; i < totalWalls; i++) {
            int u = w.a()[i];
            int v = w.b()[i];
            int c = w.cost()[i];

            to[edgeIdx] = v;
            cost[edgeIdx] = c;
            next[edgeIdx] = head[u];
            head[u] = edgeIdx++;

            to[edgeIdx] = u;
            cost[edgeIdx] = c;
            next[edgeIdx] = head[v];
            head[v] = edgeIdx++;
        }

        boolean[] visited = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        visited[0] = true;
        for (int e = head[0]; e != -1; e = next[e]) {
            pq.offer(new int[]{to[e], cost[e]});
        }

        long totalCost = 0;
        int visitedCount = 1;

        while (!pq.isEmpty() && visitedCount < n) {
            int[] curr = pq.poll();
            int u = curr[0];
            int c = curr[1];

            if (visited[u]) continue;

            visited[u] = true;
            totalCost += c;
            visitedCount++;

            for (int e = head[u]; e != -1; e = next[e]) {
                int v = to[e];
                if (!visited[v]) {
                    pq.offer(new int[]{v, cost[e]});
                }
            }
        }

        return totalCost;
    }
}