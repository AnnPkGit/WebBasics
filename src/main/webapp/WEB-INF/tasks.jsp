<%@ page import="itstep.learning.data.entity.User" %>
<%@ page import="itstep.learning.data.entity.Team" %>
<%@ page import="java.util.List" %>
<%@ page import="itstep.learning.data.entity.Task" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String domain = request.getContextPath() ;
    User authUser = (User) request.getAttribute( "authUser" ) ;
    List<Team> teams = (List<Team>) request.getAttribute( "teams" ) ;
    List<Task> tasks = (List< Task>) request.getAttribute( "tasks" ) ;
%>
<div class="row">

    <div class="col s5 m4 l3">
        <!-- region Блок задач -->
        <% for( Task task : tasks ) { %>
        <div class="row">
            <div class="col">
                <div class="card blue-grey darken-1">
                    <div id="<%= task.getId() %>" style="float: right; font-weight: bolder; color: greenyellow; text-align: center; margin: 5px"></div>
                    <div class="card-content white-text">
                        <span class="card-title"><%= task.getName() %></span>
                        <p><%= task.getCreatedDt() %> -- <%= task.getDeadline() %></p>
                    </div>
                    <div class="card-action">
                        <a href="#<%= task.getId() %>">Discus</a>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
        <!-- endregion Конец блока задач -->
    </div>

    <div class="col s7 m8 l9">
        <!-- region Блок обсуждения (комментариев) -->
        <p id="chat">
        </p>
        <form
                method="post" id="story-form">
            <textarea id="textarea1" class="materialize-textarea" name="story-text"></textarea>
            <label for="textarea1">Textarea</label>
            <div class="row input-field right-align">
                <button class="btn waves-effect waves-teal" type="submit">отправить<i class="material-icons right">add</i></button>
            </div>
            <input type="hidden" name="story-id-task" />
        </form>

        <textarea id="textarea2"></textarea>
        <button onclick="sendClick()">Send</button>
        <!-- endregion Блок обсуждения (комментариев) -->
    </div>

</div>


<!-- region Добавить задачу -->
<div class="row">
    <h4>Добавить задачу</h4>
    <form class="col s10 offset-s1 m8 offset-m2 l6 offset-l3"
          method="post" id="task-form">
        <div class="row input-field"><i class="material-icons prefix">content_paste</i>
            <input id="task-name" type="text" name="task-name">
            <label for="task-name">Название</label>
        </div>
        <div class="row input-field"><i class="material-icons prefix">people_outline</i>
            <select name="task-team">
                <option value="" disabled selected>Выберите команду</option>
                <% for( Team team : teams ) { %>
                <option value="<%= team.getId() %>"><%= team.getName() %></option>
                <% } %>
            </select>
            <label>Команда</label>
        </div>
        <div class="row input-field"><i class="material-icons prefix">event_available</i>
            <input id="task-deadline" type="text" class="datepicker" name="task-deadline">
            <label for="task-deadline">Завершение</label>
        </div>
        <div class="row input-field"><i class="material-icons prefix">priority_high</i>
            <select name="task-priority">
                <option value="" disabled selected>Выберите приоритет</option>
                <option value="0">Обычный</option>
                <option value="1">Высокий</option>
                <option value="2">Экстремальный</option>
            </select>
            <label>Приоритет</label>
        </div>
        <div class="row input-field right-align">
            <button class="btn waves-effect waves-teal" type="submit">создать<i class="material-icons right">add</i></button>
        </div>
    </form>
</div>
<!-- endregion -->

Д.З. Разработать метод передачи ID задачи в websocket-сообщении
При получении websocket-сообщения проверять ID задачи:
- если эта задача открыта в обсуждении, то выводить новое сообщение в чат
- если открыта другая задача, то добавлять символ/стиль нового сообщения в задаче

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var elems = document.querySelectorAll('select');
        var instances = M.FormSelect.init(elems, {});
        elems = document.querySelectorAll('.datepicker');
        instances = M.Datepicker.init(elems, {format: "yyyy-mm-dd"});
        initWebsocket() ;
    });

    function sendClick() {
        let index = window.location.href.lastIndexOf('#');
        let taskId = window.location.href.substring(index + 1);

        window.websocket.send(
            document.getElementById("textarea2").value + " |id:" + taskId
        ) ;
    }
    function initWebsocket() {
        window.websocket = new WebSocket( "ws://localhost:8080/WebBasics/chat" ) ;
        window.websocket.onopen = onWsOpen;
        window.websocket.onmessage = onWsMessage;
        window.websocket.onclose = onWsClose;
        window.websocket.onerror = onWsError;
    }
    function onWsOpen( e ) {
        // console.log( "onWsOpen", e ) ;
    }
    function onWsMessage( e ) {
        // console.log( "onWsMessage", e.data ) ;
        let index = e.data.lastIndexOf("|id:");
        let taskId = e.data.substring(index + 4);
        //
        let curentIndex = window.location.href.lastIndexOf('#');
        let curentTaskId = window.location.href.substring(curentIndex + 1);

        if(taskId == curentTaskId) {
            let text = e.data.substring(0, index);
            const chat = document.getElementById("chat");
            chat.innerText += text + '\n\n';
            document.getElementById("textarea2").value = "";
        }
        else {
            if(document.getElementById(taskId).innerText != "") {
                let num = parseInt(document.getElementById(taskId).innerText);
                document.getElementById(taskId).innerText = num + 1;
            }
            else {
                document.getElementById(taskId).innerText = 1;
            }
        }
    }
    function onWsClose( e ) {
        // console.log( "onWsClose", e ) ;
    }
    function onWsError( e ) {
        console.log( "onWsError", e ) ;
    }
    document.addEventListener('submit', e => {
        e.preventDefault() ;
        switch( e.target.id ) {
            case 'story-form': sendStoryForm(); break ;
            case 'task-form' : sendTaskForm();  break ;
        }
    });
    function sendTaskForm() {
        fetch( window.location.href, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams( new FormData( document.querySelector("#task-form") ) )
        }).then( r=> r.text() )
            .then( t => {
                console.log(t);
                if(t === "OK") window.location.reload() ;
            } ) ;
    }
    function sendStoryForm() {
        fetch( '<%= domain + "/story" %>', {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams( new FormData( document.querySelector("#story-form") ) )
        }).then( r=> r.text() )
            .then( console.log ) ;
    }
    window.addEventListener('hashchange', () => {
        const taskId = window.location.hash.substring(1) ;
        if( ! /[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/.test(taskId)) return ;
        document.querySelector('input[name="story-id-task"]').value = taskId ;
        console.log(taskId) ;
    });
</script>