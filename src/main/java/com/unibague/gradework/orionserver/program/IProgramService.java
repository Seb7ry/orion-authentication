package com.unibague.gradework.orionserver.program;

public interface IProgramService {
    Program getProgramByName(String programName);
    Program createProgram(Program program);
}
