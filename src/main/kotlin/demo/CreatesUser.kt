package demo

class UserController(private val createsUser: CreatesUser) {
    fun create(email: String): Response {
        val result = createsUser.execute(NewUser(email))
        return when (result) {
            is Result.Success -> Response(200, "ok")
            is Result.Failure -> Response(400, "oops")
        }
    }
}

data class Response(val statusCode: Int, val message: String)


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
