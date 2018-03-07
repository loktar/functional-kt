package demo

import demo.CreateUserFailure.*

class UserController(private val createsUser: CreatesUser) {
    fun create(email: String): Response {
        val result = createsUser.execute(NewUser(email))
        return when (result) {
            is Result.Success -> Response(200, "ok")
            is Result.Failure -> failureResponse(result.failure)
        }
    }

    private fun failureResponse(failure: CreateUserFailure): Response {
        return when (failure) {
            is UserAlreadyExists -> Response(400, "User already exists")
            is InvalidEmail -> Response(400, "Invalid email: ${failure.email}")
            is UserIsBanned -> Response(400, "User is banned: ${failure.email}")
        }
    }
}

data class Response(val statusCode: Int, val message: String)


class CreatesUser(
        private val userRepository: UserRepository,
        private val newUserValidator: NewUserValidator
) {
    fun execute(newUser: NewUser): Result<User, CreateUserFailure> {
        return newUserValidator.validate(newUser)
                .flatMap(this::userNotBanned)
                .flatMap(userRepository::create)
    }

    private fun userNotBanned(newUser: NewUser): Result<NewUser, CreateUserFailure> {
        return when (userRepository.isNotBanned(newUser.email)) {
            true -> Result.Success(newUser)
            false -> Result.Failure(UserIsBanned(newUser.email))
        }
    }
}


interface UserRepository {
    fun create(newUser: NewUser): Result<User, CreateUserFailure>
    fun isNotBanned(email: String): Boolean
}

interface NewUserValidator {
    fun validate(newUser: NewUser): Result<NewUser, CreateUserFailure>
}

data class NewUser(val email: String)
data class User(val id: Long, val email: String)

sealed class CreateUserFailure(val email: String) {
    class UserAlreadyExists(email: String) : CreateUserFailure(email)
    class InvalidEmail(email: String) : CreateUserFailure(email)
    class UserIsBanned(email: String) : CreateUserFailure(email)
}
