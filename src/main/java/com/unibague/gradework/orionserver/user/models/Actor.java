package com.unibague.gradework.orionserver.user.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Actor extends User {
    private String position;
}