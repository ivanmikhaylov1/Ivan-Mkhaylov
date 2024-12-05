<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная страница</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css-1.6.0.min.css"/>
</head>

<body>

<h1>Список статей</h1>
<table>
    <tr>
        <th>ID</th>
        <th>Название</th>
        <th>Количество комментариев</th>
        <th>Теги</th>
    </tr>
    <#list articles as article>
        <tr>
            <td>${article.id!}</td>
            <td>${article.title!}</td>
            <td>${article.number!}</td>
            <td>${article.tags!}</td>
            <td>
            </td>
        </tr>
    </#list>
</table>

</body>
</html>