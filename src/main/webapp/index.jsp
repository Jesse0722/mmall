<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Hello World!</h2>
<p>SpringMvc上传文件</p>
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc文件上传"/>
</form>
<form name="form2" action="/manage/product/richtext_img_upload.do" enctype="multipart/form-data" >
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本图片文件上传"/>
</form>
</body>
</html>
