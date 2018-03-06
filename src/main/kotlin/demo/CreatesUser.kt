package demo

class CreatesUser(
        private val userRepository: UserRepository
) {
    fun execute(newUser: NewUser): Result<User, CreateUserFailure> {
        return userRepository.create(newUser)
    }
}



interface UserRepository {
    fun create(newUser: NewUser): Result<User, CreateUserFailure>
}

data class NewUser(val email: String)
data class User(val id: Long, val email: String)

sealed class CreateUserFailure(val email: String) {
    class UserAlreadyExists(email: String): CreateUserFailure(email)
}
