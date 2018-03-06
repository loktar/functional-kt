package demo;

class UserControllerJava {
    private final CreatesUserJava createsUser;

    UserControllerJava(CreatesUserJava createsUser) {
        this.createsUser = createsUser;
    }

    public Response create(String email) {
        if (createsUser.execute(new NewUser(email))) {
            return new Response(200, "ok");
        }
        else {
            return new Response(400, "oops");
        }
    }
}

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
