package me.jinjjahalgae.domain.common.usecase_example;

import org.springframework.stereotype.Service;

@Service
public class ExampleUseCaseImpl implements ExampleUseCase {
    @Override
    public ExampleResponse execute(ExampleRequest exampleRequest) {
        return new ExampleResponse("수혁");
    }
}
