package com.maira.assistant.core

enum class AssistantState {
    IDLE_STANDBY,      // Waiting for wake word
    WAKEWORD_DETECTED, // Wake word detected, preparing to listen
    LISTENING_COMMAND, // Actively listening for command
    PROCESSING,        // Processing command
    SPEAKING,          // Speaking response
    ERROR              // Error state, will recover
}
