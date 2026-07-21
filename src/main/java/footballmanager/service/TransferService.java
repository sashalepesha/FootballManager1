package footballmanager.service;

import footballmanager.domain.Transfer;
import footballmanager.exception.InvalidCurrencyException;
import footballmanager.repository.TransferRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransferService {

    private final TransferRepository transferRepository;
    private final CurrencyService currencyService;

    public TransferService(TransferRepository transferRepository, CurrencyService currencyService) {
        this.transferRepository = transferRepository;
        this.currencyService = currencyService;
    }

    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    public Transfer findOne(Long id) {
        return transferRepository.findById(id).orElseThrow(() -> new RuntimeException("Transfer not found"));
    }

    public Transfer save(Transfer transfer) {
        validateCurrency(transfer);
        return transferRepository.save(transfer);
    }

    public Transfer update(Transfer transfer) {
        validateCurrency(transfer);
        return transferRepository.save(transfer);
    }

    public void delete(Long id) {
        transferRepository.deleteById(id);
    }

    private void validateCurrency(Transfer transfer) {
        if (transfer.getTransferFeeUsd() == null || transfer.getTransferFeeByn() == null) {
            throw new InvalidCurrencyException("Transfer fee values are required.");
        }

        BigDecimal usdRate = currencyService.getUsdRate();

        BigDecimal expectedByn = transfer.getTransferFeeUsd().multiply(usdRate).setScale(4, RoundingMode.HALF_UP);

        BigDecimal difference = transfer.getTransferFeeByn().subtract(expectedByn).abs();

        System.out.println("========== TRANSFER VALIDATION ==========");
        System.out.println("USD from client = " + transfer.getTransferFeeUsd());
        System.out.println("BYN from client = " + transfer.getTransferFeeByn());
        System.out.println("Rate from NBRB = " + usdRate);
        System.out.println("Expected BYN = " + expectedByn);
        System.out.println("Difference = " + difference);
        System.out.println("=========================================");

        if (difference.compareTo(new BigDecimal("0.01")) > 0) {
            throw new InvalidCurrencyException("Transfer fee in BYN does not match current NBRB exchange rate.");
        }
    }
}
