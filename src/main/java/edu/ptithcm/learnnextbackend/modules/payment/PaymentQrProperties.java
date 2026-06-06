package edu.ptithcm.learnnextbackend.modules.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentQrProperties {
    private final String bankBin;
    private final String accountNumber;
    private final String accountName;

    public PaymentQrProperties(
            @Value("${payment.vietqr.bank-bin:970423}") String bankBin,
            @Value("${payment.vietqr.account-number:10000232922}") String accountNumber,
            @Value("${payment.vietqr.account-name:LEARNEXT}") String accountName
    ) {
        this.bankBin = bankBin;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }

    public String qrImageUrl(BigDecimal amount, String paymentCode) {
        if (paymentCode == null || paymentCode.isBlank()) {
            return null;
        }
        return "https://img.vietqr.io/image/" + bankBin + "-" + accountNumber + "-compact2.png"
                + "?amount=" + amount.toPlainString()
                + "&addInfo=" + paymentCode
                + "&accountName=" + accountName.replace(" ", "%20");
    }
}
