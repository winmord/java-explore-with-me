package ru.practicum.main.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.user.model.User;

public interface UsersRepository extends JpaRepository<User, Long> {
}
