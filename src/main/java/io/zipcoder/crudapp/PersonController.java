package io.zipcoder.crudapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        p = personService.createPerson(p);

        HttpHeaders httpHeaders = new HttpHeaders();
        URI newUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(p.getId())
                .toUri();

        httpHeaders.setLocation(newUri);

        log.info("[POST] " + p);
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.GET)
    ResponseEntity<?> getPerson(@PathVariable Long id) {
        HttpStatus status;

        Person person = personService.getPerson(id);
        status = (person != null) ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        log.info("[GET] " + person);
        return new ResponseEntity<>(person, status);
    }

    @RequestMapping(value = "/people/", method = RequestMethod.GET)
    ResponseEntity<List<Person>> getPersonList() {
        List<Person> people = personService.getPersonList();

        log.info("[GET] " + people.toString());
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updatePerson(@RequestBody Person p) {
        HttpStatus status;

        p = personService.updatePerson(p);
        status = (p != null) ? HttpStatus.OK : HttpStatus.CREATED;

        log.info("[PUT] " + p);
        return new ResponseEntity<>(status);
    }

    @RequestMapping(value = "/people/{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deletePerson(@PathVariable @Valid  Long id) {
        personService.deletePerson(id);

        log.info("[DELETE] Deleted " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
