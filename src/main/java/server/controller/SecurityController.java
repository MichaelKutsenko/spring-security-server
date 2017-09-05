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

    private final UserRepository userRepository;
    private final EncryptService encryptService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public SecurityController(UserRepository userRepository, EncryptService encryptService) {
        this.userRepository = userRepository;
        this.encryptService = encryptService;
    }

    @GetMapping(value = "/user")
    public ResponseEntity sayHello() {
        server.domain.User user = new server.domain.User();
        user.setUserName("user");
        user.setPswrd("pswrd");
//        userRepository.save(user);

        return new ResponseEntity("user saved", HttpStatus.OK);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity verify(@RequestBody VerificationRequestDto loginFormDto) {
        if (!isCorrectClient(loginFormDto)) return new ResponseEntity(HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByUserName(loginFormDto.getUsername());
//        if (user == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        //hide password
//        user.setPswrd(null);
        return new ResponseEntity(user, HttpStatus.OK);
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
