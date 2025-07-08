package me.jinjjahalgae.api;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.invite.usecase.interfaces.CreateInviteLinkUseCase;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController {

    private final CreateInviteLinkUseCase createInviteLinkUseCase;


}
