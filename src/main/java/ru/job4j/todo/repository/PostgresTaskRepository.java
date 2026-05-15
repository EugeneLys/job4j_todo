package ru.job4j.todo.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresTaskRepository implements TaskRepository {
    private final SessionFactory sf;

    public PostgresTaskRepository(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Task save(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.persist(task);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return task;
    }

    @Override
    public Optional<Task> findById(int id) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Optional<Task> result = session.createSelectionQuery("From Task t where t.id = :fId",
                            Task.class)
                    .setParameter("fId", id)
                    .uniqueResultOptional();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean update(Task task) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int result = session.createMutationQuery(
                            "UPDATE Task SET name = :fName, description = :fDescription, "
                                    + "done = :fDone WHERE id = :fId")
                    .setParameter("fName", task.getName())
                    .setParameter("fId", task.getId())
                    .setParameter("fDescription", task.getDescription())
                    .setParameter("fDone", task.isDone())
                    .executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean done(int id) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int result = session.createMutationQuery(
                            "UPDATE Task SET done = :fDone WHERE id = :fId")
                    .setParameter("fId", id)
                    .setParameter("fDone", true)
                    .executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public Collection<Task> findAll() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            List<Task> result = session.createSelectionQuery("from Task", Task.class)
                    .getResultList();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean delete(int id) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            int result = session.createMutationQuery(
                            "DELETE Task WHERE id = :fId")
                    .setParameter("fId", id)
                    .executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public Collection<Task> findByDone(boolean done) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            List<Task> result = session.createSelectionQuery(
                            "from Task t where t.done = :fDone order by t.id", Task.class)
                    .setParameter("fDone", done)
                    .getResultList();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
