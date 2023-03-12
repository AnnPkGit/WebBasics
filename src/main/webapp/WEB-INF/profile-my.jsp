<%@ page import="itstep.learning.data.entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<h2>My profile</h2>
<%
    User profileUser = (User) request.getAttribute( "authUser" ) ;
    String contextPath = request.getContextPath() ;
%>
<div class="row">
    <div class="col l10 m11 s12">
        <h2 class="header">Personal profile</h2>
        <div class="card horizontal">
            <div class="card-image">
                <label for="user-avatar">
                <img style="max-width: 90%"
                     id = "avatar"
                     src="<%= contextPath %>/image/<%= profileUser.getAvatar() %>"/>
                </label>
                <input type="file" hidden id="user-avatar">
            </div>
            <div class="card-stacked">
                <div class="card-content">
                    <p>Login: <b><%= profileUser.getLogin() %></b></p>
                    <p>Real Name:
                        <b id="user-name"><%= profileUser.getName() %></b>
                        <a id="user-name-btn" class="btn-floating btn-small waves-effect waves-light teal">
                            <i class="material-icons">edit</i>
                        </a>
                    </p>
                    <p>Email:
                        <b id="user-email"><%= profileUser.getEmail() %></b>
                        <a id="user-email-btn" class="btn-floating btn-small waves-effect waves-light teal">
                            <i class="material-icons">edit</i>
                        </a>
                    </p>
                    <p>Active from: <%= profileUser.getRegDt() %></p>
                </div>
                <div class="card-action">
                    <a href="mailto:<%= profileUser.getEmail() %>">Send Message</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const userNameBtn = document.getElementById("user-name-btn");
        userNameBtn.addEventListener("click", EditClick("user-name", "userName", userNameBtn.innerText));
        const userEmailBtn = document.getElementById("user-email-btn");
        userEmailBtn.addEventListener("click", EditClick("user-email", "userEmail", userNameBtn.innerText));
        const avatarInput = document.getElementById("user-avatar");
        avatarInput.addEventListener('change', avatarChange)

    });

    function EditClick(elementName, elementParam, priviousValue){
        let func = function userNameClick(e) {
            const userName = document.getElementById(elementName);
            if(userName.getAttribute("contenteditable")) {

                if(priviousValue == userName.innerText) {
                    userName.removeAttribute("contenteditable");
                    e.target.innerHTML = '<i class="material-icons">edit</i>';
                    return;
                }
                else {
                    priviousValue = userName.innerText;
                }
                fetch(window.location.href, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: `{"${elementParam}": "${userName.innerText}"}`
                }).then(r => r.text()).then(console.log);

                userName.removeAttribute("contenteditable");
                e.target.innerHTML = '<i class="material-icons">edit</i>';
            }
            else {
                userName.setAttribute("contenteditable", "true");
                userName.focus();
                e.target.innerHTML = '<i class="material-icons">save</i>';
            }
        }
        return func;
    }

    function avatarChange(e) {
            const avatar = document.getElementById("avatar");
            const file = e.target.files[0];
            if (fileTypes.includes(file.type)) {
                let formData = new FormData();
                formData.append("userAvatar", file);
                fetch(window.location.href, {
                    method: "PATCH",
                    body: formData
                }).then(r => r.text().then(console.log));
                avatar.src = URL.createObjectURL(file)
            }
            else {
                alert(`File name ${file.name} not a valid file type`)
            }
    }
    const fileTypes = [    "image/apng",    "image/bmp",    "image/gif",    "image/jpeg",    "image/pjpeg",    "image/png",    "image/svg+xml",    "image/tiff",    "image/webp",    "image/x-icon"];
</script>