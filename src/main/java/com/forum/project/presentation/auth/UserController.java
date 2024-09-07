package com.forum.project.presentation.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> requestSignup(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(id);
    }
}
