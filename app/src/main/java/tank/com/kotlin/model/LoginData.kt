package tank.com.kotlin.model

class LoginData(uid: Long?, age: Int?, name: String?, gender: Gender?) {

    var uid: Long = uid?.let { uid } ?: -1L

    var age: Int = age?.let { if (age > 0) age else 0 } ?: 0

    var name: String? = name?.let { name } ?: ""

    var gender: Gender? = gender?.let { gender } ?: Gender.Female

    enum class Gender(tag: String) {
        Male("男"), Female("女")
    }

    override fun toString(): String {
        return "LoginData(uid=$uid, age=$age, name=$name, gender=$gender)"
    }
}