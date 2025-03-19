package mealplanner.dao


import mealplanner.model.MealInfo
import java.sql.Connection
import java.sql.Statement

/**
 * Data Access Object (DAO) for performing meal-related database operations.
 *
 * This class provides methods to retrieve meal options, detailed meal information,
 * and to insert new meals and plans into the database.
 *
 * @param connection A valid SQL Connection used for database operations.
 */
class MealDOA(private val connection: Connection) {

    /**
     * Retrieves a list of meal names for a given category.
     *
     * @param category The category of meals to retrieve (e.g., "breakfast", "lunch", or "dinner").
     * @return A mutable list of meal names in the specified category.
     */
    fun getMealOptions(category: String): MutableList<String> {
        val mealSQL = "SELECT " +
                "meal " +
                "FROM meals " +
                "WHERE category = ? " +
                "ORDER BY meal"
        return connection.prepareStatement(mealSQL).use { pstmt ->
            pstmt.setString(1, category)
            val mealResult = pstmt.executeQuery()
            val mealOptions = mutableListOf<String>()
            while (mealResult.next()) {
                mealOptions.add(mealResult.getString("meal"))
            }
            mealOptions
        }
    }

    /**
     * Retrieves detailed meal information, including meal names and their IDs, for a given category.
     *
     * @param category The meal category for which to retrieve details.
     * @return A list of MealInfo objects containing the meal name and meal ID.
     */
    fun getMealsWithIDs(category: String): List<MealInfo> {
        val showMealsSQL = "SELECT " +
                "meal, meal_id " +
                "FROM meals " +
                "WHERE category= ? " +
                "ORDER BY meal_id"
        return connection.prepareStatement(showMealsSQL).use { pstmt ->
            pstmt.setString(1, category)
            val meals = pstmt.executeQuery()
            val mealInfos = mutableListOf<MealInfo>()
            while (meals.next()) {
                val mealName = meals.getString("meal")
                val mealId = meals.getInt("meal_id")
                mealInfos.add(MealInfo(mealName, mealId))
            }
            mealInfos
        }
    }

    /**
     * Retrieves a list of meals from the current plan.
     *
     * This method queries the plan table and returns all meal entries (breakfast, lunch, and dinner).
     *
     * @return A mutable list of meal names from the plan.
     */
    fun getMealsFromPlan(): MutableList<String> {
        val savePlanSQL = "SELECT " +
                "breakfast, lunch, dinner " +
                "FROM plan"

        return connection.createStatement().use { stmt ->
            val mealsInPlanResult = stmt.executeQuery(savePlanSQL)
            val mealsInPlan = mutableListOf<String>()
            while (mealsInPlanResult.next()) {
                mealsInPlan.add(mealsInPlanResult.getString("breakfast"))
                mealsInPlan.add(mealsInPlanResult.getString("lunch"))
                mealsInPlan.add(mealsInPlanResult.getString("dinner"))
            }
            mealsInPlan
        }

    }

    /**
     * Retrieves the meal ID for a meal given its name.
     *
     * @param name The name of the meal.
     * @return The meal ID associated with the given meal name.
     */
    fun getMealIDByName(name: String): Int {
        val mealIDQuery = "SELECT " +
                "meal_id " +
                "FROM meals " +
                "WHERE meal = ?"
        return connection.prepareStatement(mealIDQuery).use { pstm ->
            pstm.setString(1, name)
            val mealIDResult = pstm.executeQuery()
            mealIDResult.getInt("meal_id")
        }
    }

    /**
     * Retrieves a list of ingredients for a given meal ID.
     *
     * @param mealId The ID of the meal.
     * @return A mutable list of ingredient names associated with the given meal ID.
     */
    fun getIngredientsByMealId(mealId: Int): MutableList<String> {
        val ingredientsQuery = "Select ingredient " +
                "FROM ingredients " +
                "WHERE meal_id = ?"
        return connection.prepareStatement(ingredientsQuery).use { pstm ->
            pstm.setInt(1, mealId)
            val ingredientsResult = pstm.executeQuery()
            val ingredients = mutableListOf<String>()
            while (ingredientsResult.next()) {
                ingredients.add(ingredientsResult.getString("ingredient"))
            }
            ingredients
        }
    }

    /**
     * Inserts a new plan into the database.
     *
     * @param day The day for which the plan is being added.
     * @param breakfast The breakfast meal for the day.
     * @param lunch The lunch meal for the day.
     * @param dinner The dinner meal for the day.
     */
    fun insertPlan(
        day: String,
        breakfast: String,
        lunch: String,
        dinner: String) {
        val mealSQL = "INSERT INTO plan " +
                "(day, breakfast, lunch, dinner) " +
                "VALUES (" +
                "?, " +
                "?, " +
                "?, " +
                "?)"
        connection.prepareStatement(mealSQL).use { pstm ->
            pstm.setString(1, day)
            pstm.setString(2, breakfast)
            pstm.setString(3, lunch)
            pstm.setString(4, dinner)
            pstm.executeUpdate()
        }
    }

    /**
     * Inserts a new meal along with its ingredients into the database.
     *
     * First, the meal is inserted into the meals table, and the generated meal ID is retrieved.
     * Then, each ingredient is inserted into the ingredients table with a reference to the meal ID.
     *
     * @param mealCategory The category of the meal (e.g., "breakfast").
     * @param mealName The name of the meal.
     * @param ingredients A list of ingredients for the meal.
     */
    fun insertMeal(
        mealCategory: String,
        mealName: String,
        ingredients: List<String>
                  ) {
        val mealSQL = "INSERT INTO meals " +
                "(category, meal) VALUES " +
                "(?, ?)"
        var mealID = -1
        connection.prepareStatement(mealSQL, Statement.RETURN_GENERATED_KEYS).use { pstm ->
            pstm.setString(1, mealCategory)
            pstm.setString(2, mealName)
            pstm.executeUpdate()
            pstm.generatedKeys.use { generatedKeys ->
                if (generatedKeys.next()) {
                    mealID = generatedKeys.getInt(1)
                }
            }
        }
        val ingredientSQL = "INSERT INTO ingredients" +
                "(ingredient, meal_id) VALUES" +
                "(?, ?)"
        connection.prepareStatement(ingredientSQL).use { pstm ->
            for (ingredient in ingredients) {
                pstm.setString(1, ingredient)
                pstm.setInt(2, mealID)
                pstm.addBatch()
            }
            pstm.executeBatch()
        }
    }
}