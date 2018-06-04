package org.teknux.webapp.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.StoreService

@RestController
@RequestMapping("/api/actions")
class ClockActionController {

    @Autowired
    private lateinit var storeService: StoreService

    @GetMapping
    fun getAll(@RequestParam userId: String?): Set<ClockAction> {
        userId?.let {
            storeService.getUser(it)?.let {
                return storeService.getActions(it).orEmpty()
            }
        }

        return storeService.getActions().orEmpty()
    }
}