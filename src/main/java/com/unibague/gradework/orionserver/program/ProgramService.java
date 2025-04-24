package com.unibague.gradework.orionserver.program;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProgramService implements IProgramService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String PROGRAM_SERVICE_URL = "http://localhost:8093/service/program";

    @Override
    public Program getProgramByName(String programName) {
        try {
            String url = PROGRAM_SERVICE_URL + "/name/" + programName;
            return restTemplate.getForObject(url, Program.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Programa no encontrado: " + programName);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el programa: " + e.getMessage(), e);
        }
    }

    @Override
    public Program createProgram(Program program) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Program> request = new HttpEntity<>(program, headers);

            ResponseEntity<Program> response = restTemplate.postForEntity(PROGRAM_SERVICE_URL, request, Program.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el programa: " + e.getMessage(), e);
        }
    }
}
