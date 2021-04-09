function pay(){
    alert("当前暂不支持支付功能");
    return;
    var check = $("#check")[0].checked;
    if(!check){
        alert("请勾选同意条款");
        return;
    }
    //进行下单
    var amt = $("#amt").val();
    var par = getRequest();
    var openid = par.openid;
    // var openid = "oIcV7w_gfltIgTUKhjHHQ2GFQeXQ";
    $.ajax({
        type: "post",
        url: "https://mtest.eycard.cn/wxpay/toPay",
        // url: "http://localhost:8778/wxpay/toPay",
        data: {
            "tradeamt": amt,
            "openid": openid
        },
        dataType: "json",
        success: function(data) {
            console.log(data);
            if (data) {
                wxpay();
            } else {
                alert("下单失败");
            }
        },
        error: function(err) {
            alert("访问失败,稍后再试")
        }
    });
}

//提取参数
function getRequest() {
    var url = window.location.search; //获取url中"?"符后的字串
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

//调取微信支付接口
function wxpay(data) {

    // var orderdesc = JSON.parse(data.orderdesc);

    function onBridgeReady() {
        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', data,
            function(res) {
                var cancel = false;
                if (res.err_msg == "get_brand_wcpay_request:cancel") {
                    cancel = true;
                }
                prepays(data, res, cancel)
            }
        );
    }
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    } else {
        onBridgeReady();
    }
}

function toIndex(){
    var check = $("#check")[0].checked;
    if(!check){
        alert("请勾选同意条款");
        return;
    }
    window.location = "https://mtest.eycard.cn/wxpay/start";
}