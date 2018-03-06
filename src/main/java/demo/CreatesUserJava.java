package demo;

class CreatesUserJava {
    private final UserRepositoryJava userRepository;

    CreatesUserJava(UserRepositoryJava userRepository) {
        this.userRepository = userRepository;
    }

    public boolean execute(NewUser newUser) {
        return userRepository.create(newUser);
    }

    interface UserRepositoryJava {
        boolean create(NewUser newUser);
    }
}
