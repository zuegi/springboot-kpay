var chart;
var paymentPipelineChart;
var accountTable;

$(document).ready(function () {

    $( function() {
        $("#timingSlider").slider({
            range: "min",
            value: 500,
            min: 10,
            max: 30000,
            slide: function (event, ui) {
                $("#timingSliderInfo").val( ui.value + " ms");

                $.ajax({
                    method: 'GET',
                    url: '/api/control/paymentProducer/adjustRate/'+ui.value
                });
            }

        });
        $("#timingSliderInfo").val( $("#timingSlider").slider("value") + " ms");
    });



    accountTable = $('#accountTable').DataTable({
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
            {"data": "value.lastAmount"},
            {"data": "value.lastPayment.amount"},
            {"data": "value.lastPayment.txnId"},
            {"data": "value.lastPayment.from"},
            {"data": "value.lastPayment.to"},
            {"data": "value.lastPayment.state"}
        ]
    });

    refreshStartPauseButtons();

    /* Start Payments Button*/
    $('#startPayments').click(function () {
        if(!isPaymentRunningFunc()) {
            $.get({
                url: "/api/control/paymentProducer/start",
                success: function (e) {
                    $('#startPayments').prop('disabled', true);
                    $('#pausePayments').prop('disabled', false);
                }
            });
        }
    });

    $('#pausePayments').click(function () {
        if(isPaymentRunningFunc()) {
            $.get({
                url: "/api/control/paymentProducer/stop",
                success: function (e) {
                    $('#startPayments').prop('disabled', false );
                    $('#pausePayments').prop('disabled', true);
                }
            });
        }
    });


    $('#sendPayment').click(function () {
            $.get({
                url: "/api/control/paymentProducer/send"
            });
    });

    createStuff();


});


function createStuff() {
    createLatencyChart();
    createPaymentPipelineChart();

    refreshLatencyChart();
    refreshPaymentPipelineChart();
    isPaymentRunningFunc();

}

function refreshStartPauseButtons(){
    if(isPaymentRunningFunc()) {
        $('#startPayments').prop('disabled', true);
        $('#pausePayments').prop('disabled', false);
    } else {
        $('#startPayments').prop('disabled', false);
        $('#pausePayments').prop('disabled', true);
    }
}

function isPaymentRunningFunc() {
    var result;
    $.get({
        url: '/api/control/paymentProducer/running',
        success: function(data) {
            result = data;
        },
        async:false
    });
    return result;
}

function refreshLatencyChart() {
    $.get({
        url: "/api/metrics/throughput",
        success: function (e) {
            var totalPayments = new Object();
            totalPayments.y = e.totalPayments;
            totalPayments.t = e.timestamp;

            var totalDollarAmount = new Object();
            totalDollarAmount.y = e.totalDollarAmount;
            totalDollarAmount.t = e.timestamp;

            var maxLatency = new Object();
            maxLatency.y = e.maxLatency;
            maxLatency.t = e.timestamp;

            var minLatency = new Object();
            minLatency.y = e.minLatency;
            minLatency.t = e.timestamp;

            chart.data.datasets[0].data.push(totalPayments);
            chart.data.datasets[1].data.push(totalDollarAmount);
            chart.data.datasets[2].data.push(maxLatency);
            chart.data.datasets[3].data.push(minLatency);
            chart.update();
            return false;
        }
    })
}


