

class Find(private val value: String) : Command {
    override fun isValid(): Boolean {
        return value.matches(Regex("""[+][0-9]{10,}"""))
                || value.matches(Regex("[a-zA-Z0-9._%+-]+@[A-z]+\\.[A-z]{2,}"))
    }
}