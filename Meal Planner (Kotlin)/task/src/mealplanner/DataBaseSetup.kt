package mealplanner

import java.sql.Connection

/**
 * Handles the initialization of the database schema for the meal planner application.
 *
 * This class is responsible for creating the necessary tables (meals, ingredients, plan)
 * if they do not already exist in the database.
 *
 * @param connection A valid SQL Connection used to execute the schema setup statements.
 */
class DataBaseSetup(private val connection: Connection) {

    /**
     * Initializes the database by creating the required tables.
     *
     * The following tables are created if they do not already exist:
     * - **meals**: Contains meal details, including category, meal name, and a generated meal ID.
     * - **ingredients**: Contains ingredients associated with meals, with a foreign key reference
     *   to the meal ID in the meals table.
     * - **plan**: Contains the planned meals for each day, including breakfast, lunch, and dinner.
     */
    fun initialize() {
        val mealsTableSQL =
            "CREATE TABLE IF NOT EXISTS " +
                    "meals " +
                    "(category TEXT," +
                    " meal TEXT," +
                    " meal_id INTEGER PRIMARY KEY AUTOINCREMENT)"
        val ingredientsTableSQL =
            "CREATE TABLE IF NOT EXISTS " +
                    "ingredients (ingredient TEXT, ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, meal_id INTEGER," +
                    "FOREIGN KEY (meal_id) REFERENCES meals(meal_id))"
        val planTableSQL =
            "CREATE TABLE IF NOT EXISTS " +
                    "plan (day TEXT, breakfast TEXT, lunch TEXT, dinner TEXT)"

        connection.createStatement().use { statement ->
            statement.addBatch(mealsTableSQL)
            statement.addBatch(ingredientsTableSQL)
            statement.addBatch(planTableSQL)
            statement.executeBatch()
        }
    }
}
