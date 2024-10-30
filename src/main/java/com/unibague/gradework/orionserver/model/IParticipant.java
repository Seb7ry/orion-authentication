package com.unibague.gradework.orionserver.model;

import java.util.List;

public interface IParticipant
{
    List<User> getParticipantPerson();
    String getParticipantRole();
}
