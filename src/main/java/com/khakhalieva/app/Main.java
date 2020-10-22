package com.khakhalieva.app;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {
    private static final int n = 3;
    public static void main(String[] args) {
        List<Integer[]> pipeSysList = new ArrayList<>();
        readFromCsvFile(pipeSysList,"pipeline_system.csv");
        List<Integer[]> pointsList = new ArrayList<>();
        readFromCsvFile(pointsList,"points.csv");
        Integer max = Arrays.asList(pipeSysList.get(0)).get(0);
        for (Integer[] value : pipeSysList) {
            for (int j = 0; j < n - 1; j++) {
                if (max < Arrays.asList(value).get(j)) {
                    max = Arrays.asList(value).get(j);
                }
            }
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:"+"./Database/my");
            Statement statement = connection.createStatement();
            Class.forName("org.h2.Driver");
            statement.executeUpdate("DROP TABLE IF EXISTS pipeline_system");
            statement.executeUpdate("CREATE TABLE pipeline_system(idx INTEGER,idy INTEGER,length INTEGER)");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO pipeline_system VALUES(?,?,?)");
            for (Integer[] integers : pipeSysList) {
                for (int j = 0; j < n; j++) {
                    preparedStatement.setInt(j + 1, Arrays.asList(integers).get(j));
                }
                preparedStatement.executeUpdate();
            }
            // System.out.println("Table created");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Node[] nodes = new Node[max];
        PipelineSystem systemIds = new PipelineSystem();
        for(int i=0; i<max; i++){
            nodes[i] = new Node(i+1);
            systemIds.addNode(nodes[i]);
        }
        for (Integer[] integers : pipeSysList) {
            nodes[(Arrays.asList(integers).get(0)) - 1].addDestination(nodes[(Arrays.asList(integers).get(1)) - 1], Arrays.asList(integers).get(2));
        }
        List<String[]> resultList = new ArrayList<>();
        for (Integer[] integers : pointsList) {
            String[]result=(calculateShortestPathBetweenPoints(systemIds, nodes[(Arrays.asList(integers).get(0)) - 1], Arrays.asList(integers).get(1))).split(",");
            resultList.add(result);
            //           System.out.println(calculateShortestPathBetweenPoints(systemIds, nodes[(Arrays.asList(integers).get(0)) - 1], Arrays.asList(integers).get(1)));
        }
        Options options = new Options();
        options.addRequiredOption("r", "requiredOption",
                false, "");
        options.addOption("g","generate",true, "generate csv file with existed routes and minimum length");
        options.addOption("ds", "displaySystem", false,
                "display water pipeline system");
        options.addOption("dp", "displayPoints", false,
                "display points between which will be found minimum length");
        options.addOption("s", "showResult", false,
                "display the content of generated file");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "water pipeline system", options );
            System.exit(1);
        }
        if (cmd.hasOption("g")) {
            writeIntoCsvFile(resultList,cmd.getOptionValue("g"));
            System.out.println("The file has been generated");
        }
        if (cmd.hasOption("ds")) {
            System.out.println("Water pipeline system:");
            System.out.println("idx:idy:length");
            for (Integer[] integers : pipeSysList) {
                System.out.println(Arrays.asList(integers));
            }
        }
        if (cmd.hasOption("dp")) {
            System.out.println("Points:");
            System.out.println("idx:idy");
            for (Integer[] integers : pointsList) {
                for (int j = 0; j < n - 1; j++) {
                    System.out.print(Arrays.asList(integers).get(j));
                    if(j==0)System.out.print("    ");
                }
                System.out.println();
            }
        }
        if (cmd.hasOption("s")) {
            System.out.println("Route existence and minimum length:");
            for (String[] result : resultList) {
                System.out.println(Arrays.asList(result));
            }
        }
    }
    private static void writeIntoCsvFile(List<String[]> resultList, String file){
        try(FileWriter writer = new FileWriter(file)) {
            for (String[] results : resultList) {
                for (int i = 0; i < results.length; i++) {
                    writer.append(results[i]);
                    if(i < (results.length-1))
                        writer.append(",");
                }
                writer.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void readFromCsvFile(List<Integer[]> list, String fileName){
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            Scanner scan;
            while((line=reader.readLine())!=null){
                scan = new Scanner(line);
                scan.useDelimiter(",");
                Integer[] array = new Integer[n];
                int i = 0;
                while(scan.hasNext() && i < n){
                    array[i] = Integer.parseInt(scan.next());
                    i++;
                }
                list.add(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String calculateShortestPathBetweenPoints(PipelineSystem system, Node firstPoint, Integer secondPoint){
        String result = "";
        for(Node node:Main.calculateShortestPathFromSource(system, firstPoint).getNodes()){
            if(node.getId()==secondPoint){
                if(node.getDistance()==Integer.MAX_VALUE){
                    return "FALSE";
                }
                result = "TRUE"+","+node.getDistance();
            }
        }
        return result;
    }
    private static PipelineSystem calculateShortestPathFromSource(PipelineSystem system, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry< Node, Integer> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return system;
    }
    private static Node getLowestDistanceNode(Set <Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeight, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
