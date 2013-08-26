<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  ~ Copyright 2013 EnergyOS.org
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>

<!DOCTYPE html>
<html lang="en">

<jsp:include page="/WEB-INF/pages/tiles/head.jsp"/>

<body>

<jsp:include page="/WEB-INF/pages/tiles/custodian/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>Upload Usage Points</h2>

            <form:form modelAttribute="uploadForm" class="form-horizontal" action="/custodian/retailcustomers/${retailCustomer.id}/upload" enctype="multipart/form-data">
                <form:errors path="*" cssClass="alert alert-error" element="div" />

                <div class="control-group">
                    <label class="control-label" for="file">File</label>
                    <div class="controls">
                        <input type="file" name="file" />
                        <input type="submit" name="upload" value="Upload"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>

    <hr>

    <jsp:include page="/WEB-INF/pages/tiles/footer.jsp"/>

</div>

</body>
</html>
