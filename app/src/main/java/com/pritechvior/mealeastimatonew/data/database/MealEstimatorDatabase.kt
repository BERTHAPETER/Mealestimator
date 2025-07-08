package com.pritechvior.mealeastimatonew.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.pritechvior.mealeastimatonew.data.model.*

class MealEstimatorDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mealestimator.db"
        private const val DATABASE_VERSION = 2

        // Table Names
        private const val TABLE_USERS = "users"
        private const val TABLE_MEALS = "meals"
        private const val TABLE_INGREDIENTS = "ingredients"
        private const val TABLE_SAVED_PREDICTIONS = "saved_predictions"
        private const val TABLE_PREDICTION_INGREDIENTS = "prediction_ingredients"

        // Common Column Names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"

        // Users Table Columns
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PROFILE_IMAGE = "profile_image"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"

        // Meals Table Columns
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IMAGE_URL = "image_url"
        private const val COLUMN_EMOJI = "emoji"
        private const val COLUMN_PREPARATION_TIME = "preparation_time"
        private const val COLUMN_DIFFICULTY = "difficulty"

        // Ingredients Table Columns
        private const val COLUMN_PREPARATION_TIME_INGREDIENT = "preparation_time"

        // Saved Predictions Table Columns
        private const val COLUMN_MEAL_NAME = "meal_name"
        private const val COLUMN_MEAL_TYPE = "meal_type"
        private const val COLUMN_NUM_PEOPLE = "num_people"
        private const val COLUMN_AGE_GROUPS = "age_groups"
        private const val COLUMN_NOTES = "notes"
        private const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_PROFILE_IMAGE TEXT,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        // Create Meals table
        val createMealsTable = """
            CREATE TABLE $TABLE_MEALS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_EMOJI TEXT,
                $COLUMN_PREPARATION_TIME INTEGER,
                $COLUMN_DIFFICULTY TEXT
            )
        """.trimIndent()

        // Create Ingredients table
        val createIngredientsTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PREPARATION_TIME_INGREDIENT INTEGER
            )
        """.trimIndent()

        // Create Saved Predictions table
        val createSavedPredictionsTable = """
            CREATE TABLE $TABLE_SAVED_PREDICTIONS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_MEAL_NAME TEXT NOT NULL,
                $COLUMN_MEAL_TYPE TEXT NOT NULL,
                $COLUMN_NUM_PEOPLE INTEGER NOT NULL,
                $COLUMN_AGE_GROUPS TEXT NOT NULL,
                $COLUMN_NOTES TEXT,
                $COLUMN_USER_ID TEXT NOT NULL,
                FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        // Create Prediction Ingredients table (junction table)
        val createPredictionIngredientsTable = """
            CREATE TABLE $TABLE_PREDICTION_INGREDIENTS (
                prediction_id TEXT NOT NULL,
                ingredient_name TEXT NOT NULL,
                quantity REAL NOT NULL,
                unit TEXT NOT NULL,
                is_optional INTEGER NOT NULL,
                FOREIGN KEY(prediction_id) REFERENCES $TABLE_SAVED_PREDICTIONS($COLUMN_ID),
                PRIMARY KEY(prediction_id, ingredient_name)
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createMealsTable)
        db.execSQL(createIngredientsTable)
        db.execSQL(createSavedPredictionsTable)
        db.execSQL(createPredictionIngredientsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add new columns to users table
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_PROFILE_IMAGE TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_CREATED_AT INTEGER DEFAULT ${System.currentTimeMillis()}")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_UPDATED_AT INTEGER DEFAULT ${System.currentTimeMillis()}")
        }
    }

    // User Operations
    fun getUserByUsername(username: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            User(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                profileImage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
                updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
            )
        } else null.also { cursor.close() }
    }

    fun insertUser(user: User): Boolean {
        // Check if username already exists
        if (getUserByUsername(user.username) != null) {
            return false
        }

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, user.id)
            put(COLUMN_USERNAME, user.username.lowercase()) // Store username in lowercase
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_PROFILE_IMAGE, user.profileImage)
            put(COLUMN_CREATED_AT, user.createdAt)
            put(COLUMN_UPDATED_AT, user.updatedAt)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun getUser(username: String, password: String): User? {
        val db = this.readableDatabase
        // First get the user by username
        val user = getUserByUsername(username.lowercase()) // Convert to lowercase for comparison
        
        // If user exists and password matches, return the user
        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }

    fun updateUserProfileImage(userId: String, imageUri: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PROFILE_IMAGE, imageUri)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId))
        return result > 0
    }

    fun deleteUserProfileImage(userId: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            putNull(COLUMN_PROFILE_IMAGE)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId))
        return result > 0
    }

    fun updateUserPassword(userId: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, newPassword)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId))
        return result > 0
    }

    // Meal Operations
    fun insertMeal(meal: Meal): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, meal.id)
            put(COLUMN_NAME, meal.name)
            put(COLUMN_DESCRIPTION, meal.description)
            put(COLUMN_IMAGE_URL, meal.imageUrl)
            put(COLUMN_EMOJI, meal.emoji)
            put(COLUMN_PREPARATION_TIME, meal.basePreparationTime)
            put(COLUMN_DIFFICULTY, meal.difficulty)
        }
        val result = db.insert(TABLE_MEALS, null, values)
        return result != -1L
    }

    fun getAllMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_MEALS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                meals.add(
                    Meal(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        emoji = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMOJI)),
                        basePreparationTime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PREPARATION_TIME)),
                        difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return meals
    }

    // Ingredient Operations
    fun insertIngredient(ingredient: Ingredient): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, ingredient.id)
            put(COLUMN_NAME, ingredient.name)
            put(COLUMN_PREPARATION_TIME_INGREDIENT, ingredient.preparationTime)
        }
        val result = db.insert(TABLE_INGREDIENTS, null, values)
        return result != -1L
    }

    fun getAllIngredients(): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_INGREDIENTS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(
                    Ingredient(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        preparationTime = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PREPARATION_TIME_INGREDIENT))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredients
    }

    // Saved Prediction Operations
    fun insertSavedPrediction(prediction: SavedPrediction, userId: String): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Insert main prediction
            val predictionValues = ContentValues().apply {
                put(COLUMN_ID, prediction.id)
                put(COLUMN_MEAL_NAME, prediction.mealName)
                put(COLUMN_MEAL_TYPE, prediction.mealType)
                put(COLUMN_NUM_PEOPLE, prediction.numberOfPeople)
                put(COLUMN_AGE_GROUPS, prediction.ageGroups.joinToString(","))
                put(COLUMN_NOTES, prediction.notes)
                put(COLUMN_USER_ID, userId)
            }
            db.insert(TABLE_SAVED_PREDICTIONS, null, predictionValues)

            // Insert prediction ingredients
            prediction.ingredients.forEach { ingredient ->
                val ingredientValues = ContentValues().apply {
                    put("prediction_id", prediction.id)
                    put("ingredient_name", ingredient.name)
                    put("quantity", ingredient.quantity)
                    put("unit", ingredient.unit)
                    put("is_optional", if (ingredient.isOptional) 1 else 0)
                }
                db.insert(TABLE_PREDICTION_INGREDIENTS, null, ingredientValues)
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun getSavedPredictions(userId: String): List<SavedPrediction> {
        val predictions = mutableListOf<SavedPrediction>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_SAVED_PREDICTIONS,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val predictionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val ingredients = getPredictionIngredients(predictionId)

                predictions.add(
                    SavedPrediction(
                        id = predictionId,
                        mealName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_NAME)),
                        mealType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEAL_TYPE)),
                        numberOfPeople = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUM_PEOPLE)),
                        ageGroups = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE_GROUPS))
                            .split(","),
                        ingredients = ingredients,
                        notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return predictions
    }

    private fun getPredictionIngredients(predictionId: String): List<SavedIngredient> {
        val ingredients = mutableListOf<SavedIngredient>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_PREDICTION_INGREDIENTS,
            null,
            "prediction_id = ?",
            arrayOf(predictionId),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(
                    SavedIngredient(
                        name = cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        quantity = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        unit = cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        isOptional = cursor.getInt(cursor.getColumnIndexOrThrow("is_optional")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredients
    }

    fun deleteSavedPrediction(predictionId: String, userId: String): Boolean {
        val db = this.writableDatabase
        // Delete from prediction ingredients first (to maintain referential integrity)
        db.delete(
            TABLE_PREDICTION_INGREDIENTS,
            "prediction_id = ?",
            arrayOf(predictionId)
        )
        // Then delete from saved predictions
        val result = db.delete(
            TABLE_SAVED_PREDICTIONS,
            "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?",
            arrayOf(predictionId, userId)
        )
        return result > 0
    }
} 