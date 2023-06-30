package ru.practicum.main.admin.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UsersRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUsersService {
    private final UsersRepository usersRepository;

    public AdminUsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Collection<UserDto> getUsers() {
        Collection<User> users = usersRepository.findAll();
        log.info("Запрошено {} пользователей", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = usersRepository.save(UserMapper.toUser(newUserRequest));
        log.info("Сохранён пользователь {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    public void deleteUser(Long catId) {
        usersRepository.deleteById(catId);
        log.info("Удалён пользователь {}", catId);
    }
}
