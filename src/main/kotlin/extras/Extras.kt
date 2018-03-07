package extras

// region Immutable values

data class User(
        val id: Int,
        val name: String,
        val email: String,
        val isAdmin: Boolean
)

fun immutableValues() {
    val user = User(id = 1, name = "Ian", email = "ifisher@pivotal.io", isAdmin = true)
    user = User(id = 2, name = "Not Ian", email = "whoami@pivotal.io", isAdmin = true)

    var mysteryUser = User(id = 1, name = "Ian", email = "ifisher@pivotal.io", isAdmin = true)
    mysteryUser = User(id = 2, name = "Not Ian", email = "whoami@pivotal.io", isAdmin = true)
}

// endregion

// region Fixtures

object Fixtures {
    val user = User(id = -1, name = "Fixture User", email = "fixture@example.com", isAdmin = false)
}

fun fixturesAreNeat() {
    val admin = Fixtures.user.copy(isAdmin = true)

    val user1 = Fixtures.user.copy(id = 1, email = "foo@example.com")
    val user2 = Fixtures.user.copy(id = 2, email = "foo@example.com")
}

// endregion
