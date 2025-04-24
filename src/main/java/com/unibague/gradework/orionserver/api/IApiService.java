package com.unibague.gradework.orionserver.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface IApiService {
    Optional<JsonNode> findStudentByEmail(String email);
    Optional<JsonNode> findActorByEmail(String email);
}
