// script.js
document.addEventListener('DOMContentLoaded', function () {
    console.log("✅ Support System JS loaded successfully!");

    // Example: Refresh button function
    const refreshBtn = document.querySelector('.refresh-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            window.location.reload();
        });
    }

    // Example: Confirm delete
    const deleteButtons = document.querySelectorAll('.confirm-button');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            return confirm("⚠️ Are you sure you want to delete this item?");
        });
    });

    // Example: Add small animation to all buttons
    document.querySelectorAll('button, .btn-primary').forEach(btn => {
        btn.addEventListener('mouseenter', () => {
            btn.style.transform = 'scale(1.05)';
        });
        btn.addEventListener('mouseleave', () => {
            btn.style.transform = 'scale(1)';
        });
    });
});
