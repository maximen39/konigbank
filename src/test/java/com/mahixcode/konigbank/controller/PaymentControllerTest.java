package com.mahixcode.konigbank.controller;

import com.mahixcode.konigbank.model.WalletModel;
import com.mahixcode.konigbank.pojo.DepositRequest;
import com.mahixcode.konigbank.pojo.ExchangeRequest;
import com.mahixcode.konigbank.pojo.SendRequest;
import com.mahixcode.konigbank.pojo.WithdrawRequest;
import com.mahixcode.konigbank.service.ClientStorageService;
import com.mahixcode.konigbank.service.KonigbankPaymentService;
import com.mahixcode.konigbank.service.PaymentService;
import com.mahixcode.konigbank.service.PaymentStorageService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author maximen39
 */
class PaymentControllerTest {
    private PaymentStorageService paymentStorageService;
    private ClientStorageService clientStorageService;
    private PaymentService paymentService;
    private Random random = new Random();

    public PaymentControllerTest() {
        this.paymentStorageService = mock(PaymentStorageService.class);
        this.clientStorageService = mock(ClientStorageService.class);

        when(paymentStorageService.saveWallet(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        this.paymentService = new KonigbankPaymentService(
                paymentStorageService,
                clientStorageService
        );
    }

    @Test
    public void exchange() {
        PaymentController paymentController = new PaymentController(paymentService, paymentStorageService);

        BigDecimal fromBalance = BigDecimal.valueOf(random.nextInt(5000) + 1000);
        BigDecimal toBalance = BigDecimal.valueOf(random.nextInt(5000));
        BigDecimal amount = BigDecimal.valueOf(random.nextInt(1000));

        WalletModel fromWallet = new WalletModel()
                .setBalance(fromBalance)
                .setId(1);
        WalletModel toWallet = new WalletModel()
                .setBalance(toBalance)
                .setId(2);
        mockFindWallet(fromWallet, toWallet);
        ExchangeRequest exchangeRequest = new ExchangeRequest()
                .setFromWallet(fromWallet.getId())
                .setToWallet(toWallet.getId())
                .setAmount(amount);
        List<WalletModel> exchange = paymentController.exchange(exchangeRequest);

        assertEquals(
                fromBalance.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                exchange.get(0).getBalance()
        );
        assertEquals(
                toBalance.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                exchange.get(1).getBalance()
        );
    }

    @Test
    public void send() {
        PaymentController paymentController = new PaymentController(paymentService, paymentStorageService);

        BigDecimal fromBalance = BigDecimal.valueOf(random.nextInt(5000) + 1000);
        BigDecimal toBalance = BigDecimal.valueOf(random.nextInt(5000));
        BigDecimal amount = BigDecimal.valueOf(random.nextInt(1000));

        WalletModel fromWallet = new WalletModel()
                .setBalance(fromBalance)
                .setId(1);
        WalletModel toWallet = new WalletModel()
                .setBalance(toBalance)
                .setId(2);
        WalletModel anotherWallet = new WalletModel()
                .setBalance(BigDecimal.valueOf(256))
                .setId(3);

        mockFindWallet(fromWallet, toWallet);
        mockFindWallets(1, Arrays.asList(toWallet, anotherWallet));

        SendRequest sendRequest = new SendRequest()
                .setFromWallet(fromWallet.getId())
                .setToClient(1)
                .setAmount(amount);
        List<WalletModel> send = paymentController.send(sendRequest);

        assertEquals(
                fromBalance.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                send.get(0).getBalance()
        );
        assertEquals(
                toBalance.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                send.get(1).getBalance()
        );
    }

    @Test
    public void deposit() {
        PaymentController paymentController = new PaymentController(paymentService, paymentStorageService);

        BigDecimal balance = BigDecimal.valueOf(random.nextInt(5000));
        BigDecimal amount = BigDecimal.valueOf(random.nextInt(1000));

        WalletModel wallet = new WalletModel()
                .setBalance(balance)
                .setId(1);

        mockFindWallet(wallet);

        DepositRequest depositRequest = new DepositRequest()
                .setWalletId(wallet.getId())
                .setAmount(amount);

        WalletModel deposit = paymentController.deposit(depositRequest);

        assertEquals(
                balance.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                deposit.getBalance()
        );
        assertThrows(
                NullPointerException.class,
                () -> paymentController.deposit(depositRequest.setWalletId(0))
        );
    }

    @Test
    public void withdraw() {
        PaymentController paymentController = new PaymentController(paymentService, paymentStorageService);

        BigDecimal balance = BigDecimal.valueOf(random.nextInt(5000));
        BigDecimal amount = BigDecimal.valueOf(random.nextInt(1000));

        WalletModel wallet = new WalletModel()
                .setBalance(balance)
                .setId(1);

        mockFindWallet(wallet);

        WithdrawRequest withdrawRequest = new WithdrawRequest()
                .setWalletId(wallet.getId())
                .setAmount(amount);

        WalletModel deposit = paymentController.withdraw(withdrawRequest);

        assertEquals(
                balance.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP),
                deposit.getBalance()
        );
        wallet.setBalance(BigDecimal.valueOf(0));
        assertThrows(
                IllegalStateException.class,
                () -> paymentController.withdraw(withdrawRequest)
        );
        assertThrows(
                NullPointerException.class,
                () -> paymentController.withdraw(withdrawRequest.setWalletId(0))
        );
    }

    private void mockFindWallet(WalletModel... wallets) {
        for (WalletModel model : wallets) {
            when(paymentStorageService.findWallet(model.getId()))
                    .thenReturn(Optional.of(model));
        }
    }

    private void mockFindWallets(long clientId, List<WalletModel> wallets) {
        when(clientStorageService.findWallets(clientId))
                .thenReturn(wallets);
    }
}