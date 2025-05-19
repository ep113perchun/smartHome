package pe.app.smartHome.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.app.smartHome.model.Users;
import pe.app.smartHome.service.secureService.AppService;

@RestController
@RequestMapping("/api/user/registration/")
@AllArgsConstructor
public class userRegistrationController {
    AppService appService;

    @PostMapping("new-user")
    public String addUser(@RequestBody Users user){
        appService.addUser(user);
        return "user is saved";
    }
}
