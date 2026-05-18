package ru.job4j.todo.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import java.util.Optional;

@Slf4j
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
            log.error("Failed to save user: {}", user, e);
        } finally {
            session.close();
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            var result = session.createSelectionQuery("From User u where u.login = :fLogin and "
                                    + "u.password = :fPassword",
                            User.class)
                    .setParameter("fLogin", login)
                    .setParameter("fPassword", password)
                    .uniqueResultOptional();
            System.out.println("User found: " + result.isPresent());
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            log.error("Failed to find user with such login and password");
        }
        return Optional.empty();
    }
}
