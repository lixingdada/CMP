'use strict'

$(function (){

    let $productAutoList = $('#productAutoList');
    let $information = $('#information');

// 自动补全
    $information.on('input',function (){

        let $searchValue = $(this).val();
        // console.log(search);
        handleAutoComplete($searchValue);
    });

    $information.on('blur',function (){
        setTimeout(function () {
            $productAutoList.hide();
        }, 200);
    });

    $information.on('focus', function () {
        let $searchValue = $(this).val();
        if($searchValue.trim() != null && $searchValue.trim() != '' && $productAutoList.children().length > 0)
            $productAutoList.show();
    });

    function handleAutoComplete(searchValue){
        $productAutoList.html('');
        if(searchValue.trim() != null && searchValue.trim() != ''){
            let search = $information.serialize();
            $.ajax({
                type : 'post',
                url : 'main',
                data : search,
                success : function (data){
                    for(let i = 0; i < data.length; i++){
                        let html = '';
                        html += '<form  id="form'+i+'" action="./product" method="post">' +
                            '<input type="hidden" name="information" value="'+ data[i] +'"/>' +
                            '<li class = "productAutoItem" data-form = "'+i+'">'+data[i]+'</li>' +
                            '</form>';
                        $productAutoList.append(html);
                    }
                    $productAutoList.on('click','.productAutoItem',function (){
                        let num = $(this).data('form');
                        $('#form'+num).submit();
                    });
                    $productAutoList.show();
                },
                error : function (errorMsg){
                    console.log(errorMsg);
                }
            });
        }
    }

    // 点击图片显示弹窗
    $(".left-image img").on("click", function () {
        let imgSrc = $(this).attr("src");
        $(".image-modal").fadeIn();
        $(".modal-content").attr("src", imgSrc);
    });

    // 点击关闭按钮隐藏弹窗
    $(".close-btn").on("click", function () {
        $(".image-modal").fadeOut();
    });

    // 点击弹窗背景隐藏弹窗
    $(".image-modal").on("click", function (e) {
        if ($(e.target).hasClass("image-modal")) {
            $(".image-modal").fadeOut();
        }
    });

});