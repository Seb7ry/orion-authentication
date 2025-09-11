package com.unibague.gradework.orionauth.user;

import com.unibague.gradework.orionauth.user.models.Actor;
import com.unibague.gradework.orionauth.user.models.Role;
import com.unibague.gradework.orionauth.user.models.Student;
import com.unibague.gradework.orionauth.user.models.UserLogDTO;

public interface IUserService {
    UserLogDTO getUserByEmail(String email);
    void createStudent(Student student);
    void createActor(Actor actor);
    Role findRoleByName(String roleName);
    Role createRole(Role role);
}
