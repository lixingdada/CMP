var xhr;
var username = document.getElementById('username').textContent.trim();  //获取用户名


//1.数量输入框事件
var quantityInputs = document.querySelectorAll('input[type="number"]');  //获取所有数量输入框

for (var i = 0, length = quantityInputs.length; i < length; i++) {
    quantityInputs[i].addEventListener('input', function () {    //监听数量输入框的输入事件
        var itemId   = this.closest('tr').querySelector('td[id="itemId"]').textContent,   //获取商品id
            quantity = this.value;      //获取商品数量
        if (isNaN(quantity) || quantity < 0 || quantity % 1 !== 0 || !/^\d+$/.test(quantity)) {   //判断输入是否合法
            this.value = '';
            quantity = 1;
            this.title = '数量只能为非负数，最少为0';
        }else {
            this.title = '';
        }

        if(quantity === '0') {
            sendRemoveRequest(itemId); // 发送请求删除该项
            var row = this.closest('tr'); // 获取当前行
            row.parentNode.removeChild(row); // 删除当前行
        } else if (quantity !== null) {
            sendNumberRequest(itemId, quantity);
            this.closest('tr').querySelector('td[id="itemTotal"]').textContent = (quantity * parseFloat(this.closest('tr').querySelector('td[id="itemPrice"]').textContent)).toFixed(2);
        }
    });

    //处理空值情况，焦点丢失时设置默认数量为1
    quantityInputs[i].addEventListener('blur', function () {
        var quantity = this.value;

        if (quantity === null || quantity === '') {
            this.value= 1;
            this.title = '无数量输入时，自动补1';
            sendNumberRequest(this.closest('tr').querySelector('td[id="itemId"]').textContent, 1); // 发送请求更新总价
            this.closest('tr').querySelector('td[id="itemTotal"]').textContent = parseFloat(this.closest('tr').querySelector('td[id="itemPrice"]').textContent).toFixed(2);
        }
    });
}

//发送请求
function sendNumberRequest(itemId, quantity) {
    //console.log('我走了');
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = processNumber;
    xhr.open('GET', 'updateCart?itemId='+itemId+'&quantity='+quantity+'&username='+username);
    xhr.send(null);
}

//处理响应
function processNumber() {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            var responseInfo = xhr.responseText;
            var totalAmountElement = document.getElementById('cartTotal');
            totalAmountElement.textContent = responseInfo;
        }
    }
}



//2.移除按钮事件
var deleteButtons = document.querySelectorAll('button[id="remove"]');

for (var j = 0, length2 = deleteButtons.length; j < length2; j++) {
    deleteButtons[j].addEventListener('click', function () {
        var itemId = this.closest('tr').querySelector('td[id="itemId"]').textContent;
        sendRemoveRequest(itemId);
        var row = this.closest('tr');
        row.parentNode.removeChild(row);
    });
}

//发送请求
function sendRemoveRequest(itemId) {
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = processRemove;
    xhr.open('GET', 'removeCartItem?itemId='+itemId+'&username='+username);
    xhr.send(null);
}

//处理响应
function processRemove(row) {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            var responseInfo = xhr.responseText;
            var totalAmountElement = document.getElementById('cartTotal');
            if(responseInfo === '0') {
                totalAmountElement.textContent = '0.00';
            } else {
                totalAmountElement.textContent = responseInfo;
            }
        }
    }
}




