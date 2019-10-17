<template>
    <div id="clients">
        <div>
            <b-button v-bind:style="`margin-bottom: 10px`" @click="openTransactions()">Transactions</b-button>

            <b-table striped hover :busy="isBusy" :fields="fields" :items="items">
                <template v-slot:cell(name)="data">
                    <b-link @click="showClient(data.item)">{{ data.value }}</b-link>
                </template>
                <template v-slot:table-busy>
                    <div class="text-center text-danger my-2">
                        <b-spinner class="align-middle"></b-spinner>
                        <strong>Loading...</strong>
                    </div>
                </template>
            </b-table>
        </div>
        <b-button v-b-modal.createClient>Add client</b-button>

        <b-modal @ok="onSubmit" id="createClient" ref="createClient" title="Create Client">
            <b-form>
                <b-form-group id="input-group-1" label="Name:" label-for="input-1">
                    <b-form-input
                            id="input-1"
                            v-model="form.name"
                            required
                            placeholder="Enter name"
                    ></b-form-input>
                </b-form-group>
                <b-form-group id="input-group-2" label="Address:" label-for="input-2">
                    <b-form-input
                            id="input-2"
                            v-model="form.address"
                            required
                            placeholder="Enter address"
                    ></b-form-input>
                </b-form-group>
                <b-form-group id="input-group-3" label="Age:" label-for="input-3">
                    <b-form-input
                            id="input-3"
                            v-model="form.age"
                            required
                            type="number"
                            placeholder="Enter age"
                    ></b-form-input>
                </b-form-group>
            </b-form>
        </b-modal>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
        name: "clients",
        data() {
            return {
                fields: [
                    {key: 'name', label: 'Name'},
                    {key: 'address', label: 'Address'},
                    {key: 'age', label: 'Age'},
                    {key: 'createdAt', label: 'Created Date', formatter: value => new Date(value).toLocaleString()}
                ],
                form: {name: '', address: '', age: ''},
                items: null,
                isBusy: true
            }
        },
        mounted() {
            axios.get('http://localhost/client')
                .then(response => {
                    this.items = response.data;
                    this.isBusy = false;
                });
        },
        methods: {
            showClient(c) {
                this.$parent.openClient(c);
            },
            openTransactions() {
                this.$parent.openTransactions();
            },
            onSubmit() {
                this.$refs['createClient'].hide();
                axios.put('http://localhost/client', this.form)
                    .then(value => (this.items.push(value.data)));
            }
        }
    }
</script>

<style>
    #clients {
        margin: 60px auto auto;
        max-width: 40%;
    }
</style>