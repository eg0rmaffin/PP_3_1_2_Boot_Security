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

$(document).ready(function() {
    $('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
        e.target // newly activated tab
        e.relatedTarget // previous active tab
    })
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

document.addEventListener('DOMContentLoaded', function() {
    loadUserDetails();
});