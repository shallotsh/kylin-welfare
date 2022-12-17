var global_config = {
    isPredict: false,
    canKill: false,
    canExport: false,
    config: {
        isGroup: true,
        isDirect: true
    }
}


var app = new Vue({
    el:'#app',
    data:{
        sequence:'',
        w2dTailValues:null,
        w3dTailValues:null,

        sumValue:null,
        boldCodeSeq: null,
        kdSeq:null,
        freqSeted: false,
        wCodes: null,
        wyfMessage:'这一行是统计数据展示区域',
        codesCount: 0,
        config:global_config,
        cacheQueue: new Array(),

        drawNoticeOverview: ''
    },
    created: function(){
        this.export_format = 0;
    },
    mounted: function(){
        axios.get("/api/3d/draw/notice?", {
            params: {
                name: '3d',
                issueCount: 1
            }
        }).then(function (resp) {
            this.drawNotice = resp.data.data;
            var latestDrawRet = this.drawNotice.result[0];
            var desc = "开奖期数: 【" + latestDrawRet.code
                + " 】（" + latestDrawRet.date + "），中奖号码: 【"
                + latestDrawRet.red + "】";

            app.drawNoticeOverview = desc;

        }).catch(function (reason) {
            console.log("resp error:" + JSON.stringify(reason));
        });
    },
    methods:{
        doPermutate: function () {

            if(isEmpty(this.sequence)){
                this.handleException("请先输入预测码");
                return;
            }

            var paramArray = [];
            paramArray.push(this.sequence);
            // console.log('input:'+ JSON.stringify(paramArray));
            var args = {
                "sequences": paramArray
            };
            this.wyfMessage = "正在计算...";
            axios({
              method: 'post',
              url: '/api/tail-sum/shuffle',
              data: args
            }).then(function(response) {
                    app.freqSeted = false;
                    app.handleResponse(response.data.data, "和尾计算");
                    app.config.isPredict = true;
                    app.isGroup = true;
                })
                .catch(function(error){
                    console.log(error)
                });

        },
        handleResponse: function (data, msg) {
            console.log(JSON.stringify(data))
            this.w2dTailValues = data.tailSumValuesOf2d;
            this.w3dTailValues = data.tailSumValuesOf3d;
            app.wyfMessage =  msg + "计算成功";
        },

        resetInput: function () {
            this.config.isPredict = false;
            this.sequence ='',
                this.sequence = null,
                this.wyfMessage = '这一行是统计数据展示区域',
            this.w2dTailValues=null,
                this.w3dTailValues =null

        },

        handleException: function (msg) {
            alert(msg);
        }
    },
    computed: {
        w3dTailValue: function (){
            if(this.w3dTailValues != null){
                return this.w3dTailValues.join(",");
            }else{
                return "";
            }
        },
        w2dTailValue: function (){
            if(this.w2dTailValues != null){
                return this.w2dTailValues.join(",");
            }else{
                return "";
            }
        }
    }
});

function isEmpty(str){
    if(str == '' || str == null || str == undefined){
        return true;
    }
    return false;
}

