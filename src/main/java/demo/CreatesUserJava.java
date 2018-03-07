package demo;

class UserControllerJava {
    private final CreatesUserJava createsUser;

    UserControllerJava(CreatesUserJava createsUser) {
        this.createsUser = createsUser;
    }

    public Response create(String email) {
        try {
            final User user = createsUser.execute(new NewUser(email));
            return new Response(200, "created: " + user);
        } catch (
                CreatesUserJava.UserAlreadyExistsException |
                CreatesUserJava.InvalidEmailException |
                CreatesUserJava.UserBannedException e
        ) {
            return new Response(400, e.getMessage());
        }
    }
}

class CreatesUserJava {
    private final UserRepositoryJava userRepository;
    private final NewUserValidatorJava newUserValidator;

    CreatesUserJava(
            UserRepositoryJava userRepository,
            NewUserValidatorJava newUserValidator
    ) {
        this.userRepository = userRepository;
        this.newUserValidator = newUserValidator;
    }

    public User execute(NewUser newUser) throws InvalidEmailException, UserAlreadyExistsException, UserBannedException {
        if (!newUserValidator.validate(newUser)) {
            throw new InvalidEmailException(newUser);
        }
        if (!userRepository.isNotBanned(newUser.getEmail())) {
            throw new UserBannedException(newUser);
        }
        return userRepository.create(newUser);
    }

    interface UserRepositoryJava {
        User create(NewUser newUser) throws UserAlreadyExistsException;

        boolean isNotBanned(String email);
    }

    interface NewUserValidatorJava {
        boolean validate(NewUser newUser);
    }

    public class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(NewUser newUser) {
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
