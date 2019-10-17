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

import com.mahixcode.konigbank.model.WalletModel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author maximen39
 */
public interface PaymentService {

    List<WalletModel> exchange(long fromWalletId, long toWalletId, BigDecimal amount);

    List<WalletModel> sendTo(long fromWalletId, long toClientId, BigDecimal amount);

    WalletModel deposit(long walletId, BigDecimal amount);

    WalletModel withdraw(long walletId, BigDecimal amount);

}
