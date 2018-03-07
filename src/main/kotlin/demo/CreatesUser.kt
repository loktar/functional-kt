package demo

import demo.CreateUserFailure.*
import java.util.logging.Logger

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
            is UserAlreadyExists -> Response(409, "User already exists")
            is InvalidEmail -> Response(422, "Invalid email: ${failure.email}")
            is UserIsBanned -> Response(422, "User is banned: ${failure.email}")
        }
    }
}

data class Response(val statusCode: Int, val message: String)


class CreatesUser(
        private val userRepository: UserRepository,
        private val newUserValidator: NewUserValidator,
        private val logger: Logger
) {
    fun execute(newUser: NewUser): Result<User, CreateUserFailure> {
        return this.validateNewUser(newUser)
                .flatMap(this::verifyUserNotBanned)
                .flatMap(this::persistUser)
                .tap(
                        onSuccess = this::logNewUserCreated,
                        onFailure = this::logNewUserFailure
                )
    }

    private fun validateNewUser(newUser: NewUser) = newUserValidator.validate(newUser)

    private fun persistUser(newUser: NewUser) = userRepository.create(newUser)

    private fun verifyUserNotBanned(newUser: NewUser): Result<NewUser, CreateUserFailure> {
        return when (userRepository.isNotBanned(newUser.email)) {
            true -> Result.Success(newUser)
            false -> Result.Failure(UserIsBanned(newUser.email))
        }
    }

    private fun logNewUserCreated(user: User) {
        logger.info("Created user <${user.id}> with email <${user.email}>")
    }

    private fun logNewUserFailure(failure: CreateUserFailure) {
        logger.warning("Failed to create user with email <${failure.email}>: ${failure::class.simpleName}")
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
