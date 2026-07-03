package com.umeetech.photofixai.domain.model

/** Premium subscription plans shown on the Premium screen (UI only for now). */
data class PremiumPlan(
    val id: String,
    val title: String,
    val price: String,
    val period: String,
    val badge: String? = null,
    val highlighted: Boolean = false
) {
    companion object {
        val plans = listOf(
            PremiumPlan("monthly", "Monthly", "$4.99", "per month"),
            PremiumPlan("yearly", "Yearly", "$29.99", "per year", badge = "Best value", highlighted = true),
            PremiumPlan("lifetime", "Lifetime", "$59.99", "one-time")
        )

        val benefits = listOf(
            "No ads",
            "Unlimited exports",
            "HD export",
            "Batch background remover",
            "Premium templates",
            "Passport print sheets",
            "Product photo presets",
            "No watermark"
        )
    }
}
