package com.khakhalieva.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {
    private int id;
    private List<Node> shortestPath = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;
    private Map<Node, Integer> adjacentNodes = new HashMap<>();
    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }
    public Node(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    public Integer getDistance() {
        return distance;
    }
    public Map<Node,Integer> getAdjacentNodes() {
        return adjacentNodes;
    }
    public List<Node> getShortestPath() {
        return shortestPath;
    }
    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }
}
