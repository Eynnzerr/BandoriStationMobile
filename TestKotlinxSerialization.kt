import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main() {
    // 创建一个配置与您WebSocket中相同的Json实例
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    // 测试缺少字段的情况
    val jsonWithMissingField = """{"name":"John", "age":30}"""
    
    try {
        val user = json.decodeFromString<UserInfo>(jsonWithMissingField)
        println("反序列化成功!")
        println("User: $user")
        println("Role是否为默认值: ${user.role == UserRole.USER}")
    } catch (e: Exception) {
        println("反序列化失败: ${e.message}")
    }
}

@Serializable
data class UserInfo(
    val name: String,
    val age: Int,
    val role: UserRole = UserRole.USER // 设置默认值
)

@Serializable
enum class UserRole {
    USER,
    ADMIN,
    GUEST
}