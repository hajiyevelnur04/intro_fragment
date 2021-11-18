package com.runle.intro.model

/**
 * Created by elha on 10.08.2021.
 */
data class WelcomeModel(
    var logo: String,
    var title: String,
    var description: String,
    var contentImage: String,
    var duration: Long,
    var backgroundColor: String,
    var getStartedButtonText: String = "Get Started",
)