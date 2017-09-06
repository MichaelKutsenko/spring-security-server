package server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import server.domain.User;
import server.dto.VerificationRequestDto;
import server.repository.UserRepository;
import server.service.EncryptService;

/**
 * Created by Mocart on 04-Sep-17.
 */
@Controller
public class SecurityController {
    @Value("${client.id}")
    private String ID;
    @Value("${client.key}")
    private String KEY;

    private final UserRepository userRepository;
    private final EncryptService encryptService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public SecurityController(UserRepository userRepository, EncryptService encryptService) {
        this.userRepository = userRepository;
        this.encryptService = encryptService;
    }

    @GetMapping(value = "/user")
    public ResponseEntity sayHello() {
        return new ResponseEntity("verification service works", HttpStatus.OK);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity verify(@RequestBody VerificationRequestDto loginFormDto) {
        prepareDb();
        if (!isCorrectClient(loginFormDto)) return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByUserName(loginFormDto.getUsername());
        if (user != null) {
            user.setPswrd(encryptService.decrypt(user.getPswrd(), KEY));
        }
        return new ResponseEntity(user, HttpStatus.OK);
    }

    private void prepareDb() {
        User user = userRepository.findByUserName("user");
        if (user == null) {
            user = new User();
            user.setUserName("user");
            user.setRole("USER");
            user.setPswrd(encryptService.encrypt("user", KEY));
            userRepository.save(user);
        }
        User user1 = userRepository.findByUserName("manager");
        if (user1 == null) {
            user1 = new User();
            user1.setUserName("manager");
            user1.setRole("MANAGER");
            user1.setPswrd(encryptService.encrypt("manager", KEY));
            userRepository.save(user1);
        }
        User user3 = userRepository.findByUserName("admin");
        if (user3 == null) {
            user3 = new User();
            user3.setUserName("admin");
            user3.setRole("ADMIN");
            user3.setPswrd(encryptService.encrypt("admin", KEY));
            userRepository.save(user3);
        }
    }

    private boolean isCorrectClient(VerificationRequestDto loginFormDto) {
        String hashId = encryptService.encrypt(loginFormDto.getId(), loginFormDto.getKey());

        logger.debug("verifying id: " + loginFormDto.getId());
        if (ID.equals(hashId)) {
            logger.debug("verification finished with success.");
            return true;
        }

        logger.error("verification failed: " + loginFormDto.getId());
        return false;
    }
}
