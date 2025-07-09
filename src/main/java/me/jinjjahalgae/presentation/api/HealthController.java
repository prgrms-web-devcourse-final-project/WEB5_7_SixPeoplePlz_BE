package me.jinjjahalgae.presentation.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health API", description = "Health API")
@RestController
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
