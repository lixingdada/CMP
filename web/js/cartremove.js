var xhr;
var username = document.getElementById('username').textContent.trim();  //获取用户名
console.log(username);

//删除按钮事件
var deleteButtons = document.querySelectorAll('button[id="remove"]');

for (var i = 0, length = deleteButtons.length; i < length; i++) {
    deleteButtons[i].addEventListener('click', function () {
        var itemId = this.closest('tr').querySelector('a[id="itemId"]').textContent;
        sendRemoveRequest(itemId);
    });
}

function sendRemoveRequest(itemId) {
    xhr = new XMLHttpRequest();
    xhr.onreadystatechange = processRemove;
    xhr.open('GET', 'removeCartItem?itemId='+itemId+'&username='+username);
    xhr.send(null);
}

function processRemove() {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            var responseInfo = xhr.responseText;
            var totalAmountElement = document.getElementById('cartTotal');
            totalAmountElement.textContent = responseInfo;
            var row = this.closest('tr');
            row.parentNode.removeChild(row);
        }
    }
}