package demo

sealed class Result<S, F> {



    class Success<S, F>(val success: S): Result<S, F>()
    class Failure<S, F>(val failure: F): Result<S, F>()
}
