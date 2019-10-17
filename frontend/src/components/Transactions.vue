<template>
    <div id="client">
        <b-button v-bind:style="`margin-bottom: 10px`" @click="back()">Back</b-button>
        <b-form-group
                label="Filter"
                label-cols-sm="3"
                label-align-sm="right"
                label-size="sm"
                label-for="filterInput"
        >
            <b-input-group size="sm">
                <b-form-input
                        v-model="filter"
                        type="search"
                        id="filterInput"
                        placeholder="Type to Search"
                ></b-form-input>
                <b-input-group-append>
                    <b-button @click="filter = ''">Clear</b-button>
                </b-input-group-append>
            </b-input-group>
        </b-form-group>
        <b-table
                id="table-transition-example"
                :items="items"
                :fields="fields"
                :filter="filter"
                @filtered="onFiltered"
                striped
                small
                sticky-header="600px"
                primary-key="createdAt"
                :tbody-transition-props="transProps"
        >
            <template v-slot:cell(show_details)="row">
                <b-button size="sm" @click="showDetails(row)" class="mr-2">
                    {{ row.detailsShowing ? 'Hide' : 'Show'}} Details
                </b-button>
            </template>
            <template v-slot:row-details="row">
                <b-card>
                    <b-spinner :ref="`spinner-` + row.item.id" v-show="!row.item.spinner" type="grow"
                               label="Loading..."></b-spinner>
                    <span>{{row.item.info}}</span>
                </b-card>
            </template>
        </b-table>
    </div>
</template>

<script>
    import axios from 'axios';
    import Vue from 'vue';

    export default {
        name: "transactions",
        data() {
            return {
                transProps: {
                    name: 'flip-list'
                },
                filter: null,
                items: null,
                fields: [
                    {key: 'from.id', sortable: true, label: 'Wallet From'},
                    {key: 'to.id', sortable: true, label: 'Wallet To'},
                    {key: 'amount', sortable: true, label: 'Amount'},
                    {
                        key: 'createdAt',
                        sortable: true,
                        label: 'Created Date',
                        formatter: value => new Date(value).toLocaleString()
                    },
                    {key: 'show_details', label: 'Action'}
                ]
            }
        },
        mounted() {
            axios.get('http://localhost/payment').then(value => (this.items = value.data.map(value => {
                let _cellVariants = {};
                if (value.from === null) {
                    _cellVariants['from.id'] = 'danger';
                }
                if (value.to === null) {
                    _cellVariants['to.id'] = 'danger';
                }
                value._cellVariants = _cellVariants;
                return value;
            })));
        },
        computed: {
            sortOptions() {
                return this.fields
                    .filter(f => f.sortable)
                    .map(f => {
                        return {text: f.label, value: f.key}
                    })
            }
        },
        methods: {
            back() {
                this.$parent.openClients()
            },
            onFiltered() {

            },
            showDetails(row) {
                row.toggleDetails();
                if (row.item.spinner) {
                    return;
                }
                let item = row.item;
                if (item.from !== null && item.to !== null) {
                    Promise.all([
                        axios.get('http://localhost/client/' + item.from.clientIdd),
                        axios.get('http://localhost/client/' + item.to.clientIdd)
                    ]).then(value => {
                        let clientFrom = value[0].data;
                        let clientTo = value[1].data;
                        Vue.set(row.item, 'spinner', true);
                        Vue.set(row.item, 'info', 'Transfer ' + item.amount + ' money unit [' +
                            clientFrom.name + '(ID: ' + clientFrom.id + ') --> ' +
                            clientTo.name + '(ID: ' + clientTo.id + ')]');
                    })
                } else if (item.from !== null) {
                    axios.get('http://localhost/client/' + item.from.clientIdd).then(value => {
                        Vue.set(row.item, 'spinner', true);
                        Vue.set(row.item, 'info', value.data.name + '(ID: ' + value.data.id + ') ' +
                            'withdraw ' + item.amount + ' money unit');
                    })
                } else if (item.to !== null) {
                    axios.get('http://localhost/client/' + item.to.clientIdd).then(value => {
                        Vue.set(row.item, 'spinner', true);
                        Vue.set(row.item, 'info', value.data.name + '(ID: ' + value.data.id + ') ' +
                            'deposit ' + item.amount + ' money unit');
                    })
                }
            }
        }
    }
</script>

<style>
    table#table-transition-example .flip-list-move {
        transition: transform 1s;
    }
</style>