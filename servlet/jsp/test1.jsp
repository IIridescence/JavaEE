<!-- 表明此为一个JSP页面 -->

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML>
    <HEAD>
    <TITLE>第一个JSP页面</TITLE>
    </HEAD
	
    <BODY>
    <!-- 下面是Java脚本 Scriptlet-->
    <%for(int i = 0 ; i < 10; i++)
    {
    out.println(i);
    %>
    <br>
    <%}%>
    </BODY>
    </HTML>