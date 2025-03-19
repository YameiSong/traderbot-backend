package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.config.JwtProvider;
import com.cryptoai.traderbot.model.TwoFactorOTP;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.repository.UserRepository;
import com.cryptoai.traderbot.response.AuthResponse;
import com.cryptoai.traderbot.service.CustomUserDetailsService;
import com.cryptoai.traderbot.service.EmailService;
import com.cryptoai.traderbot.service.TwoFactorOtpService;
import com.cryptoai.traderbot.service.WatchlistService;
import com.cryptoai.traderbot.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User emailExists = userRepository.findByEmail(user.getEmail());

        if (emailExists != null) {
            throw new Exception("Email is already used by another account");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());

        User savedUser = userRepository.save(newUser);

        watchlistService.createWatchlist(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("User registered successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        if (user.getTwoFactorAuth().isEnabled()) {
            /*
            * === Initial Request: ===
            * - User credentials (username and password) are validated, and authentication is partially established.
            * - JWT is generated but not exposed to the client yet.
            * - OTP is generated and associated with the user's 2FA session.
            * === Response after Initial Request: ===
            * - The response includes session indicating an ongoing 2FA session, prompting the client to ask the user for the OTP.
            * - The status remains false or omitted, indicating further steps are needed.
            * - No jwt is returned at this point.
            * */
             AuthResponse response = new AuthResponse();
             response.setTwoFactorAuthEnabled(true);
             response.setMessage("Two factor authentication enabled");

             String otp = OtpUtils.generateOtp();

             User authUser = userRepository.findByEmail(username);
             TwoFactorOTP oldTwoFactorOtp = twoFactorOtpService.findByUser(authUser.getId());
             if (oldTwoFactorOtp != null) {
                 twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
             }

             TwoFactorOTP newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt);

             emailService.sendVerificationOtpEmail(username, otp);

             response.setSession(newTwoFactorOtp.getId());
             return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("User logged in successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        System.out.println(userDetails.getUsername() + " " + userDetails.getPassword());

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifyOtp(@PathVariable String otp, @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if (twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse response = new AuthResponse();
            response.setJwt(twoFactorOTP.getJwt());
            response.setStatus(true);
            response.setTwoFactorAuthEnabled(true);
            response.setMessage("Two factor authentication verified successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        throw new Exception("Invalid OTP");
    }

}
