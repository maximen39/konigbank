<template>
    <div id="client">
        <b-button v-bind:style="`margin-bottom: 10px`" @click="back()">Back</b-button>
        <b-alert v-model="show" variant="danger" dismissible>
            The client does not have wallets!
        </b-alert>
        <b-table striped hover :busy="isBusy" :fields="fields" :items="wallets">
            <template v-slot:table-busy>
                <div class="text-center text-danger my-2">
                    <b-spinner class="align-middle"></b-spinner>
                    <strong>Loading...</strong>
                </div>
            </template>
        </b-table>
        <b-button v-b-modal.createWallet class="m-1">Add Wallet</b-button>
        <b-button v-show="wallets.length !== 0" variant="info" v-b-modal.sendClient class="m-1">Send to client
        </b-button>
        <b-button v-show="wallets.length !== 0" variant="info" v-b-modal.exchange class="m-1">Exchange</b-button>
        <b-button v-show="wallets.length !== 0" variant="success" v-b-modal.deposit class="m-1">Deposit</b-button>
        <b-button v-show="wallets.length !== 0" variant="danger" v-b-modal.withdraw class="m-1">Withdraw</b-button>
        <b-modal @ok="onCreateWallet" id="createWallet" ref="createWallet" title="Add Wallet">
            <b-form>
                <b-form-group id="input-group-3" label="Balance:" label-for="input-3">
                    <b-form-input
                            id="input-3"
                            v-model="createWalletForm.balance"
                            required
                            type="number"
                            placeholder="Enter balance"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
        <b-modal @ok="onSendClient" id="sendClient" ref="sendClient" title="Send money to client">
            <b-form>
                <b-form-group id="input-group-1" label="From wallet:" label-for="input-1">
                    <b-form-select
                            id="input-1"
                            v-model="sendForm.fromWallet"
                            :options="optionWallets"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-2" label="To client:" label-for="input-2">
                    <b-form-select
                            id="input-2"
                            v-model="sendForm.toClient"
                            :options="optionClients"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-3" label="Amount:" label-for="input-3">
                    <b-form-input
                            id="input-3"
                            v-model="sendForm.amount"
                            required
                            min="0"
                            type="number"
                            placeholder="Enter amount"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
        <b-modal @ok="onExchange" id="exchange" ref="exchange" title="Exchange money">
            <b-form>
                <b-form-group id="input-group-1" label="From wallet:" label-for="input-1">
                    <b-form-select
                            id="input-1"
                            v-model="exchangeForm.fromWallet"
                            :options="optionWallets"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-2" label="To wallet:" label-for="input-2">
                    <b-form-select
                            id="input-2"
                            v-model="exchangeForm.toWallet"
                            :options="optionWallets"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-3" label="Balance:" label-for="input-3">
                    <b-form-input
                            id="input-3"
                            v-model="exchangeForm.amount"
                            required
                            min="0"
                            type="number"
                            placeholder="Enter balance"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
        <b-modal @ok="onDeposit" id="deposit" ref="deposit" title="Deposit">
            <b-form>
                <b-form-group id="input-group-1" label="Wallet:" label-for="input-1">
                    <b-form-select
                            id="input-1"
                            v-model="ioBalanceForm.walletId"
                            :options="optionWallets"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-2" label="Amount:" label-for="input-2">
                    <b-form-input
                            id="input-2"
                            v-model="ioBalanceForm.amount"
                            required
                            min="0"
                            type="number"
                            placeholder="Enter amount"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
        <b-modal @ok="onWithdraw" id="withdraw" ref="withdraw" title="Withdraw">
            <b-form>
                <b-form-group id="input-group-1" label="Wallet:" label-for="input-1">
                    <b-form-select
                            id="input-1"
                            v-model="ioBalanceForm.walletId"
                            :options="optionWallets"
                            required
                    ></b-form-select>
                </b-form-group>
                <b-form-group id="input-group-2" label="Amount:" label-for="input-2">
                    <b-form-input
                            id="input-2"
                            v-model="ioBalanceForm.amount"
                            required
                            min="0"
                            type="number"
                            placeholder="Enter amount"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: 'client',
        props: {
            props: null
        },
        data() {
            return {
                fields: [
                    {key: 'id', label: 'Number'},
                    {key: 'balance', label: 'Balance'},
                    {key: 'createdAt', label: 'Created Date', formatter: value => new Date(value).toLocaleString()}
                ],
                createWalletForm: {balance: 0, clientId: this.props.id},
                exchangeForm: {toWallet: 0, fromWallet: 0, amount: 0},
                sendForm: {toClient: 0, fromWallet: 0, amount: 0},
                ioBalanceForm: {amount: 0, walletId: null},
                optionWallets: [],
                optionClients: [],
                wallets: [],
                show: false,
                isBusy: true
            }
        },
        mounted() {
            axios.get('http://localhost/client/' + this.props.id + '/wallets').then(value => {
                    if (value.data.length !== 0) {
                        this.wallets = value.data;
                        this.updateOptions();
                    } else {
                        this.show = this;
                    }
                    this.isBusy = false;
                }
            );
            axios.get('http://localhost/client').then(value => {
                this.optionClients = value.data.map(o => ({text: o.name + ' (ID: ' + o.id + ')', value: o.id}));
            });
        },
        methods: {
            back() {
                this.$parent.openClients()
            },
            updateOptions() {
                if (this.wallets.length !== 0) {
                    this.optionWallets = this.wallets.map(o => ({text: 'ID: ' + o.id, value: o.id}));
                    this.exchangeForm.fromWallet = this.wallets[0].id;
                    this.exchangeForm.toWallet = this.wallets[0].id;
                    this.ioBalanceForm.walletId = this.wallets[0].id;
                    this.sendForm.fromWallet = this.wallets[0].id;
                }
            },
            onCreateWallet() {
                this.$refs['createWallet'].hide();
                axios.put('http://localhost/payment/wallet', this.createWalletForm).then(value => {
                    this.wallets.push(value.data);
                    this.show = false;
                    this.updateOptions();
                });
            },
            onSendClient() {
                this.$refs['sendClient'].hide();
                axios.post('http://localhost/payment/send', this.sendForm).then(value => {
                    value.data.forEach((ro) => {
                        this.wallets.forEach((o, index) => {
                            if (ro.id === o.id) {
                                this.wallets.splice(index, 1, ro);
                            }
                        });
                    });
                });
            },
            onExchange() {
                this.$refs['exchange'].hide();
                axios.post('http://localhost/payment/exchange', this.exchangeForm).then(value => {
                    value.data.forEach((ro) => {
                        this.wallets.forEach((o, index) => {
                            if (ro.id === o.id) {
                                this.wallets.splice(index, 1, ro);
                            }
                        });
                    });
                });
            },
            onDeposit() {
                this.$refs['deposit'].hide();
                axios.post('http://localhost/payment/deposit', this.ioBalanceForm).then(value => {
                    this.wallets.forEach((o, index) => {
                        if (value.data.id === o.id) {
                            this.wallets.splice(index, 1, value.data);
                        }
                    });
                });
            },
            onWithdraw() {
                this.$refs['withdraw'].hide();
                axios.post('http://localhost/payment/withdraw', this.ioBalanceForm).then(value => {
                    this.wallets.forEach((o, index) => {
                        if (value.data.id === o.id) {
                            this.wallets.splice(index, 1, value.data);
                        }
                    });
                });
            }
        }
    }
</script>

<style>
    #client {
        margin: 60px auto auto;
        max-width: 40%;
    }
</style>