package com.pritechvior.mealeastimatonew

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pritechvior.mealeastimatonew.util.FoodPortionProcessor
import com.pritechvior.mealeastimatonew.util.PortionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FoodPortionProcessorTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testCalculatePortion_realData() {
        val result = FoodPortionProcessor.calculatePortion(
            context,
            ingredient = "Cereals (maize/rice)",
            ageGroup = "Adults (18+ yrs)",
            numberOfPeople = 2
        )
        assertTrue(result is PortionResult.RealData)
        val grams = (result as PortionResult.RealData).grams
        // The expected value is 307 (from JSON) * 2
        assertEquals(614.0, grams, 0.1)
    }

    @Test
    fun testCalculatePortion_notAvailable() {
        val result = FoodPortionProcessor.calculatePortion(
            context,
            ingredient = "Nonexistent Ingredient",
            ageGroup = "Adults (18+ yrs)",
            numberOfPeople = 2
        )
        assertTrue(result is PortionResult.NotAvailable)
    }
} 