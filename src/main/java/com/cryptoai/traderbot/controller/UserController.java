package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.request.ForgotPasswordTokenRequest;
import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.ForgotPasswordToken;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.VerificationCode;
import com.cryptoai.traderbot.request.ResetPasswordRequest;
import com.cryptoai.traderbot.response.ApiResponse;
import com.cryptoai.traderbot.response.AuthResponse;
import com.cryptoai.traderbot.service.EmailService;
import com.cryptoai.traderbot.service.ForgotPasswordService;
import com.cryptoai.traderbot.service.UserService;
import com.cryptoai.traderbot.service.VerificationCodeService;
import com.cryptoai.traderbot.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt, @PathVariable VerificationType verificationType) throws Exception {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if (verificationType == VerificationType.EMAIL) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        // TODO: Verify OTP by mobile

        return new ResponseEntity<>("Verification OTP string sent successfully", HttpStatus.CREATED);
    }

    @PatchMapping("/api/users/enable-tow-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuth(@RequestHeader("Authorization") String jwt, @PathVariable String otp) throws Exception {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType() == VerificationType.EMAIL ? verificationCode.getEmail() : verificationCode.getMobile();

        boolean isOtpValid = verificationCode.getOtp().equals(otp);

        if (isOtpValid) {
            User updatedUser = userService.enableTwoFactorAuth(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCode(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.ACCEPTED);
        }

        throw new Exception("Invalid OTP");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp( @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.generateOtp();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if (token == null) {
            token = forgotPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
        }

        if (req.getVerificationType() == VerificationType.EMAIL) {
            emailService.sendVerificationOtpEmail(user.getEmail(), otp);
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset OTP sent successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id, @RequestHeader("Authorization") String jwt, @RequestBody ResetPasswordRequest req) throws Exception {

        ForgotPasswordToken token = forgotPasswordService.findById(id);

        boolean isOtpValid = token.getOtp().equals(req.getOtp());

        if (isOtpValid) {
            userService.updatePassword(token.getUser(), req.getPassword());
            forgotPasswordService.deleteToken(token);
            ApiResponse response = new ApiResponse();
            response.setMessage("Password reset successfully");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        throw new Exception("Invalid OTP");
    }

}
