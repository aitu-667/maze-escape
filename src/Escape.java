import java.util.*;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell.
 * Iterative DFS using explicit arrays to prevent stack overflow on large mazes (1000x1000).
 * Order of neighbours tried: UP, RIGHT, DOWN, LEFT.
 */
public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int rows = w.rows();
        int cols = w.cols();
        int n = rows * cols;
        int target = n - 1;

        int[][] openGraph = Graphs.open(w, broken);

        int deadEndsCount = 0;
        for (int i = 0; i < n; i++) {
            if (openGraph[i].length == 1) {
                deadEndsCount++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int[] stack = new int[n];
        int stackPtr = 0;

        stack[stackPtr++] = 0;
        visited[0] = true;
        int visitedCount = 0;

        while (stackPtr > 0) {
            int u = stack[--stackPtr];
            visitedCount++;

            if (u == target) {
                break;
            }

            int r = u / cols;
            int c = u % cols;

            int[] candidates = new int[4];
            int candCount = 0;

            if (r > 0) candidates[candCount++] = u - cols;       // UP
            if (c < cols - 1) candidates[candCount++] = u + 1;   // RIGHT
            if (r < rows - 1) candidates[candCount++] = u + cols; // DOWN
            if (c > 0) candidates[candCount++] = u - 1;          // LEFT

            // Push in reverse candidate order (LEFT, DOWN, RIGHT, UP)
            // so popped order is UP, RIGHT, DOWN, LEFT
            for (int i = candCount - 1; i >= 0; i--) {
                int nxt = candidates[i];
                if (isOpenNeighbour(openGraph[u], nxt) && !visited[nxt]) {
                    visited[nxt] = true;
                    parent[nxt] = u;
                    stack[stackPtr++] = nxt;
                }
            }
        }

        // Path reconstruction
        List<Integer> pathList = new ArrayList<>();
        int curr = target;
        while (curr != -1) {
            pathList.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(pathList);

        int[] path = new int[pathList.size()];
        for (int i = 0; i < pathList.size(); i++) {
            path[i] = pathList.get(i);
        }

        return new Result(path, visitedCount, deadEndsCount);
    }

    private static boolean isOpenNeighbour(int[] openList, int cell) {
        for (int v : openList) {
            if (v == cell) return true;
        }
        return false;
    }
}