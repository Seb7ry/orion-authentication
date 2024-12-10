package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "students")
public class Student extends User {
    private Long studentID;
    private boolean status;
    private String semester;
    private String category;
    private String program;
}
