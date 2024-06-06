package org.example.blps_lab3_monolit.security.jaas;

import com.sun.security.auth.UserPrincipal;
import org.example.blps_lab3_monolit.security.userdetails.MyUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;

public class MyLoginModule implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    private String username;
    private boolean loginSucceeded;

    @Override
    public void initialize(
            Subject subject,
            CallbackHandler callbackHandler,
            Map<String, ?> sharedState,
            Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.userDetailsService = (UserDetailsService) options.get("userDetailsService");
        this.passwordEncoder = (PasswordEncoder) options.get("passwordEncoder");
    }

    @Override
    public boolean login() throws LoginException{
        final NameCallback nameCallback = new NameCallback("username");
        final PasswordCallback passwordCallback = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
            username = nameCallback.getName();
            final String password = String.valueOf(passwordCallback.getPassword());
            MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                throw new BadCredentialsException("Unknown user " + username);
            }
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Bad password");
            }
            loginSucceeded = true;

        } catch (BadCredentialsException | UnsupportedCallbackException e) {
            loginSucceeded = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loginSucceeded;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        if (username == null) {
            throw new LoginException("Username is null during the commit");
        }
        final var principal = new UserPrincipal(username);
        subject.getPrincipals().add(principal);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        return false;
    }
}
