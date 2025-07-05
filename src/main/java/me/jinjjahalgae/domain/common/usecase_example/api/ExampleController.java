package me.jinjjahalgae.domain.common.usecase_example;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleUseCase exampleUseCase;

    @PostMapping
    public ExampleResponse example(ExampleRequest req){
        return exampleUseCase.execute(req);
    }
}
