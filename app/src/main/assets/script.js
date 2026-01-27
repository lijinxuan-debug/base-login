const addBtn = document.getElementById('addSkillBtn');
const input = document.getElementById('skillInput');
const container = document.getElementById('skillsContainer');

let username = ""

addBtn.addEventListener('click', function() {
    const val = input.value.trim();

    if (val === "") {
        window.AndroidBridge.showToastWarn();
        return;
    }

    const card = document.createElement('div');
    card.className = 'skill-card';
    card.innerText = val;

    card.addEventListener('dblclick', function() {
         container.removeChild(this);
    });

    container.appendChild(card);

    input.value = "";
    input.focus();
});

document.querySelectorAll('.skill-card').forEach(item => {
    item.addEventListener('dblclick', function() {
        container.removeChild(this);
    });
});

function sayHello() {
    if (window.AndroidBridge) {
            // 调用安卓类里的 showToast 方法
            window.AndroidBridge.showToastHello(username);
        } else {
            // 如果在电脑浏览器打开，降级使用普通的 alert
            alert(`你好！我是${username} ，很高兴认识你！`);
        }
}

window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const nameParam = urlParams.get('name');

    if (nameParam) {
        document.querySelectorAll('.user-name-display').forEach(el => {
                el.innerText = nameParam;
            });
            username = nameParam;
    }
};