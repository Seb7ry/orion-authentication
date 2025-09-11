package com.unibague.gradework.orionauth.user.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    private String studentID;
    private boolean status;
    private String semester;
}