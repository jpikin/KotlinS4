

data class Person(val name: String, var phones: ArrayList<String>, var emails: ArrayList<String>) {

    override fun toString(): String {
        return "Имя: $name\nТелефоны: $phones\nАдреса e-mail: $emails\n---------------------"

    }
}