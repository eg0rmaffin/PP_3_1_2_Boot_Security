// просто закрывалки открывалки модалок
$(document).ready(function() {
    $('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
        e.target // newly activated tab
        e.relatedTarget // previous active tab
    })
});

$(document).ready(function() {
    $('.edit-user').on('click', function() {
        var userId = $(this).data('userid');
        var firstName = $(this).data('firstname');
        var lastName = $(this).data('lastname');
        var email = $(this).data('email');

        $('#editUserId').val(userId);
        $('#editUserFirstName').val(firstName);
        $('#editUserLastName').val(lastName);
        $('#editUserEmail').val(email);

        $('#editUserModal').modal('show');
    });

    $('.delete-user').on('click', function() {
        var userId = $(this).data('userid');
        $('#deleteUserId').val(userId);
        $('#deleteUserModal').modal('show');
    });
});






async function addUser() {
    const newUser = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        roles: Array.from(document.getElementById('roles').selectedOptions).map(option => ({ name: option.value }))
    };

    console.log('New user data:', JSON.stringify(newUser));  // Вывод данных для отладки

    try {
        const response = await fetch('/api/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },  // Изменение здесь
            body: JSON.stringify(newUser)
        });
        if (!response.ok) throw new Error(`Failed to add user: ${response.statusText}`);
        alert('User added successfully!');
        document.getElementById('addUserForm').reset(); // Очистка формы после успешного добавления
        loadUsers(); // Вызов функции загрузки пользователей для обновления таблицы
        $('.nav-tabs a[href="#allUsers"]').tab('show');
    } catch (error) {
        console.error('Error adding user:', error);
        alert('Failed to add user: ' + error.message);
    }
}

async function submitEditUser() {
    const userId = document.getElementById('editUserId').value;
    const updatedUser = {
        firstName: document.getElementById('editUserFirstName').value,
        lastName: document.getElementById('editUserLastName').value,
        email: document.getElementById('editUserEmail').value,
        password: document.getElementById('editUserPassword').value,
        roles: Array.from(document.getElementById('editUserRoles').selectedOptions).map(option => ({ name: option.value }))
    };

    console.log('Updated user data:', JSON.stringify(updatedUser));  // Логирование данных на клиентской стороне

    try {
        const response = await fetch(`/api/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedUser)
        });
        if (!response.ok) throw new Error(`Failed to update user: ${response.statusText}`);
        alert('User updated successfully!');
        $('#editUserModal').modal('hide');
        loadUsers();  // Обновление списка пользователей
    } catch (error) {
        console.error('Error updating user:', error);
        alert('Error updating user: ' + error.message);
    }
}




function deleteUser() {
    const userId = document.getElementById('deleteUserId').value;
    console.log('Attempting to delete user with ID:', userId);  // Для отладки

    fetch(`/api/users/${userId}`, {
        method: 'DELETE'
    }).then(response => {
        if (!response.ok) throw new Error(`Failed to delete user: ${response.statusText}`);
        console.log('User deleted successfully');
        $('#deleteUserModal').modal('hide');
        loadUsers();  // Refresh the list of users
    }).catch(error => {
        console.error('Error deleting user:', error);
        alert('Error deleting user: ' + error.message);
    });
}


document.addEventListener('DOMContentLoaded', function() {
    loadUserDetails();
    loadUsers();
});

function loadUserDetails() {
    fetch('/api/users/details')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user details');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('userEmail').textContent = data.username;
            document.getElementById('userRoles').textContent = data.authorities.join(', ');
        })
        .catch(error => {
            document.getElementById('userInfo').textContent = 'Error: ' + error.message;
            console.error('Error loading user details:', error);
        });
}

function loadUsers() {
    console.log("Запуск loadUsers");
    fetch('/api/users')
        .then(response => {
            console.log("Получен ответ от сервера");
            if (!response.ok) {
                throw new Error(`Failed to fetch users: ${response.statusText}`);
            }
            return response.json();
        })
        .then(users => {
            console.log("Обработка полученных данных");
            const usersTableBody = document.getElementById('usersTableBody');
            usersTableBody.innerHTML = ''; // Очистка таблицы перед заполнением
            users.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(role => role.name).join(', ')}</td>
                <td><button class="btn btn-primary" onclick="editUser(${user.id})">Edit</button></td>
                <td><button class="btn btn-danger" onclick="prepareDelete(${user.id})">Delete</button></td>
            `;
                usersTableBody.appendChild(row);
            });
            console.log("Данные загружены и отображены");
        })
        .catch(error => {
            console.error('Error loading users:', error);
            alert('Error loading users: ' + error.message);
        });
}


