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
import com.mahixcode.konigbank.model.WalletModel;
import com.mahixcode.konigbank.pojo.ClientRequest;
import com.mahixcode.konigbank.service.ClientStorageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author maximen39
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientStorageService clientStorageService;

    public ClientController(ClientStorageService clientStorageService) {
        this.clientStorageService = clientStorageService;
    }

    @GetMapping
    public List<ClientModel> clients() {
        return this.clientStorageService.findClients();
    }

    @PutMapping
    public ClientModel create(@RequestBody ClientRequest clientRequest) {
        ClientModel clientModel = new ClientModel()
                .setName(clientRequest.getName())
                .setAddress(clientRequest.getAddress())
                .setAge(clientRequest.getAge());
        return this.clientStorageService.saveClient(clientModel);
    }

    @GetMapping("/{id}")
    public ClientModel client(@PathVariable long id) {
        Optional<ClientModel> clientOptional = this.clientStorageService.findClient(id);
        return clientOptional.orElseThrow(() -> new NullPointerException("Client not found!"));
    }

    @GetMapping("/{id}/wallets")
    public List<WalletModel> wallets(@PathVariable long id) {
        return clientStorageService.findWallets(id);
    }
}
