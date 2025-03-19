package mealplanner

import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import kotlin.io.path.writeText

/**
 * The entry point of the Meal Planner application.
 *
 * This function establishes the database connection, initializes the database schema,
 * creates an instance of MealPlanner, and starts the command loop.
 */
fun main() {
    val connection = DriverManager.getConnection("jdbc:sqlite:meals.db")
    val dbSetup = DataBaseSetup(connection)
    dbSetup.initialize()
    val mealplanner = MealPlanner(connection)
    mealplanner.waitForCommand()
}