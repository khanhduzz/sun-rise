package com.fjb.sunrise.config.security;

import com.fjb.sunrise.enums.EStatus;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.UserRepository;
import com.fjb.sunrise.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhone(username);
        if (user == null) {
            throw new NotFoundException(username);
        }

        boolean isDisable = user.getStatus() != EStatus.ACTIVE;

        if (isDisable) {
            try {
                throw new Exception(Constants.ErrorCode.ACCOUNT_HAS_BEEN_BLOCKED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
        //    .password(user.getPassword())
        //    .roles(String.valueOf(user.getRole()))
        //    .disabled(isDisable)
        //    .build();

        return user;
    }
}
