package br.caffeine.demo.service.impl;

import br.caffeine.demo.model.AppUser;
import br.caffeine.demo.repository.UserRepository;
import br.caffeine.demo.service.UserService;
import br.caffeine.demo.utils.CacheConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public AppUser getUserByUsername(String username) {
        simulateWaitTime(); // Testa o cache
        Optional<AppUser> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Cacheable(value = CacheConstants.DATABASE_CACHE)
    public AppUser getUserByUsernameCached(String username) {
        simulateWaitTime();
        Optional<AppUser> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public AppUser saveNewUser(AppUser appUser) {
        Optional<AppUser> userOptional = userRepository.findByUsername(appUser.getUsername());

        if (userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
        } else {
            return userRepository.save(appUser);
        }
    }

    @Override
    public AppUser updateUserStatus(String username, String status) {
        Optional<AppUser> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            AppUser appUser = userOptional.get();
            appUser.setStatus(status);
            return userRepository.save(appUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @CacheEvict(value = CacheConstants.DATABASE_CACHE, key = "#username")
    public AppUser updateUserStatusWithEvict(String username, String status) {
        Optional<AppUser> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            AppUser appUser = userOptional.get();
            appUser.setStatus(status);
            return userRepository.save(appUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void simulateWaitTime() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
