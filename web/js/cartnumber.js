var xhr;
var username = document.getElementById('username').textContent.trim();  //获取用户名
console.log(username);

var quantityInputs = document.querySelectorAll('input[type="number"]');  //获取所有数量输入框

for (var i = 0, length = quantityInputs.length; i < length; i++) {
    quantityInputs[i].addEventListener('input', function () {    //监听数量输入框的输入事件
        var itemId   = this.closest('tr').querySelector('a[id="itemId"]').textContent,   //获取商品id
            quantity = this.value;      //获取商品数量

        if (isNaN(quantity) || quantity < 0 || quantity % 1 !== 0) {   //判断输入是否合法
            this.value= 1;
            this.title = '数量只能为非负数，最少为0';
        }else {
            this.title = '';
        }

        if (quantity) {    //发送请求更新总价
            sendRequest(itemId, quantity);
            this.closest('tr').querySelector('td[id="itemTotal"]').textContent = (quantity * parseFloat(this.closest('tr').querySelector('td[id="itemPrice"]').textContent)).toFixed(2);
        }
    });

    //处理空值情况，焦点丢失时设置默认数量为1
    quantityInputs[i].addEventListener('blur', function () {
        var quantity = this.value;
        if (!quantity) {
            this.value= 1;
            this.title = '无数量输入时，自动补1';
            sendRequest(this.closest('tr').querySelector('a[id="itemId"]').textContent, 1); // 发送请求更新总价
            this.closest('tr').querySelector('td[id="itemTotal"]').textContent = parseFloat(this.closest('tr').querySelector('td[id="itemPrice"]').textContent).toFixed(2);
        }
    });
}

//发送请求
function sendRequest(itemId, quantity) {
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = process;
    xhr.open('GET', 'updateCart?itemId='+itemId+'&quantity='+quantity+'&username='+username);
    xhr.send(null);
}

//处理响应
function process() {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            var responseInfo = xhr.responseText;
            var totalAmountElement = document.getElementById('cartTotal');
            totalAmountElement.textContent = responseInfo;
        }
    }
}



