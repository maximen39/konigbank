<template>
    <div id="app">
        <component :is="currentComponent" :props="currentProperties"></component>
    </div>
</template>

<script>
    import clients from './components/Clients.vue'
    import client from './components/Client.vue'
    import transactions from './components/Transactions.vue'

    export default {
        name: 'app',
        data() {
            return {
                currentComponent: clients,
                currentProperties: null,
            }
        },
        methods: {
            openClient(c) {
                this.currentProperties = c;
                this.currentComponent = client;
            },
            openTransactions() {
                this.currentProperties = null;
                this.currentComponent = transactions;
            },
            openClients() {
                this.currentProperties = null;
                this.currentComponent = clients;
            }
        }
    }
</script>

<style>

</style>
