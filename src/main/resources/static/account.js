$(document).ready(function () {
    $('#accountTable').DataTable({
        ajax: {
            url: 'http://localhost:8080/api/listAccounts',
            method: "GET",
            xhrFields: {
                withCredentials: true
            },
            dataSrc: ''
        },
        "columns": [
            {"data": "key"},
            {"data": "value.name"},
            {"data": "value.lastPayment.txnId"},
            {"data": "value.lastPayment.from"},
            {"data": "value.lastPayment.to"},
            {"data": "value.lastPayment.state"},
            {"data": "value.lastPayment.amount"}
        ]
    });

});
/*
{
    "key": "carsten",
    "value": {
    "name": "carsten",
        "lastPayment": {
        "id": "carsten",
            "txnId": "iqOzAaTQXy",
            "from": "ueli",
            "to": "carsten",
            "amount": 100.988945647705,
            "state": "credit",
            "timestamp": 1570368126158,
            "processStartTime": 1570368126167
    },
    "amount": -3698.4777639538847
}*/
