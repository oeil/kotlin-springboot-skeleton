package org.teknux.webapp.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.teknux.webapp.model.User
import org.teknux.webapp.service.StoreService


@RestController
@RequestMapping("/api/users")
class UserController {

    @Autowired
    private lateinit var storeService: StoreService

    @GetMapping
    fun getAll(): Iterable<User> {
        return storeService.getUsers()
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): ResponseEntity<User> {
        return storeService.getUser(id)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Int): ResponseEntity<Unit> {
        storeService.getUser(id)?.let {
            storeService.removeUser(id)
            return ResponseEntity(HttpStatus.OK)
        } ?: return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun addUser(@RequestBody user: User): ResponseEntity<User> {
        storeService.newUser(user)?.let {
            return ResponseEntity.ok(user)
        }
    }
}