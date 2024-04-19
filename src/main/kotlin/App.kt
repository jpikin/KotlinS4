import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter

fun main() {
    val contacts = ArrayList<Person>()


    while (true) {
        print("Введите команду: ")

        when (val input = readlnOrNull()) {

            "exit" -> {
                println("Программа завершена.")
                break
            }

            "help" -> {
                printHelp()
            }

            else -> {
                try {
                    val command = readCommand(input ?: "")
                    if (command.isValid()) {
                        when (command) {
                            is Export -> {
                                val com = input?.split(" ")
                                try {
                                    export(contacts, com?.get(1) ?: "")
                                }
                                catch (e: FileNotFoundException) {
                                    e.stackTrace
                                }
                            }

                            is ShowCommand -> {
                                val com = input?.split(" ")
                                showList(contacts, com?.get(1))
                            }

                            is Find -> {
                                val com = input?.split(" ")
                                val value = com?.get(1) ?: ""
                                findPerson(contacts, value)
                            }

                            is AddCommand -> {
                                val com = input?.split(" ")
                                val name = com?.get(1) ?: ""
                                val type = com?.get(2) ?: ""
                                val value = com?.get(3) ?: ""
                                if (type == "phone") {
                                    addInfo(contacts, name, value, "")
                                } else {
                                    addInfo(contacts, name, "", value)
                                }
                            }

                            is AddValues -> {
                                val com = input?.split(" ")
                                val type = com?.get(0) ?: ""
                                val name = com?.get(1) ?: ""
                                val value = com?.get(2) ?: ""
                                if(!checkNameInList(contacts, name)) {
                                    if (type == "addPhone") {
                                        addInfo(contacts, name, value, "")
                                    } else {
                                        addInfo(contacts, name, "", value)
                                    }
                                } else {
                                    println("Пользователь с именем $name не найден.\nЧтобы " +
                                            "добавить нового пользователя воспользуйтесь командой 'add'")
                                }
                            }
                        }
                    } else {
                        println("Некорректные данные для команды. Введите 'help' для списка команд!")
                    }
                } catch (e: IllegalArgumentException) {
                    println("Некорректная команда. Введите 'help' для списка команд.")
                }
            }
        }
    }
}

fun export(contacts: ArrayList<Person>, adress: String) {
    val directory = File(adress)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File(directory,"newJson.json")
    val text: String = toJson(contacts)
    FileWriter(file).use {it.write(text)}

}
fun toJson(list: ArrayList<Person>):String{
    var str = "["
    for (contact in list)
        str += "{\"name\": \"${contact.name}\", \"phones\": ${getInfo(contact.phones)}, \"emails\": ${getInfo(contact.emails)}},"
    str = replaceLastChar(str, ']')
    println(str)
    return str
}

fun getInfo(contact: ArrayList<String>): String {
    if (contact.isEmpty()){return "[]"}
    else {
        var output = "["
        for(phone in contact)
            output += "\"$phone\","
        output = replaceLastChar(output,']')
        return output
    }

}

fun replaceLastChar(input: String, newChar: Char): String {
    if (input.isEmpty()) return input
    val lastIndex = input.length - 1
    val updatedString = StringBuilder(input).apply {
        setCharAt(lastIndex, newChar)
    }
    return updatedString.toString()
}

fun findPerson(list: ArrayList<Person>, value: String) {
    for(person in list){
        if(value in person.phones || value in person.emails)
            println(person)
        else
            println("Пользователь с такими данными не найден")
    }
}

fun checkNameInList(list: ArrayList<Person>, name: String): Boolean {
    for (person in list) {
        if (person.name == name) {
            return false
        }
    }
    return true

}

fun showList(list: ArrayList<Person>, name: String?) {
    if (list.size == 0) {
        println("Список контактов пуст")
    } else {
        for (person in list) {
            if (person.name == name) {
                println(person)
                return
            }
        }
        println("Такой человек не найден")
    }
}

fun addInfo(list: ArrayList<Person>, name: String, phone: String, email: String) {
    for (person in list) {
        if (person.name == name) {
            if (phone !in person.phones && phone != "")
                person.phones.add(phone)
            if (email !in person.emails && email != "")
                person.emails.add(email)
            println("Данные контакта $name обновлены!")
            return
        }
    }

    val phones = arrayListOf<String>()
    val emails = arrayListOf<String>()
    if (phone != "") phones.add(phone)
    if (email != "") emails.add(email)
    val newPerson = Person(name, phones, emails)
    list.add(newPerson)
    println("Новый контакт $name добавлен")


}

fun readCommand(input: String): Command {
    val regexAdd = Regex("""add ([A-z]+) (phone|email) (.+)""")
    val regexShow = Regex("""show ([A-z]+)""")
    val regexAddValues = Regex("""(addPhone|addEmail) ([A-z]+) (.+)""")
    val regexFind = Regex("""find (.+)""")
    val regexExport = Regex("""(export) (.+)""")


    return when {
        regexExport.matches(input) -> {
            val matchResult = regexExport.find(input)!!
            val value = matchResult.groupValues[1]
            Export(value)
        }

        regexFind.matches(input) -> {
            val matchResult = regexFind.find(input)!!
            val value = matchResult.groupValues[1]
            Find(value)
        }

        regexAddValues.matches(input) -> {
            val matchResult = regexAddValues.find(input)!!
            val value = matchResult.groupValues[3]
            AddValues(value)
        }

        regexAdd.matches(input) -> {
            val matchResult = regexAdd.find(input)!!
            val value = matchResult.groupValues[3]
            AddCommand(value)
        }

        regexShow.matches(input) -> {
            val matchResult = regexShow.find(input)!!
            val name = matchResult.groupValues[1]
            ShowCommand(name)
        }

        else -> {
            throw IllegalArgumentException("Invalid command format")
        }
    }

}


