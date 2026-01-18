package com.example.repository;

import com.example.model.User;
import com.vcinsidedigital.webcore.annotations.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository
{
    private final Map<Long, User> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        // Dados iniciais
        save(new User(null, "Vinicius Cortez", "cotezvinicius881@gmail.com"));
        save(new User(null, "Maria Santos", "maria@gmail.com"));
        save(new User(null, "Pedro Costa", "pedrocosta@gmail.com"));
    }

    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        database.put(user.getId(), user);
        return user;
    }
}
