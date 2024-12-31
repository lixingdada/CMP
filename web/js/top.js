'use strict';

$(function () {

// 获取头像和悬浮卡片元素
    let profileCircle = document.querySelector('.profile-circle');
    let hoverCard = document.querySelector('.hover-card1');
    let $greet = $('.greeting');
    let $settings = $('.settings');

// 定义一个标志，判断鼠标是否在头像或卡片内
    let isHovering = false;

    $settings.on('click', '.settingItem', function () {
        let $this = $(this);
        let href = $this.data('href'); // 修正：先获取 href 值
        console.log(href); // 确保 href 输出到控制台
        if (href) {
            window.location.href = href; // 页面跳转
        } else {
            console.error('Href not found!'); // 如果 href 为空，输出错误提示
        }
    });

        // 获取当前时间并显示问候语
        const currentHour = new Date().getHours();
        const greeting =
        currentHour < 12 ? "上午好" :
        currentHour < 18 ? "下午好" : "晚上好";

        $greet.prepend(greeting);


// 鼠标移入头像时
    profileCircle.addEventListener('mouseenter', () => {
        isHovering = true;
        // profileCircle.classList.add('expand'); // 放大头像
        hoverCard.classList.add('show'); // 显示卡片
    });

// 鼠标移入悬浮卡片时
    hoverCard.addEventListener('mouseenter', () => {
        isHovering = true;
    });

// 鼠标移出头像时
    profileCircle.addEventListener('mouseleave', () => {
        isHovering = false;
        setTimeout(() => {
            if (!isHovering) {
                hoverCard.classList.remove('show'); // 隐藏卡片
                // profileCircle.classList.remove('expand'); // 恢复头像大小
            }
        }, 300); // 延迟隐藏，避免快速移入移出导致闪烁
    });

// 鼠标移出悬浮卡片时
    hoverCard.addEventListener('mouseleave', () => {
        isHovering = false;
        setTimeout(() => {
            if (!isHovering) {
                hoverCard.classList.remove('show'); // 隐藏卡片
                profileCircle.classList.remove('expand'); // 恢复头像大小
            }
        }, 300); // 延迟隐藏
    });




});