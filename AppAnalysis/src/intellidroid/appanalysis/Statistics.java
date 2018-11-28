package intellidroid.appanalysis;

import java.util.*;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.wala.classLoader.*;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.util.collections.*;

class Statistics {
    static private Date _startTime = null;
    static private Date _endTime = null;

    static private Date _callGraphStartTime = null;
    static private Date _callGraphEndTime = null;

    static private Date _constraintStartTime = null;
    static private Date _constraintEndTime = null;

    static private long _numberOfNodes = 0;
    static private long _numberOfEdges = 0;
    static private Set<CGNode> _pathNodes = new HashSet<CGNode>();
    static private Set<Pair<IMethod, IMethod>> _pathEdges = new HashSet<Pair<IMethod, IMethod>>();

    //=========================================================================


    static public void setNumberOfNodes(long num) {
        if (IntelliDroidAppAnalysis.Config.GenerateStats) {
            _numberOfNodes = num;
        }
    }

    static public void setNumberOfEdges(long num) {
        if (IntelliDroidAppAnalysis.Config.GenerateStats) {
            _numberOfEdges = num;
        }
    }

    // STATS TO BE REPORTED ON ALL APPS

    static public void startAnalysis() {
        _startTime = new Date();
    }

    static public void endAnalysis() {
        _endTime = new Date();
    }

    static public void startCallGraph() {
        _callGraphStartTime = new Date();
    }

    static public void endCallGraph() {
        _callGraphEndTime = new Date();
    }


    static public void startConstraintAnalysis() {
            _constraintStartTime = new Date();
    }

    static public void endConstraintAnalysis() {
            _constraintEndTime = new Date();
    }

    static public void trackPath(List<CGNode> path, IMethod targetMethod) {
        // MODIFIED TO ALWAYS ADD NODES AND EDGES
            _pathNodes.addAll(path);

            for (int i = 0; i < path.size() - 1 ; i++) {
                _pathEdges.add(Pair.make(path.get(i).getMethod(), path.get(i + 1).getMethod()));
            }

            if (targetMethod != null) {
                _pathEdges.add(Pair.make(path.get(path.size() - 1).getMethod(), targetMethod));
            }

    }

    static public void writeToGraphFile() {
        try {
            // Create Dot File with Path Edges
            PrintWriter graphWriter = new PrintWriter(IntelliDroidAppAnalysis.Config.OutputDirectory + "/" + IntelliDroidAppAnalysis.Config.GraphName, "UTF-8");
            graphWriter.write("digraph CallG {\n");

            for (Pair<IMethod, IMethod> pathEdge : _pathEdges) {
                String pathEdgeString = String.format("\"%s\"->\"%s\"\n", pathEdge.fst.getSignature(), pathEdge.snd.getSignature());
                graphWriter.write(pathEdgeString);
            }

            graphWriter.write("}");
            graphWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void writeToAppJsonInfoFile(ManifestAnalysis manifestAnalysis, EntrypointAnalysis entrypointAnalysis) {

        // Print call path and constraint information
        JsonObject appInfoJson = new JsonObject();
        appInfoJson.addProperty("packageName", manifestAnalysis.getPackageName());
        appInfoJson.addProperty("mainActivity", manifestAnalysis.getMainActivityName());
        appInfoJson.addProperty("totalTime", Long.toString(getTotalTime()));
        appInfoJson.addProperty("callGraphGenerationTime", Long.toString(getCallGraphTime()));
        appInfoJson.addProperty("targetedCallGraphAnalysisTime", Long.toString(getConstraintAnalysisTime()));
        appInfoJson.addProperty("entrypointsUtilized", Integer.toString(entrypointAnalysis.getEntrypoints().size()));
        appInfoJson.addProperty("callGraphNumNodes", Integer.toString(_pathNodes.size()));
        appInfoJson.addProperty("callGraphNumEdges", Integer.toString(_pathEdges.size()));

        String appInfoFileName = "appInfo.json";

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            PrintWriter appInfoWriter = new PrintWriter(IntelliDroidAppAnalysis.Config.OutputDirectory + "/" + appInfoFileName, "UTF-8");
            appInfoWriter.print(gson.toJson(appInfoJson));
            appInfoWriter.close();
        } catch (Exception e) {
            System.err.println("Exception: " + e.toString());
            e.printStackTrace();
        }

    }

    //=========================================================================

    static public void writeToFile() {
        if (IntelliDroidAppAnalysis.Config.GenerateStats) {
            try {
                // Print timing information
                FileWriter fileWriter = new FileWriter("./timingStats.csv", true);
                PrintWriter timeWriter = new PrintWriter(fileWriter);
                String timingStr = IntelliDroidAppAnalysis.Config.AppDirectory + "," + getTotalTime() + "," + getCallGraphTime() + "," + getConstraintAnalysisTime();
                timeWriter.println(timingStr);
                timeWriter.close();
                fileWriter.close();

                // Print static analysis statitics 
                FileWriter statsFileWriter = new FileWriter( "./staticStats.csv", true);
                PrintWriter statsWriter = new PrintWriter(statsFileWriter);
                String statsStr = IntelliDroidAppAnalysis.Config.AppDirectory + "," + _numberOfNodes + "," + _numberOfEdges + "," + _pathNodes.size() + "," + _pathEdges.size();
                statsWriter.println(statsStr);
                statsWriter.close();
                statsFileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //=========================================================================

    static private long getTotalTime() {
        return _endTime.getTime() - _startTime.getTime();
    }

    static private long getCallGraphTime() {
        return _callGraphEndTime.getTime() - _callGraphStartTime.getTime();
    }

    static private long getConstraintAnalysisTime() {
        return _constraintEndTime.getTime() - _constraintStartTime.getTime();
    }

    static private long getNumberOfNodes() {
        return _numberOfNodes;
    }

    static private long getNumberOfEdges() {
        return _numberOfEdges;
    }

    static private long getNumberOfPathNodes() {
        return _pathNodes.size();
    }

    static private long getNumberOfPathEdges() {
        return _pathEdges.size();
    }

}

