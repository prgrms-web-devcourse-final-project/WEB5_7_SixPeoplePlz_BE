package me.jinjjahalgae.domain.common.usecase_example;

import me.jinjjahalgae.domain.common.UseCase;

public interface ExampleUseCase extends UseCase<ExampleRequest, ExampleResponse> {
    @Override
    ExampleResponse execute(ExampleRequest exampleRequest);
}
