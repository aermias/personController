package io.zipcoder.crudapp.controllers;

import io.zipcoder.crudapp.Person;
import io.zipcoder.crudapp.services.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class PersonController {
    private static final Logger log = LoggerFactory.getLogger(PersonController.class);
    private PersonService personService;

    @Autowired
    public PersonController(PersonService personService) { this.personService = personService; }

    @RequestMapping(value = "/people", method = RequestMethod.POST)
    ResponseEntity<?> createPerson(@RequestBody @Valid Person p) {
        HttpStatus status = HttpStatus.CREATED;
        String response;

        p = personService.createPerson(p);

        HttpHeaders httpHeaders = new HttpHeaders();
        URI newUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(p.getId())
                .toUri();

        httpHeaders.setLocation(newUri);

        response = "Successfully created person with ID " + p.getId();

        log.info("[POST] " + p);
        return new ResponseEntity<>(response, httpHeaders, status);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.GET)
    ResponseEntity<?> getPerson(@PathVariable @Valid Long id) throws MethodArgumentTypeMismatchException, NumberFormatException {
        HttpStatus status;
        Object response;

        Person person = personService.getPerson(id);

        if (person != null) {
            // person does exist
            status = HttpStatus.OK;
            response = person;
        } else {
            // person does not exist
            status = HttpStatus.NOT_FOUND;
            response = "Person does not exist.";
        }

        log.info("[GET] " + person);
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(value = "/people", method = RequestMethod.GET)
    ResponseEntity<?> getPersonList() {
        HttpStatus status;
        Object response;

        List<Person> people = personService.getPersonList();

        if (!people.isEmpty()) {
            // existing people
            status = HttpStatus.OK;
            response = people;
        } else {
            // list is empty
            status = HttpStatus.NOT_FOUND;
            response = "There are no existing people objects.";
        }

        log.info("[GET] " + people.toString());
        return new ResponseEntity<>(people, status);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updatePerson(@RequestBody Person p, @PathVariable Long id) {
        HttpStatus status;
        String response;

        Person old_value = personService.getPerson(id);
        personService.updatePerson(p);

        if (old_value != null) {
            // person did exist prior
            status = HttpStatus.OK;
            response = "Successfully updated person with ID " + id + ".";
        } else {
            // person did not exist prior
            status = HttpStatus.CREATED;
            response = "Successfully created new person with ID " + id + ".";
        }

        log.info("[PUT] " + p);
        return new ResponseEntity<>(response, status);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deletePerson(@PathVariable Long id) {
        HttpStatus status = HttpStatus.NO_CONTENT;
        String response = "Deleted person with ID " + id + ".";

        personService.deletePerson(id);
        log.info("[DELETE] Deleted ID-" + id);

        return new ResponseEntity<>(response, status);
    }

    // TODO | HttpRequestMethodNotSupportedException does not work !
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    @ResponseBody
//    ResponseEntity<?> httpRequestMethodNotSupportedExceptionHandler(Exception ex, HttpServletRequest req) {
//        String response = "Method is not supported for this endpoint!";
//        log.info("[" + req.getMethod().toUpperCase() + "] Invalid method (" + req.getRequestURI() + ")");
//
//        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
//    }

    // Bad endpoint href
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    ResponseEntity<?> typeMismatchExceptionHandler(Exception ex, HttpServletRequest req) {
        String response = "Unacceptable Path Name!";
        log.info("[" + req.getMethod().toUpperCase() + "] Type Mismatch (" + req.getRequestURI() + ")");

        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    // Bad JSON format when use POST/PUT
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ResponseEntity<?> httpMessageNotReadableExceptionHandler(Exception ex, HttpServletRequest req) {
        String response = "Body dontent was not able to be read!";
        log.info("[" + req.getMethod().toUpperCase() + "] Unreadable Content (" + req.getRequestURI() + ")");

        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    // No data at endpoint
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseBody
    ResponseEntity<?> emptyResultDataAccessExceptionHandler(Exception ex, HttpServletRequest req) {
        String response = "There was no data at that endpoint!";
        log.info("[" + req.getMethod().toUpperCase() + "] No data to retrieve from endpoint (" + req.getRequestURI() + ")");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
