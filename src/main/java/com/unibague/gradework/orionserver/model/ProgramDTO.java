package com.unibague.gradework.orionserver.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an academic program.
 * This class encapsulates basic information about a program.
 */
@Data
@RequiredArgsConstructor
public class ProgramDTO {

    /**
     * The unique identifier of the program.
     */
    private String idProgram;

    /**
     * The name of the academic program.
     */
    private String programName;

    /**
     * Constructs a new {@code ProgramDTO} instance with the specified attributes.
     *
     * @param idProgram   The unique identifier of the program.
     * @param programName The name of the academic program.
     */
    public ProgramDTO(String idProgram, String programName) {
        this.idProgram = idProgram;
        this.programName = programName;
    }
}
