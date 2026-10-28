package com.maira.assistant.multistep

data class Step(
    val id: Int,
    val action: String,
    val description: String,
    val status: String = "pending"
)

class MultiStepExecutor {
    private val steps = mutableListOf<Step>()
    
    fun createPlan(command: String): List<Step> {
        steps.clear()
        if (command.lowercase().contains("routine")) {
            steps.add(Step(1, "weather", "मौसम"))
            steps.add(Step(2, "news", "समाचार"))
        } else {
            steps.add(Step(1, "understand", "समझें"))
            steps.add(Step(2, "execute", "करें"))
        }
        return steps
    }
    
    fun getStatus(): String = "${steps.count { it.status == "done" }}/${steps.size} पूर्ण"
}
