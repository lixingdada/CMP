'use strict'

$(function (){
    let $editProfileModal = $('#editProfileModal');
    let $editAddressModal = $('#editAddressModal');
    let $addAddressModal = $('#addAddressModal');
    let $closeButtons = $('.close');
    let $openButtons = $('.btn');

    let $userInfoForm = $('#userInfoForm');

    let $addresses = $('#addresses');
    let $editAddressItem;


    //打开个人编辑跟新增的一张小表
    $openButtons.on('click', function () {
        switch ($(this).data('modal')){
            case 'editProfileModal': $editProfileModal.show();break;
            case 'addAddressModal': {
                $('.tips').text('');
                $('#recipient').val('');
                $('#receiverPhone1').val('');
                $('#address').val('');
                $addAddressModal.show();
                break;
            }
        }
    });

    //打开修改地址的小表（这个比较特殊，因为他在.addresses里面）使用事件委托
    $addresses.on('click', '#editAddressButton',function () {
        $('.tips').text('');
        let $thisButton = $(this);
        //从编辑按钮入手获得要修改的东西先
        $editAddressItem = $thisButton.closest('.address-item');
        $('#receiverName').val($thisButton.data('receiver-name'));
        $('#receiverPhone').val($thisButton.data('receiver-phone'));
        $('#receiverAddress').val($thisButton.data('receiver-address'));
        $('#oldReceiverName').val($thisButton.data('receiver-name'));
        $('#oldReceiverPhone').val($thisButton.data('receiver-phone'));
        $('#oldReceiverAddress').val($thisButton.data('receiver-address'));
        $editAddressModal.show();
    });

    $closeButtons.on('click',function () {
        switch (this.id){
            case 'closeEditProfileModal': $editProfileModal.hide();break;
            case 'closeEditAddressModal':$editAddressModal.hide();break;
            case 'closeAddAddressModal':$addAddressModal.hide();break;
        }
    });

    $userInfoForm.on('submit',function (e){
        e.preventDefault();
        let userInfoFormData = $userInfoForm.serialize();
        console.log(userInfoFormData);
        $.ajax({
            type : 'post',
            url : 'userInfo',
            data : userInfoFormData,
            success : function (data){
                alert("个人信息修改成功！");
                $('#oldVirtualName').html('<strong>昵称: </strong> '+$('#virtualName').val())  ;
                $('#oldBirthday').html('<strong>生日: </strong>'+$('#birthday').val())  ;
                $('#oldEmail').html('<strong>邮箱: </strong> ' + $('#email').val()) ;
                $('#oldPhone').html('<strong>电话: </strong>'+$('#phone').val()) ;
                $editProfileModal.hide();
            },
            error : function (errorMsg){
                console.log("个人信息修改异步请求错误："+errorMsg);
            }
        });
    });

    //删除地址的异步请求
    $addresses.on('submit','#deleteForm',function (e){
        e.preventDefault();
        //这里的this是一个form
        let $this = $(this);
        let deleteData =  $this.serialize();
        let $addressItem = $this.closest('.address-item'); // 找到对应的 address-item

        $.ajax({
           type : 'post',
           url : 'editAddress',
            data : deleteData,
            success : function () {
                $addressItem.remove();
            },
            error : function (errorMsg) {
                console.log("收货地址删除异步请求错误："+errorMsg);
            }
        });
    });

    //增加地址的异步请求(注意新增的dom元素就算id，class是正确的，但是不会绑定事件)
    $('#addForm').on('submit',function (e){
        e.preventDefault();
        let $this = $(this);
        let addData =  $this.serialize();
        let $receiverName = $('#recipient').val();
        let $receiverPhone = $('#receiverPhone1').val();
        let $receiverAddress = $('#address').val();

        $.ajax({
            type : 'post',
            url : 'editAddress',
            data : addData,
            success : function (response) {
                if(response === 'repeat'){
                    $('.tips').text("您新增的地址已存在！");
                    console.log('新增有重复！')
                    return;
                }
                $addAddressModal.hide();
                let html = '';
                alert("新增收货地址成功！");
                html = '<div class="address-item">\n' +
                    '                    <div class="address-info">\n' +
                    '                        <p><strong>收货人:</strong>'+$receiverName+'</p>\n' +
                    '                        <p><strong>电话:</strong>'+$receiverPhone+'</p>\n' +
                    '                        <p><strong>地址:</strong>'+$receiverAddress+'</p>\n' +
                    '                    </div>\n' +
                    '                    <button class="btn" id="editAddressButton"\n' +
                    '                            data-modal ="editAddressModal"\n' +
                    '                            data-receiver-name="'+$receiverName+'"\n' +
                    '                            data-receiver-phone="'+$receiverPhone+'"\n' +
                    '                            data-receiver-address="'+$receiverAddress+'">\n' +
                    '                        修改\n' +
                    '                    </button>\n' +
                    '                    <form id="deleteForm" action="editAddress" method="post">\n' +
                    '                        <input type="hidden" name ="flag" value="delete">\n' +
                    '                        <input type="hidden" name="receiverName" value="'+$receiverName+'">\n' +
                    '                        <input type="hidden" name="receiverPhone" value="'+$receiverPhone+'">\n' +
                    '                        <input type="hidden" name="receiverAddress" value="'+$receiverAddress+'">\n' +
                    '                        <button type="submit" class="btn">删除</button>\n' +
                    '                    </form>\n' +
                    '                </div>'
                $addresses.append(html);
            },
            error : function (errorMsg) {
                console.log("新增收货地址异步请求错误："+errorMsg);
            }
        });

        //修改地址的异步请求


    });


        $('#editAddressForm').on('submit',function (e){
            e.preventDefault();
            let $this = $(this);
            let editData =  $this.serialize();

            let $receiverName = $('#receiverName').val();
            let $receiverPhone = $('#receiverPhone').val();
            let $receiverAddress = $('#receiverAddress').val();

            $.ajax({
                type : 'get',
                url : 'editAddress',
                data : editData,
                success : function (response) {
                    if(response === 'repeat'){
                        $('.tips').text("您的修改后的地址已存在！");
                        console.log('修改有重复！')
                        return;
                    }
                    $editAddressItem.remove();
                    $editAddressModal.hide();
                    let html = '';
                    alert("修改收货地址成功！");
                    html = '<div class="address-item">\n' +
                        '                    <div class="address-info">\n' +
                        '                        <p><strong>收货人:</strong>'+$receiverName+'</p>\n' +
                        '                        <p><strong>电话:</strong>'+$receiverPhone+'</p>\n' +
                        '                        <p><strong>地址:</strong>'+$receiverAddress+'</p>\n' +
                        '                    </div>\n' +
                        '                    <button class="btn"\n' +
                        '                            data-modal ="editAddressModal" id="editAddressButton"\n' +
                        '                            data-receiver-name="'+$receiverName+'"\n' +
                        '                            data-receiver-phone="'+$receiverPhone+'"\n' +
                        '                            data-receiver-address="'+$receiverAddress+'">\n' +
                        '                        修改\n' +
                        '                    </button>\n' +
                        '                    <form id="deleteForm" action="editAddress" method="post">\n' +
                        '                        <input type="hidden" name ="flag" value="delete">\n' +
                        '                        <input type="hidden" name="receiverName" value="'+$receiverName+'">\n' +
                        '                        <input type="hidden" name="receiverPhone" value="'+$receiverPhone+'">\n' +
                        '                        <input type="hidden" name="receiverAddress" value="'+$receiverAddress+'">\n' +
                        '                        <button type="submit" class="btn">删除</button>\n' +
                        '                    </form>\n' +
                        '                </div>'
                    $addresses.prepend(html);
                },
                error : function (errorMsg) {
                    console.log("修改收货地址异步请求错误："+errorMsg);
                }
            });

        });

        // 头像上传
    $('#profileAvatar').on('click', function () {
        $('#avatarUpload').click();
    });

    $('#avatarUpload').on('change',function (e){
        let file = e.target.files[0];

        if (file && file.type.startsWith('image/')) {
            const formData = new FormData();
            formData.append('avatar', file);
            // console.log(formData);

            $.ajax({
                type : 'post',
                url : 'uploadAvatar',
                data : formData,
                processData: false,     // 阻止 jQuery 将 data 转换为字符串
                contentType: false,     // 告诉 jQuery 不要设置默认的 Content-Type
                success : function (data){
                    console.log('我在这');
                    $('#profileAvatar').attr('src', `images/${file.name}`);
                    $('#avatarImg').attr('src', `images/${file.name}`);
                },
                error : function (errorMsg){
                    console.log(errorMsg);
                    console.log('修改头像异步请求失败');
                }
            })
        }else{
            alert('请选择有效的图片文件！');
        }


    });

});