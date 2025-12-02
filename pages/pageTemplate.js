export function pageTemplate(title, contents, onload) {
  return `
  <!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <title>Library</title>
      <link type="text/css" rel="stylesheet" href="/global.css" />
      <script type="text/javascript" src="/client.js"></script>
    </head>

    <body onload="${onload}">
      <button class="reset-btn" onclick="handleResetBtn()">Reset</button>
      <h1 id="page-title">${title}</h1>
      <div id="page-contents">${contents}</div>
    </body>
  </html>
  `;
}
