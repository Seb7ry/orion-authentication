package com.unibague.gradework.orionserver.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Student extends User
{
    private boolean status;
    private int semester;
    private String category;
    private String program;
}
