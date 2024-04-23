package cat.insVidreres.socialpostphone.imp.profile

import cat.insVidreres.socialpostphone.imp.entity.User
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParseException

class UserTypeAdapter : JsonDeserializer<User> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): User {
        val jsonObject = json.asJsonObject

        return User(
            id = jsonObject.get("id")?.asString,
            email = jsonObject.get("email").asString,
            password = jsonObject.get("password").asString,
            firstName = jsonObject.get("firstName")?.asString,
            lastName = jsonObject.get("lastName")?.asString,
            age = jsonObject.get("age")?.asDouble,
            phoneNumber = jsonObject.get("phoneNumber")?.asString,
            img = jsonObject.get("img").asString
        )
    }
}