(function () {
    const API = "/api/auth";

    const alertBanner = document.getElementById("alertBanner");

    function showBanner(message, type) {
        if (!alertBanner) return;
        if (!message) {
            alertBanner.textContent = "";
            alertBanner.classList.add("alert--hidden");
            return;
        }
        alertBanner.textContent = message;
        alertBanner.classList.remove("alert--hidden", "alert--danger", "alert--success");
        alertBanner.classList.add(type === "error" ? "alert--danger" : "alert--success");
    }

    async function fetchJson(url, options) {
        const res = await fetch(url, options);
        const text = await res.text();
        let data = null;
        if (text) {
            try {
                data = JSON.parse(text);
            } catch {
                if (!res.ok) throw new Error(text || "Invalid response");
            }
        }
        if (!res.ok) {
            throw new Error((data && data.error) || text || res.statusText || "Request failed");
        }
        return data;
    }

    function setCurrentUser(user) {
        localStorage.setItem("currentUser", JSON.stringify(user));
        if (user && user.token) {
            localStorage.setItem("authToken", user.token);
        }
    }

    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (ev) => {
            ev.preventDefault();
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;
            try {
                const user = await fetchJson(`${API}/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password })
                });
                setCurrentUser(user);
                window.location.href = "/index.html";
            } catch (err) {
                showBanner(err.message, "error");
            }
        });
    }

    const signupForm = document.getElementById("signupForm");
    if (signupForm) {
        signupForm.addEventListener("submit", async (ev) => {
            ev.preventDefault();
            const name = document.getElementById("name").value;
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;
            try {
                const user = await fetchJson(`${API}/signup`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name, email, password })
                });
                setCurrentUser(user);
                window.location.href = "/index.html";
            } catch (err) {
                showBanner(err.message, "error");
            }
        });
    }
})();

