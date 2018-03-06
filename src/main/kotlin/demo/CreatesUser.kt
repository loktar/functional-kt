package demo

class CreatesUser(
        private val userRepository: UserRepository
) {
    fun execute(newUser: NewUser): User {
        return userRepository.create(newUser)
    }
}



interface UserRepository {
    fun create(newUser: NewUser): User
}

data class NewUser(val email: String)
data class User(val id: Long, val email: String)
