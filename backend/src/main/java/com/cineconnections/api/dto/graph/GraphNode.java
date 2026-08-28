package com.cineconnections.api.dto.graph;

public record GraphNode(

        String id,

        GraphNodeType type,

        String label,

        String imagePath,

        Long tmdbId

) {
}
