package org.example.bookride

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookRideApplication

fun main(args: Array<String>) {
    runApplication<BookRideApplication>(*args)
}
