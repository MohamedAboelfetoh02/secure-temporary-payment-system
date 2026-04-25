document.addEventListener("DOMContentLoaded", function () {
    initPasswordToggles();
    initPaymentMethodFields();
});

function initPasswordToggles() {
    var toggleButtons = document.querySelectorAll("[data-toggle-password]");

    toggleButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            var targetId = button.getAttribute("data-toggle-password");
            var input = document.getElementById(targetId);

            if (!input) {
                return;
            }

            var showing = input.getAttribute("type") === "text";
            input.setAttribute("type", showing ? "password" : "text");
            button.textContent = showing ? "Show" : "Hide";
        });
    });
}

function initPaymentMethodFields() {
    var paymentForm = document.querySelector("[data-payment-form]");
    if (!paymentForm) {
        return;
    }

    var methodInputs = paymentForm.querySelectorAll('input[name="paymentMethod"]');
    var methodSections = {
        "PayPal": document.getElementById("paypalFields"),
        "Credit/Debit Card": document.getElementById("cardFields"),
        "Game Wallet": document.getElementById("walletFields")
    };

    function updateMethodFields() {
        var selectedMethod = "";
        methodInputs.forEach(function (input) {
            if (input.checked) {
                selectedMethod = input.value;
            }
        });

        Object.keys(methodSections).forEach(function (key) {
            if (methodSections[key]) {
                methodSections[key].style.display = key === selectedMethod ? "grid" : "none";
            }
        });
    }

    methodInputs.forEach(function (input) {
        input.addEventListener("change", updateMethodFields);
    });

    updateMethodFields();
}
