

class ShowCommand(val name: String) : Command {
    override fun isValid(): Boolean {
        return true
    }
}