package com.exercise.api.repository;

import com.exercise.api.model.MedicalObservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalObservationRepository extends JpaRepository<MedicalObservation, String> {
}
