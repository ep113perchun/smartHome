document.getElementById('authForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const authData = {
        name: username,
        password: password
    };

    try {
        const response = await fetch('http://localhost:8085/api/user/auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(authData)
        });

        if (response.ok) {
            const data = await response.json();
            alert('Вход выполнен успешно!');
            // Здесь можно сохранить токен и перенаправить на главную страницу
            // localStorage.setItem('token', data.token);
            // window.location.href = 'index.html';
        } else {
            const errorData = await response.json();
            alert(`Ошибка: ${errorData.message || 'Неверные учетные данные'}`);
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при отправке запроса');
    }
});