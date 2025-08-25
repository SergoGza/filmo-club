package com.videoclub.filmoapp.film.repository;


import com.videoclub.filmoapp.film.domain.Film;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto:create-drop"
        }
)

class FilmDAOTest {

    @Autowired private FilmDAO filmDAO;

    @BeforeEach
    void beforeEach() {

        Film film1 = Film.builder().id(1L).title("Film 1").build();
        Film film2 = Film.builder().id(2L).title("Film 2").build();

        filmDAO.saveAll(List.of(film1, film2));

    }


    @Test
    void givenFilms_whenFindAll_thenReturnOk() {

        List<Film> films = filmDAO.findAll();

        Assertions.assertNotNull(films);
        Assertions.assertEquals(2, films.size());

    }

//    @Test
//    void givenFilms_whenFindByTitle_thenReturnOk() {
//
//        List<Film> filmOrFilms = filmDAO.findByTitleLike("%Film%");
//        Assertions.assertNotNull(filmOrFilms);
//        Assertions.assertEquals(2, filmOrFilms.size());
//
//        filmOrFilms = filmDAO.findByTitleLike("Film 1");
//        Assertions.assertEquals(1, filmOrFilms.size());
//
//
//    }

}