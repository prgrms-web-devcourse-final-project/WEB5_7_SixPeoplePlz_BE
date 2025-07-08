package me.jinjjahalgae.domain.proof.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.proof.usecase.interfaces.CreateProofUseCase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProofController {

    private final CreateProofUseCase proofCreateUseCase;



}
