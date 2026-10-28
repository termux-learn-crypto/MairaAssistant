package com.maira.assistant.planning

data class TaskStep(
    val stepNumber: Int,
    val description: String,
    val completed: Boolean = false
)

class TaskPlanner {
    
    fun planTask(task: String): List<TaskStep> {
        val steps = mutableListOf<TaskStep>()
        when {
            task.contains("meal") || task.contains("खाना") -> {
                steps.add(TaskStep(1, "रेसिपी खोजें"))
                steps.add(TaskStep(2, "सामग्री चेक करें"))
                steps.add(TaskStep(3, "खाना बनाना शुरू करें"))
                steps.add(TaskStep(4, "परोसें"))
            }
            task.contains("travel") || task.contains("यात्रा") -> {
                steps.add(TaskStep(1, "गंतव्य तय करें"))
                steps.add(TaskStep(2, "मार्ग देखें"))
                steps.add(TaskStep(3, "जरूरी सामान पैक करें"))
                steps.add(TaskStep(4, "यात्रा शुरू करें"))
            }
            else -> {
                steps.add(TaskStep(1, "कार्य समझें"))
                steps.add(TaskStep(2, "आवश्यक चीजें इकट्ठा करें"))
                steps.add(TaskStep(3, "कार्य निष्पादित करें"))
                steps.add(TaskStep(4, "परिणाम जांचें"))
            }
        }
        return steps
    }
    
    fun verifySteps(steps: List<TaskStep>): String {
        val completed = steps.count { it.completed }
        val total = steps.size
        return "पूर्ण: $completed/$total चरण"
    }
}
