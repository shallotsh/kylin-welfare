var global_config = {
    isPredict: false,
    canKill: false,
    canExport: false
}


var app = new Vue({
    el:'#app',
    data:{
        sequence:'',
        boldCodeSeq: null,
        gossipCodeSeq: null,
        sumValue:null,
        kdSeq: null,
        seqKill: null,
        wCodes: null,
        deletedCodesPair: null,
        wyfMessage:'这一行是统计数据展示区域',
        config: global_config,
        isDirect: false,
        cacheQueue: new Array(),
        compItems: [],
        drawNotice: null,
        drawNoticeOverview: '',
        pairCount:null,
        nonPairCount:null,
        hundred: null
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
        doExecute: function () {

            var paramArray = [];
            paramArray.push(this.sequence);

            var args = {
                "sequences": paramArray
            };
            console.log("sequences:" + JSON.stringify(args));
            app.wyfMessage = "正在计算...";
            axios({
              method: 'post',
              url: '/api/two-derive-three/shuffle',
              data: args
            }).then(function(response) {
                    // 处理结果
                    app.handle3DCodeResponse(response.data.data, '二组三码法组码');
                    app.config.isPredict = true;
                    app.isDirect = false;
                })
                .catch(function(error){
                    console.log(error)
                });

        },

        convertToDirect: function (){
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }
            if(this.isDirect){
                console.log("转换标识:" + JSON.stringify(this.isDirect));
                this.handleException("已转为直选");
                return;
            }
            var args = {
                "wCodes": this.wCodes
            };
            console.log("转换:" + JSON.stringify(args));
            axios({
                method:"POST",
                url:"api/two-derive-three/convert/direct",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "转直选");
                app.isDirect = true;
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("转直选请求失败!");
            });
        },

        handle3DCodeResponse: function (data, msg) {
            console.log(JSON.stringify(data))
            this.wCodes = data.wCodes;
            this.pairCount = data.pairCodes;
            this.nonPairCount = data.nonPairCodes;
            this.deletedCodesPair = data.deletedCodes;
            app.wyfMessage =  msg + " : "  + this.wCodes.length + " 注(对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ; ;
        },

        resetInput: function () {
            this.config.isPredict = false;
            this.sequence ='',
            this.boldCodeSeq = null,
            this.gossipCodeSeq = null,
                this.kdSeq = null,
                this.seqKill = null,
                this.isDirect = false,
            this.wyfMessage = '这一行是统计数据展示区域',
            this.wCodes = null,
                this.deletedCodesPair= null,
                this.pairCount = null,
                this.nonPairCount = null,
                this.hundred = null
        },
        handleDownload: function(data) {
            console.log("downloads:"+data);
            if(data == null){
                console.log("request error.");
                return;
            }
            window.location = "/api/welfare/download?fileName=" + data;
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
                url:"api/two-derive-three/kill/code",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "二组三杀码");
                app.wyfMessage = "总计 " + count + " 注, 杀码 " + (count - app.wCodes.length) + " 注, 余 " + app.wCodes.length + " 注(对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ;
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("杀码请求失败!");
            });

        },

        filterBySeq: function (){

            var args = {
                "wCodes": this.wCodes,
                "seqKill": this.seqKill
            };
            this.killCode(args);
        },

        filterCodes: function (){
            var args = {
                "wCodes": this.wCodes,
                "boldCodeSeq": this.boldCodeSeq,
                "sumTailValues": this.sumValue,
                "kdSeq": this.kdSeq
            };
            this.killCode(args);
        },

        filterByBit: function (){

            if(!this.isDirect){
                this.handleException("未转直选，不支持按位杀码");
                return;
            }

            let bitUnitDTO = {
                "hundredSeq": this.hundred,
                "needDeletedCodes": false
            }
            let args = {
                "wCodes": this.wCodes,
                "bitUnitDTO": bitUnitDTO
            }
            console.log('bitSelect' + JSON.stringify(args))
            this.killCode(args);
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
                wCodes: exportCodes
            };

            console.log('canshu:' + JSON.stringify(args, null, 2));
            axios({
                method:"POST",
                url:"api/two-derive-three/codes/export",
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
                code = this.wCodes[idx];
                if(code.beDeleted){
                    console.log('被删除code不展示' + JSON.stringify(code));
                    continue;
                }
                // code.codes.reverse();
                var codeString = code.codes.join("");
                printCodes.push(codeString);
            }
            return printCodes;
        }
    }
});

function deepCopy(source) {
    if(source instanceof  Array){
        return source.slice(0);
    }
    var result = {};
    for (var key in source){

        if(source[key] instanceof Array){
            result[key] = source[key].slice(0);
            continue;
        }

        result[key] = typeof source[key] === 'object' ? deepCopy(source[key]) : source[key];
    }

    return result;
}
