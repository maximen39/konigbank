/*
 * Copyright (C) 2019 maximen39
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mahixcode.konigbank.service;

import com.mahixcode.konigbank.KeyLock;
import com.mahixcode.konigbank.model.ClientModel;
import com.mahixcode.konigbank.model.TransactionModel;
import com.mahixcode.konigbank.model.WalletModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author maximen39
 */
@Service
public class KonigbankPaymentService implements PaymentService {
    private KeyLock<Long> keyLock;
    private final PaymentStorageService paymentStorageService;
    private final ClientStorageService clientStorageService;

    public KonigbankPaymentService(PaymentStorageService paymentStorageService,
                                   ClientStorageService clientStorageService) {
        this.keyLock = new KeyLock<>();
        this.paymentStorageService = paymentStorageService;
        this.clientStorageService = clientStorageService;
    }

    @Override
    public List<WalletModel> exchange(long fromWalletId, long toWalletId, BigDecimal amount) {
        WalletModel withdrawWallet = syncWithdraw(fromWalletId, amount);
        WalletModel depositWallet = syncDeposit(toWalletId, amount);
        paymentStorageService.saveTransaction(new TransactionModel()
                .setFrom(withdrawWallet)
                .setTo(depositWallet)
                .setAmount(amount)
        );
        return Arrays.asList(withdrawWallet, depositWallet);
    }

    @Override
    public List<WalletModel> sendTo(long fromWalletId, long toClientId, BigDecimal amount) {
        List<WalletModel> wallets = clientStorageService.findWallets(toClientId);
        WalletModel toWallet = wallets.stream().findFirst().orElse(new WalletModel()
                .setClient(new ClientModel().setId(toClientId))
                .setBalance(BigDecimal.valueOf(0))
        );
        if (toWallet.getId() == 0) {
            paymentStorageService.saveWallet(toWallet);
        }
        return exchange(fromWalletId, toWallet.getId(), amount);
    }

    private WalletModel syncDeposit(long walletId, BigDecimal amount) {
        ReentrantLock lock = keyLock.getLock(walletId);
        try {
            lock.lock();
            WalletModel walletModel = paymentStorageService.findWallet(walletId)
                    .orElseThrow(() -> new NullPointerException("Wallet not found!"));
            walletModel.setBalance(walletModel.getBalance()
                    .add(amount)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
            );
            return paymentStorageService.saveWallet(walletModel);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WalletModel deposit(long walletId, BigDecimal amount) {
        WalletModel walletModel = syncDeposit(walletId, amount);
        paymentStorageService.saveTransaction(new TransactionModel()
                .setTo(walletModel)
                .setAmount(amount)
        );
        return walletModel;
    }

    private WalletModel syncWithdraw(long walletId, BigDecimal amount) {
        ReentrantLock lock = keyLock.getLock(walletId);
        try {
            lock.lock();
            WalletModel walletModel = paymentStorageService.findWallet(walletId)
                    .orElseThrow(() -> new NullPointerException("Wallet not found!"));
            if (walletModel.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Not enough money!");
            }
            walletModel.setBalance(walletModel.getBalance()
                    .subtract(amount)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
            );
            return paymentStorageService.saveWallet(walletModel);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public WalletModel withdraw(long walletId, BigDecimal amount) {
        WalletModel walletModel = syncWithdraw(walletId, amount);
        paymentStorageService.saveTransaction(new TransactionModel()
                .setFrom(walletModel)
                .setAmount(amount)
        );
        return walletModel;
    }
}
