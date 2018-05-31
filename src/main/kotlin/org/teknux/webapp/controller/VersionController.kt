package org.teknux.webapp.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/version")
class VersionController {

    @GetMapping
    fun get(): String {
        return "0.1.0"
    }
}