package com.unibague.gradework.orionserver.user;

import com.unibague.gradework.orionserver.user.models.Actor;
import com.unibague.gradework.orionserver.user.models.Role;
import com.unibague.gradework.orionserver.user.models.Student;
import com.unibague.gradework.orionserver.user.models.UserLogDTO;

public interface IUserService {
    UserLogDTO getUserByEmail(String email);
    void createStudent(Student student);
    void createActor(Actor actor);

    Role findRoleByName(String roleName);
    Role createRole(Role role);
}
