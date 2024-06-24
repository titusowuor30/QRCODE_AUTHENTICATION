function updateQRCode() {
    fetch('/get-qr-code/')
        .then(response => response.json())
        .then(data => {
            document.getElementById('qr-code-img').src = `data:image/png;base64,${data.qr_code_base64}`;
            currentKey = data.key;
            let webkey=document.getElementById('websocket_key').innerText = currentKey;
            webkey.addClass('display','none')
            document.getElementById('status').innerText = 'Pending';
        });
}
// Initial QR code load
updateQRCode();
// Update QR code every 60 seconds
setInterval(updateQRCode, 60000);

