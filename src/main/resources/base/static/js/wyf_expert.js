var global_config = {
    isPredict: false,
    canKill: false,
    canExport: false,
    queueIndex: 1
}


var app = new Vue({
    el:'#app',
    data:{
        sequence:'',
        boldCodeSeq: null,
        gossipCodeSeq: null,
        inverseCodeSeq: null,
        sumValue:null,
        kdSeq: null,
        seqKill: null,
        wCodes: null,
        deletedCodesPair: null,
        wyfMessage:'这一行是统计数据展示区域',
        config: global_config,
        isGroup: false,
        isTo2D: false,
        cacheQueue: new Array(),
        compItems: [],
        drawNotice: null,
        drawNoticeOverview: '',
        pairCount:null,
        nonPairCount:null,
        hundred: null,
        decade: null,
        unit: null
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
            console.log("3d resp error:" + JSON.stringify(reason));
        });
    },
    methods:{
        doPermutate: function () {

            var paramArray = [];
            paramArray.push(this.sequence);

            var args = {
                "sequences": paramArray
            };
            console.log("sequences:" + JSON.stringify(args));
            app.wyfMessage = "正在计算...";
            axios({
              method: 'post',
              url: '/api/expert/3d/shuffle',
              data: args
            }).then(function(response) {
                    // 前置赋值
                    app.freqSeted = false;
                    // 处理结果
                    app.handle3DCodeResponse(response.data.data, '专家推荐法组码');
                    app.isGroup = false;
                    app.isTo2D = false;
                    app.config.isPredict = true;
                })
                .catch(function(error){
                    console.log(error)
                });

        },
        convertTo2D: function (){
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }
            if(this.isGroup){
                this.handleException("组选不能转2D");
                return;
            }

            if(this.isTo2D){
                this.handleException("已经转为2D");
                return;
            }

            var args = {
                "wCodes": this.wCodes,
                "deletedCodes": this.deletedCodesPair,
                "freqSeted": this.freqSeted
            };
            console.log("转换2D:" + JSON.stringify(args));
            var count = this.wCodes.length;
            axios({
                method:"POST",
                url:"/api/expert/3d/convert/2d",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "转2D");
                app.isTo2D = true;
                app.wyfMessage = "总计 " + count + " 注, 减少 " + (count - app.wCodes.length) + " 注, 余 " + app.wCodes.length + " 注(对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ;
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("杀码请求失败!");
            });
        }
        ,

        convertToGroup: function (){
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }
            if(this.isGroup){
                this.handleException("已转为组选");
                return;
            }
            if(this.isTo2D){
                this.handleException("已经转为2D，不能再转组选");
                return;
            }
            var args = {
                "wCodes": this.wCodes,
                "deletedCodes": this.deletedCodesPair,
                "freqSeted": this.freqSeted
            };
            console.log("转换:" + JSON.stringify(args));
            var count = this.wCodes.length;
            axios({
                method:"POST",
                url:"/api/expert/3d/convert/group",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "转组选");
                app.isGroup = true;
                app.wyfMessage = "总计 " + count + " 注, 减少 " + (count - app.wCodes.length) + " 注, 余 " + app.wCodes.length + " 注(对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ;
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("杀码请求失败!");
            });
        },

        handle3DCodeResponse: function (data, msg) {
            console.log(JSON.stringify(data))
            this.wCodes = data.wCodes;
            this.pairCount = data.pairCodes;
            this.nonPairCount = data.nonPairCodes;
            this.deletedCodesPair = data.deletedCodes;
            if(data.freqSeted) {
                this.freqSeted = data.freqSeted;
            }
            app.wyfMessage =  msg + " : "  + this.wCodes.length + " 注(对子:" + app.pairCount + " 注,非对子:" + app.nonPairCount + " 注)" ; ;
        },

        resetInput: function () {
            this.config.isPredict = false;
            this.sequence ='',
            this.boldCodeSeq = null,
            this.gossipCodeSeq = null,
            this.inverseCodeSeq = null,
                this.kdSeq = null,
                this.seqKill = null,
                this.hundred = null,
                this.decade = null,
                this.unit = null,
                this.isGroup = false,
                this.isTo2D = false,
            this.wyfMessage = '这一行是统计数据展示区域',
            this.wCodes = null,
                this.deletedCodesPair= null,
                this.pairCount = null,
                this.nonPairCount = null

        },

        addQueue: function () {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }

            var obj =  deepCopy(this.wCodes);

            this.cacheQueue.push(obj);

            console.log('入队成功.');
        },



        delQueue: function(cursor){
            this.cacheQueue.splice(cursor, 1);
            console.log('delete item from queue, index='+cursor);
        },

        selectQueue: function(cursor){
            console.log('cursor=' + cursor);
            this.wCodes = this.cacheQueue[cursor];
            var count = 0;

            for(var idx in this.compItems){
                count += this.cacheQueue[this.compItems[idx]].length;
                console.log('idx=' + idx + ',cpm[idx]='+this.compItems[idx] + ',cursor='+ cursor);
                if(this.compItems[idx] != cursor){
                    this.wCodes = this.wCodes.concat(this.cacheQueue[this.compItems[idx]]);
                }
            }
            this.wyfMessage = '已选择队列【' + this.compItems + '】共计 ' + count + ' 注三码.';
            console.log('Item(index='+this.compItems+' in the cache queue was selected.');
        },

        compSelect: function () {
            if(this.cacheQueue.length < 1){
                this.handleException("预测队列为空，请先添加预测码到队列");
                return;
            }

            var selectedQueues = new Array();

            if(this.compItems.length < 1){

                for(var idx=0; idx < this.cacheQueue.length; idx ++){
                    selectedQueues.push({index: idx, wCodes: this.cacheQueue[idx]});
                }

            }else {
                for (var idx in this.compItems) {
                    selectedQueues.push({index: idx, wCodes: this.cacheQueue[idx]});
                }
            }

            var args = {
                "xCodePairs": selectedQueues
                // "arrayIndexes": this.compItems
            };
            console.log("compArgs:" + JSON.stringify(args));
            app.wyfMessage = "正在计算...";
            axios({
                method: 'post',
                url: '/api/expert/3d/comp/select',
                data: JSON.stringify(args),
                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, '综合选码');
            })
                .catch(function(error){
                    console.log(error)
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

        killCode: function (args) {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }
            console.log('args' + JSON.stringify(args));

            var count = this.wCodes.length;

            axios({
                method:"POST",
                url:"/api/expert/3d/kill/code",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handle3DCodeResponse(response.data.data, "专家推荐法杀码");
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

        filterByBit: function (){

            if(this.isTo2D){
                this.handleException("已经转为2D，暂不支持位选");
                return;
            }

            var bitUnitDTO = {
                "hundredSeq": this.hundred,
                "decadeSeq": this.decade,
                "unitSeq": this.unit
            }
            var args = {
                "wCodes": this.wCodes,
                "bitUnitDTO": bitUnitDTO
            }
            console.log('bitSelect' + JSON.stringify(args))
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
                freqSeted: this.freqSeted
            };

            console.log('canshu:' + JSON.stringify(args, null, 2));
            axios({
                method:"POST",
                url:"/api/expert/3d/codes/export",
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
                if(this.freqSeted){
                    codeString = '[' + code.freq + ']' + codeString;
                }
                printCodes.push(codeString);
            }
            return printCodes;
        }
        // ,
        // drawNoticeDesc: function(){
        //
        //     if(this.drawNotice == null
        //         || this.drawNotice.state != 0
        //         || this.drawNotice.result.length <= 0) {
        //         console.log("参数问题");
        //         return "";
        //     }
        //
        //     var latestDrawRet = this.drawNotice.result[0];
        //     var desc = "开奖期数: 【" + latestDrawRet.code
        //         + " 】（" + latestDrawRet.date + "），中奖号码: 【"
        //         + latestDrawRet.red + "】";
        //
        //     console.log("desc : " + desc);
        //     return desc;
        // }
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
