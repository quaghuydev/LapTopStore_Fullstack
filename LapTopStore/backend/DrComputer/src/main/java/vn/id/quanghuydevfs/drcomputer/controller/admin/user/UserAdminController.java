package vn.id.quanghuydevfs.drcomputer.controller.admin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.controller.auth.AuthenticationResponse;
import vn.id.quanghuydevfs.drcomputer.dto.user.DeleteUserRequest;
import vn.id.quanghuydevfs.drcomputer.dto.user.UserDto;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/management/users")
@CrossOrigin(origins = "http://localhost:3000")
//@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Optional<User>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletes")
    public ResponseEntity<Void> deleteAllUser(@RequestBody DeleteUserRequest request) {
        try {
            userService.deleteMultiple(request.getUserIds());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AuthenticationResponse> updateUser(@PathVariable Long id, @RequestBody UserDto user) {

        if (id != null) {
            return ResponseEntity.ok(userService.updateUserById(id, user));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}
