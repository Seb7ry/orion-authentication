package com.unibague.gradework.orionauth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.unibague.gradework.orionauth.user.models.Actor;
import com.unibague.gradework.orionauth.user.models.Student;

public interface IApiMapperService {
    Student toStudent(JsonNode studentNode, String imageUrl);
    Actor toActor(JsonNode actorNode, String imageUrl);
}
