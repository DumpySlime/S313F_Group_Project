package com.example.publictransportapp.util;


import com.example.publictransportapp.model.RouteStop;
import java.util.*;

public class GraphBuilder {
    public Map<String, List<String>> buildGraph(List<RouteStop> routeStops) {
        Map<String, List<String>> graph = new HashMap<>();

        Map<Triple, List<RouteStop>> grouped = new HashMap<>();
        for (RouteStop stop : routeStops) {
            Triple key = new Triple(stop.getRoute(), stop.getBound(), stop.getServiceType());
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(stop);
        }

        for (List<RouteStop> stops : grouped.values()) {
            stops.sort(Comparator.comparingInt(s -> Integer.parseInt(s.getSeq())));
            for (int i = 0; i < stops.size() - 1; i++) {
                String from = stops.get(i).getStop();
                String to = stops.get(i + 1).getStop();

                graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                graph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
            }
        }

        return graph;
    }

    private static class Triple {
        String route, bound, serviceType;

        Triple(String route, String bound, String serviceType) {
            this.route = route;
            this.bound = bound;
            this.serviceType = serviceType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Triple triple = (Triple) o;
            return Objects.equals(route, triple.route) &&
                    Objects.equals(bound, triple.bound) &&
                    Objects.equals(serviceType, triple.serviceType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(route, bound, serviceType);
        }
    }
}