package com.cryptoai.traderbot.controller;

import com.cryptoai.traderbot.domain.VerificationType;
import com.cryptoai.traderbot.model.User;
import com.cryptoai.traderbot.model.VerificationCode;
import com.cryptoai.traderbot.service.EmailService;
import com.cryptoai.traderbot.service.UserService;
import com.cryptoai.traderbot.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);
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

        return new ResponseEntity<String>("Verification OTP string sent successfully", HttpStatus.OK);
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
            return new ResponseEntity<User>(updatedUser, HttpStatus.OK);
        }

        throw new Exception("Invalid OTP");
    }
}
