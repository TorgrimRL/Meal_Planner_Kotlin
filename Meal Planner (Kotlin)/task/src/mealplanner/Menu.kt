package mealplanner

import mealplanner.model.MealInfo

/**
 * Handles all user interactions for the meal planner application.
 *
 * This class is responsible for displaying options, prompting for input,
 * validating user responses, and formatting output for the application.
 */
class Menu() {
    val validCategories = listOf(
            "breakfast", "lunch", "dinner")

    /**
     * Checks if the given input is valid.
     * Valid input must not be blank and can only contain letters and whitespace.
     *
     * @param input The string to validate.
     * @return True if the input is valid; false otherwise.
     */
    private fun checkFormat(input: String): Boolean {
        if (input.isBlank()) {
            println("Wrong format. Use letters only!")
            return false
        }
        for (char in input.toCharArray()) {
            if (!char.isLetter() && !char.isWhitespace()) {
                println("Wrong format. Use letters only!")
                return false
            }
        }
        return true
    }

    /**
     * Prompts the user to select a meal from the provided options for a given day and category.
     *
     * The available options are printed, and the user must enter one of the valid choices.
     *
     * @param day The day for which the meal is being chosen.
     * @param category The meal category (e.g., "breakfast").
     * @param options A list of available meal options.
     * @return The chosen meal as a String.
     */
    fun getMealChoiceForDay(day: String, category: String, options: List<String>): String {
        for (option in options) {
            println(option)
        }
        println("Choose the $category for $day from the list above:")
        while (true) {
            val choice = readln()
            if (!options.contains(choice)) {
                println("This meal doesn’t exist. Choose a meal from the list above.")
            } else {
                return choice
            }
        }
    }

    /**
     * Presents the meal plan for the week.
     *
     * For each day, it prints the day name and the corresponding breakfast, lunch, and dinner.
     *
     * @param week A list of day names.
     * @param breakfast A list of breakfast choices (must correspond with the week list).
     * @param lunch A list of lunch choices (must correspond with the week list).
     * @param dinner A list of dinner choices (must correspond with the week list).
     */
    fun presentPlan(
        week: MutableList<String>,
        breakfast: MutableList<String>,
        lunch: MutableList<String>,
        dinner: MutableList<String>) {
        for (day in week) {
            println(day)
            println("Breakfast: ${breakfast[week.indexOf(day)]}")
            println("Lunch: ${lunch[week.indexOf(day)]}")
            println("Dinner: ${dinner[week.indexOf(day)]}\n")
        }
    }

    /**
     * Displays a confirmation message that the meals for the given day have been planned.
     *
     * @param day The day for which the meals have been planned.
     */
    fun validDayPlannedResponse(day: String) = println("Yeah! We planned the meals for $day.")

    /**
     * Displays the main command options to the user.
     */
    fun displayOptions() = println("What would you like to do (add, show, plan, save, exit)?")

    /**
     * Reads and returns the user's command input.
     *
     * @return The trimmed command entered by the user.
     */
    fun getCommand(): String {
        return readln().trim()
    }

    /**
     * Displays the given message to the user.
     *
     * @param message The message to be displayed.
     */
    fun displayMessage(message: String) = println(message)

    /**
     * Prompts the user to input a filename and returns it.
     *
     * @return The filename entered by the user.
     */
    fun promptForFileName(): String {
        println("Input a filename:")
        return readln().trim()
    }

    /**
     * Prompts the user to enter a valid meal category for adding a meal.
     *
     * Repeats until the user enters one of the valid categories.
     *
     * @return A valid meal category.
     */
    fun promptForMealCategory(): String {
        while (true) {
            println("Which meal do you want to add (breakfast, lunch, dinner)?")
            val category = readln().trim()
            if (validCategories.contains(category)) {
                return category
            } else {
                println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
            }
        }
    }

    /**
     * Prompts the user to input the meal's name and validates its format.
     *
     * @return A valid meal name.
     */
    fun promptForMealName(): String {
        while (true) {
            println("Input the meal's name:")
            val mealName = readln().trim()
            if (checkFormat(mealName)) {
                return mealName
            }
        }
    }

    /**
     * Prompts the user to input ingredients as a comma-separated list.
     *
     * Splits the input and validates each ingredient's format.
     *
     * @return A list of valid ingredients.
     */
    fun promptForIngredients(): List<String> {
        while (true) {
            println("Input the ingredients:")
            var unTrimmedIngredients = readln().split(",", ", ")
            val ingredients = mutableListOf<String>()
            for (ingredient in unTrimmedIngredients) {
                ingredients.add(ingredient.trim(',', ' '))
            }
            if (ingredients.all { checkFormat(it) }) {
                return ingredients
            }
        }
    }

    /**
     * Prompts the user to enter a meal category for printing the plan.
     *
     * @return A valid meal category.
     */
    fun promptForCategoryForPrinting(): String {
        while (true) {
            println("Which category do you want to print (breakfast, lunch, dinner)?")
            val category = readln()
            if (validCategories.contains(category)) {
                return category
            } else {
                println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
            }
        }
    }

    /**
     * Displays the meal list along with ingredients.
     *
     * It prints the category and iterates over each MealInfo to print the meal name
     * and its associated ingredients, retrieved via the provided function.
     *
     * @param category The category of meals to display.
     * @param mealInfos A list of MealInfo objects (each containing a meal name and ID).
     * @param getIngredientsByMealId A function that returns a list of ingredients given a meal ID.
     */
    fun displayMealList(
        category: String, mealInfos: List<MealInfo>, getIngredientsByMealId: (Int) -> MutableList<String>) {
        println("Category: $category")
        println()
        for (mealInfo in mealInfos) {
            println("Name: ${mealInfo.meal}")
            println("Ingredients:")
            val ingredients = getIngredientsByMealId(mealInfo.mealId)
            for (ingredient in ingredients) {
                println(ingredient)
            }
            println()
        }
    }
}