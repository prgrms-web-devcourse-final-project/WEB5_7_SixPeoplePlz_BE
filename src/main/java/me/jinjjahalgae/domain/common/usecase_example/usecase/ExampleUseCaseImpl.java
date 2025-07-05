package me.jinjjahalgae.domain.common.usecase_example.usecase;

import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleRequest;
import me.jinjjahalgae.domain.common.usecase_example.dto.ExampleResponse;
import me.jinjjahalgae.domain.common.usecase_example.usecase.interfaces.ExampleUseCase;
import org.springframework.stereotype.Service;

@Service
public class ExampleUseCaseImpl implements ExampleUseCase {
    @Override
    public ExampleResponse execute(ExampleRequest exampleRequest) {
        return new ExampleResponse("수혁");
    }
}
