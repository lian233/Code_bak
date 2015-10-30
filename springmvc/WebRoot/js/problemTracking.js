/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var customerlist = {};
//allData变量声明
//用于存储返回的初始化数据
if (typeof allData === "undefined") allData = {};

var DelG = null;
var Asel = null;
var Anote = null;
var Aquest = null;
var Atel = null;
var Aemail = null;
//品牌列表
var brandlist = null;

//产品线列表
var productLineList = null;

//字段索引定义
var idxDeliveryGroup = 1;
var idxdelivery = 2;
var idxState = 3;
var idxCity = 4;
var idxDistrict = 5;
var idxStatus = 6;
var idxReceivedZone = 7;
var idxNoReceivedZone = 8;
var idxPartReceivedZone = 9;
var idxNoReceivedZoneEx = 10;

//每页行数设定
var pageSizeSetting = 11;



//弹出提示框
function showTips(text){
    var dlg = $("#tips_dlg");
    dlg.show();

    var dbody = dlg.find(".dialog_body");
    dbody.css("left", ($(document).width()-dbody.width())/2+"px");
    dbody.css("top", "100px");

    $(".tips").html(text);
}

$(function(){
    //当前选中的行
    var selectedRow = null;
    //最后搜索的条件参数
    var lastSearchParam = null;
    //翻页功能回调函数(ok)
    page_ctrl_to_callback = function(pn){
        if (lastSearchParam){
            lastSearchParam.pn = pn;

            $.ajax({
                url: "./qryCustomerService.do",
                type: "post",
                data: JSON.stringify(lastSearchParam),
                success: function(rsp){
                    if (rsp.errorCode!=0){
                        showTips(rsp.msg);
                    }else{
                        clearGrid();
                        fillGrid(rsp.data);
                        if (typeof(afterChangedPage)!=="undefined"){
                            afterChangedPage(rsp.pageInfo);
                        }
                    }
                },
                error: function(){
                    showTips("请求出错了");
                },
                dataType: "json"
            });
        }
    };
        $.ajax({
            url: "./qryCustomerList.do",
            type: "post",
            data: "{}",
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    customerlist = rsp.data;	//获取到的客户列表放到全局变量中
                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });



    $.ajaxSetup({async: false});

    //初始化数据
    function init(){
        if (allData){
            //当前登录的管理员信息
            if ("curLogin" in allData){
                var info = $(".acount_info");
                info.html(allData.curLogin.Name+"["+allData.curLogin.CName+"]");
                info.attr("data", allData.curLogin.ID);

                var logo = $("#systemLogo");
                logo.attr("src",allData.curLogin.SystemLogo );
                logo = $("#systemName");
                logo.text(allData.curLogin.SystemName);
            }

            //初始化菜单栏
            if ("menu" in allData){
                setActiveMenu("业务管理", "快递区域");
            }
            //客户列表
                var selectlist = $("#customer");
                var ln = customerlist.length;
                for(var i=0; i<ln; i++){
                    $("<option value='"+customerlist[i].ID+"'>"+customerlist[i].Name+"</option>").appendTo(selectlist);
                }




            //设置分页
            if (typeof(resetPageCtrl)!=="undefined"){
                resetPageCtrl({rowCnt:0, page:0, psize:pageSizeSetting});
            }

            //按情况隐藏操作栏
            hideCustomer();
        }

    };

    //全选(ok)
    $("#all_check").click(function(){
        var _this = this;
        var v = this.checked;

        $("table :checkbox").each(function(){
            if (this!==_this) this.checked = v;
        });
    });

    //禁止编辑表格(ok)
    function disableGrid(){
        $("table input").attr("disabled", "disabled");
        $("table select").attr("disabled", "disabled");
    }

    //允许编辑表格(ok)
    function enableGrid(){
        $("table input").removeAttr("disabled");
        $("table select").removeAttr("disabled");
    }

    //清除表格所有数据(ok)
    function clearGrid(){
        selectedRow = null;
        $("#Goods_List tr:gt(0)").each(function(){
            $(this).remove();
        });
    }

    //添加新行(ok)
    function addGridRow(){
        var txt = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        $("#Goods_List tbody").append(txt);
        var tr = $("#Goods_List tr:last");

        txt.attr("style","color: blue");
        var cells = tr.find("td");
        //客户
        $(cells[0]).html("<label></label>");
        //类型
        $(cells[1]).html("<label></label>");
        //问题
        $(cells[2]).html("<label></label>");
        //提问人
        $(cells[3]).html("<label></label>");
        //提问人电话
        $(cells[4]).html("<label></label>");
        //提问人电子邮件
        $(cells[5]).html("<label></label>");
        //答复人
        $(cells[6]).html("<label></label>");
        //答复内容
        $(cells[7]).html("<label></label>");
        //状态
        $(cells[8]).html("<label></label>");
        //备注
        $(cells[9]).html("<label></label>");
        //修改
        $(cells[10]).html("<label></label>");

        hideCustomer();

        return tr;
    }

    //填充数据到表格(ok)
    function fillGrid(data){
        var ln = data.length;
        for(var i=0; i<ln; i++)
        {
            var tr = addGridRow();
            var GoodsList = data[i];
            var labels = tr.find("label");
            //客户
            var field = $(labels[0]);
            field.attr("id",GoodsList.ID);
            field.attr("customer",GoodsList.CustomerID);
            field.html(GoodsList.CustomerName);
            //类型
            if(GoodsList.TypeID == 1){
            field = $(labels[1]);
            field.attr("TypeID",GoodsList.TypeID);
            field.html($.trim("快递问题"));
            }
            if(GoodsList.TypeID == 2){
                field = $(labels[1]);
                field.attr("TypeID",GoodsList.TypeID);
                field.html($.trim("使用问题"));
            }
            if(GoodsList.TypeID == 3){
                field = $(labels[1]);
                field.attr("TypeID",GoodsList.TypeID);
                field.html($.trim("结算问题"));
            }
            if(GoodsList.TypeID == 100){
                field = $(labels[1]);
                field.attr("TypeID",GoodsList.TypeID);
                field.html($.trim("其他问题"));
            }
            //问题
            field = $(labels[2]);
            field.attr("Question",GoodsList.Question);
            field.html($.trim(GoodsList.Question));
            //提问人
            field = $(labels[3]);
            field.attr("Questioner",GoodsList.Questioner);
            field.html($.trim(GoodsList.Questioner));
            //提问人电话
            field = $(labels[4]);
            field.attr("LinkTele",GoodsList.LinkTele);
            field.html($.trim(GoodsList.LinkTele));
            //提问人电子邮件
            field = $(labels[5]);
            field.attr("Email",GoodsList.Email);
            field.html($.trim(GoodsList.Email));
            //答复人
            field = $(labels[6]);
            field.attr("Answerer",GoodsList.Answerer);
            field.html($.trim(GoodsList.Answerer));
            //答复内容
            field = $(labels[7]);
            field.attr("Answer",GoodsList.Answer);
            field.html($.trim(GoodsList.Answer));
            //状态
            if(GoodsList.Flag == 0){
                field = $(labels[8]);
                field.attr("TypeID",GoodsList.Flag);
                field.html($.trim("编辑"));
            }
            if(GoodsList.Flag == 10){
                field = $(labels[8]);
                field.attr("TypeID",GoodsList.Flag);
                field.html($.trim("已确认"));
            }
            if(GoodsList.Flag == 20){
                field = $(labels[8]);
                field.attr("TypeID",GoodsList.Flag);
                field.html($.trim("已解答"));
            }
            if(GoodsList.Flag == 100){
                field = $(labels[8]);
                field.attr("TypeID",GoodsList.Flag);
                field.html($.trim("完结"));
            }
            if(GoodsList.Flag == 99){
                field = $(labels[8]);
                field.attr("TypeID",GoodsList.Flag);
                field.html($.trim("取消"));
            }
            //备注
            field = $(labels[9]);
            field.attr("Note",GoodsList.Note);
            field.html($.trim(GoodsList.Note));
            //修改
            field = $(labels[10]);
            field.attr("id",GoodsList.ID);
            field.attr("TypeID",GoodsList.TypeID);
            field.attr("Question",GoodsList.Question);
            field.attr("Questioner",GoodsList.Questioner);
            field.attr("LinkTele",GoodsList.LinkTele);
            field.attr("Email",GoodsList.Email);
            field.attr("Note",GoodsList.Note);
            field.html("<img class='row_icon' src='./images/add_1.png'/>");
        }
    }



    //选择的行高亮显示(ok)
    $("#Goods_List").on("click","tr:gt(0)",function(){
        if (selectedRow!==this){
            if (selectedRow!==null) $(selectedRow).removeClass("tr_high_light");
            $(this).addClass("tr_high_light");
            selectedRow = this;
            var label = $(selectedRow).find("label");
            DelG = $(label[0]).attr("id");
        }
    });


    //修改按钮
    $("table").on("click", ".row_icon", function (){
        var dlg = $("#upProblem"); //弹出对话框
        dlg.show();
        var dbody = dlg.find(".dialog_body");
        dbody.css("left", ($(document).width()-dbody.width())/2+"px");
        dbody.css("top", "30px");
        $("#question").val($(this).parents("label").attr("questioner"));
        $("#questionPhone").val($(this).parents("label").attr("linkTele"));
        $("#questEmail").val($(this).parents("label").attr("email"));
        $("#note").val($(this).parents("label").attr("note"));
        $("#questContent").val($(this).parents("label").attr("question"));
       var sel =  $(this).parents("label").attr("typeid");
        if(sel == 1)
        {$("#mode option[value='1']").attr("selected","selected");}
        if(sel == 2)
        {$("#mode option[value='2']").attr("selected","selected");}
        if(sel == 3)
        {$("#mode option[value='3']").attr("selected","selected");}
        if(sel == 100)
        {$("#mode option[value='4']").attr("selected","selected");}
        if(sel == 0)
        {$("#mode option[value='0']").attr("selected","selected");}
    });

    //查询按钮(ok)
        $("#search").click(function(){
            DelG = null;
        //取参数
        var cus = $("#customer").val();//客户
        var sel = $("#selProblem").val();//问题类型
        var pro = $("#problem").val();//问题
        var quiz = $("#quiz").val();//提问人
        var reply = $("#reply").val();//答复人
        var state = $("#state").val();//状态
        lastSearchParam = {};
        //客户
        if(parseInt(cus) >= 0)
            lastSearchParam.CustomerID = parseInt(cus);
        //问题类型
        if(parseInt(sel) >= 0)
            lastSearchParam.TypeID = parseInt(sel);
        //问题
        if(pro.length > 0)
            lastSearchParam.Question = pro;
        //提问人
        if(quiz.length > 0)
            lastSearchParam.Questioner = quiz;
        //答复人
        if(reply.length > 0)
            lastSearchParam.Answerer = reply;
        //状态
        if(parseInt(state) >=0 )
            lastSearchParam.Flag = parseInt(state);

        //刷新列表
        refList();
    });

    //执行查询,刷新列表(ok)
    function refList(){
        $.ajax({
            url: "./qryCustomerService.do",
            type: "post",
            data: JSON.stringify(lastSearchParam),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    clearGrid();
                    fillGrid(rsp.data);
                    if (typeof(resetPageCtrl)!="undefined"){
                        resetPageCtrl(rsp.pageInfo);
                    }
                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    }







    //按情况隐藏操作栏(ok)
    function hideCustomer(){
        if("curLogin" in allData)
            if (allData.curLogin.CustomerID > 0)
            {
                var foot = $(".foot");
                foot.hide();
                disableGrid();
            }
    }

    //关闭窗口(ok)
    $(".dialog_close").click(function(){
        var dialogbody = $(this).parent("div");
        $(dialogbody).parent("div").hide();
    });
    $(".dlg_Close_btn").click(function(){
        $(this).parents(".dialog").hide();
    });

    //问题提交框
    $("#up").click(function(){
        DelG = null;
        //提问人
        $("#question").val(allData.curLogin.CustomerName);
        //提问人电话
        $("#questionPhone").val(allData.curLogin.CustomerTele);
        //提问人电子邮箱
        $("#questEmail").val(allData.curLogin.CustomerEmail);
        var dlg = $("#upProblem"); //弹出对话框
        dlg.show();
        var dbody = dlg.find(".dialog_body");
        dbody.css("left", ($(document).width()-dbody.width())/2+"px");
        dbody.css("top", "30px");
    });


    //问题确认提交按钮
    $("#qConfirm").click(function(){
        //取参数
        var mode = $("#mode").val();//类型
        var quest = $("#question").val();//提问人
        var qPhone = $("#questionPhone").val();//提问电话
        var qEmail = $("#questEmail").val();//提问人电子邮箱
        var note = $("#note").val();//备注
        var qContent = $("#questContent").val();//内容
        var lastSearchParamQc = {};
        //问题ID
        if(DelG == null){
            lastSearchParamQc.ID = -1;
        }
        else
        {lastSearchParamQc.ID = DelG;}
        //类型
        if(parseInt(mode) >= 0)
            lastSearchParamQc.TypeID = mode;
        else
        {alert("请选择类型");
        return}
        //提问人
        if(quest.length > 0)
            lastSearchParamQc.Questioner = quest;
        //提问人电话
        if(qPhone.length > 0)
            lastSearchParamQc.LinkTele = qPhone;
        //提问人电子邮箱
        if(qEmail.length > 0)
            lastSearchParamQc.Email = qEmail;
        //备注
        if(note.length > 0)
            lastSearchParamQc.Note = note;
        //内容
        if(qContent.length > 0)
            lastSearchParamQc.Question = qContent;
        //提交问题
        questUp(lastSearchParamQc);

    });

    //提交问题
    function questUp(lastSearchParamQc){
        $.ajax({
            url: "./saveCustomerService.do",
            type: "post",
            data: JSON.stringify({CustomerServices:[lastSearchParamQc]}),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    refList();
                    $("#upProblem").css('display','none');
                    alert("提交成功");
                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    }



    //答复框
    $("#answer").click(function(){
        if(DelG == null)
        {alert("请选择一条问题来回答");
        return;
        }
        var dlg = $("#answer_q"); //弹出对话框
        dlg.show();
        var dbody = dlg.find(".dialog_body");
        dbody.css("left", ($(document).width()-dbody.width())/2+"px");
        dbody.css("top", "30px");
    });

    //回答问题
    $("#aConfirm").click(function(){
        //取参数
        var qContent = $("#answerContent").val();//内容
        var lastSearchParamAc = {};
        //问题ID
        lastSearchParamAc.ID = DelG;
        //回答人
        lastSearchParamAc.Answerer = allData.curLogin.CustomerName;
        if(qContent.length > 0)
            lastSearchParamAc.Answer = qContent;
        //提交问题
        answerUp(lastSearchParamAc);
    });

    //提交回答
    function answerUp(lastSearchParamAc){
        $.ajax({
            url: "./saveCustomerService.do",
            type: "post",
            data: JSON.stringify({CustomerServices:[lastSearchParamAc]}),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    refList();
                    $("#answer_q").css('display','none');
                    alert("提交成功");

                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    }

    //完结
    $("#over").click(function(){
        //取参数
        var qContent = $("#answerContent").val();//内容
        var lastSearchParamA = {};
        //问题ID
        lastSearchParamA.ID = DelG;
        //回答人
        lastSearchParamA.Flag = 100;
        //提交问题
        over(lastSearchParamA);
    });

    //提交完结
    function over(lastSearchParamA){
        $.ajax({
            url: "./saveCustomerService.do",
            type: "post",
            data: JSON.stringify({CustomerServices:[lastSearchParamA]}),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    refList();
                    alert("执行成功");

                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    }


    //取消
    $("#cancel").click(function(){
        //取参数
        var qContent = $("#answerContent").val();//内容
        var lastSearchParamB = {};
        //问题ID
        lastSearchParamB.ID = DelG;
        //回答人
        lastSearchParamB.Flag = 99;
        //提交问题
        cancel(lastSearchParamB);
    });

    //提交取消
    function cancel(lastSearchParamB){
        $.ajax({
            url: "./saveCustomerService.do",
            type: "post",
            data: JSON.stringify({CustomerServices:[lastSearchParamB]}),
            success: function(rsp){
                if (rsp.errorCode!=0){
                    showTips(rsp.msg);
                }else{
                    refList();
                    alert("执行成功");
                }
            },
            error: function(){
                showTips("请求出错了");
            },
            dataType: "json"
        });
    }


    //开始初始化
    init();
});
