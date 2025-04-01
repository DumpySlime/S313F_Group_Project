package com.example.publictransportapp.util;


import java.util.*;

public class Dijkstra {
    public List<String> shortestPath(Map<String, List<String>> graph, String start, String end) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Pair> queue = new PriorityQueue<>(Comparator.comparingDouble(Pair::getDistance));

        // Initialize distances
        for (String node : graph.keySet()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.add(new Pair(start, 0.0));

        while (!queue.isEmpty()) {
            Pair current = queue.poll();
            if (current.node.equals(end)) break;

            for (String neighbor : graph.getOrDefault(current.node, Collections.emptyList())) {
                double alt = distances.get(current.node) + 1; // Default edge weight = 1
                if (alt < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current.node);
                    queue.add(new Pair(neighbor, alt));
                }
            }
        }

        // Build path
        LinkedList<String> path = new LinkedList<>();
        String at = end;
        while (at != null) {
            path.addFirst(at);
            at = previous.get(at);
        }

        return path.getFirst().equals(start) ? path : Collections.emptyList();
    }

    private static class Pair {
        String node;
        double distance;

        Pair(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        double getDistance() {
            return distance;
        }
    }
}
