package com.cineconnections.api.dto.graph;

public record GraphEdge(

        String id,

        String source,

        String target,

        GraphEdgeType type,

        String characterName,

        String job

) {
}
