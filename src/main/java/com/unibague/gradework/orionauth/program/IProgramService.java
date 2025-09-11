package com.unibague.gradework.orionauth.program;

public interface IProgramService {
    Program getProgramByName(String programName);
    Program createProgram(Program program);
}
