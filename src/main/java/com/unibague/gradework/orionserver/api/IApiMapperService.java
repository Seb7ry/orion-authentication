package com.unibague.gradework.orionserver.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionserver.user.models.Actor;
import com.unibague.gradework.orionserver.user.models.Student;

public interface IApiMapperService {
    Student toStudent(JsonNode studentNode, String imageUrl);
    Actor toActor(JsonNode actorNode, String imageUrl);
}
