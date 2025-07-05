package me.jinjjahalgae.domain.common.usecase_example.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.common.usecase_example.usecase.interfaces.ExampleUseCase;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleRequest;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleResponse;
import me.jinjjahalgae.global.common.ApiResponse;
import me.jinjjahalgae.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleUseCase exampleUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ExampleResponse>> example(ExampleRequest req){
        ExampleResponse result = exampleUseCase.execute(req);

        return ResponseEntity.status(201).body(ApiResponse.success(result));
    }
}
