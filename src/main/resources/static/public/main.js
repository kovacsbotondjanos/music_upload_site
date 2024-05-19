document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname === '/profile') {
        fetch('/songs', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const songs = document.getElementById('songs');
            //TODO: iterate thorugh the data adn add it in divs
        })
        .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
        });

        fetch('/albums', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const songs = document.getElementById('albums');
            console.log(data);
            //TODO: iterate thorugh the data adn add it in divs
        })
        .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
        });
    }
});