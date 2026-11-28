package com.talentbridge.finance;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import com.talentbridge.finance.dto.RecordRewardTransactionRequest;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/finance")
@PreAuthorize("hasAnyRole('FINANCE', 'SUPER_ADMIN')")
public class FinanceController {

    private final RewardLedgerService ledgerService;
    private final RewardTransactionRepository repository;

    public FinanceController(RewardLedgerService ledgerService, RewardTransactionRepository repository) {
        this.ledgerService = ledgerService;
        this.repository = repository;
    }

    @GetMapping("/ledger")
    public ApiResponse<PageResponse<RewardTransaction>> ledger(@RequestParam(required = false) RewardStatus status,
                                                                 @RequestParam(required = false) Integer page,
                                                                 @RequestParam(required = false) Integer size) {
        var pageable = PageRequestUtil.of(page, size);
        var result = status != null ? repository.findByStatus(status, pageable) : repository.findAll(pageable);
        return ApiResponse.ok(PageResponse.from(result), CorrelationIdFilter.current());
    }

    @PostMapping("/ledger")
    public ApiResponse<RewardTransaction> recordExpected(@AuthenticationPrincipal CurrentUser user,
                                                           @Valid @RequestBody RecordRewardTransactionRequest request) {
        return ApiResponse.ok(ledgerService.recordExpected(request, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }

    public record AdvanceRequest(String evidenceReference, LocalDate receivedDate, String secondApproverUserId) {
    }

    @PatchMapping("/ledger/{referralId}/status")
    public ApiResponse<RewardTransaction> advance(@AuthenticationPrincipal CurrentUser user,
                                                    @PathVariable String referralId,
                                                    @RequestParam RewardStatus to,
                                                    @RequestBody AdvanceRequest request) {
        var received = request.receivedDate() != null ? new RewardLedgerService.LocalDateReceived(request.receivedDate()) : null;
        return ApiResponse.ok(ledgerService.advance(referralId, to, request.evidenceReference(), received,
                        user.getUserId(), user.getRole().name(), request.secondApproverUserId()),
                CorrelationIdFilter.current());
    }

    @GetMapping("/summary")
    public ApiResponse<java.util.Map<String, Object>> summary() {
        return ApiResponse.ok(ledgerService.revenueSummary(), CorrelationIdFilter.current());
    }
}
