const passwordInput = document.getElementById('password');//パスワード入力欄を取得します。
const togglePassword = document.querySelector('.toggle-password');//目のアイコンのspan要素を取得します

togglePassword.addEventListener('click', function(){//アイコンがクリックされたときに実行される処理を登録します
    //パスワード入力欄のtypeを切り替える
    //現在のtype属性（passwordまたはtext）を取得
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    
    //type属性を切り替え
    passwordInput.setAttribute('type', type);

    //アイコンを切り替える
    this.textContent = (type === 'password') ? '👁️' : '🙈'; // 👁️または🙈に変更
});