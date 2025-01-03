'use strict'

$(function (){

    let isHover = false;

    $('.activeImg').hover(

        function (){
            isHover = true;
            $(this).siblings('.overlay-card').show();
        },
        function (){
            if (!isHover) {
                $(this).siblings('.overlay-card').hide();
            }
        }
    );

    $('.overlay-card').hover(
        function () {
            isHover = true;
        },
        function () {
            isHover = false;
            $(this).hide();

        }
    );



});