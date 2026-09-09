package com.cineconnections.api.dto.person;

import java.util.List;

public record PersonSearchResponse(
        List<PersonSearchResult> results,
        boolean imported
) {
}
