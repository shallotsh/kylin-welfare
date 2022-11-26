var global_config = {
    isPredict: false,
    canKill: false,
    canExport: false,
    isP5:false,
    config3: {
        isGroup: true,
        isDirect: true
    }
}


var app = new Vue({
    el:'#app',
    data:{
        sequence1:'',
        sequence2:'',
        sequence3:'',
        sequence4:'',
        sumValue:null,
        boldCode:null,
        binSumValue:null,
        rangeCode:null,

        // gossipCode:null,
        wyfMessage:'这一行是统计数据展示区域',
        codesCount: 0,
        wyfCodes:[],
        config:global_config,
        welfareCode: null,
        backupCode: null,
        boldCodeFive: null,
        myriabit: null,
        kilobit: null,
        hundred: null,
        decade:null,
        unit:null,
        p3Code:null,
        isRandomKill:null,
        randomKillCodes:null,
        // wyf_abc:null,
        // wyf_acb:null,
        // wyf_bac:null,
        // wyf_bca:null,
        // wyf_cab:null,
        // wyf_cba:null,
        // savePoint: null,
        // bitAB:null,
        // bitBC:null,
        // bitCD:null,
        // bitDE:null,
        // export_format: null,
        drawNoticeOverview: '',
        extendCount: null,
        // extendRatio: null
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
            paramArray.push(this.sequence1);
            paramArray.push(this.sequence2);
            paramArray.push(this.sequence3);
            paramArray.push(this.sequence4);
            console.log('input:'+ JSON.stringify(paramArray));
            var args = {
                "riddles": paramArray,
                "targetCodeType": 3
            };
            this.wyfMessage = "正在计算...";
            axios({
              method: 'post',
              url: '/api/welfare/codes/predict',
              data: args
            }).then(function(response) {
                    app.handleThreeCodeResponse(response.data.data);
                })
                .catch(function(error){
                    console.log(error)
                });

        },
        handleThreeCodeResponse:function (data) {
            if(!data){
                this.wyfMessage='远程服务返回数据为空';
                console.log('请求数据为空');
                return;
            }
            this.welfareCode = data;
            this.wyfCodes = this.welfareCode.codes;
            this.config.isPredict=true;
            this.config.canKill=true;
            this.config.canExport=true;
            if(this.welfareCode.codeTypeEnum == "DIRECT"){
                this.config.config3.isGroup = false;
                this.config.config3.isDirect = true;
                this.wyfMessage = "本次直选预测3D码: " + this.welfareCode.w3DCodes.length + " 注";
            }else {
                this.config.config3.isGroup = true;
                this.config.config3.isDirect = false;
                this.wyfMessage = "本次组选预测3D码: " + this.welfareCode.w3DCodes.length + " 注";
            }
        },

        handleDownload: function(data) {
            console.log("downloads:"+data);
            if(data == null){
                console.log("request error.");
                return;
            }
            window.location = "/api/welfare/download?fileName=" + data;
        },
        killCode: function () {
            if(!this.config.isPredict){
                this.handleException("请先完成预测");
                return;
            }

            var args = {
                "welfareCode": this.welfareCode,
                "sumValue": this.sumValue,
                "boldCode": this.boldCode,
                "range": this.rangeCode
            };

            var count = this.wyfCodes.length;

            axios({
                method:"POST",
                url:"/api/welfare/codes/filter",
                data: JSON.stringify(args),
                headers:{
                    "Content-Type": "application/json; charset=UTF-8"
                }
            }).then(function(response) {
                app.handleThreeCodeResponse(response.data.data);
                app.wyfMessage = "总计 " + count + " 注, 杀码 " + (count - app.welfareCode.w3DCodes.length) + " 注, 余 " + app.welfareCode.w3DCodes.length + " 注.";
            }).catch(function(response) {
                console.log("resp:" + JSON.stringify(response.data, null, 2));
                app.handleException("杀码请求失败!");
            });

        },

        exportCodes: function(){
            if(!this.config.isP5){
                this.handleException("请先完成排5");
                return;
            }

            var args = {
                wCodes: this.welfareCode ,
                randomCount: this.boldCodeFive,
                randomKill: this.isRandomKill,
                freqSeted : this.freqSeted,
                exportFormat: this.export_format,
                deletedCodesPair: this.deletedCodesPair,
                savePoint: this.savePoint,
                deletedCodes: this.deletedCodes
            };

            // console.log('canshu:' + JSON.stringify(args, null, 2));
            axios({
                method:"POST",
                url:"/api/p5/codes/export",
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

        exportCodesHalfPage: function(){
            if(!this.config.isP5){
                this.handleException("请先完成排5");
                return;
            }

            var args = {
                wCodes: this.welfareCode
            };

            // console.log(JSON.stringify($rootScope.welfareCode, null, 2));
            axios({
                method:"POST",
                url:"/api/p5/codes/export/half",
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
    }
});

