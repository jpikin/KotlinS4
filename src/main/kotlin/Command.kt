

sealed interface Command {
    fun isValid(): Boolean
}