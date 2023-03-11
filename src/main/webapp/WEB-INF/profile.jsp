<%@ page import="itstep.learning.data.entity.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
%>
<h2>Personal profile</h2>
<img style="height: 100px" src="<%= request.getContextPath() %>/image/<%= profileUser.getAvatar()%>"> </br>
Login: <%= profileUser.getLogin() %> </br>
RealName: <%= profileUser.getName() %> </br>
Email: <%= profileUser.getEmail() %> </br>
Reg dt: <%= profileUser.getRegDt() %> </br>