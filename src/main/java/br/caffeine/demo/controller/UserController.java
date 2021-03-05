package br.caffeine.demo.controller;

import br.caffeine.demo.config.JavaCacheConfig;
import br.caffeine.demo.model.AppUser;
import br.caffeine.demo.service.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final JavaCacheConfig javaCacheConfig;

    @GetMapping(path = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return new ResponseEntity(userService.getUserByUsername(username), HttpStatus.OK);
    }

    @GetMapping(path = "cached/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsernameCached(@PathVariable String username) {
        return new ResponseEntity(userService.getUserByUsernameCached(username), HttpStatus.OK);
    }

    @PutMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserStatus(@PathVariable String username, @RequestParam("status") String status, @RequestParam("evict") boolean evict) {
        if(evict) {
            return new ResponseEntity(userService.updateUserStatusWithEvict(username, status), HttpStatus.OK);
        } else {
            return new ResponseEntity(userService.updateUserStatus(username, status), HttpStatus.OK);
        }
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsernameCached(@RequestBody AppUser appUser) {
        return new ResponseEntity(userService.saveNewUser(appUser), HttpStatus.CREATED);
    }

    @PostMapping("/cache")
    public ResponseEntity<?> postMessageInCache(@RequestParam String key, @RequestParam String value) {
        Cache<Object, Object> cache = this.javaCacheConfig.getCache(); // This will be saved for 20 seconds.
        cache.put("#" + key, value);
        return ResponseEntity.ok("Key: " + key + ", Value: " + value);
    }

    @GetMapping("/cache")
    public ResponseEntity<?> getMessageFromCache(@RequestParam String key){
        Cache<Object, Object> cache = this.javaCacheConfig.getCache();
        String result = (String) cache.getIfPresent("#" + key);


        return ResponseEntity.ok(result);
    }

}

