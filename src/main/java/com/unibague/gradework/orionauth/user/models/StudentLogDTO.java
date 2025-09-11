package com.unibague.gradework.orionauth.user.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StudentLogDTO extends UserLogDTO {
    private String studentID;
    private boolean status;
    private String semester;
}