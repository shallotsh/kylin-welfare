var global_config = {
    isPredict: false,
    canKill: false,
    canExport: false,
    config: {
        isGroup: true,
        isDirect: true
    }
}


const app = {
    data() {
       return {
           sequence:'',
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
        }
    },
    created(){
        this.export_format = 0;
    },
    mounted(){
        axios.get("/api/3d/draw/notice?", {
            params: {
                name: '3d',
                issueCount: 1
            }
        }).then(resp => {
            this.drawNotice = resp.data.data;
            let latestDrawRet = this.drawNotice.result[0];
            let desc = "开奖期数: 【" + latestDrawRet.code
                + " 】（" + latestDrawRet.date + "），中奖号码: 【"
                + latestDrawRet.red + "】";

            this.drawNoticeOverview = desc;

        }).catch(function (reason) {
            console.log("3d resp error:" + JSON.stringify(reason));
        });
    },
    methods:{
        doPermutate() {

            if(isEmpty(this.sequence)){
                this.handleException("请先输入二码和");
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
              url: '/api/2sum-dict/shuffle',
              data: args
            }).then(response => {
                    this.freqSeted = false;
                    this.handle3DCodeResponse(response.data.data, "二码和字典法");
                    this.config.isPredict = true;
                    this.isGroup = true;
                })
                .catch(function(error){
                    console.log(error)
                });

        },
        handle3DCodeResponse (data, msg) {
            // console.log(JSON.stringify(data))
            this.wCodes = data.wCodes;
            this.pairCount = data.pairCodes;
            this.nonPairCount = data.nonPairCodes;
            this.deletedCodesPair = data.deletedCodes;
            if(data.freqSeted) {
                this.freqSeted = data.freqSeted;
            }
            this.wyfMessage =  msg + " : "  + this.wCodes.length + " 注(对子:" + this.pairCount + " 注,非对子:" + this.nonPairCount + " 注)" ;
        },

        resetInput() {
            this.config.isPredict = false;
            this.sequence ='',
                this.sumValue = null,
                this.boldCodeSeq = null,
                this.gossipCodeSeq = null,
                this.inverseCodeSeq = null,
                this.kdSeq = null,
                this.isGroup = false,
                this.wyfMessage = '这一行是统计数据展示区域',
                this.wCodes = null,
                this.deletedCodesPair= null,
                this.pairCount = null,
                this.nonPairCount = null

        },
        doKillCode() {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }

            var args = {
                "wCodes": this.wCodes,
                "boldCodeSeq": this.boldCodeSeq,
                "sumTailValues": this.sumValue,
                "kdSeq": this.kdSeq
            };
            this.killCode(args);
        },
        // 添加全集全偶
        doKillCodeForOddAndEven() {
            if(!this.config.isPredict){
                this.handleException("请先完成组码");
                return;
            }

            var args = {
                "wCodes": this.wCodes,
                "killAllOddAndEven": true
            };
            this.killCode(args);
        },

        killCode(args) {
            if(!this.config.isPredict){
                this.handleException("请先完成组码");
                return;
            }
            console.log('args' + JSON.stringify(args));

            var count = this.wCodes.length;

            axios({
                method:"POST",
                url:"/api/2sum-dict/kill/code",
                data: JSON.stringify(args),

                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(response => {
                this.handle3DCodeResponse(response.data.data, "专家推荐法杀码");
                this.wyfMessage = "总计 " + count + " 注, 杀码 " + (count - this.wCodes.length) + " 注, 余 " + this.wCodes.length + " 注(对子:" + this.pairCount + " 注,非对子:" + this.nonPairCount + " 注)" ;
            }).catch(function(err) {
                console.log("resp:" + JSON.stringify(err, null, 2));
                this.handleException("杀码请求失败!");
            });

        },

        handleDownload(data) {
            console.log("downloads:"+data);
            if(data == null){
                console.log("request error.");
                return;
            }
            window.location = "/api/welfare/download?fileName=" + data;
        },
        exportCodes(){
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

            // console.log('canshu:' + JSON.stringify(args, null, 2));
            axios({
                method:"POST",
                url:"/api/2sum-dict/codes/export",
                data: JSON.stringify(args),
                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(response => {
                this.handleDownload(response.data.data);
            }).catch(function(reason) {
                console.log(reason);
                this.hanhandleException("导出请求失败!");
            });
        },
        handleException (msg) {
            alert(msg);
        }
    },
    computed: {
        wyfCodes: function(){
            var printCodes = [];
            for( idx in this.wCodes){
                // console.log("wCodes变化");
                code = this.wCodes[idx];
                if(code.beDeleted){
                    // console.log('被删除code不展示' + JSON.stringify(code));
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
};

Vue.createApp(app).mount('#app');

function isEmpty(str){
    if(str == '' || str == null || str == undefined){
        return true;
    }
    return false;
}

