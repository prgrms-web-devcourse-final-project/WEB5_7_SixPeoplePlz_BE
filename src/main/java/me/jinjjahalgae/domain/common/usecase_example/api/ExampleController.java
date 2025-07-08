package me.jinjjahalgae.domain.common.usecase_example.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.common.usecase_example.usecase.interfaces.ExampleUseCase;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleRequest;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleResponse;
import me.jinjjahalgae.global.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleUseCase exampleUseCase;

    @PostMapping
    public ResponseEntity<CommonResponse<ExampleResponse>> example(ExampleRequest req){
        ExampleResponse result = exampleUseCase.execute(req);

        return ResponseEntity.status(201).body(CommonResponse.success(result));
    }

    @PostMapping("/2")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<ExampleResponse> example2(ExampleRequest req){
        ExampleResponse result = exampleUseCase.execute(req);
        
        return CommonResponse.success(result);
    }
}
