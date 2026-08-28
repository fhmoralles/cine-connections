package com.cineconnections.api.dto.graph;

import java.util.List;

public record GraphResponse(

        GraphNode root,

        List<GraphNode> nodes,

        List<GraphEdge> edges

) {
}
