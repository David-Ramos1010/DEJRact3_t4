package com.davidramos1010.democrud.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davidramos1010.democrud.models.Film;
import com.davidramos1010.democrud.models.Language;
import com.davidramos1010.democrud.repository.FilmRepository;
import com.davidramos1010.democrud.repository.LanguageRepository;

@Service 
public class FilmService {
        private final FilmRepository filmRepo;
    private final LanguageRepository languageRepo;

    public FilmService(FilmRepository filmRepo, LanguageRepository languageRepo) {
        this.filmRepo = filmRepo;
        this.languageRepo = languageRepo;
    }

    public Page<Film> listar(int page) {
        return filmRepo.findAll(PageRequest.of(page, 20, Sort.by("id")));
    }

    public Film buscar(Integer id) {
        return filmRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada: " + id));
    }

    public List<Language> idiomas() {
        return languageRepo.findAll();
    }

    @Transactional
    public Film guardar(Film film) {
        if (film.getTitle() == null || film.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (film.getLanguage() == null || film.getLanguage().getId() == null) {
            throw new IllegalArgumentException("Selecciona un idioma");
        }
        if (film.getRentalRate() != null && film.getRentalRate().signum() < 0) {
            throw new IllegalArgumentException("La tarifa no puede ser negativa");
        }
        if (film.getRentalDuration() == null) film.setRentalDuration(3);
        if (film.getRentalRate() == null) film.setRentalRate(new BigDecimal("4.99"));
        if (film.getRating() != null && film.getRating().isBlank()) film.setRating(null);

        Language idioma = languageRepo.findById(film.getLanguage().getId())
                .orElseThrow(() -> new IllegalArgumentException("El idioma no existe"));
        film.setLanguage(idioma);

        return filmRepo.save(film);
    }

    @Transactional
    public void eliminar(Integer id) {
        filmRepo.deleteById(id);
        filmRepo.flush();
    }
}
