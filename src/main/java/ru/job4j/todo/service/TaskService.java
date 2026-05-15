package ru.job4j.todo.service;

import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.Optional;

public interface TaskService {

    Task save(Task task);

    Optional<Task> findById(int id);

    boolean update(Task task);

    boolean done(int id);

    Collection<Task> findAll();

    boolean delete(int id);

    Collection<Task> findByDone(boolean done);
}
