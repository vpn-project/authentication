package com.example.authentication.controller;


import com.example.authentication.dto.UpdateUserRoleDto;
import com.example.authentication.entity.User;
import com.example.authentication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/user_list")
    public ResponseEntity<?> getUserList(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(adminService.getUserList(offset, limit));
    }

    @PostMapping("/create_user")
    public ResponseEntity<?> createNewUser(@RequestBody User user) {
        return new ResponseEntity<>(adminService.createNewUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/update_role")
    public ResponseEntity<?> updateRole(@RequestBody UpdateUserRoleDto updateUserRoleDto) {
        return ResponseEntity.ok(adminService.updateRole(updateUserRoleDto));
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody String username) {
        return ResponseEntity.ok(adminService.deleteUser(username));
    }

}
