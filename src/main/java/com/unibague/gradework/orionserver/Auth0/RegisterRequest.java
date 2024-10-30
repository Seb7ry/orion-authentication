package com.unibague.gradework.orionserver.Auth0;

import com.unibague.gradework.orionserver.enumerator.TypeSex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest
{
    private String firstName;
    private String lastName;
    private int phone;
    private String email;
    private String image;
    private TypeSex sex;
    private String password;
}
