package com.unibague.gradework.orionserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibague.gradework.orionserver.model.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProgramService implements IProgramService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PROGRAM_SERVICE_URL = "http://localhost:8093/service/program";

    @Override
    public Program getProgramByName(String programName) {
        try {
            String url = "http://localhost:8093/service/program/name/" + programName;
            return restTemplate.getForObject(url, Program.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Programa no encontrado: " + programName);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el programa: " + e.getMessage());
        }
    }
}
