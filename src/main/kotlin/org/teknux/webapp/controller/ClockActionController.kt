package org.teknux.webapp.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.teknux.webapp.model.ClockAction
import org.teknux.webapp.service.StoreService

@RestController
@RequestMapping("/api/actions")
class ClockActionController {

    @Autowired
    private lateinit var storeService: StoreService

    @GetMapping
    fun getAll(@RequestParam userId: Int?): Set<ClockAction> {
        userId?.let {
            storeService.getUser(it)?.let {
                return storeService.getActions(it).orEmpty()
            }
        }

        return storeService.getActions().orEmpty()
    }

    @PostMapping
    fun addAction(@RequestBody action: ClockAction): ResponseEntity<ClockAction> {
        action!!.let {
            it.userId!!.let {
                return ResponseEntity.ok(storeService.addAction(action))
            }
        }
    }
}