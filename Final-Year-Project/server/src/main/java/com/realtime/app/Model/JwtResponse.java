package com.realtime.app.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class JwtResponse {
    private String message;
    private Status status;
    private String exceptionType;
    private String jwt;
    private Jws<Claims> jws;
    private PlayerModel user;

    public enum Status {
        SUCCESS, ERROR
    }

    public JwtResponse() {
    }

    public JwtResponse(String jwt) {
        this.jwt = jwt;
        this.status = Status.SUCCESS;
    }

    public JwtResponse(Jws<Claims> jws) {
        this.jws = jws;
        this.status = Status.SUCCESS;
    }
}
