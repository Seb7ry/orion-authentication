package com.unibague.gradework.orionserver.service;

import com.unibague.gradework.orionserver.model.Program;

public interface IProgramService {
    Program getProgramByName(String programName);
}
