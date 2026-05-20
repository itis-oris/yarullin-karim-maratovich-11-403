<!DOCTYPE html>
<html lang="ru">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${pageTitle!'Tour Booking'}</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/main.css" rel="stylesheet">
<meta name="_csrf" content="${(_csrf.token)!''}"/><meta name="_csrf_header" content="${(_csrf.headerName)!''}"/>
</head><body class="d-flex flex-column min-vh-100 bg-light">
<#include "header.ftlh">
<main class="flex-grow-1 container py-4">
<#if successMessage??><div class='alert alert-success'>${successMessage}</div></#if>
<#if errorMessage??><div class='alert alert-danger'>${errorMessage}</div></#if>
<#if errors??><div class='alert alert-danger'><ul><#list errors as e><li>${e}</li></#list></ul></div></#if>
<#if pageContent??><#include pageContent></#if>
</main>
<#include "footer.ftlh">
<div id="notification-area" class="position-fixed bottom-0 end-0 p-3" style="z-index:1100;"></div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/app.js"></script><script src="/js/ajax-utils.js"></script>
</body></html>
