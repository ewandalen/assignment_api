package com.exercise.api.controller;

import com.exercise.api.helper.CSVHelper;
import com.exercise.api.helper.ResponseMessage;
import com.exercise.api.model.MedicalObservation;
import com.exercise.api.repository.MedicalObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MedicalObservationController {

    @Autowired
    private MedicalObservationRepository medicalObservationRepository;

    @GetMapping(value = "/medicalObservation")
    public List<MedicalObservation> get() {
        return medicalObservationRepository.findAll();
    }

    @GetMapping(value = "/medicalObservation/{code}")
    public ResponseEntity<MedicalObservation> getById(@PathVariable(value = "code") String code) {
        Optional<MedicalObservation> medicalObservation = medicalObservationRepository.findById(code);
        if(medicalObservation.isPresent()) {
            return new ResponseEntity<MedicalObservation>(medicalObservation.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/medicalObservation")
    public MedicalObservation post(@RequestBody MedicalObservation medicalObservation) {
        return medicalObservationRepository.save(medicalObservation);
    }

    @PutMapping(value = "/medicalObservation/{code}")
    public ResponseEntity<MedicalObservation> put(@PathVariable(value = "code") String code, @RequestBody MedicalObservation newMedicalObservation){
        Optional<MedicalObservation> oldMedicalObservation = medicalObservationRepository.findById(code);

        if (oldMedicalObservation.isPresent()) {
            MedicalObservation medicalObservation = oldMedicalObservation.get();
            medicalObservation.setSource(newMedicalObservation.getSource());
            medicalObservation.setCodeListCode(newMedicalObservation.getCodeListCode());
            medicalObservation.setCode(newMedicalObservation.getCode());
            medicalObservation.setDisplayValue(newMedicalObservation.getDisplayValue());
            medicalObservation.setLongDescription(newMedicalObservation.getLongDescription());
            medicalObservation.setFromDate(newMedicalObservation.getFromDate());
            medicalObservation.setToDate(newMedicalObservation.getToDate());
            medicalObservation.setSortingPriority(newMedicalObservation.getSortingPriority());
            medicalObservationRepository.save(medicalObservation);
            return new ResponseEntity<MedicalObservation>(medicalObservation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/medicalObservation/{code}")
    public ResponseEntity<MedicalObservation> delete(@PathVariable(value = "code") String code) {
        Optional<MedicalObservation> MedicalObservation = medicalObservationRepository.findById(code);
        if (MedicalObservation.isPresent()) {
            medicalObservationRepository.delete(MedicalObservation.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/medicalObservation")
    public ResponseEntity<MedicalObservation> deleteAll() {
        medicalObservationRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/uploadCSV")
    public ResponseEntity<ResponseMessage> post(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            if (file.getContentType().equals("text/csv")) {
                List<MedicalObservation> MedicalObservationList = CSVHelper.csvToMedicalObservation(file.getInputStream());
                medicalObservationRepository.saveAll(MedicalObservationList);
                message = "The file was uploaded successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch(Exception ex) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
        }
    }

    @GetMapping(value = "/exportCSV", produces = "text/csv")
    public ResponseEntity exportCSV() {
        try {
            return CSVHelper.medicalObservationsToCSV(medicalObservationRepository.findAll());
        } catch(Exception ex) {
            String message = "Could not generate the file!";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
        }
    }
}
