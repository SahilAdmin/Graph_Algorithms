/**
 * ----------------------------------------Description----------------------------------------*
 * This is an implementation of Dijkstra's algorithm, used to find shortest path in a graph
 * containing no negative edge weights. It's time complexity is O(E*log(v)) which is better than
 * most shortest path algorithms.
 *
 * This implementation uses D-ary Heap for fast decrease key operations which take logarithmic
 * time to relax edges as compared to linear time in normal priority queue.
 *
 * @author -> Sahil Mahajan
 * @credits -> William Fiset
 * --------------------------------------------------------------------------------------------*
 * */

import java.util.*;

public class Dijkstra {
    public static void main(String[] args) {
        /**----------------------------Example--------------------------------*/
        dj<Integer> dj = new dj<>();
        dj.insert(0, 1, 4);
        dj.insert(0, 2, 1);
        dj.insert(1, 3, 1);
        dj.insert(2, 1, 2);
        dj.insert(2, 3, 5);
        dj.insert(3, 4, 3);
        dj.dj(0, Integer.MIN_VALUE);
        System.out.println(Arrays.toString(dist));
        List<Integer> l = dj.rePath(0, 4);
        System.out.println(l);
    }

    /*Use insert to add edge, dj(start, end) to get min dist b/w start and end put end = null;
      if you want min dist b/w all the pairs, rePath to reconstruct path b/w start and end
      use dist method to get dist since every node is mapped to index*/
    /**---------------------------------------variables------------------------------------------*/
    static double[] dist;
    static Integer[] prev;

    /**-------------------------------------Dijkstra Class---------------------------------------*/
    static class dj <T>{
        HashMap<T, Integer> ht = new HashMap<>();
        private int idx = 0;
        private int edgeCt = 0;

        static class pr {
            int to;
            double cost;
            pr (int to, double cost) {
                this.to = to;
                this.cost = cost;
            }
        }

        public List<List<pr>> graph = new ArrayList<>();

        public void insert (T from, T to, double cost) {
            if(!ht.containsKey(from) && !ht.containsKey(to)) {
                ht.put(from, idx++);
                ht.put(to, idx++);
                graph.add(new ArrayList<>());
                graph.add(new ArrayList<>());
            } else if(!ht.containsKey(from)) {
                ht.put(from, idx++);
                graph.add(new ArrayList<>());
            } else if(!ht.containsKey(to)){
                ht.put(to, idx++);
                graph.add(new ArrayList<>());
            }
            graph.get(ht.get(from)).add(new pr(ht.get(to), cost));
            edgeCt++;
        }

        public void dj(T st, T end) {
            int n = ht.size();
            int deg = edgeCt/n;
            dist = new double[n];
            Arrays.fill(dist, Double.POSITIVE_INFINITY);
            dist[ht.get(st)] = 0.0;
            prev = new Integer[n];
            boolean[] vis = new boolean[n];
            dhp<Double> ipq = new dhp<>(deg, n);
            ipq.insert(ht.get(st), 0.0);

            while(!ipq.isEmpty()) {
                int id = ipq.peekMinKeyIndex();
                vis[id] = true;
                double minVal = ipq.pollMinValue();

                // This won't happen since we are using decrease key operation
                if(minVal > dist[id]) continue;

                for(pr p: graph.get(id)) {
                    if(vis[p.to]) continue;

                    double distance = dist[id]+p.cost;
                    if(distance < dist[p.to]) {
                        dist[p.to]=distance;
                        prev[p.to]=id;

                        if(!ipq.contains(p.to)) ipq.insert(p.to, distance);
                        else ipq.decrease(p.to, distance);
                    }
                }

                if(end != null && ht.get(end) == id) return;
            }
        }

        public double dist(T elem) {
            return dist[ht.get(elem)];
        }

        public List<Integer> rePath(int st, int end) {
            List<Integer> pt = new ArrayList<>();
            if(prev[end]==null) return pt;
            pt.add(end);
            while(prev[end] != null) {
                pt.add(prev[end]);
                end = prev[end];
            }
            Collections.reverse(pt);
            return pt;
        }
    }

    /**-----------------------Min Deary Heap => credits @ William Fiset------------------------*/

    private static class dhp <T extends Comparable<T>> {
        private int sz;
        private final int N;
        private final int D;
        private final int[] child, parent;
        public final int[] pm;
        public final int[] im;
        public final Object[] values;

        public dhp(int degree, int maxSize) {
            if (maxSize <= 0) throw new IllegalArgumentException("maxSize <= 0");

            D = Math.max(2, degree);
            N = Math.max(D + 1, maxSize);

            im = new int[N];
            pm = new int[N];
            child = new int[N];
            parent = new int[N];
            values = new Object[N];

            for (int i = 0; i < N; i++) {
                parent[i] = (i - 1) / D;
                child[i] = i * D + 1;
                pm[i] = im[i] = -1;
            }
        }

        public boolean isEmpty() { return sz == 0;}
        public boolean contains(int ki) {  return pm[ki] != -1;}
        public int peekMinKeyIndex() { return im[0]; }
        public T peekMinValue() { return (T) values[im[0]]; }
        public T pollMinValue() { T minValue = peekMinValue();delete(peekMinKeyIndex());return minValue; }
        public void insert(int ki, T value) {pm[ki] = sz; im[sz] = ki; values[ki] = value; swim(sz++);}

        public T delete(int ki) {
            final int i = pm[ki];
            swap(i, --sz);sink(i);swim(i);
            T value = (T) values[ki];
            values[ki] = null;
            pm[ki] = -1;
            im[sz] = -1;
            return value;
        }
        public void decrease(int ki, T value) {
            if (less(value, values[ki])) {
                values[ki] = value;
                swim(pm[ki]);
            }
        }
        private void sink(int i) {
            for (int j = minChild(i); j != -1; ) {
                swap(i, j);
                i = j;
                j = minChild(i);
            }
        }
        private void swim(int i) {
            while (less(i, parent[i])) {
                swap(i, parent[i]);
                i = parent[i];
            }
        }
        private int minChild(int i) {
            int index = -1, from = child[i], to = Math.min(sz, from + D);
            for (int j = from; j < to; j++) if (less(j, i)) index = i = j;
            return index;
        }
        private void swap(int i, int j) {
            pm[im[j]] = i;
            pm[im[i]] = j;
            int tmp = im[i];
            im[i] = im[j];
            im[j] = tmp;
        }
        private boolean less(int i, int j) {
            return ((Comparable<? super T>) values[im[i]]).compareTo((T) values[im[j]]) < 0;
        }
        private boolean less(Object obj1, Object obj2) {
            return ((Comparable<? super T>) obj1).compareTo((T) obj2) < 0;
        }
    }

    /**-------------------------------------------end--------------------------------------------*/
}