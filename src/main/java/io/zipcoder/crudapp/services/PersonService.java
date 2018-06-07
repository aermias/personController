package io.zipcoder.crudapp.services;

import io.zipcoder.crudapp.Person;
import io.zipcoder.crudapp.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(Person p) {
        return personRepository.save(p);
    }

    public Person getPerson(Long id) {
        return personRepository.findOne(id);
    }

    public List<Person> getPersonList() {
        List<Person> list = new ArrayList<>();
        personRepository.findAll().forEach(list::add);

        return list;
    }

    public Person updatePerson(Person p) {
        return personRepository.save(p);
    }

    public void deletePerson(Long id) {
        personRepository.delete(id);
    }
}
