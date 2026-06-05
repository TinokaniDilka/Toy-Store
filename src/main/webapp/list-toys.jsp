<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="/common/theme-header.jsp" %>
<body>
<div class="floating-toys">
    <i class="fas fa-teddy-bear toy" style="top: 10%; left: 10%; animation-delay: 0s;"></i>
    <i class="fas fa-robot toy" style="top: 20%; right: 15%; animation-delay: 1s;"></i>
    <i class="fas fa-gamepad toy" style="bottom: 15%; left: 20%; animation-delay: 2s;"></i>
    <i class="fas fa-puzzle-piece toy" style="bottom: 25%; right: 25%; animation-delay: 3s;"></i>
</div>

<%@ include file="/common/store-navbar.jsp" %>

<div class="container toy-container mt-2" style="position: relative; z-index: 1;">
    <div class="row mb-4 align-items-center">
        <div class="col">
            <h2 class="store-title mb-1" style="color: var(--primary-color); font-size: 2.2em;">
                <i class="fas fa-gift me-2"></i>Welcome to Toy Store
            </h2>
            <p class="text-muted mb-0">Browse toys, place an order, pay, and leave a review — all in one place.</p>
        </div>
        <c:if test="${not empty sessionScope.admin}">
            <div class="col-auto">
                <a href="${pageContext.request.contextPath}/add-toy" class="btn btn-register">
                    <i class="fas fa-plus"></i> Add Toy
                </a>
            </div>
        </c:if>
    </div>

    <div class="row mb-4">
        <div class="col">
            <form action="${pageContext.request.contextPath}/list-toys" method="get" class="form-inline">
                <div class="input-group w-100">
                    <input type="text" name="search" class="form-control"
                           style="border-radius: 30px 0 0 30px; font-family: 'Comic Sans MS', cursive, sans-serif; font-size: 1.2em;"
                           placeholder="Search toys..." value="${param.search}">
                    <button type="submit" class="btn"
                            style="background: #FF6B6B; color: #fff; border-radius: 0 30px 30px 0; width: 48px; display: flex; align-items: center; justify-content: center; font-size: 1.2em; margin-left: -1px;">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row row-cols-1 row-cols-md-3 g-4">
        <c:forEach items="${toys}" var="toy">
            <div class="col">
                <div class="card toy-card h-100" style="border-radius: 30px; box-shadow: 0 10px 30px rgba(255,107,107,0.10);">
                    <c:set var="imageUrl" value="${pageContext.request.contextPath}/images/${toy.imageName}"/>
                    <div class="card-img-top" style="height: 200px; overflow: hidden; background-color: #f8f9fa;">
                        <c:choose>
                            <c:when test="${not empty toy.imageName}">
                                <img src="${imageUrl}" alt="${toy.name}" class="toy-img"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/default-toy.jpg';">
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex align-items-center justify-content-center h-100">
                                    <i class="fas fa-image fa-3x text-muted"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <h5 class="toy-title"><i class="fas fa-cube me-2"></i>${toy.name}</h5>
                        <p class="toy-desc">${toy.description}</p>
                        <p class="toy-price">$${toy.price}</p>
                    </div>
                    <div class="card-footer bg-transparent border-top-0 pb-3">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/order/checkout?toyId=${toy.imageName}&qty=1"
                               class="btn btn-register">
                                <i class="fas fa-shopping-cart"></i> Buy Now
                            </a>
                            <a href="${pageContext.request.contextPath}/review?productId=${toy.imageName}"
                               class="btn btn-outline-secondary btn-sm">
                                <i class="fas fa-star"></i> Reviews
                            </a>
                        </div>
                        <c:if test="${not empty sessionScope.admin}">
                            <div class="btn-group w-100 mt-2">
                                <a href="${pageContext.request.contextPath}/edit-toy?imageName=${toy.imageName}"
                                   class="btn btn-edit btn-sm me-1">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                                <a href="${pageContext.request.contextPath}/delete-toy?imageName=${toy.imageName}"
                                   class="btn btn-delete btn-sm"
                                   onclick="return confirm('Delete this toy?')">
                                    <i class="fas fa-trash"></i> Delete
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <c:if test="${empty toys}">
        <div class="alert alert-info mt-4 text-center">
            <i class="fas fa-info-circle me-2"></i>No toys available yet.
            <c:if test="${not empty sessionScope.admin}">
                <a href="${pageContext.request.contextPath}/add-toy">Add the first toy</a>
            </c:if>
        </div>
    </c:if>
</div>

<style>
.floating-toys { position: fixed; width: 100vw; height: 100vh; pointer-events: none; z-index: 0; top: 0; left: 0; }
.toy { position: absolute; font-size: 32px; opacity: 0.7; animation: float 6s infinite; }
@keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-20px) rotate(10deg); } }
</style>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
