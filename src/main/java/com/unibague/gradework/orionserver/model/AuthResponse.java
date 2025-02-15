package com.unibague.gradework.orionserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;

    /**
     * The authenticated user's details, which can be either StudentDTO or ActorDTO.
     */
    private Object user;
}
