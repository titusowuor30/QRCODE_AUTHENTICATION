let currentKey = "";

function updateQRCode() {
    fetch('/get-qr-code/')
        .then(response => response.json())
        .then(data => {
            document.getElementById('qr-code-img').src = `data:image/png;base64,${data.qr_code_base64}`;
            currentKey = data.key;
            document.getElementById('status').innerText = 'Pending';
        });
}

function checkAuthStatus() {
    if (currentKey) {
        fetch(`/check-auth-status/${currentKey}/`)
            .then(response => response.json())
            .then(data => {
                if (data.status === 'Completed') {
                    document.getElementById('status').innerText = 'Completed';
                    // Redirect to login URL with login key
                    window.location.href = `/login/?key=${data.login_key}`;
                }
            });
    }
}

// Update QR code every 60 seconds
setInterval(updateQRCode, 60000);
// Check auth status every 30 seconds
setInterval(checkAuthStatus, 10000);

// Initial QR code load
updateQRCode();
