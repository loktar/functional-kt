package demo;

class CreatesUserJava {
    private final UserRepository userRepository;

    CreatesUserJava(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(NewUser newUser) {
        return userRepository.create(newUser);
    }
}
