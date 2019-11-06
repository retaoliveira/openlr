package openlr.encoder.LrpHandler;

import openlr.OpenLRProcessingException;
import openlr.encoder.data.LocRefPoint;
import openlr.encoder.properties.OpenLREncoderProperties;
import openlr.encoder.routesearch.SecondShortestRouteChecker;
import openlr.map.Line;

import java.util.ArrayList;
import java.util.List;

public class AlternatePathLrpHandler implements LrpHandler {
    OpenLREncoderProperties properties;
    Double alternatePathLowerThreshold;

    private AlternatePathLrpHandler(Double alternatePathLowerThreshold, OpenLREncoderProperties properties) {
        this.alternatePathLowerThreshold = alternatePathLowerThreshold;
        this.properties = properties;
    }

    public static AlternatePathLrpHandler with(OpenLREncoderProperties properties) throws OpenLRProcessingException {
        Double alternatePathRelativeTolerance = properties.isAlternatePathCheckerConfigAvailable() ? properties.getAlternatePathRelativeLowerThreshold() :
                null;
        return new AlternatePathLrpHandler(alternatePathRelativeTolerance, properties);
    }

    private List<Integer> determineNewIntermediatePoints(List<Line> route) throws OpenLRProcessingException {
        List<Integer> intermediates = new ArrayList<>();
        SecondShortestRouteChecker checker = SecondShortestRouteChecker.on(route, alternatePathLowerThreshold);
        for (int index = 1; index < route.size(); ++index) {
            if (checker.exclude(index) == true) {
                intermediates.add(index);
            }
        }
        return intermediates;
    }

    private List<LocRefPoint> createNewLRPs(List<Line> location, List<Integer> lrpPositions) throws OpenLRProcessingException {
        List<LocRefPoint> checkedList = new ArrayList<>();
        LocRefPoint firstPoint = new LocRefPoint(location.subList(0, lrpPositions.get(0)), properties);
        checkedList.add(firstPoint);
        for (int index = 0; index < lrpPositions.size() - 1; ++index) {
            int from = lrpPositions.get(index);
            int to = lrpPositions.get(index + 1);
            LocRefPoint intermediatePoint = new LocRefPoint(location.subList(from, to), properties);
            checkedList.add(intermediatePoint);
        }
        int from = lrpPositions.get(lrpPositions.size() - 1);
        LocRefPoint lastPoint = new LocRefPoint(location.subList(from, location.size()), properties);
        checkedList.add(lastPoint);
        return checkedList;
    }


    public List<LocRefPoint> process(List<LocRefPoint> lrps) throws OpenLRProcessingException {

        if (properties.isAlternatePathCheckerConfigAvailable()) {
            List<LocRefPoint> checkedList = new ArrayList<>();
            for (int index = 0; index < lrps.size() - 1; ++index) {
                LocRefPoint lrp = lrps.get(index);
                Line firstLineOfNextLrp = lrps.get(index + 1).getLine();
                List<Line> oldRoute = lrp.getRoute();
                List<Line> connectedRoute = new ArrayList<>();
                for (Line line : oldRoute) {
                    connectedRoute.add(line);
                }
                connectedRoute.add(firstLineOfNextLrp);
                List<Integer> intermediateLrpPositions = determineNewIntermediatePoints(connectedRoute);
                if (intermediateLrpPositions.isEmpty()) {
                    checkedList.add(lrp);
                } else {
                    checkedList.addAll(createNewLRPs(lrp.getRoute(), intermediateLrpPositions));
                }
            }
            checkedList.add(lrps.get(lrps.size() - 1));
            return checkedList;
        } else {
            return lrps;
        }
    }
}
