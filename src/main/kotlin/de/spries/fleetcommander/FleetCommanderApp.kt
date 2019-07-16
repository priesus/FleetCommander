package de.spries.fleetcommander

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@SpringBootApplication
open class FleetCommanderApp {

    @Bean
    open fun jacksonBuilder(): Jackson2ObjectMapperBuilder {
        val b = Jackson2ObjectMapperBuilder()
        b.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        return b
    }
}

fun main(args: Array<String>) {
    runApplication<FleetCommanderApp>(*args)
}