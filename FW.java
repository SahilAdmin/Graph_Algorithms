import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ---------------------------------------Description------------------------------------------
 * Floyd Warshall algorithm is an all pairs shortest path algorithm. It can be used with graph
 * containing negative edge weights. It's time complexity is O(V^3) and space complexity is O(V^2)
 *
 * You can change INF and NINF according to your needs
 * 
 * @author -> Sahil Mahajan
 * This code was inspired from algorithms github repository of William Fiset
 */

public class FW {
    long[][] dist, mat;
    int[][] next;
    long INF = (long)10e16;
    long NINF = (long)-10e16;
    boolean solved = false;

    public FW(long[][] mat) {
        this.mat = mat;
        int n = mat.length;
        this.next = new int[n][n];
        this.dist = new long[n][n];
        for(int i = 0; i < n; i++) {
            Arrays.fill(next[i], -1);
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
        }
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(dist[i][j] > mat[i][j]) dist[i][j] = mat[i][j];
                if(mat[i][j] != INF) next[i][j] = j;
            }
        }
    }

    public FW(int n) {
        this.mat = new long[n][n];
        this.next = new int[n][n];
        this.dist = new long[n][n];
        for(int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            Arrays.fill(next[i], -1);
            Arrays.fill(mat[i], INF);
            dist[i][i] = 0;
            next[i][i] = i;
        }
    }

    public void addEdge(int from, int to, long cost) {
        mat[from][to] = Math.min(mat[from][to], cost);
        dist[from][to] = Math.min(dist[from][to], cost);
        if(mat[from][to] != INF) next[from][to] = to;
    }

    public void solve() {
        int n = mat.length;
        if(solved) return;

        for(int c = 0; c < n; c++) {
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(dist[i][j] > add(dist[i][c], dist[c][j])) {
                        dist[i][j] = dist[i][c]+dist[c][j];
                        next[i][j] = next[i][c];
                    }
                }
            }
        }

        // To identify negative cycles
        // If values of i, j decreases then i, j is in a negative cycle
        for(int c = 0; c < n; c++) {
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(add(dist[i][c],dist[c][j]) < dist[i][j]) {
                        dist[i][c] = NINF;
                        next[i][j] = -1;
                    }
                }
            }
        }

        solved = true;
    }

    public long add(long a, long b) {
        if(a == INF || b == INF) {
            return INF;
        } else if(b  == NINF || a == NINF) {
            return NINF;
        } else return a+b;
    }

    public List<Integer> reconPath(int from, int to) {
        if(!solved) solve();

        if(dist[from][to] == -1) return null;
        List<Integer> path = new ArrayList<>();

        while(from != to) {
            path.add(from);
            from = next[from][to];
            if(from == -1) return null;
        }

        path.add(to);
        return path;
    }
}
