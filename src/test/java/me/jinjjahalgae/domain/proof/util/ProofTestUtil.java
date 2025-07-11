package me.jinjjahalgae.domain.proof.util;

import me.jinjjahalgae.domain.contract.entity.Contract;
import me.jinjjahalgae.domain.contract.enums.ContractType;
import me.jinjjahalgae.domain.feedback.entity.Feedback;
import me.jinjjahalgae.domain.feedback.enums.FeedbackStatus;
import me.jinjjahalgae.domain.participation.entity.Participation;
import me.jinjjahalgae.domain.proof.entities.Proof;
import me.jinjjahalgae.domain.proof.entities.ProofImage;
import me.jinjjahalgae.domain.proof.enums.ProofStatus;
import me.jinjjahalgae.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class ProofTestUtil {

    public static Proof createProof(Long id, String comment, ProofStatus status, Long contractId, Long proofId, LocalDateTime date) {
        Proof proof = Proof.builder()
                .comment(comment)
                .status(status)
                .contractId(contractId)
                .proofId(proofId)
                .build();

        ReflectionTestUtils.setField(proof, "id", id);
        ReflectionTestUtils.setField(proof, "createdAt", date);

        return proof;
    }

    public static Contract createContract(Long id, int totalSupervisor) {
        Contract cont = Contract.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title("Test Contract " + id)
                .goal("Test Goal")
                .penalty("Test Penalty")
                .reward("Test Reward")
                .life(10)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .build();

        cont.initialize();

        ReflectionTestUtils.setField(cont, "id", id);
        ReflectionTestUtils.setField(cont, "totalSupervisor", totalSupervisor);

        return cont;
    }

    public static ProofImage createProofImage() {
        return ProofImage.builder().build();
    }

    public static Feedback createFeedback(Long id, FeedbackStatus status, Proof proof) {
        Feedback feedback = Feedback.builder()
                .status(status)
                .proof(proof)
                .build();

        ReflectionTestUtils.setField(feedback, "id", id);
        return feedback;
    }

    // ------------------------------------------ slice test ----------------------------------------------
    public static Contract createContractBeforeSave(int totalSupervisor, User user) {
        Contract cont = Contract.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .title("Test Contract ")
                .goal("Test Goal")
                .penalty("Test Penalty")
                .reward("Test Reward")
                .life(10)
                .proofPerWeek(3)
                .oneOff(false)
                .type(ContractType.BASIC)
                .user(user)
                .build();

        cont.initialize();

        ReflectionTestUtils.setField(cont, "totalSupervisor", totalSupervisor);
        return cont;
    }

    public static Proof createProofBeforeSave(String comment, ProofStatus status, Long contractId, Long proofId) {
        return Proof.builder()
                .comment(comment)
                .status(status)
                .contractId(contractId)
                .proofId(proofId)
                .build();
    }

    public static ProofImage createProofImageBeforeSave(Proof proof, int index) {
        return ProofImage.builder()
                .imageKey("imageKey")
                .proof(proof)
                .index(index)
                .build();
    }

    public static Participation createParticipationBeforeSave(Contract contract, User user) {

        return Participation.builder()
                .contract(contract)
                .user(user)
                .build();
    }

    public static Feedback createFeedbackBeforeSave(String comment, FeedbackStatus status, Long userId, Proof proof) {
        return Feedback.builder()
                .status(status)
                .userId(userId)
                .proof(proof)
                .build();
    }
}
