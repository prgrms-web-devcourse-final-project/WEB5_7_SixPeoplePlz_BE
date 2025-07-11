package me.jinjjahalgae.domain.contract.usecase.get.historylist;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.contract.usecase.get.list.dto.ContractListResponse;
import me.jinjjahalgae.domain.contract.usecase.get.historylist.dto.ContractHistoryRequest;
import me.jinjjahalgae.domain.participation.enums.Role;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetContractHistoryListUseCaseImpl implements GetContractHistoryListUseCase{

    @Override
    public ContractListResponse getContractHistoryList(Long userId, Role role, ContractHistoryRequest request) {
        // 파라미터들은 프론트 -> 컨트롤러 -> usecase로 넘어옴


        // status에는 null, COMPLETED(이행 성공), FAILED(이행 실패), ABANDONED(중간 포기)가능
        // 이게 아닌 status 들어왔으면
//        ErrorCode.INVALID_REQUEST

        // 키워드 검색
        // 계약종료일 < 입력된 날짜인 계약들

        // repository에서 조건에 맞는 Contract 검색하기


        return null;
    }
}
