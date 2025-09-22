package com.unibague.gradework.orionauth.program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ProgramService implements IProgramService {

    private static final Logger log = LoggerFactory.getLogger(ProgramService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${program.service.url}")
    private String programServiceUrl;

    @Override
    public Program getProgramByName(String programName) {
        try {
            String url = programServiceUrl + "/name/" + programName;
            log.info("Consultando programa con nombre: ", programName);
            return restTemplate.getForObject(url, Program.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Programa no encontrado: ", programName);
            throw new RuntimeException("Programa no encontrado: " + programName);
        } catch (Exception e) {
            log.error("Error al obtener el programa: ", programName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el programa: " + e.getMessage(), e);
        }
    }

    @Override
    public Program createProgram(Program program) {
        try {
            HttpEntity<Program> request = new HttpEntity<>(program);

            log.info("Creando programa: ", program.getProgramName());
            ResponseEntity<Program> response = restTemplate.postForEntity(programServiceUrl, request, Program.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                log.info("Programa creado exitosamente: ", response.getBody().getProgramName());
                return response.getBody();
            } else {
                log.warn("Fallo al crear el programa. Código de estado: ", response.getStatusCode());
                throw new RuntimeException("No se pudo crear el programa.");
            }
        } catch (Exception e) {
            log.error("Error al crear el programa: ", program.getProgramName(), e.getMessage(), e);
            throw new RuntimeException("Error al crear el programa: " + e.getMessage(), e);
        }
    }
}
