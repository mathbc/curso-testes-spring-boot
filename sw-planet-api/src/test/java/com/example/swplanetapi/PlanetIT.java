package com.example.swplanetapi;

import static com.example.swplanetapi.common.PlanetConstants.ALDERAAN;
import static com.example.swplanetapi.common.PlanetConstants.PLANET;
import static com.example.swplanetapi.common.PlanetConstants.TATOOINE;
import static com.example.swplanetapi.common.PlanetConstants.YAVINIV;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.example.swplanetapi.domain.Planet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/import_planets.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/remove_planets.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createPlanet_ReturnsCreated() {
        ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PLANET, Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody()).isNotNull();
        assertThat(sut.getBody().getId()).isNotNull();
        assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
        assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
    }

    @Test
    public void getPlanet_RetunsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getName()).isEqualTo(TATOOINE.getName());
        assertThat(sut.getBody().getTerrain()).isEqualTo(TATOOINE.getTerrain());
        assertThat(sut.getBody().getClimate()).isEqualTo(TATOOINE.getClimate());
    }

    @Test
    public void getPlanetByName_ReturnsPlanet() {
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/" + ALDERAAN.getName(), Planet.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody().getName()).isEqualTo(ALDERAAN.getName());
        assertThat(sut.getBody().getTerrain()).isEqualTo(ALDERAAN.getTerrain());
        assertThat(sut.getBody().getClimate()).isEqualTo(ALDERAAN.getClimate());
    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {
        ResponseEntity<List> sut = restTemplate.getForEntity("/planets", List.class);
        List<Planet> planets = objectMapper.convertValue(sut.getBody(), new TypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(planets).hasSize(3);
        assertThat(planets.get(0)).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ByClimate_ReturnsPlanets() {
        ResponseEntity<List> sut = restTemplate.getForEntity("/planets?climate=arid", List.class);
        List<Planet> planets = objectMapper.convertValue(sut.getBody(), new TypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(planets).hasSize(1);
        assertThat(planets.get(0)).isEqualTo(TATOOINE);
    }

    @Test
    public void listPlanets_ByTerrain_ReturnsPlanets() {
        ResponseEntity<List> sut = restTemplate.getForEntity("/planets?terrain=gass", List.class);
        List<Planet> planets = objectMapper.convertValue(sut.getBody(), new TypeReference<List<Planet>>() {});

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(planets).hasSize(2);
        assertThat(planets.get(0)).isEqualTo(ALDERAAN);
        assertThat(planets.get(1)).isEqualTo(YAVINIV);
    }

    @Test
    public void removePlanet_ReturnsNoContent() {
        ResponseEntity<Void> sut = restTemplate.exchange("/planets/1", HttpMethod.DELETE, null, Void.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
