package com.unibague.gradework.orionserver.user.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ActorLogDTO extends UserLogDTO {
    private String position;
}
