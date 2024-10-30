package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
//@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Student extends User
{
    private Long idStudent;
    private boolean status;
    private int semester;
    private String category;
    private Program program;
}
