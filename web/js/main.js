'use strict';

$(function (){

    let $menuItems = $('.menu-item');
    let $hoverCard = $('.hover-card');
    let $categoryContainer = $('.category-container');
    let isHovering = false;
    let cache = {};  // 用于缓存已经加载的数据
    let $productAutoList = $('#productAutoList');
    let $information = $('#information');
    let userInput = $information.val();

    //我感觉这个地方其实可以写在jsp里面
    // $.ajax({
    //     type : 'GET',
    //     url : './mainShow',
    //     data : null,
    //     success : function (data){
    //         for(let i = 0; i < data.length; i++){
    //             let htmlString = '';
    //             htmlString += '<a href="" class="category-box">';
    //             htmlString += ' <img src="images/'+data[i].attribute2+'" alt="Python">';
    //             htmlString += '<div className="category-text">'+data[i].itemId+'</div>';
    //             htmlString += '</a>';
    //             $categoryContainer.append(htmlString);
    //         }
    //     },
    //     error : function (errorMsg){
    //         console.log(errorMsg);
    //     }
    // });

    $menuItems.on('mouseenter', function () {
        isHovering = true;
        let category = $(this).data('target');
        $hoverCard.html('');

        // 如果缓存中有该类别的内容，直接展示并返回(好处：减少请求次数、避免了多次显示同一内容)
        if (cache[category]) {
            // $.each(cache[category], function(i, item) {
            //     let htmlString = '';
            //     htmlString += '  |  <a href="" class ="no-underline"> ' + item.name + '</a>';
            //     $hoverCard.append(htmlString);
            // });
            for (let i = 0; i < cache[category].length; i++){
                let productName = cache[category][i].name;
                let productId = cache[category][i].productId;
                let htmlString = '';
                    htmlString += '  |  <a href="product?productId='+ productId +'&productName='+productName+'" class ="no-underline"> ' + productName + '</a>';
                    $hoverCard.append(htmlString);
            }
            $hoverCard.show();
            return;  // 如果数据已加载，直接返回
        }

        // 否则，发起新的 AJAX 请求

        $.ajax({
            type: 'GET',
            url: './category?category=' + category,
            data: null,
            success: function(data) {
                // 缓存数据
                cache[category] = data;

                // 填充悬浮卡片
                for (let i = 0; i < data.length; i++) {
                    let productName = cache[category][i].name;
                    let productId = cache[category][i].productId;
                    let htmlString = '';
                    htmlString +=  '  |  <a href="product?productId='+ productId +'&productName='+productName+'" class ="no-underline"> ' + productName + '</a>';
                    $hoverCard.append(htmlString);
                }
                $hoverCard.show();
            },
            error: function(errorMsg) {
                console.log(errorMsg);
            }
        });
    });

    $menuItems.on('mouseleave', function () {
        isHovering = false;
        setTimeout(() => {
            if (!isHovering) {
                $hoverCard.hide();
            }
        }, 30);
    });

// 悬浮卡片的 hover 状态
    $hoverCard.on('mouseenter', () => {
        isHovering = true;
        // $hoverCard.show();
    });

    $hoverCard.on('mouseleave', () => {
        isHovering = false;
        setTimeout(() => {
            if (!isHovering) {
                $hoverCard.hide();
            }
        }, 30);
    });

    // 自动补全
    $information.on('input',function (){

        let $searchValue = $(this).val();
        userInput = $searchValue;
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

    $productAutoList.on('mouseenter','.productAutoItem',function (){
        let $thisLi = $(this).text();
        $information.val($thisLi);
    })

    $productAutoList.on('mouseleave','.productAutoItem',function (){
        let $thisLi = $(this).text();
        $information.val(userInput);
    })

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

});


// 悬浮选项卡显示/隐藏逻辑
// const menuItems = document.querySelectorAll(".menu-item");
// const hoverCards = document.querySelectorAll(".hover-card");
//
// let isHovering = false; // 标志是否在悬浮窗或触发区域内
//
// menuItems.forEach(item => {
//     item.addEventListener("mouseenter", () => {
//         const target = item.getAttribute("data-target");
//         const hoverCard = document.getElementById(target);
//
//         isHovering = true; // 标志鼠标在触发区域
//         hoverCard.style.display = "block"; // 显示悬浮窗
//     });
//
//     item.addEventListener("mouseleave", () => {
//         const target = item.getAttribute("data-target");
//         const hoverCard = document.getElementById(target);
//         isHovering = false; // 鼠标移出悬浮窗区域
//         setTimeout(() => {
//             if (!isHovering) {
//                 hoverCard.style.display = "none"; // 如果鼠标已移出，隐藏悬浮窗
//             }
//         },0); // 延迟关闭，避免快速闪烁
//     });
// });

// hoverCards.forEach(card => {
//     card.addEventListener("mouseenter", () => {
//         isHovering = true; // 标志鼠标在悬浮窗区域
//         card.style.display = "block"; // 保持显示悬浮窗
//     });
//
//     card.addEventListener("mouseleave", () => {
//         isHovering = false; // 鼠标移出悬浮窗区域
//         setTimeout(() => {
//             if (!isHovering) {
//                 card.style.display = "none"; // 如果鼠标完全移出，隐藏悬浮窗
//             }
//         }, 0); // 延迟关闭，避免快速闪烁
//     });
// });