function prepareDelete(userId) {
    document.getElementById('deleteUserId').value = userId;
    $('#deleteUserModal').modal('show');
}

function editUser(userId) {
    fetch(`/api/users/${userId}`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch user details');
            return response.json();
        })
        .then(user => {
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editUserFirstName').value = user.firstName;
            document.getElementById('editUserLastName').value = user.lastName;
            document.getElementById('editUserEmail').value = user.email;

            // Установим текущие роли пользователя
            const editRolesSelect = document.getElementById('editUserRoles');
            Array.from(editRolesSelect.options).forEach(option => {
                option.selected = user.roles.some(role => role.name === option.value);
            });

            $('#editUserModal').modal('show');
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Error fetching user: ' + error.message);
        });
}


document.addEventListener('DOMContentLoaded', function() {
    // Fetch current user details
    fetch('/api/users/details')
        .then(response => response.json())
        .then(data => {
            if (data.username) {
                document.getElementById('currentUserInfo').innerHTML = `
                    <p><strong>Username:</strong> ${data.username}</p>
                    <p><strong>Roles:</strong> ${data.authorities.join(', ')}</p>
                `;
            } else {
                document.getElementById('currentUserInfo').innerHTML = '<p>User not authenticated</p>';
            }
        })
        .catch(error => console.error('Error fetching user details:', error));
});

function loadRoles() {
    fetch('/api/users/roles')
        .then(response => response.json())
        .then(data => {
            const rolesSelect = document.getElementById('roles');
            rolesSelect.innerHTML = data.map(role => `<option value="${role}">${role}</option>`).join('');

            const editRolesSelect = document.getElementById('editUserRoles');
            editRolesSelect.innerHTML = data.map(role => `<option value="${role}">${role}</option>`).join('');
        })
        .catch(error => console.error('Error fetching roles:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    loadRoles();
});

function loadEditUserRoles(userId) {
    fetch('/api/users/roles')
        .then(response => response.json())
        .then(data => {
            const rolesSelect = document.getElementById('editUserRoles');
            rolesSelect.innerHTML = data.map(role => `<option value="${role}">${role}</option>`).join('');

            // Load existing user roles
            fetch(`/api/users/${userId}`)
                .then(response => response.json())
                .then(user => {
                    user.roles.forEach(role => {
                        const option = rolesSelect.querySelector(`option[value="${role}"]`);
                        if (option) {
                            option.selected = true;
                        }
                    });
                });
        })
        .catch(error => console.error('Error fetching roles:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    // Fetch current user details
    fetch('/api/users/currentUser')
        .then(response => response.json())
        .then(data => {
            if (data.email) {
                document.getElementById('currentUserInfo').innerHTML = `
                    <p><strong>ID:</strong> ${data.id}</p>
                    <p><strong>First Name:</strong> ${data.firstName}</p>
                    <p><strong>Last Name:</strong> ${data.lastName}</p>
                    <p><strong>Email:</strong> ${data.email}</p>
                    <p><strong>Roles:</strong> ${data.roles.map(role => role.name).join(', ')}</p>
                `;
            } else {
                document.getElementById('currentUserInfo').innerHTML = '<p>User not authenticated</p>';
            }
        })
        .catch(error => console.error('Error fetching user details:', error));
});


function loadFullUserDetails() {
    fetch('/api/users/currentUserDetails')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user details');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('fullUserId').textContent = data.id;
            document.getElementById('fullUserFirstName').textContent = data.firstName;
            document.getElementById('fullUserLastName').textContent = data.lastName;
            document.getElementById('fullUserEmail').textContent = data.email;
            document.getElementById('fullUserRoles').textContent = data.roles.join(', ');
        })
        .catch(error => {
            document.getElementById('fullUserInfo').textContent = 'Error: ' + error.message;
            console.error('Error loading full user details:', error);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    loadFullUserDetails(); // Новый метод для загрузки полной информации
});