function createLatencyChart() {
    var date = moment().subtract(1, 'hour');

    var data = [randomBar(date, 30)];
    while (data.length < 60) {
        date = date.clone().add(1, 'minute');
        data.push(randomBar(date, data[data.length - 1].y));
    }

    var ctx = document.getElementById('latencyChart').getContext('2d');
    var cfg = {
        type: 'bar',
        data: {
            datasets: [{
                label: 'Payment Count',
                data: [],
                type: 'line',
                pointRadius: 1,
                fill: false,
                lineTension: 0,
                borderWidth: 2,
                cubicInterpolationMode: 'monotone'
            },
                {
                    label: 'Payment $ Total ',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.yellow,
                    borderColor: window.chartColors.yellow,

                    lineTension: 0,
                    borderWidth: 2,
                    cubicInterpolationMode: 'monotone'
                }, {
                    label: 'Max latency (ms)',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.orange,
                    borderColor: window.chartColors.orange,
                    lineTension: 0,
                    borderWidth: 2,
                    cubicInterpolationMode: 'monotone'
                }, {
                    label: 'Min latency (ms)',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.blue,
                    borderColor: window.chartColors.blue,
                    lineTension: 0,
                    borderWidth: 2
                }]
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    distribution: 'series',
                    ticks: {
                        autoSkip: false,
                        maxRotation: 45,
                        minRotation: 45
                    },
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Pipeline latency'
                    }
                }]
            }
        }
    };
    chart = new Chart(ctx, cfg);
}

function randomBar(date, lastClose) {
    var open = randomNumber(lastClose * 0.95, lastClose * 1.05);
    var close = randomNumber(open * 0.95, open * 1.05);
    return {
        t: date.valueOf(),
        y: 0//close
    };
}

function randomNumber(min, max) {
    return Math.random() * (max - min) + min;
}

window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};

function createPaymentPipelineChart() {
    var date = moment().subtract(1, 'hour');

    var data = [randomBar(date, 30)];
    while (data.length < 60) {
        date = date.clone().add(1, 'minute');
        data.push(randomBar(date, data[data.length - 1].y));
    }

    var ctx = document.getElementById('paymentPipelineChart').getContext('2d');
    var cfg = {
        type: 'bar',
        data: {
            datasets: [{
                label: 'Inflight Count',
                data: [],
                type: 'line',
                pointRadius: 1,
                fill: false,
                lineTension: 0,
                borderWidth: 2,
                cubicInterpolationMode: 'monotone'
            },
                {
                    label: 'Inflight $',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.yellow,
                    borderColor: window.chartColors.yellow,
                    lineTension: 0,
                    borderWidth: 2,
                    cubicInterpolationMode: 'monotone'
                }, {
                    label: 'Confirmed Count',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.orange,
                    borderColor: window.chartColors.orange,
                    lineTension: 0,
                    borderWidth: 2,
                    cubicInterpolationMode: 'monotone'
                }, {
                    label: 'Confirmed $',
                    data: [],
                    type: 'line',
                    pointRadius: 1,
                    fill: false,
                    backgroundColor: window.chartColors.blue,
                    borderColor: window.chartColors.blue,
                    lineTension: 0,
                    borderWidth: 2
                }]
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        round: "true"
                    },
                    ticks: {
                        autoSkip: false,
                        maxRotation: 45,
                        minRotation: 45
                    },
                    distribution: 'series'
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Pipeline performance'
                    }
                }]
            }
        }
    };
    paymentPipelineChart = new Chart(ctx, cfg);
}

function refreshPaymentPipelineChart() {
    $.get({
        url: "/api/metrics/pipeline",
        success: function (e) {
            var inflightCount = new Object();
            inflightCount.y = e.k.count;
            inflightCount.t = e.k.timestamp;

            var inflightAmount = new Object();
            inflightAmount.y = e.k.amount;
            inflightAmount.t = e.k.timestamp;

            var confirmedCount = new Object();
            confirmedCount.y = e.v.count;
            confirmedCount.t = e.v.timestamp;

            var confirmedAmount = new Object();
            confirmedAmount.y = e.v.amount;
            confirmedAmount.t = e.v.timestamp;

            paymentPipelineChart.data.datasets[0].data.push(inflightCount);
            paymentPipelineChart.data.datasets[1].data.push(inflightAmount);
            paymentPipelineChart.data.datasets[2].data.push(confirmedCount);
            paymentPipelineChart.data.datasets[3].data.push(confirmedAmount);
            paymentPipelineChart.update();
            return false;
        }
    })
}

setInterval(function () {
    refreshLatencyChart();
    refreshPaymentPipelineChart();
    accountTable.ajax.reload();
    refreshStartPauseButtons();
}, 5000);
