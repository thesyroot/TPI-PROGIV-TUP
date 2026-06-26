// Main application JavaScript for Prode

document.addEventListener('DOMContentLoaded', function () {
    // Auto-hide flash messages after 5 seconds
    const flashMessages = document.querySelectorAll('.bg-green-50, .bg-red-50');
    flashMessages.forEach(function (msg) {
        setTimeout(function () {
            msg.style.opacity = '0';
            msg.style.transition = 'opacity 0.5s ease-out';
            setTimeout(function () {
                msg.remove();
            }, 500);
        }, 5000);
    });

    // Confirm delete before form submission
    const deleteForms = document.querySelectorAll('form[action*="/delete"]');
    deleteForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            if (confirm('Esta seguro de que desea eliminar este elemento?')) {
                form.submit();
            }
        });
    });

    // Prevent selecting same team for local and visitor in match form
    const localSelect = document.getElementById('localId');
    const visitanteSelect = document.getElementById('visitanteId');

    if (localSelect && visitanteSelect) {
        function validateTeams() {
            if (localSelect.value && localSelect.value === visitanteSelect.value) {
                visitanteSelect.setCustomValidity('El equipo visitante debe ser diferente al local');
            } else {
                visitanteSelect.setCustomValidity('');
            }
        }

        localSelect.addEventListener('change', validateTeams);
        visitanteSelect.addEventListener('change', validateTeams);
    }

    // Number input: prevent negative values
    document.querySelectorAll('input[type="number"][min="0"]').forEach(function (input) {
        input.addEventListener('blur', function () {
            if (this.value && parseInt(this.value) < 0) {
                this.value = 0;
            }
        });
    });

    // Style active sidebar links
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-link').forEach(function (link) {
        if (link.getAttribute('data-href') === currentPath) {
            link.classList.add('active');
        }
    });
});

// buscador de jugadores en tiempo real (Formulario de Equipos)
const playerSearchInput = document.getElementById('playerSearch');

if (playerSearchInput) {
    playerSearchInput.addEventListener('input', function () {
        // Convertimos la búsqueda a minúsculas para comparar
        const filter = this.value.toLowerCase();
        const rows = document.querySelectorAll('.player-row');

        rows.forEach(function (row) {
            // buscamos el span que tiene la clase text-gray-700 (el nombre real)
            const nameSpan = row.querySelector('span.text-gray-700');
            const name = nameSpan ? nameSpan.textContent.toLowerCase() : '';

            // si el nombre incluye lo que buscamos, mostramos la fila, si no, la ocultamos
            if (name.includes(filter)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });
}
