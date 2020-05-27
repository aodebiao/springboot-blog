

var wait = 0;
var timer = null;


$('#send-email').click(function () {
    $("#send-email").addClass('disabled');//点击之后禁用
    $('.ui.form').submit();
    if ($('.ui.form.bind').form('is valid', 'email')) {
        $('.verificationCode.field').removeClass('error');
        $('.ui.error.message').empty();
        var flag = $("#send-email").data('flag')
        var email = $('input[name="email"]').val();
        $.post("/user/sendEmail", {'email': email, 'flag': flag}, function (res) {
            if (!res.success) {
                $('.ui.error.message').append("<ul class='list'><li class='li'></li></ul>");
                $('.li').append(res.message);
                $("#send-email").removeClass('disabled');
                $("#send-email").text('获取邮箱验证码!')
                return;
            } else {
                $("#send-email").addClass('disabled');//成功后修改鼠标的样式，变为不可点击
                wait = parseInt(res.time_interval);
                timer = setInterval(Countdown, 1000);//定时器，发送成功后，禁用按钮，60恢复
                return;
            }
        })
    }else{
        $("#send-email").removeClass('disabled');//点击之后禁用
    }
});


function showPassword() {
    $('#fragment-modal').modal({
        onShow:function () {
            var message = "qq用户绑定成功的初始密码：<font color='red'>QWER111</font>"
            $('#show-message').html(message);
        }
    }).modal('show');
}
function Countdown() {
    wait = wait - 1;
    if (wait == 0) {
        $("#send-email").removeClass('disabled');
        $("#send-email").text("点我就送验证码！");
        wait = 60;
        clearInterval(timer);
        return;
    }
    $("#send-email").html("邮件已发送！<font color = 'red'>" + wait + "</font>秒");
}


function clearRegisterModal() {
    $("#username").val('');
    $("#nickname").val('');
    $("#password").val('');
    $("#check-password").val('');
    $("#email").val('');
    $("#verificationCode").val('');
    $("#avatar").val('');
    $('input:radio[name="gender"]:checked').val('');
}


$('#forget-button').click(function () {
    alert("嘿嘿，还没做好。。。");
});




