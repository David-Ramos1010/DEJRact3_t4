package com.davidramos1010.democrud.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.davidramos1010.democrud.models.Film;
import com.davidramos1010.democrud.models.Language;
import com.davidramos1010.democrud.service.FilmService;

@Controller 
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/films";
    }

    // READ
    @GetMapping("/films")
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Film> films = service.listar(page);
        model.addAttribute("page", films);
        return "films/index";
    }

    // CREATE (formulario)
    @GetMapping("/films/nuevo")
    public String nuevo(Model model) {
        Film film = new Film();
        film.setLanguage(new Language());
        model.addAttribute("film", film);
        model.addAttribute("idiomas", service.idiomas());
        return "films/form";
    }

    // UPDATE (formulario)
    @GetMapping("/films/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("film", service.buscar(id));
        model.addAttribute("idiomas", service.idiomas());
        return "films/form";
    }

    // CREATE / UPDATE (guardar)
    @PostMapping("/films/guardar")
    public String guardar(@ModelAttribute("film") Film film, Model model, RedirectAttributes ra) {
        try {
            service.guardar(film);
            ra.addFlashAttribute("ok", "Película guardada correctamente");
            return "redirect:/films";
        } catch (IllegalArgumentException e) {
            if (film.getLanguage() == null) film.setLanguage(new Language());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idiomas", service.idiomas());
            return "films/form";
        }
    }

    // DELETE
    @PostMapping("/films/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.eliminar(id);
            ra.addFlashAttribute("ok", "Película eliminada");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error",
                "No se puede eliminar: la película está usada en inventario, actores o categorías.");
        }
        return "redirect:/films";
    }
}
