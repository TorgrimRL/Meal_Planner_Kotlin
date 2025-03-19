package mealplanner

import mealplanner.dao.MealDOA
import java.nio.file.Paths
import java.sql.Connection
import kotlin.io.path.writeText

/**
 * Coordinates the core business logic for the meal planner application.
 *
 * This class interacts with the user via the Menu and performs all operations related to
 * meals, plans, and shopping list generation by delegating data access tasks to MealDAO.
 *
 * @param connection A valid database connection.
 */
class MealPlanner(connection: Connection) {
    private val menu = Menu()
    private val mealDOA = MealDOA(connection)


    /**
     * Main command loop that repeatedly prompts the user for an action.
     *
     * Depending on the user’s command ("add", "show", "plan", "save", or "exit"), it delegates
     * the operation to the appropriate method. The loop terminates when the user enters "exit".
     */
    fun waitForCommand() {
        while (true) {
            menu.displayOptions()
            val command = menu.getCommand()
            when (command) {
                "add" -> addMeal()
                "show" -> showMeals()
                "plan" -> addPlan()
                "save" -> savePlan()
                "exit" -> {
                    println("Bye!")
                    break
                }
            }
        }
    }

    /**
     * Generates a shopping list from the planned meals and saves it to a file.
     *
     * This method retrieves the planned meals via the DAO, aggregates the meal counts,
     * then fetches the ingredients for each meal. The aggregated shopping list is formatted
     * as a string and written to a file whose name is obtained from the user.
     * If no meals have been planned, it notifies the user.
     */
    private fun savePlan() {
        val mealsInPlan = mealDOA.getMealsFromPlan()
        if (mealsInPlan.isEmpty()) {
            menu.displayMessage("Unable to save. Plan your meals first.")
        } else {
            val meals = mutableMapOf<String, Int>()
            for (meal in mealsInPlan) {
                if (!meals.containsKey(meal)) {
                    meals[meal] = 1
                } else {
                    meals.computeIfPresent(meal) { _, count -> count + 1 }
                }
            }
            val ingredients = mutableMapOf<String, Int>()
            for (meal in meals) {
                val mealId = mealDOA.getMealIDByName(meal.key)
                val ingredientsList = mealDOA.getIngredientsByMealId(mealId)
                for (ingredient in ingredientsList) {
                    if (!ingredients.containsKey(ingredient)) {
                        ingredients[ingredient] = meal.value
                    } else {
                        ingredients.computeIfPresent(ingredient) { _, _ -> +meal.value }
                    }
                }
            }


            val file = Paths.get(menu.promptForFileName())
            var ingredientsString = ""
            for (ingredient in ingredients) {
                if (ingredient.value == 1) {
                    ingredientsString + ("${ingredient.key}\n")
                } else {
                    ingredientsString + "${ingredient.key} x${ingredient.value}"
                }
            }
            file.writeText(ingredientsString)
            menu.displayMessage("Saved!")
        }

    }

    /**
     * Prompts the user to plan meals for each day of the week.
     *
     * The method retrieves available meal options from the DAO and uses the Menu to prompt the user
     * for choices for breakfast, lunch, and dinner for each day. It then displays the final plan
     * and saves it to the database.
     */
    private fun addPlan() {
        val week = mutableListOf(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        val breakfastOptions = mealDOA.getMealOptions("breakfast")
        val lunchOptions = mealDOA.getMealOptions("lunch")
        val dinnerOptions = mealDOA.getMealOptions("dinner")


        val chosenBreakfastChoices = mutableListOf<String>()
        val chosenLunchChoices = mutableListOf<String>()
        val chosenDinnerChoices = mutableListOf<String>()
        for (day in week) {
            println(day)
            chosenBreakfastChoices.add(
                    menu.getMealChoiceForDay(
                            day, "breakfast", breakfastOptions))
            chosenLunchChoices.add(
                    menu.getMealChoiceForDay(
                            day, "lunch", lunchOptions))
            chosenDinnerChoices.add(
                    menu.getMealChoiceForDay(
                            day, "dinner", dinnerOptions))
            menu.validDayPlannedResponse(day)
        }
        menu.presentPlan(
                week, chosenBreakfastChoices, chosenLunchChoices, chosenDinnerChoices)

        for (day in week) {
            for (category in menu.validCategories) {
                mealDOA.insertPlan(
                        day,
                        chosenBreakfastChoices[week.indexOf(day)],
                        chosenLunchChoices[week.indexOf(day)],
                        chosenDinnerChoices[week.indexOf(day)])
            }
        }
    }

    /**
     * Prompts the user to add a new meal by gathering meal category, name, and ingredients.
     *
     * The method uses the Menu to obtain validated input and then calls the DAO to insert the meal
     * into the database. After successful insertion, it displays a confirmation message.
     */
    private fun addMeal() {

        // Get validated inputs from the user.
        val mealCategory = menu.promptForMealCategory()
        val mealName = menu.promptForMealName()
        val ingredients = menu.promptForIngredients()

        // Save the meal in the database via the DAO.
        mealDOA.insertMeal(mealCategory, mealName, ingredients)
        menu.displayMessage("The meal has been added!")

    }

    /**
     * Retrieves and displays meals of a given category.
     *
     * The method prompts the user for a category, retrieves the corresponding meal information
     * (including meal IDs) from the DAO, and then delegates the presentation of the meal list to the Menu.
     * If no meals are found for the chosen category, it displays an appropriate message.
     */
    private fun showMeals() {
        val category = menu.promptForCategoryForPrinting()
        val mealInfos = mealDOA.getMealsWithIDs(category)
        if (mealInfos.isEmpty()) {
            menu.displayMessage("No meals found.")
        } else {
            menu.displayMealList(category, mealInfos, mealDOA::getIngredientsByMealId)
        }
    }
}