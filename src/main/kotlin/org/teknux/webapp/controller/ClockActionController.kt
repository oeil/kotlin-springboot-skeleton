package org.teknux.webapp.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.IStoreService

@RestController
@RequestMapping("/api/actions")
class ClockActionController {

    @Autowired
    private lateinit var storeService: IStoreService

    @GetMapping
    fun getAll(@RequestParam userId: Int?): Set<ClockAction> {
        return userId?.let {
            storeService.getActions(it).orEmpty()
        } ?: storeService.getActions().orEmpty()
    }

    @PostMapping
    fun addAction(@RequestBody action: ClockAction): ResponseEntity<ClockAction> {
        return ResponseEntity.ok(storeService.addAction(action))
    }
}