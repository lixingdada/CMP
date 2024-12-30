
'use strict';

$(function () {

// 获取头像和悬浮卡片元素
    let profileCircle = document.querySelector('.profile-circle');
    let hoverCard = document.querySelector('.hover-card1');
    let $greet = $('.greeting');

// 定义一个标志，判断鼠标是否在头像或卡片内
    let isHovering = false;



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