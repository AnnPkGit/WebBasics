<%@ page import="com.google.inject.Inject" %>
<%@ page import="itstep.learning.service.auth.SessionAuthService" %>
<%@ page import="itstep.learning.data.entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String contextPath = request.getContextPath() ;
    String viewName = (String) request.getAttribute( "viewName" ) ;
    if( viewName == null ) viewName = "index" ;
    String viewPage = "/WEB-INF/" + viewName + ".jsp" ;
    User authUser = (User) request.getAttribute( "authUser" ) ;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebBasics</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" />
    <link rel="icon" href="<%= contextPath%>/image/logo.png">
</head>
<body>
<%=authUser%>
<div class="container" style="width:85%; margin-top:5px">
    <div class="row">
        <nav class="nav teal darken-2">
            <div class="nav-wrapper">
                <div class="col s12">
                    <a href="<%= contextPath %>" class="brand-logo">Web Basics</a>
                    <a href="#" data-target="mobile-demo" class="sidenav-trigger"><i class="material-icons">menu</i></a>

                    <% if( authUser != null ) { %>
                    <a href="<%= contextPath %>/profile/<%= authUser.getLogin() %>">
                    <img src="<%= contextPath %>/image/<%= authUser.getAvatar() %>"
                         class="right"
                         style="width: 44px; height: 44px; border-radius: 50%; margin-top:10px">
                    </a>
                    <% } %>

                    <ul class="right hide-on-med-and-down" id="main-menu">
                        <li <%= viewName.equals( "index" ) ? "class='active'" : "" %> ><a href="<%= contextPath %>/home"><i class="material-icons left">home</i>Home</a></li>
                        <li <%= viewName.equals( "reg-user" ) ? "class='active'" : "" %> ><a href="<%= contextPath %>/register"><i class="material-icons left">person</i>Registration</a></li>

                        <% if( authUser == null ) { %>
                            <li><a href="#auth-modal" class="waves-effect waves-light modal-trigger"><i class="material-icons left">person_outline</i>Log in</a></li><% }
                        else { %>
                            <li><a href="#log-out-modal" class="waves-effect waves-light modal-trigger"><i class="material-icons left">exit_to_app</i>Log out</a></li>
                        <% } %>

                    </ul>
                    <script>const mainMenu = document.getElementById("main-menu"); const mob = mainMenu.cloneNode(true); mob.className = "sidenav"; mob.id = "mobile-demo"; mainMenu.parentNode.appendChild(mob);</script>
                </div>
            </div>
        </nav>
    </div>

    <div class="container" style="width:95%">
        <jsp:include page="<%= viewPage %>" />
    </div>
</div>

<!-- Modal Structure -->
  <div id="auth-modal" class="modal">
    <div class="modal-content">
      <h4>Authentication</h4>
      <div class="row input-field">
          <i class="material-icons prefix">person</i>
          <input id="auth-login" type="text">
          <label for="auth-login">Login</label>
      </div>
        <label id="login-error" style="color: red"></label>
      <div class="row input-field">
          <i class="material-icons prefix">lock_open</i>
          <input id="auth-pass" type="password">
          <label for="auth-pass">Password</label>
      </div>
        <label id="pass-error" style="color: red"></label>
        <label id="log-in-error" style="color: red"></label>
    </div>
    <div class="modal-footer">
      <div class="btn" id="auth-button">
        <span>LOG IN</span>
      </div>
    </div>
  </div>

<div id="log-out-modal" class="modal">
    <div class="modal-content">
        <h4>Are you sure you want to log out?</h4>
    </div>
    <div class="modal-footer">
        <a id="cancel-log-out" class="btn"><i class="material-icons left">exit_to_app</i>Cancel</a>
        <a class="btn" href="?logout"><i class="material-icons left">exit_to_app</i>Log out</a>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script>document.addEventListener('DOMContentLoaded', function() {
  M.Sidenav.init(document.querySelectorAll('#mobile-demo'), { });  // options: https://materializecss.com/sidenav.html#options
  var elems = document.querySelectorAll('.modal');
  var instances = M.Modal.init(elems, {});

  document.getElementById("auth-button")
    .addEventListener("click",e => {
        const authLogin = document.getElementById("auth-login").value;
        const authPass = document.getElementById("auth-pass").value;

        const loginError = document.getElementById("login-error");
        const passError = document.getElementById("pass-error");
        var errorOccured = false;

        if(authLogin == "" || authLogin == null){
            loginError.innerHTML = "Login can not be empty";
            errorOccured = true;
        }
        else {
            loginError.innerHTML = "";
        }

        if(authPass == "" || authPass == null){
            passError.innerHTML = "Password can not be empty";
            errorOccured = true;
        }
        else {
            passError.innerHTML = "";
        }

        if(errorOccured)
            return;

        fetch("<%= contextPath %>/auth", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `auth-login=${authLogin}&auth-pass=${authPass}`
        }).then(r=>r.text()).then(t => {
            const logInError = document.getElementById("log-in-error");
            if(t == "OK") {
                logInError.innerHTML = "";
                window.location = window.location
            }
            else {
                logInError.innerHTML = "Unauthorized";
            }
        }) ;
    });

  document.getElementById("cancel-log-out")
      .addEventListener("click", () => {
          window.location = window.location
  });
});</script>
</body>
</html>