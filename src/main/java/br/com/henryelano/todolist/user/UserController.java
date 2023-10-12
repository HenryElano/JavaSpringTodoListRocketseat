package br.com.henryelano.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel oUser) {
        oUser.setPassword(BCrypt.withDefaults().hashToString(12, oUser.getPassword().toCharArray()));
        if(this.userExist(oUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário Já existe!");
        }
        return ResponseEntity.ok().body(this.userRepository.save(oUser));
    } 

    /**
     * Retorna se o usuário já está cadastrado.
     * @param UserModel oUser
     * @return boolean
     */
    private boolean userExist(UserModel oUser) {
        return this.userRepository.findByUsername(oUser.getUsername()) != null ? true : false;
    }

}
