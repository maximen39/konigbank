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

import com.mahixcode.konigbank.jpa.ClientDao;
import com.mahixcode.konigbank.jpa.TransactionDao;
import com.mahixcode.konigbank.jpa.WalletDao;
import com.mahixcode.konigbank.model.ClientModel;
import com.mahixcode.konigbank.model.TransactionModel;
import com.mahixcode.konigbank.model.WalletModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author maximen39
 */
@Service
@Transactional(readOnly = true)
public class KonigbankJpaService implements ClientStorageService, PaymentStorageService {
    private final ClientDao clientDao;
    private final WalletDao walletDao;
    private final TransactionDao transactionDao;

    public KonigbankJpaService(ClientDao clientDao, WalletDao walletDao, TransactionDao transactionDao) {
        this.clientDao = clientDao;
        this.walletDao = walletDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public Optional<ClientModel> findClient(long id) {
        return clientDao.findById(id);
    }

    @Override
    public List<ClientModel> findClients() {
        return clientDao.findAll();
    }

    @Override
    public List<WalletModel> findWallets(long clientId) {
        return walletDao.findAllByClientId(clientId);
    }

    @Override
    @Transactional
    public ClientModel saveClient(ClientModel clientModel) {
        return clientDao.save(clientModel);
    }

    @Override
    public Optional<WalletModel> findWallet(long walletId) {
        return walletDao.findById(walletId);
    }

    @Override
    @Transactional
    public WalletModel saveWallet(WalletModel walletModel) {
        return walletDao.save(walletModel);
    }

    @Override
    public List<TransactionModel> findTransactions() {
        return transactionDao.findAll();
    }

    @Override
    @Transactional
    public TransactionModel saveTransaction(TransactionModel transactionModel) {
        return transactionDao.save(transactionModel);
    }
}
