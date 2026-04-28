(function () {
    const API = "/api";

    const monthSelect = document.getElementById("monthSelect");
    const yearInput = document.getElementById("yearInput");
    const applyPeriod = document.getElementById("applyPeriod");
    const authBar = document.getElementById("authBar");

    const alertBanner = document.getElementById("alertBanner");
    const totalSpentEl = document.getElementById("totalSpent");
    const budgetLimitEl = document.getElementById("budgetLimit");
    const remainingEl = document.getElementById("remaining");
    const budgetStatusEl = document.getElementById("budgetStatus");

    const budgetForm = document.getElementById("budgetForm");
    const budgetAmount = document.getElementById("budgetAmount");

    const expenseForm = document.getElementById("expenseForm");
    const expAmount = document.getElementById("expAmount");
    const expDate = document.getElementById("expDate");
    const expCategory = document.getElementById("expCategory");
    const expDesc = document.getElementById("expDesc");

    const filterCategory = document.getElementById("filterCategory");
    const expenseRows = document.getElementById("expenseRows");
    const emptyState = document.getElementById("emptyState");

    const editModal = document.getElementById("editModal");
    const editForm = document.getElementById("editForm");
    const editId = document.getElementById("editId");
    const editAmount = document.getElementById("editAmount");
    const editDate = document.getElementById("editDate");
    const editCategory = document.getElementById("editCategory");
    const editDesc = document.getElementById("editDesc");

    let categories = [];
    let pieChart = null;
    let lineChart = null;

    const monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];

    function fmtMoney(n) {
        if (n == null || Number.isNaN(Number(n))) return "—";
        return "₹ " + Number(n).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function showBanner(message, type) {
        if (!message) {
            alertBanner.textContent = "";
            alertBanner.classList.add("alert--hidden");
            return;
        }
        alertBanner.textContent = message;
        alertBanner.classList.remove("alert--hidden", "alert--danger", "alert--success");
        alertBanner.classList.add(type === "error" ? "alert--danger" : "alert--success");
    }

    function getYearMonth() {
        return {
            year: parseInt(yearInput.value, 10),
            month: parseInt(monthSelect.value, 10)
        };
    }

    function daysInMonth(year, month) {
        return new Date(year, month, 0).getDate();
    }

    function pad2(n) {
        return String(n).padStart(2, "0");
    }

    function getCurrentUser() {
        try {
            const raw = localStorage.getItem("currentUser");
            return raw ? JSON.parse(raw) : null;
        } catch {
            return null;
        }
    }

    function getAuthToken() {
        return localStorage.getItem("authToken");
    }

    function renderAuthBar() {
        if (!authBar) return;
        const user = getCurrentUser();
        if (!user) {
            authBar.innerHTML = `
                <a class="btn btn--secondary" href="/login.html">Login</a>
                <a class="btn btn--ghost" href="/signup.html">Sign up</a>
            `;
            return;
        }
        authBar.innerHTML = `
            <span style="color: var(--muted); font-size: 0.85rem;">Hi, ${escapeHtml(user.name || "User")}</span>
            <button type="button" class="btn btn--secondary" id="logoutBtn">Logout</button>
        `;
        const logoutBtn = document.getElementById("logoutBtn");
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("currentUser");
            localStorage.removeItem("authToken");
            renderAuthBar();
            showBanner("Logged out.", "success");
        });
    }

    async function fetchJson(url, options) {
        const token = getAuthToken();
        const headers = Object.assign({}, (options && options.headers) || {});
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
        const res = await fetch(url, Object.assign({}, options || {}, { headers }));
        const text = await res.text();
        let data = null;
        if (text) {
            try {
                data = JSON.parse(text);
            } catch {
                if (!res.ok) {
                    throw new Error(text || "Invalid response");
                }
                data = null;
            }
        }
        if (!res.ok) {
            const err = (data && data.error) || text || res.statusText || "Request failed";
            throw new Error(err);
        }
        return data;
    }

    async function loadCategories() {
        categories = await fetchJson(`${API}/categories`);
        expCategory.innerHTML = "";
        editCategory.innerHTML = "";
        filterCategory.innerHTML = '<option value="">All</option>';
        categories.forEach((c) => {
            const opt = document.createElement("option");
            opt.value = c.id;
            opt.textContent = c.name;
            expCategory.appendChild(opt.cloneNode(true));
            editCategory.appendChild(opt);
            const f = document.createElement("option");
            f.value = c.id;
            f.textContent = c.name;
            filterCategory.appendChild(f);
        });
    }

    async function loadBudget() {
        const { year, month } = getYearMonth();
        const b = await fetchJson(`${API}/budget?year=${year}&month=${month}`);
        if (b.set && b.amountLimit != null) {
            budgetAmount.value = b.amountLimit;
        } else {
            budgetAmount.value = "";
        }
    }

    async function loadSummary() {
        const { year, month } = getYearMonth();
        const s = await fetchJson(`${API}/analytics/summary?year=${year}&month=${month}`);

        totalSpentEl.textContent = fmtMoney(s.totalSpent);

        if (s.budgetSet) {
            budgetLimitEl.textContent = fmtMoney(s.budgetLimit);
            remainingEl.textContent = fmtMoney(s.remaining);
            budgetStatusEl.className = "badge";
            if (s.overBudget) {
                budgetStatusEl.textContent = "Over budget";
                budgetStatusEl.classList.add("badge--bad");
                showBanner(
                    `Warning: spending for ${monthNames[month - 1]} ${year} exceeds your monthly budget.`,
                    "error"
                );
            } else {
                budgetStatusEl.textContent = "Within budget";
                budgetStatusEl.classList.add("badge--ok");
                showBanner("", "success");
            }
        } else {
            budgetLimitEl.textContent = "Not set";
            remainingEl.textContent = "—";
            budgetStatusEl.className = "badge badge--neutral";
            budgetStatusEl.textContent = "No budget";
            showBanner("", "success");
        }

        updatePieChart(s.byCategory || []);
    }

    function updatePieChart(byCategory) {
        const ctx = document.getElementById("pieChart");
        const labels = byCategory.map((x) => x.categoryName);
        const data = byCategory.map((x) => Number(x.total));
        const colors = [
            "#38bdf8", "#a78bfa", "#f472b6", "#fbbf24", "#4ade80",
            "#fb923c", "#2dd4bf", "#94a3b8"
        ];

        if (pieChart) {
            pieChart.destroy();
            pieChart = null;
        }

        if (!labels.length || data.every((d) => d === 0)) {
            pieChart = new Chart(ctx, {
                type: "doughnut",
                data: {
                    labels: ["No data"],
                    datasets: [{ data: [1], backgroundColor: ["#334155"] }]
                },
                options: {
                    plugins: { legend: { labels: { color: "#94a3b8" } } }
                }
            });
            return;
        }

        pieChart = new Chart(ctx, {
            type: "doughnut",
            data: {
                labels,
                datasets: [{
                    data,
                    backgroundColor: labels.map((_, i) => colors[i % colors.length]),
                    borderWidth: 0
                }]
            },
            options: {
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: { color: "#cbd5e1", boxWidth: 12 }
                    }
                }
            }
        });
    }

    async function loadDailyChart() {
        const { year, month } = getYearMonth();
        const daily = await fetchJson(`${API}/analytics/daily?year=${year}&month=${month}`);
        const map = new Map();
        daily.forEach((p) => {
            const d = p.date;
            const key = typeof d === "string" ? d : `${d[0]}-${pad2(d[1])}-${pad2(d[2])}`;
            map.set(key, Number(p.amount));
        });

        const dim = daysInMonth(year, month);
        const labels = [];
        const values = [];
        for (let day = 1; day <= dim; day++) {
            const iso = `${year}-${pad2(month)}-${pad2(day)}`;
            labels.push(String(day));
            values.push(map.get(iso) || 0);
        }

        const ctx = document.getElementById("lineChart");
        if (lineChart) {
            lineChart.destroy();
            lineChart = null;
        }

        lineChart = new Chart(ctx, {
            type: "line",
            data: {
                labels,
                datasets: [{
                    label: "Daily spend (₹)",
                    data: values,
                    borderColor: "#38bdf8",
                    backgroundColor: "rgba(56, 189, 248, 0.15)",
                    fill: true,
                    tension: 0.25,
                    pointRadius: 2
                }]
            },
            options: {
                scales: {
                    x: {
                        ticks: { color: "#94a3b8", maxTicksLimit: 12 },
                        grid: { color: "rgba(148, 163, 184, 0.15)" }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: "#94a3b8" },
                        grid: { color: "rgba(148, 163, 184, 0.15)" }
                    }
                },
                plugins: {
                    legend: { labels: { color: "#cbd5e1" } }
                }
            }
        });
    }

    async function loadExpenses() {
        const { year, month } = getYearMonth();
        const cat = filterCategory.value;
        const q = cat ? `&categoryId=${encodeURIComponent(cat)}` : "";
        const list = await fetchJson(`${API}/expenses?year=${year}&month=${month}${q}`);

        expenseRows.innerHTML = "";
        if (!list.length) {
            emptyState.classList.remove("hidden");
            return;
        }
        emptyState.classList.add("hidden");

        list.forEach((e) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${e.expenseDate}</td>
                <td>${escapeHtml(e.categoryName)}</td>
                <td>${escapeHtml(e.description || "—")}</td>
                <td class="num">${fmtMoney(e.amount)}</td>
                <td class="actions">
                    <button type="button" class="btn btn--small btn--secondary" data-edit="${e.id}">Edit</button>
                    <button type="button" class="btn btn--small btn--danger" data-del="${e.id}">Delete</button>
                </td>`;
            expenseRows.appendChild(tr);
        });

        expenseRows.querySelectorAll("[data-edit]").forEach((btn) => {
            btn.addEventListener("click", () => openEdit(Number(btn.getAttribute("data-edit"))));
        });
        expenseRows.querySelectorAll("[data-del]").forEach((btn) => {
            btn.addEventListener("click", () => removeExpense(Number(btn.getAttribute("data-del"))));
        });
    }

    function escapeHtml(s) {
        const d = document.createElement("div");
        d.textContent = s;
        return d.innerHTML;
    }

    async function openEdit(id) {
        const e = await fetchJson(`${API}/expenses/${id}`);
        editId.value = e.id;
        editAmount.value = e.amount;
        editDate.value = e.expenseDate;
        editCategory.value = String(e.categoryId);
        editDesc.value = e.description || "";
        editModal.classList.remove("hidden");
    }

    function closeModal() {
        editModal.classList.add("hidden");
    }

    editModal.querySelectorAll("[data-close]").forEach((el) => {
        el.addEventListener("click", closeModal);
    });

    async function removeExpense(id) {
        if (!confirm("Delete this expense?")) return;
        try {
            await fetchJson(`${API}/expenses/${id}`, { method: "DELETE" });
            await refreshAll();
        } catch (err) {
            showBanner(err.message, "error");
        }
    }

    async function refreshAll() {
        try {
            await loadBudget();
            await loadSummary();
            await loadDailyChart();
            await loadExpenses();
        } catch (err) {
            showBanner(err.message || "Failed to load data. Is the API running?", "error");
        }
    }

    budgetForm.addEventListener("submit", async (ev) => {
        ev.preventDefault();
        const { year, month } = getYearMonth();
        try {
            await fetchJson(`${API}/budget`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    year,
                    month,
                    amountLimit: Number(budgetAmount.value)
                })
            });
            await refreshAll();
            showBanner("Budget saved.", "success");
        } catch (err) {
            showBanner(err.message, "error");
        }
    });

    expenseForm.addEventListener("submit", async (ev) => {
        ev.preventDefault();
        const { year, month } = getYearMonth();
        try {
            await fetchJson(`${API}/expenses`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    amount: Number(expAmount.value),
                    description: expDesc.value || null,
                    categoryId: Number(expCategory.value),
                    expenseDate: expDate.value
                })
            });
            expenseForm.reset();
            const today = new Date();
            expDate.value = `${year}-${pad2(month)}-${pad2(Math.min(today.getDate(), daysInMonth(year, month)))}`;
            await refreshAll();
            showBanner("Expense added.", "success");
        } catch (err) {
            showBanner(err.message, "error");
        }
    });

    editForm.addEventListener("submit", async (ev) => {
        ev.preventDefault();
        const id = editId.value;
        try {
            await fetchJson(`${API}/expenses/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    amount: Number(editAmount.value),
                    description: editDesc.value || null,
                    categoryId: Number(editCategory.value),
                    expenseDate: editDate.value
                })
            });
            closeModal();
            await refreshAll();
            showBanner("Expense updated.", "success");
        } catch (err) {
            showBanner(err.message, "error");
        }
    });

    filterCategory.addEventListener("change", () => {
        loadExpenses().catch((err) => showBanner(err.message, "error"));
    });

    applyPeriod.addEventListener("click", () => {
        const { year, month } = getYearMonth();
        const d = Math.min(new Date().getDate(), daysInMonth(year, month));
        expDate.value = `${year}-${pad2(month)}-${pad2(d)}`;
        refreshAll();
    });

    function init() {
        renderAuthBar();
        const token = getAuthToken();
        if (!token) {
            window.location.href = "/login.html";
            return;
        }
        const now = new Date();
        monthSelect.innerHTML = monthNames.map((name, i) =>
            `<option value="${i + 1}">${name}</option>`
        ).join("");
        monthSelect.value = String(now.getMonth() + 1);
        yearInput.value = String(now.getFullYear());

        const y = now.getFullYear();
        const m = now.getMonth() + 1;
        const d = now.getDate();
        expDate.value = `${y}-${pad2(m)}-${pad2(d)}`;

        loadCategories()
            .then(() => refreshAll())
            .catch((err) => {
                const msg = String(err.message || "");
                if (msg.toLowerCase().includes("auth")) {
                    showBanner("Please login to view your expenses.", "error");
                } else {
                    showBanner(msg, "error");
                }
            });
    }

    init();
})();
