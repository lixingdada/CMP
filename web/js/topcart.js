var xhr;
var username = document.getElementById('username').textContent.trim();  //获取用户名
console.log(username);

var cartButton = document.getElementById('cartButton');
cartButton.addEventListener('click', function(event) {
   if(!username){    //判断是否登录
       alert("您尚未登录，点击确认后1秒后自动跳转！");
       setTimeout(() => window.location.href = "loginForm", 1000);
   }else{
       loadCart(); // 调用函数加载购物车内容
   }
});

function loadCart() {
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = process;
    xhr.open('GET', 'http://localhost:8080/webTest1_war_exploded/cartForm?username=' + username);
    xhr.send(null);
}

function process() {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            document.getElementById('cartContainer').innerHTML = xhr.responseText;
        }
    }
}

