package demo;

import java.util.logging.Logger;

class UserControllerJava {
    private final CreatesUserJava createsUser;

    UserControllerJava(CreatesUserJava createsUser) {
        this.createsUser = createsUser;
    }

    public Response create(String email) {
        try {
            final User user = createsUser.execute(new NewUser(email));
            return new Response(200, "created: " + user);
        } catch (CreatesUserJava.UserAlreadyExistsException e) {
            return new Response(409, e.getMessage());
        } catch (CreatesUserJava.InvalidEmailException | CreatesUserJava.UserBannedException e) {
            return new Response(422, e.getMessage());
        }
    }
}

class CreatesUserJava {
    private final UserRepositoryJava userRepository;
    private final NewUserValidatorJava newUserValidator;
    private final Logger logger;

    CreatesUserJava(
            UserRepositoryJava userRepository,
            NewUserValidatorJava newUserValidator,
            Logger logger
    ) {
        this.userRepository = userRepository;
        this.newUserValidator = newUserValidator;
        this.logger = logger;
    }

    public User execute(NewUser newUser) throws InvalidEmailException, UserAlreadyExistsException, UserBannedException {
        if (!newUserValidator.validate(newUser)) {
            throw new InvalidEmailException(newUser);
        }
        if (!userRepository.isNotBanned(newUser.getEmail())) {
            throw new UserBannedException(newUser);
        }

        try {
            final User createdUser = userRepository.create(newUser);
            logger.info(String.format("Created user <%s> with email <%s>", createdUser.getId(), createdUser.getEmail()));
            return createdUser;
        } catch (UserAlreadyExistsException e) {
            logger.warning(String.format("Failed to create user with email <%s>: %s", e.getNewUser().getEmail(), e.getClass().getSimpleName()));
            throw e;
        }
    }

    interface UserRepositoryJava {
        User create(NewUser newUser) throws UserAlreadyExistsException;

        boolean isNotBanned(String email);
    }

    interface NewUserValidatorJava {
        boolean validate(NewUser newUser);
    }

    public class UserAlreadyExistsException extends Exception {
        private final NewUser newUser;

        public UserAlreadyExistsException(NewUser newUser) {
            this.newUser = newUser;
        }

        public NewUser getNewUser() {
            return newUser;
        }
    }

    public class InvalidEmailException extends Exception {
        public InvalidEmailException(NewUser newUser) {
        }
    }

    public class UserBannedException extends Exception {
        public UserBannedException(NewUser newUser) {
        }
    }
}
