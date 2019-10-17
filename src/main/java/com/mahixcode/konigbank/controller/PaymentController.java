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
package com.mahixcode.konigbank.controller;

import com.mahixcode.konigbank.model.ClientModel;
import com.mahixcode.konigbank.model.TransactionModel;
import com.mahixcode.konigbank.model.WalletModel;
import com.mahixcode.konigbank.pojo.*;
import com.mahixcode.konigbank.service.PaymentService;
import com.mahixcode.konigbank.service.PaymentStorageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author maximen39
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentStorageService paymentStorageService;

    public PaymentController(PaymentService paymentService, PaymentStorageService paymentStorageService) {
        this.paymentService = paymentService;
        this.paymentStorageService = paymentStorageService;
    }

    @PostMapping("/exchange")
    public List<WalletModel> exchange(@RequestBody ExchangeRequest exchangeRequest) {
        return paymentService.exchange(
                exchangeRequest.getFromWallet(),
                exchangeRequest.getToWallet(),
                exchangeRequest.getAmount()
        );
    }

    @PostMapping("/send")
    public List<WalletModel> send(@RequestBody SendRequest sendRequest) {
        return paymentService.sendTo(
                sendRequest.getFromWallet(),
                sendRequest.getToClient(),
                sendRequest.getAmount()
        );
    }

    @PostMapping("/deposit")
    public WalletModel deposit(@RequestBody DepositRequest depositRequest) {
        return paymentService.deposit(
                depositRequest.getWalletId(),
                depositRequest.getAmount()
        );
    }

    @PostMapping("/withdraw")
    public WalletModel withdraw(@RequestBody WithdrawRequest withdrawRequest) {
        return paymentService.withdraw(
                withdrawRequest.getWalletId(),
                withdrawRequest.getAmount()
        );
    }

    @GetMapping
    public List<TransactionModel> transaction() {
        return paymentStorageService.findTransactions();
    }

    @PutMapping("/wallet")
    public WalletModel wallet(@RequestBody WalletRequest walletRequest) {
        return paymentStorageService.saveWallet(new WalletModel()
                .setClient(new ClientModel().setId(walletRequest.getClientId()))
                .setBalance(walletRequest.getBalance())
        );
    }
}
