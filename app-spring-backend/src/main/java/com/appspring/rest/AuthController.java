package com.appspring.rest;

import com.appspring.entity.User;
import com.appspring.mapper.UserMapper;
import com.appspring.rest.dto.AuthRequest;
import com.appspring.rest.dto.UserRsDto;
import com.appspring.security.JwtTokenUtil;
import com.appspring.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Api(tags = "Авторизация")
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @PostMapping("/public/auth/login")
    public ResponseEntity<UserRsDto> login(@RequestBody @Validated AuthRequest request) {
        try {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    );
            Authentication authenticate = authenticationManager.authenticate(authentication);

            User user = (User) authenticate.getPrincipal();

//            userService.updateLastActivity(Instant.now(), user.getId());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,
                            jwtTokenUtil.generateAccessToken(user.getId(), user.getLogin()))
                    .body(userMapper.entityToRsDto(user));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //TODO: JWT token invalidation
    @PostMapping("/auth/logout")
    public void logout(HttpServletRequest request) {
        new SecurityContextLogoutHandler().logout(request, null, null);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
