package com.davidramos1010.democrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davidramos1010.democrud.models.Language;

public interface LanguageRepository extends JpaRepository<Language, Integer> {

}
