function disableEmptyPits(game) {
    let pits = document.getElementsByClassName("pit");
    for (let i = 0; i < pits.length; i++) {
        if (pits[i].textContent == "0") {
            pits[i].removeAttribute("onclick");
            pits[i].style.cursor = "default";
        }
    }
}

function setNextTurn(game) {
    if (Object.hasOwn(game, 'winner')) {
        let pits = document.getElementsByClassName("pit");
        for (let i = 0; i < pits.length; i++) {
            pits[i].removeAttribute("onclick");
            pits[i].style.cursor = "default";
        }
    } else {
        let playerRow, playerNameContainer;
        if (game.boardDto.nextTurn.id == game.boardDto.firstPlayer.id) {
            playerRow = document.getElementById("player-two-row");
            playerNameContainer = document.getElementById("player-one-name-container");
        } else {
            playerRow = document.getElementById("player-one-row");
            playerNameContainer = document.getElementById("player-two-name-container");
        }
        let children = playerRow.children;
        for (let i = 0; i < children.length; i++) {
            children[i].removeAttribute("onclick");
            children[i].style.cursor = "default";
        }
        playerNameContainer.insertAdjacentHTML("beforeend", "<p style=\"color:red;\">Is your turn</p>")
    }
}

function sowSeeds(gameId, playerId, pitId) {
    fetch('http://localhost:8080/game/sow', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: `{
                "gameId":${gameId},
                "playerId":${playerId},
                "pitId":${pitId}
            }`
    }).then(res => {
        return res.json();
    }).then(game => {
        document.getElementById('top-container').innerHTML = getBoard(game, id);
        appendWinnerModal(game);
        setNextTurn(game);
        disableEmptyPits(game);
    }).catch(error => console.log(error))
}

function getBoard(game, id) {
    return `<div>
                <div id="player-two-name-container">
                Player 2: ${game.boardDto.secondPlayer.name}
                </div>
            <div class="board">
            <div class="player-two store">${game.boardDto.secondPlayer.bigPitDto.seeds}</div>

            <div class="rows">
                <div id="player-two-row" class="row player-two">
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},0)">${game.boardDto.secondPlayer.smallPits[0].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},1)">${game.boardDto.secondPlayer.smallPits[1].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},2)">${game.boardDto.secondPlayer.smallPits[2].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},3)">${game.boardDto.secondPlayer.smallPits[3].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},4)">${game.boardDto.secondPlayer.smallPits[4].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.secondPlayer.id},5)">${game.boardDto.secondPlayer.smallPits[5].seeds}</div>
                </div>

                <div id="player-one-row" class="row player-one">
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},0)">${game.boardDto.firstPlayer.smallPits[0].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},1)">${game.boardDto.firstPlayer.smallPits[1].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},2)">${game.boardDto.firstPlayer.smallPits[2].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},3)">${game.boardDto.firstPlayer.smallPits[3].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},4)">${game.boardDto.firstPlayer.smallPits[4].seeds}</div>
                    <div class="pit" onclick="sowSeeds(${id},${game.boardDto.firstPlayer.id},5)">${game.boardDto.firstPlayer.smallPits[5].seeds}</div>
                </div>
            </div>

            <div class="player-one store">${game.boardDto.firstPlayer.bigPitDto.seeds}</div>
        </div>
        <div id="player-one-name-container">
        Player 1: ${game.boardDto.firstPlayer.name}</div>
        </div>`;
}

function appendWinnerModal(game) {
    if (Object.hasOwn(game, 'winner')) {
        const winnerModal = ` <div class="alert alert-success" role="alert">
                    <h4 class="alert-heading">Game is over!</h4>
                    <p> Winner is ${game.winner.name}</p>
                    <hr>
                        <p class="mb-0">You can start a new game.</p>
                </div>`;
        document.getElementById('top-container').insertAdjacentHTML('afterend', winnerModal)
    }
}

function fetchGameById(id) {
    fetch('http://localhost:8080/game/' + id)
        .then(res => {
            return res.json();
        })
        .then(game => {
            appendWinnerModal(game);
            document.getElementById('top-container').insertAdjacentHTML('beforeend', getBoard(game, id))
            setNextTurn(game);
            disableEmptyPits(game);
        }).catch(error => console.log(error))
}

function fetchAllGames() {
    fetch('http://localhost:8080/game/all')
        .then(res => {
            return res.json();
        })
        .then(data => {
            data.forEach(game => {
                let isOver = game.winner != null;
                let winnerName = "N/A";
                let endTime = "N/A";
                let playGameButton = '<a class="btn btn-outline-success" href="game.html?gameId=' + game.id + '" role="button">Play</a>';
                if (isOver) {
                    winnerName = game.winner.name;
                    endTime = game.endTime;
                    playGameButton = '<button type="button" class="btn btn-outline-danger" disabled>Ended</button>';
                }
                const tableRow = `<tr>
      <td>${game.id}</td>
      <td>${playGameButton}</td>
      <td>${game.boardDto.firstPlayer.name}</td>
      <td>${game.boardDto.secondPlayer.name}</td>
      <td>${winnerName}</td>
      <td>${game.startTime}</td>
      <td>${endTime}</td>
    </tr>`
                document.querySelector('tbody').insertAdjacentHTML('beforeend', tableRow)
            });
        }).catch(error => console.log(error))
}

function createPlayer(playerName) {
    fetch('http://localhost:8080/player/save', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: `{
                "name":"${playerName}"
            }`
    }).then(res => {
        return res.json();
    }).then(player => {

        const playerDetails = `<div class="alert alert-success" role="alert">
     A new player was created.
    Player name: ${player.name}
    Player id: ${player.id}
</div>`
        document.getElementById('form-container').insertAdjacentHTML('beforeend', playerDetails)
    }).catch(error => console.log(error))
}

function fetchAllPlayers() {
    fetch('http://localhost:8080/player/all')
        .then(res => {
            return res.json();
        })
        .then(players => {
            players.forEach(player => {

                const tableRow = `<tr>
      <td>${player.id}</td>
      <td>${player.name}</td>
    </tr>`
                document.querySelector('tbody').insertAdjacentHTML('beforeend', tableRow)
            })
        }).catch(error => console.log(error))
}

function startGame(firstPlayerId, secondPlayerId) {
    fetch('http://localhost:8080/game/start?firstPlayerId=' + firstPlayerId + '&secondPlayerId=' + secondPlayerId, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(res => {
        return res.json();
    }).then(game => {
        console.log(game);
        window.location.href="game.html?gameId="+game.id;
    }).catch(error => console.log(error))
}