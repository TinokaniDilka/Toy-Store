<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<jsp:include page="../common/header.jsp"/>

<div class="row">
    <div class="col-md-6 offset-md-3 text-center">
        <div class="card">
            <div class="card-body">
                <h1 class="display-1">404</h1>
                <h2 class="mb-4">Page Not Found</h2>
                <p class="lead">The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
                <a href="${pageContext.request.contextPath}/list-toys" class="btn btn-primary">Go to Home Page</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/> 