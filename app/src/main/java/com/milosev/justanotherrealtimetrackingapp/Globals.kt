package com.milosev.justanotherrealtimetrackingapp

object IntentAction {
    const val START_FOREGROUND_TICK_SERVICE = "startForegroundTickService"
    const val STOP_FOREGROUND_TICK_SERVICE = "stopForegroundTickService"
    const val RESTART_FOREGROUND_TICK_SERVICE = "restartForegroundTickService"
    const val MAIN_ACTIVITY_RECEIVER = "mainActivityReceiver"
    const val NUM_OF_TICKS = "numOfTicks"
}

object IntentExtras {
    const val NUM_OF_TICKS = "numOfTicks"
    const val NUM_OF_SECONDS_FOR_TICK = "numOfSecondsForTick"
}