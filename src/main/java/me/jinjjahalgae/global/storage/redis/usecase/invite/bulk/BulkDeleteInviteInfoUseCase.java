package me.jinjjahalgae.global.storage.redis.usecase.invite.bulk;

import java.util.List;

public interface BulkDeleteInviteInfoUseCase {
    // 단건 ID가 아닌, ID 리스트를 받도록 변경
    void execute(List<Long> contractIds);
}
