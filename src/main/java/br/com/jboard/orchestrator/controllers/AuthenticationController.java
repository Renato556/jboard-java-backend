package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.dtos.AuthenticationDTO;
import br.com.jboard.orchestrator.models.dtos.ChangePasswordDTO;
import br.com.jboard.orchestrator.models.dtos.LoginResponseDTO;
import br.com.jboard.orchestrator.models.dtos.RegisterDTO;
import br.com.jboard.orchestrator.services.AuthenticationService;
import br.com.jboard.orchestrator.services.TokenService;
import br.com.jboard.orchestrator.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final AuthUtils authUtils;

    public AuthenticationController(AuthenticationManager authenticationManager, AuthenticationService authenticationService, TokenService tokenService, AuthUtils authUtils){
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
        this.authUtils = authUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        authenticationService.registerUser(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity updatePassword(@RequestBody @Valid ChangePasswordDTO data, HttpServletRequest request) {
        var username = authUtils.getUsernameFromRequest(request);
        authenticationService.updatePassword(data, username);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity deleteAccount(HttpServletRequest request) {
        var username = authUtils.getUsernameFromRequest(request);
        authenticationService.deleteAccount(username);

        return ResponseEntity.ok().build();
    }
}
