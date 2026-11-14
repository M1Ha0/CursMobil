package com.example.mycursmob.presentation.nutrition

import com.example.mycursmob.R
import com.google.firebase.firestore.DocumentSnapshot
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class MealCategory(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner")
}

data class MealItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val portion: String = "100g",
    val calories: Int,
    val proteinG: Float,
    val fatsG: Float,
    val carbsG: Float,
    val emoji: String = "🍳",
    val iconRes: Int? = null,
    val imageRes: Int? = null
) {
    fun resolveIconRes(): Int {
        if (iconRes != null && iconRes != 0) return iconRes
        return when (name.lowercase()) {
            "avocado dish" -> R.drawable.ic_meal_avocado
            "salad with eggs" -> R.drawable.ic_meal_egg
            "pancakes" -> R.drawable.ic_meal_pancakes
            "slice of pineapple" -> R.drawable.ic_meal_pineapple
            else -> R.drawable.ic_meal_egg
        }
    }

    fun resolveImageRes(): Int {
        if (imageRes != null && imageRes != 0) return imageRes
        return when (name.lowercase()) {
            "avocado dish" -> R.drawable.dish_avocado
            "salad with eggs" -> R.drawable.dish_salad_egg
            "pancakes" -> R.drawable.dish_pancakes
            "slice of pineapple" -> R.drawable.dish_pineapple
            else -> R.drawable.dish_avocado
        }
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("portion", portion)
        put("calories", calories)
        put("proteinG", proteinG.toDouble())
        put("fatsG", fatsG.toDouble())
        put("carbsG", carbsG.toDouble())
        put("emoji", emoji)
        if (iconRes != null) put("iconRes", iconRes)
        if (imageRes != null) put("imageRes", imageRes)
    }

    fun toFirestoreMap(dateKey: String, category: String, userId: String): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "portion" to portion,
            "calories" to calories,
            "proteinG" to proteinG.toDouble(),
            "fatsG" to fatsG.toDouble(),
            "carbsG" to carbsG.toDouble(),
            "emoji" to emoji,
            "iconRes" to (iconRes ?: 0),
            "imageRes" to (imageRes ?: 0),
            "dateKey" to dateKey,
            "category" to category,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )
    }

    companion object {
        fun fromJson(json: JSONObject): MealItem {
            return MealItem(
                id = json.optString("id", UUID.randomUUID().toString()),
                name = json.optString("name", "Meal"),
                portion = json.optString("portion", "100g"),
                calories = json.optInt("calories", 0),
                proteinG = json.optDouble("proteinG", 0.0).toFloat(),
                fatsG = json.optDouble("fatsG", 0.0).toFloat(),
                carbsG = json.optDouble("carbsG", 0.0).toFloat(),
                emoji = json.optString("emoji", "🍳"),
                iconRes = if (json.has("iconRes")) json.getInt("iconRes") else null,
                imageRes = if (json.has("imageRes")) json.getInt("imageRes") else null
            )
        }

        fun fromFirestoreDoc(doc: DocumentSnapshot): Pair<String, MealItem>? {
            val id = doc.getString("id") ?: doc.id
            val name = doc.getString("name") ?: return null
            val portion = doc.getString("portion") ?: "100g"
            val calories = doc.getLong("calories")?.toInt() ?: 0
            val proteinG = (doc.getDouble("proteinG") ?: 0.0).toFloat()
            val fatsG = (doc.getDouble("fatsG") ?: 0.0).toFloat()
            val carbsG = (doc.getDouble("carbsG") ?: 0.0).toFloat()
            val emoji = doc.getString("emoji") ?: "🍳"
            val iconResRaw = doc.getLong("iconRes")?.toInt() ?: 0
            val imageResRaw = doc.getLong("imageRes")?.toInt() ?: 0
            val dateKey = doc.getString("dateKey") ?: return null
            val category = doc.getString("category") ?: return null

            val item = MealItem(
                id = id,
                name = name,
                portion = portion,
                calories = calories,
                proteinG = proteinG,
                fatsG = fatsG,
                carbsG = carbsG,
                emoji = emoji,
                iconRes = if (iconResRaw != 0) iconResRaw else null,
                imageRes = if (imageResRaw != 0) imageResRaw else null
            )
            return "${dateKey}_${category}" to item
        }
    }
}

data class CalendarDay(
    val date: Date,
    val monthShort: String, // e.g. "Aug", "Nov"
    val dayNumber: String,  // e.g. "12", "14"
    val fullKey: String,    // "yyyy-MM-dd"
    val isToday: Boolean
)

object CalendarHelper {
    fun generateDays(rangeDays: Int = 15): Pair<List<CalendarDay>, Int> {
        val list = mutableListOf<CalendarDay>()
        val cal = Calendar.getInstance()
        val todayCal = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
        val dayFormat = SimpleDateFormat("d", Locale.ENGLISH)
        val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val todayKey = keyFormat.format(todayCal.time)

        cal.add(Calendar.DAY_OF_YEAR, -rangeDays)

        var todayIndex = 0
        val totalDays = rangeDays * 2 + 1
        for (i in 0 until totalDays) {
            val d = cal.time
            val key = keyFormat.format(d)
            val isToday = key == todayKey
            if (isToday) todayIndex = i
            list.add(
                CalendarDay(
                    date = d,
                    monthShort = monthFormat.format(d),
                    dayNumber = dayFormat.format(d),
                    fullKey = key,
                    isToday = isToday
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return Pair(list, todayIndex)
    }
}

data class CarouselDish(
    val name: String,
    val portion: String = "100g",
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatsG: Float,
    val emoji: String,
    val iconRes: Int,
    val imageRes: Int
)

/**
 * Pre-populated dishes for the Nutrition Details swipeable carousel
 * using user-provided high quality food images and matching stickers
 */
val carouselDishes = listOf(
    CarouselDish(
        name = "Avocado Dish",
        portion = "100g",
        calories = 457,
        proteinG = 22f,
        carbsG = 1.8f,
        fatsG = 19f,
        emoji = "🥑",
        iconRes = R.drawable.ic_meal_avocado,
        imageRes = R.drawable.dish_avocado
    ),
    CarouselDish(
        name = "Salad with eggs",
        portion = "100g",
        calories = 294,
        proteinG = 25f,
        carbsG = 12f,
        fatsG = 16f,
        emoji = "🍳",
        iconRes = R.drawable.ic_meal_egg,
        imageRes = R.drawable.dish_salad_egg
    ),
    CarouselDish(
        name = "Pancakes",
        portion = "100g",
        calories = 320,
        proteinG = 12f,
        carbsG = 48f,
        fatsG = 10f,
        emoji = "🥞",
        iconRes = R.drawable.ic_meal_pancakes,
        imageRes = R.drawable.dish_pancakes
    ),
    CarouselDish(
        name = "Slice of Pineapple",
        portion = "100g",
        calories = 145,
        proteinG = 5f,
        carbsG = 38f,
        fatsG = 2f,
        emoji = "🍍",
        iconRes = R.drawable.ic_meal_pineapple,
        imageRes = R.drawable.dish_pineapple
    )
)
