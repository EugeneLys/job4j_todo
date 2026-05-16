package ru.job4j.todo.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.util.Optional;

@Repository
public class PostgresUserRepository implements UserRepository {
    private final SessionFactory sf;

    public PostgresUserRepository(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Optional<User> save(User user) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            var result = session.createSelectionQuery("From User u where u.login = :fLogin, "
                                    + "u.password = :fPassword",
                            User.class)
                    .setParameter("fLogin", login)
                    .setParameter("fPassword", password)
                    .uniqueResultOptional();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
