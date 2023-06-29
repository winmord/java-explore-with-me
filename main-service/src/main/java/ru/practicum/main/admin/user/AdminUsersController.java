package ru.practicum.main.admin.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {
    private final AdminUsersService adminUsersService;

    public AdminUsersController(AdminUsersService adminUsersService) {
        this.adminUsersService = adminUsersService;
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        return adminUsersService.getUsers();
    }

    @PostMapping
    public UserDto addUser(NewUserRequest newUserRequest) {
        return adminUsersService.addUser(newUserRequest);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestParam(name = "catId") Integer catId) {
        adminUsersService.deleteUser(catId);
    }
}
