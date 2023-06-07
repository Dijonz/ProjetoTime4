package data_classes

data class Card(
    val Nome: String,
    val emergenciaUid: String,
    var aceite: Boolean = false,
    val imgEmerg: String,
    val date: String
)

class CardBuilder {
    var Nome: String = ""
    var emergenciaUid: String = ""
    var aceite: Boolean = false
    var imgEmerg: String = ""
    var date: String = ""

    fun build(): Card = Card(Nome, emergenciaUid, false, imgEmerg, date)
}

fun card(block: CardBuilder.() -> Unit): Card = CardBuilder().apply(block).build()

fun fakeEmergencias() = mutableListOf(
    card{
        Nome = "Eric"
        emergenciaUid = "1231x11s1"
        aceite = false
        imgEmerg = "sajkldac"
        date = "23/05 18:49"
    },
    card{
        Nome = "Mateus Dias"
        emergenciaUid = "1231x11s1"
        aceite = false
        imgEmerg = "sajkldac"
        date = "23/05 18:49"
    },
    card{
        Nome = "Simpsons"
        emergenciaUid = "1231x11s1"
        aceite = false
        imgEmerg = "sajkldac"
        date = "23/05 18:49"
    },
    card{
        Nome = "Joao"
        emergenciaUid = "1231x11s1"
        aceite = false
        imgEmerg = "sajkldac"
        date = "23/05 18:49"
    }
)