package com.example.jump.core.domain

data class WorkoutCalorieEstimate(
  val minimumCalories: Int = 0,
  val maximumCalories: Int = 0,
  val referenceWeightKg: Int = DEFAULT_REFERENCE_WEIGHT_KG,
) {
  companion object {
    const val DEFAULT_REFERENCE_WEIGHT_KG = 70
  }
}
