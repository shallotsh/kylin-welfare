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
        sumValue:null,
        boldCodeSeq: null,
        kdSeq:null,
        fishManCode:null,
        lateAutumn:null,
        freqSeted: false,
        wCodes: null,
        wyfMessage:'这一行是统计数据展示区域',
        codesCount: 0,
        config:global_config,
        cacheQueue: new Array(),


        drawNoticeOverview: '',
        fourToThree: false
    },
    created: function(){
        this.export_format = 0;
    },
    mounted: function(){
        axios.get("/api/3d/draw/notice?", {
            params: {
                name: '3d',
                issueCount: 1
            },
            timeout: 2000
        }).then(function (resp) {
            this.drawNotice = resp.data.data;
            var latestDrawRet = this.drawNotice.result[0];
            var desc = "开奖期数: 【" + latestDrawRet.code
                + " 】（" + latestDrawRet.date + "），中奖号码: 【"
                + latestDrawRet.red + "】";

            app.drawNoticeOverview = desc;

        }).catch(function (reason) {
            console.log("3d resp error:" + JSON.stringify(reason));
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
              url: '/api/4d/shuffle',
              data: args
            }).then(function(response) {
                    app.freqSeted = false;
                    app.handle3DCodeResponse(response.data.data, "复式组选");
                    app.config.isPredict = true;
                    app.isGroup = true;
                    app.fourToThree = false;
                })
                .catch(function(error){
                    console.log(error)
                });

        },
        handle3DCodeResponse: function (data, msg) {
            this.wCodes = data.wCodes;
            this.pairCount = data.pairCodes;
            this.nonPairCount = data.nonPairCodes;
            this.deletedCodesPair = data.deletedCodes;
            if(data.freqSeted) {
                this.freqSeted = data.freqSeted;
            }
            app.wyfMessage =  msg + " : "  + this.wCodes.length + " 注"; // (对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ; ;
        },

        resetInput: function () {
            this.config.isPredict = false;
            this.sequence ='',
                this.sequence = null,
                this.boldCodeSeq = null,
                this.sumValue = null,
                this.kdSeq = null,
                this.fishManCode = null,
                this.lateAutumn = null,
                this.isGroup = false,
                this.wyfMessage = '这一行是统计数据展示区域',
                this.wCodes = null,
                this.deletedCodesPair= null,
                this.pairCount = null,
                this.nonPairCount = null,
                this.fourToThree = false

        },
        doKillCode: function () {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }

            let args = {
                "wCodes": this.wCodes,
                "boldCodeSeq": this.boldCodeSeq,
                "sumTailValues": this.sumValue,
                "lateAutumnCode": this.lateAutumn,
                "fourToThreeCmd": this.fourToThree,
                "deletedCodes": this.deletedCodesPair
            };
            this.killCode(args);
        },

        killCode: function (args) {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }
            console.log('args' + JSON.stringify(args));

            var count = this.wCodes.length;

            axios({
                method:"POST",
                url:"api/4d/kill/code",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "复式组选杀码");
                app.wyfMessage = "总计 " + count + " 注, 杀码 " + (count - app.wCodes.length) + " 注, 余 " + app.wCodes.length + " 注"; // (对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ;
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("杀码请求失败!");
            });

        },

        handleDownload: function(data) {
            console.log("downloads:"+data);
            if(data == null){
                console.log("request error.");
                return;
            }
            window.location = "/api/welfare/download?fileName=" + data;
        },

        transferCode: function() {
            if(!this.config.isPredict){
                this.handleException("请先完成组码");
                return;
            }
            let args = {
                "wCodes": this.wCodes,
                "fourToThreeCmd": true
            };
            // console.log('请求参数:' + JSON.stringify(args));
            axios({
                method:"POST",
                url:"api/4d/codes/transferCode",
                data: JSON.stringify(args),
                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "四转三码");
                app.fourToThree = true;
                console.log('fourToThree:' + app.fourToThree)
                app.wyfMessage = "总计 " + app.wCodes.length + " 注 "+ " (对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ;
            }).catch(function(response) {
                // console.log("respxx:" + JSON.stringify(response.data, null, 2));
                app.handleException("四码转换请求失败!");
            });
        },

        exportCodes: function(){
            if(!this.config.isPredict){
                this.handleException("请至少先完成一次预测");
                return;
            }

            var exportCodes = [];
            if(this.cacheQueue.length > 0){
                for(var idx=0; idx < this.cacheQueue.length; idx ++){
                    exportCodes = exportCodes.concat(this.cacheQueue[idx]);
                }
            }else{
                exportCodes = this.wCodes;
            }


            var args = {
                wCodes: exportCodes,
                deletedCodes: this.deletedCodesPair,
                freqSeted: this.freqSeted,
                fourToThreeCmd: this.fourToThree
            };

            // console.log('canshu:' + JSON.stringify(args, null, 2));
            axios({
                method:"POST",
                url:"api/4d/codes/export",
                data: JSON.stringify(args),
                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handleDownload(response.data.data);
            }).catch(function(reason) {
                console.log(reason);
                app.handleException("导出请求失败!");
            });
        },

        handleException: function (msg) {
            alert(msg);
        }
    },
    computed: {
        wyfCodes: function(){
            var printCodes = [];
            for( idx in this.wCodes){
                console.log("wCodes变化");
                code = this.wCodes[idx];
                if(code.beDeleted){
                    console.log('被删除code不展示' + JSON.stringify(code));
                    continue;
                }
                // code.codes.reverse();
                var codeString = code.codes.join("");
                if(this.freqSeted){
                    codeString = '[' + code.freq + ']' + codeString;
                }
                printCodes.push(codeString);
            }
            return printCodes;
        }
    }
});

function isEmpty(str){
    if(str == '' || str == null || str == undefined){
        return true;
    }
    return false;
}

