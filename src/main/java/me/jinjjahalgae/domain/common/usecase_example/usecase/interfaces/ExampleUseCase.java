package me.jinjjahalgae.domain.common.usecase_example.usecase.interfaces;

import me.jinjjahalgae.domain.common.UseCase;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleRequest;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleResponse;

public interface ExampleUseCase extends UseCase<ExampleRequest, ExampleResponse> {

    @Override
    ExampleResponse execute(ExampleRequest exampleRequest);
}
