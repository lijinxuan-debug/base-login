package com.example.thirdstage.dataModel

import org.json.JSONObject

/**
 * 学生数据模型
 * 使用 Data Class 自动生成 copy, toString, equals 等方法
 */
data class Student(
    val studentId: String,
    val name: String,
    val gender: String,
    val birthDate: String,
    val className: String
) {
    // 将对象转为 JSONObject (用于存储到 SharedPreference)
    fun toJsonObject(): JSONObject {
        val jsonObject = JSONObject()
        // 使用固定的 Key，不受混淆影响
        jsonObject.put("student_id", studentId)
        jsonObject.put("name", name)
        jsonObject.put("gender", gender)
        jsonObject.put("birth_date", birthDate)
        jsonObject.put("class_name", className)
        return jsonObject
    }

    companion object {
        // 从 JSONObject 解析出对象 (用于从 SharedPreference 读取)
        fun fromJson(json: JSONObject): Student {
            return Student(
                // 使用 optString 代替 getString，如果字段缺失会返回空字符串而不是崩溃
                studentId = json.optString("student_id", ""),
                name = json.optString("name", "未知"),
                gender = json.optString("gender", "保密"),
                birthDate = json.optString("birth_date", "2000-01-01"),
                className = json.optString("class_name", "普通班")
            )
        }
    }
}