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
            {"data": "value.name"},
            {"data": "value.amount"},
            {"data": "value.lastPayment.amount"},
            {"data": "value.lastPayment.txnId"},
            {"data": "value.lastPayment.from"},
            {"data": "value.lastPayment.to"},
            {"data": "value.lastPayment.state"}
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
        "txnId": "IE5041Zx3X",
        "from": "lars",
        "to": "carsten",
        "amount": 13.32706889812972,
        "state": "credit",
        "timestamp": 1570379624766,
        "processStartTime": 1570379624768
      },
      "amount": -4989.541702683642
    }
  }
*/
